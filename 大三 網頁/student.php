<?php
require 'config.php';

// ===== 登入檢查 =====
if (!isset($_SESSION['role']) || $_SESSION['role'] !== 'student') {
    header('Location: index.php');
    exit;
}

$student_id = $_SESSION['user_id'];

// 讀取 Session 訊息並清除
$message = '';
if (isset($_SESSION['msg'])) {
    $message = $_SESSION['msg'];
    unset($_SESSION['msg']);
}

// ===== 加選 / 退選處理 (衝堂檢查) =====
if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    if ($_POST['action'] === 'enroll') {
        $section_id = $_POST['section_id'];

        $stmt = $pdo->prepare("SELECT time_info FROM course_sections WHERE section_id = ?");
        $stmt->execute([$section_id]);
        $target_section = $stmt->fetch();
        $target_time = $target_section['time_info'];

        $checkStmt = $pdo->prepare("
            SELECT c.title FROM enrollments e
            JOIN course_sections cs ON e.section_id = cs.section_id
            JOIN courses c ON cs.course_id = c.course_id
            WHERE e.student_id = ? AND cs.time_info = ?
        ");
        $checkStmt->execute([$student_id, $target_time]);
        $conflict = $checkStmt->fetch();

        if ($conflict) {
            $_SESSION['msg'] = "❌ 衝堂警告！該時段（{$target_time}）您已選修了：【" . $conflict['title'] . "】";
        } else {
            try {
                $stmt = $pdo->prepare("INSERT INTO enrollments (student_id, section_id) VALUES (?, ?)");
                $stmt->execute([$student_id, $section_id]);
                $_SESSION['msg'] = '✅ 加選成功！';
            } catch (PDOException $e) {
                $_SESSION['msg'] = '❌ 加選失敗：您可能已經選過這門課程。';
            }
        }
    }

    if ($_POST['action'] === 'drop') {
        $stmt = $pdo->prepare("DELETE FROM enrollments WHERE student_id = ? AND section_id = ?");
        $stmt->execute([$student_id, $_POST['section_id']]);
        $_SESSION['msg'] = '✅ 退選成功';
    }

    header('Location: student.php');
    exit;
}

