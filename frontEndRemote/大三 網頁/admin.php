<?php
require 'config.php';

// 登入與權限檢查
if (!isset($_SESSION['role']) || $_SESSION['role'] !== 'admin') {
    header('Location: index.php');
    exit;
}

// 讀取 Session 中的訊息後清除，防止重新整理時訊息一直存在
$message = '';
if (isset($_SESSION['msg'])) {
    $message = $_SESSION['msg'];
    unset($_SESSION['msg']);
}

// ================================
// 後端功能處理（POST）
// ================================
if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    
    // 1. 新增課程主檔
    if ($_POST['action'] === 'add_course') {
        try {
            $stmt = $pdo->prepare(
                "INSERT INTO courses (course_code, title, credits, dept_id) VALUES (?, ?, ?, ?)"
            );
            $stmt->execute([
                $_POST['course_code'],
                $_POST['title'],
                $_POST['credits'],
                $_POST['dept_id']
            ]);
            $_SESSION['msg'] = '課程主檔新增成功';
        } catch (PDOException $e) {
            $_SESSION['msg'] = '新增失敗：課程代碼可能重複';
        }
        // 執行完畢後導向自己，防止 F5 重複提交
        header('Location: admin.php');
        exit;
    }

    // 2. 開課設定
    if ($_POST['action'] === 'add_section') {
        try {
            // 接收選擇的開始時間，並計算結束時間 (2小時)
            $start_time = $_POST['time_select'];
            $end_time = date('H:i', strtotime($start_time . ' +2 hours'));
            
            // 組合時間字串：例如 "週一 08:00-10:00"
            $final_time = "週" . $_POST['day_select'] . " " . $start_time . "-" . $end_time;

            // 存入 time_info 欄位，slot_id 設為 null
            $stmt = $pdo->prepare(
                "INSERT INTO course_sections (course_id, teacher_id, room_id, slot_id, time_info, semester, max_students) VALUES (?, ?, ?, null, ?, ?, ?)"
            );
            $stmt->execute([
                $_POST['course_id'],
                $_POST['teacher_id'],
                $_POST['room_id'],
                $final_time,
                $_POST['semester'],
                $_POST['max_students']
            ]);
            $_SESSION['msg'] = '開課成功';
        } catch (PDOException $e) {
            $_SESSION['msg'] = '開課失敗：' . $e->getMessage();
        }
        // 執行完畢後導向自己，防止 F5 重複提交
        header('Location: admin.php');
        exit;
    }
}

// ================================
// 資料讀取（供介面顯示）
// ================================
$courses     = $pdo->query("SELECT course_id, title FROM courses");
$teachers    = $pdo->query("SELECT teacher_id, name FROM teachers");
$classrooms  = $pdo->query("SELECT room_id, room_name FROM classrooms");
$departments = $pdo->query("SELECT dept_id, dept_name FROM departments");

// 開課列表查詢 (改用 time_info 欄位)
$sections = $pdo->query(
    "SELECT
        cs.section_id,
        c.title,
        t.name AS teacher,
        r.room_name,
        cs.time_info,
        cs.semester,
        cs.max_students,
        COUNT(e.enrollment_id) AS students
     FROM course_sections cs
     JOIN courses c ON cs.course_id = c.course_id
     LEFT JOIN teachers t ON cs.teacher_id = t.teacher_id
     LEFT JOIN classrooms r ON cs.room_id = r.room_id
     LEFT JOIN enrollments e ON cs.section_id = e.section_id
     GROUP BY cs.section_id"
);
?>

