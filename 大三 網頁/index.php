<?php
require 'config.php';
// ================================
// 登出處理
// ================================
if (isset($_GET['action']) && $_GET['action'] === 'logout') {
    session_destroy();
    header('Location: index.php');
    exit;
}

// ================================
// index.php（系統入口＋登入）
// ================================

// 若已登入，直接導向 dashboard
if (isset($_SESSION['role'])) {
    header('Location: dashboard.php');
    exit;
}

$message = '';

// ===== 登入處理 =====
if ($_SERVER['REQUEST_METHOD'] === 'POST') {

    $username = $_POST['username'];
    $password = $_POST['password'];
    $role     = $_POST['role'];

    // =====================
    // 學生登入
    // =====================
    if ($role === 'student') {

        $stmt = $pdo->prepare(
            "SELECT student_id, name, password FROM students WHERE student_id = ?"
        );
        $stmt->execute([$username]);
        $user = $stmt->fetch();

        if ($user && $user['password'] === $password) {
            $_SESSION['user_id'] = $user['student_id'];
            $_SESSION['name']    = $user['name'];
            $_SESSION['role']    = 'student';

            header('Location: dashboard.php');
            exit;
        } else {
            $message = '學生帳號或密碼錯誤';
        }

    // =====================
    // 管理員登入
    // =====================
    } elseif ($role === 'admin') {

        $stmt = $pdo->prepare(
            "SELECT user_id, username, password FROM users WHERE username = ?"
        );
        $stmt->execute([$username]);
        $user = $stmt->fetch();

        if ($user && $user['password'] === $password) {
            $_SESSION['user_id'] = $user['user_id'];
            $_SESSION['name']    = $user['username'];
            $_SESSION['role']    = 'admin';

            header('Location: dashboard.php');
            exit;
        } else {
            $message = '管理員帳號或密碼錯誤';
        }
    }
}
?>

<!DOCTYPE html>
<html lang="zh-TW">
<head>
<meta charset="UTF-8">
<title>選課系統登入</title>
<style>
body { font-family:"Microsoft JhengHei"; background:#f4f4f4; }
.login-box {
    width:380px; margin:80px auto; background:white;
    padding:30px; border-radius:6px;
}
input, select, button {
    width:100%; padding:8px; margin-bottom:15px;
}
button { background:#007bff; color:white; border:none; cursor:pointer; }
.alert { background:#fff3cd; padding:10px; margin-bottom:15px; }
</style>
</head>
<body>

<div class="login-box">
    <h2 style="text-align:center;">系統登入</h2>

    <?php if ($message): ?>
        <div class="alert"><?= $message ?></div>
    <?php endif; ?>

    <form method="POST">
        帳號
        <input type="text" name="username" required>

        密碼
        <input type="password" name="password" required>

        身分
        <select name="role">
            <option value="student">學生</option>
            <option value="admin">管理員</option>
        </select>

        <button type="submit">登入</button>
    </form>

</div>

</body>
</html>