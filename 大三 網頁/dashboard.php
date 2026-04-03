<?php
require 'config.php';

// ================================
// dashboard.php（純導向，不含業務邏輯）
// ================================

// 尚未登入 → 回登入頁
if (!isset($_SESSION['role'])) {
    header('Location: index.php');
    exit;
}

// 依角色導向對應頁面
switch ($_SESSION['role']) {
    case 'student':
        header('Location: student.php');
        exit;

    case 'admin':
        header('Location: admin.php');
        exit;

    default:
        // 預留其他角色（如 staff / teacher）
        echo '未知角色，請聯絡系統管理員';
        exit;
}