<!DOCTYPE html>
<html lang="zh-TW">
<head>
<meta charset="UTF-8">
<title>管理員後台 - 課程管理系統</title>
<style>
    body { font-family:"Microsoft JhengHei", sans-serif; background:#f4f4f4; padding:20px; }
    .container { max-width:1000px; margin:auto; background:white; padding:20px; border-radius: 8px; box-shadow: 0 2px 5px rgba(0,0,0,0.1); }
    .nav { display:flex; justify-content:space-between; margin-bottom:20px; align-items: center; }
    .box { background:#fafafa; padding:15px; margin-bottom:20px; border: 1px solid #eee; }
    table { width:100%; border-collapse:collapse; margin-top:10px; background: white; }
    th, td { border:1px solid #ccc; padding:10px; text-align: left; }
    th { background:#f0f0f0; }
    input, select { width:100%; padding:8px; margin-bottom:12px; box-sizing: border-box; }
    .btn { padding:8px 16px; color:white; border:none; cursor:pointer; border-radius: 4px; font-weight: bold; }
    .blue { background:#007bff; }
    .red { background:#dc3545; }
    .alert { background:#fff3cd; color: #856404; padding:12px; margin-bottom:20px; border: 1px solid #ffeeba; border-radius: 4px; }
    .time-flex { display:flex; gap:10px; margin-bottom:12px; }
    .time-flex select { width: auto; flex: 1; }
</style>
</head>
<body>

<div class="container">

    <div class="nav">
        <div>管理員：<strong><?= htmlspecialchars($_SESSION['name']) ?></strong></div>
        <a href="index.php?action=logout" class="btn red" onclick="return confirm('確定要登出嗎？')">登出</a>
    </div>

    <?php if ($message): ?>
        <div class="alert"><?= htmlspecialchars($message) ?></div>
    <?php endif; ?>

    <div class="box">
        <h3>1. 新增課程主檔</h3>
        <form method="POST">
            <input type="hidden" name="action" value="add_course">
            <label>課程代碼</label> <input name="course_code" placeholder="例如：CS101" required>
            <label>課程名稱</label> <input name="title" placeholder="例如：程式設計" required>
            <label>學分數</label> <input type="number" name="credits" min="1" max="10" value="3" required>
            <label>系所 ID (請參考下方列表)</label> <input type="number" name="dept_id" value="1" required>
            <button class="btn blue">新增課程資料</button>
        </form>
    </div>

    <div class="box">
        <h3>2. 開課設定 (每堂課 2 小時)</h3>
        <form method="POST">
            <input type="hidden" name="action" value="add_section">
            
            <label>選擇課程</label>
            <select name="course_id" required>
                <option value="">-- 請選擇課程 --</option>
                <?php while ($c = $courses->fetch()): ?>
                    <option value="<?= $c['course_id'] ?>"><?= htmlspecialchars($c['title']) ?></option>
                <?php endwhile; ?>
            </select>

            <label>授課教師</label>
            <select name="teacher_id" required>
                <?php while ($t = $teachers->fetch()): ?>
                    <option value="<?= $t['teacher_id'] ?>"><?= htmlspecialchars($t['name']) ?></option>
                <?php endwhile; ?>
            </select>

            <label>上課教室</label>
            <select name="room_id" required>
                <?php while ($r = $classrooms->fetch()): ?>
                    <option value="<?= $r['room_id'] ?>"><?= htmlspecialchars($r['room_name']) ?> </option>
                <?php endwhile; ?>
            </select>
            
            <label>時段設定</label>
            <div class="time-flex">
                <select name="day_select" required>
                    <option value="一">週一</option>
                    <option value="二">週二</option>
                    <option value="三">週三</option>
                    <option value="四">週四</option>
                    <option value="五">週五</option>
                </select>
                <select name="time_select" required>
                    <?php 
                    // 產生 08:00 到 16:00 的開課起點 (每兩小時一格)
                    for($i=8; $i<=16; $i+=2): 
                        $t = sprintf("%02d:00", $i); 
                        $end = sprintf("%02d:00", $i+2);
                    ?>
                        <option value="<?= $t ?>"><?= $t ?> - <?= $end ?></option>
                    <?php endfor; ?>
                </select>
            </div>

            <label>開課學期</label> <input name="semester" value="113-1" required>
            <label>選課人數上限</label> <input type="number" name="max_students" value="60" required>
            
            <button class="btn blue">確認正式開課</button>
        </form>
    </div>

    <div class="box">
        <h4>系所 ID 參考表</h4>
        <table style="font-size: 0.9em;">
            <tr><th>系所 ID</th><th>系所名稱</th></tr>
            <?php while ($d = $departments->fetch()): ?>
            <tr>
                <td><?= htmlspecialchars($d['dept_id']) ?></td>
                <td><?= htmlspecialchars($d['dept_name']) ?></td>
            </tr>
            <?php endwhile; ?>
        </table>
    </div>

    <h3 style="margin-top: 40px;">目前已開課程列表</h3>
    <table>
        <thead>
            <tr>
                <th>課程名稱</th>
                <th>教師</th>
                <th>教室</th>
                <th>上課時間 (2H)</th>
                <th>學期</th>
                <th>選課人數</th>
            </tr>
        </thead>
        <tbody>
            <?php while ($row = $sections->fetch()): ?>
            <tr>
                <td><strong><?= htmlspecialchars($row['title']) ?></strong></td>
                <td><?= htmlspecialchars($row['teacher']) ?></td>
                <td><?= htmlspecialchars($row['room_name']) ?></td>
                <td style="color: #007bff; font-weight: bold;"><?= htmlspecialchars($row['time_info']) ?></td>
                <td><?= htmlspecialchars($row['semester']) ?></td>
                <td><?= $row['students'] ?> / <?= $row['max_students'] ?></td>
            </tr>
            <?php endwhile; ?>
        </tbody>
    </table>

</div>

</body>
</html>