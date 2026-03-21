package com.example.demo.SingleTest;

import java.net.Socket;

public class TestTCP {
    public static void main(String[] args) {
        try (Socket s = new Socket("192.168.3.20", 502)) {
            System.out.println("connect success");
        } catch (Exception e) {
            System.out.println("connect failed: " + e.getMessage());
        }
    }
}