// ===== 1. 我的選課清單 =====
$myCourses = $pdo->prepare("
    SELECT cs.section_id, c.course_id, c.title, c.credits, t.name AS teacher, r.room_name, cs.time_info
    FROM enrollments e
    JOIN course_sections cs ON e.section_id = cs.section_id
    JOIN courses c ON cs.course_id = c.course_id
    LEFT JOIN teachers t ON cs.teacher_id = t.teacher_id
    LEFT JOIN classrooms r ON cs.room_id = r.room_id
    WHERE e.student_id = ?
");
$myCourses->execute([$student_id]);

// ===== 2. 本學期開課列表 =====
$allCourses = $pdo->query("
    SELECT cs.section_id, c.course_id, c.title, c.credits, t.name AS teacher, cs.time_info, cs.max_students, 
    (SELECT COUNT(*) FROM enrollments e2 WHERE e2.section_id = cs.section_id) AS current_students
    FROM course_sections cs
    JOIN courses c ON cs.course_id = c.course_id
    LEFT JOIN teachers t ON cs.teacher_id = t.teacher_id
    WHERE cs.semester = '113-1'
");

// 幫助函式：處理教師姓名連結
function getTeacherLink($name) {
    switch ($name) {
        case '陳宗輝':
            return '<a href="teacher_profile.php" class="link-text">' . htmlspecialchars($name) . '</a>';
        case '王藝華':
            return '<a href="teacher_profile_wang.php" class="link-text">' . htmlspecialchars($name) . '</a>';
        case '羅懷暐':
            return '<a href="teacher_profile_luo.php" class="link-text">' . htmlspecialchars($name) . '</a>';
        default:
            return htmlspecialchars($name);
    }
}
?>

<!DOCTYPE html>
<html lang="zh-TW">
<head>
    <meta charset="UTF-8">
    <title>學生選課系統</title>
    <style>
        body { font-family:"Microsoft JhengHei"; background:#f4f4f4; padding:20px; }
        .container { max-width:1100px; margin:auto; background:white; padding:20px; border-radius:8px; box-shadow: 0 2px 5px rgba(0,0,0,0.1); }
        .nav { display:flex; justify-content:space-between; margin-bottom:20px; align-items: center; }
        table { width:100%; border-collapse:collapse; margin-top:10px; }
        th, td { border:1px solid #ccc; padding:12px; text-align:left; }
        th { background:#eee; }
        .btn { padding:6px 12px; color:white; border:none; cursor:pointer; border-radius:4px; text-decoration: none; display: inline-block; font-size:14px; }
        .red { background:#dc3545; }
        .green { background:#28a745; }
        .alert { background:#fff3cd; padding:15px; margin-bottom:20px; border:1px solid #ffeeba; color: #856404; font-weight: bold; border-radius: 4px; }
        .link-text { color: #007bff; text-decoration: none; font-weight: bold; }
        .link-text:hover { text-decoration: underline; color: #0056b3; }
        .time-tag { color: #2c3e50; font-weight: bold; background: #e9ecef; padding: 2px 6px; border-radius: 3px; }
    </style>
</head>
<body>

<div class="container">
    <div class="nav">
        <div>學生：<strong><?= htmlspecialchars($_SESSION['name']) ?></strong></div>
        <a href="index.php?action=logout" class="btn red">登出系統</a>
    </div>

    <?php if ($message): ?>
        <div class="alert"><?= $message ?></div>
    <?php endif; ?>

    <h3>我的選課清單</h3>
    <table>
        <tr>
            <th>課程名稱</th><th>學分</th><th>教師</th><th>教室</th><th>上課時間</th><th>操作</th>
        </tr>
        <?php while ($row = $myCourses->fetch()): ?>
        <tr>
            <td><a href="course_detail.php?id=<?= $row['course_id'] ?>" target="_blank" class="link-text"><?= htmlspecialchars($row['title']) ?></a></td>
            <td><?= $row['credits'] ?></td>
            <td><?= getTeacherLink($row['teacher']) ?></td>
            <td><?= htmlspecialchars($row['room_name']) ?></td>
            <td><span class="time-tag"><?= htmlspecialchars($row['time_info']) ?></span></td>
            <td>
                <form method="POST" onsubmit="return confirm('確定退選嗎？')">
                    <input type="hidden" name="action" value="drop"><input type="hidden" name="section_id" value="<?= $row['section_id'] ?>">
                    <button class="btn red">退選</button>
                </form>
            </td>
        </tr>
        <?php endwhile; ?>
    </table>

    <h3 style="margin-top:40px;">本學期開課列表 (113-1)</h3>
    <table>
        <tr>
            <th>課程名稱</th><th>學分</th><th>教師</th><th>上課時間</th><th>人數</th><th>操作</th>
        </tr>
        <?php while ($row = $allCourses->fetch()): ?>
        <tr>
            <td><a href="course_detail.php?id=<?= $row['course_id'] ?>" target="_blank" class="link-text"><?= htmlspecialchars($row['title']) ?></a></td>
            <td><?= $row['credits'] ?></td>
            <td><?= getTeacherLink($row['teacher']) ?></td>
            <td><span class="time-tag"><?= htmlspecialchars($row['time_info']) ?></span></td>
            <td><?= $row['current_students'] ?> / <?= $row['max_students'] ?></td>
            <td>
                <?php if ($row['current_students'] < $row['max_students']): ?>
                    <form method="POST">
                        <input type="hidden" name="action" value="enroll"><input type="hidden" name="section_id" value="<?= $row['section_id'] ?>">
                        <button class="btn green">加選</button>
                    </form>
                <?php else: ?> 已額滿 <?php endif; ?>
            </td>
        </tr>
        <?php endwhile; ?>
    </table>
</div>

</body>
</html>