<?php
require 'config.php';

// 檢查是否提供了課程 ID
if (!isset($_GET['id'])) {
    die("未指定的課程 ID");
}

$course_id = $_GET['id'];

// 取得課程詳細資訊與系所名稱
$stmt = $pdo->prepare("
    SELECT c.*, d.dept_name 
    FROM courses c 
    JOIN departments d ON c.dept_id = d.dept_id 
    WHERE c.course_id = ?
");
$stmt->execute([$course_id]);
$course = $stmt->fetch();

if (!$course) {
    die("找不到該課程資訊");
}

// 統計該課程目前在所有開課時段中的總修課人數
$countStmt = $pdo->prepare("
    SELECT COUNT(*) as total_students 
    FROM enrollments e 
    JOIN course_sections cs ON e.section_id = cs.section_id 
    WHERE cs.course_id = ?
");
$countStmt->execute([$course_id]);
$countData = $countStmt->fetch();
?>

<!DOCTYPE html>
<html lang="zh-TW">
<head>
    <meta charset="UTF-8">
    <title>課程詳情 - <?= htmlspecialchars($course['title']) ?></title>
    <style>
        body { font-family: "Microsoft JhengHei"; background: #f4f4f4; padding: 40px; }
        .card { 
            max-width: 600px; margin: auto; background: white; 
            padding: 30px; border-radius: 12px; 
            box-shadow: 0 4px 15px rgba(0,0,0,0.1); 
        }
        h2 { color: #007bff; border-bottom: 2px solid #007bff; padding-bottom: 10px; }
        .info-row { margin: 15px 0; font-size: 1.1em; line-height: 1.6; }
        .label { font-weight: bold; color: #555; width: 100px; display: inline-block; }
        .btn-back { 
            display: inline-block; margin-top: 20px; padding: 8px 16px; 
            background: #6c757d; color: white; text-decoration: none; border-radius: 4px; 
        }
        .btn-back:hover { background: #5a6268; }
    </style>
</head>
<body>

<div class="card">
    <h2>課程詳細內容</h2>
    
    <div class="info-row">
        <span class="label">課程名稱：</span>
        <strong><?= htmlspecialchars($course['title']) ?></strong>
    </div>

    <div class="info-row">
        <span class="label">課程代碼：</span>
        <?= htmlspecialchars($course['course_code']) ?>
    </div>

    <div class="info-row">
        <span class="label">學分數：</span>
        <?= htmlspecialchars($course['credits']) ?> 學分
    </div>

    <div class="info-row">
        <span class="label">所屬系所：</span>
        <?= htmlspecialchars($course['dept_name']) ?>
    </div>

    <div class="info-row">
        <span class="label">修課人數：</span>
        目前共有 <?= $countData['total_students'] ?> 位學生選修
    </div>

    <hr>
    
    <div class="info-row" style="color: #666; font-size: 0.9em;">
        <p>課程簡介：<br>這是一門關於 <?= htmlspecialchars($course['title']) ?> 的專業課程，旨在培養學生在 <?= htmlspecialchars($course['dept_name']) ?> 領域的實務與理論基礎。</p>
    </div>

    <a href="javascript:window.close();" class="btn-back">關閉視窗</a>
</div>

</body>
</html>