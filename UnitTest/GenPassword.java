package com.example.demo;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class GenPassword {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        // 假設你的密碼是 "myPassword123"
        String result = encoder.encode("myPassword123");
        System.out.println(result);
        // 把輸出的這一串 $2a$10... 貼回資料庫的 password 欄位
    }
}