package com.example.demo.controller;

import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.demo.DTO.requestDTO.RequestLoginDTO;
import com.example.demo.Jwt.JwtUtils;
import com.example.demo.db.entity.User;
import com.example.demo.db.repository.UserRepository;

@RestController
@RequestMapping("/login")
public class ControllerAuth {

    @Autowired
    private UserRepository userRepo;
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private PasswordEncoder encoder;

    private static final Logger log = LoggerFactory.getLogger(ControllerAuth.class);

    @PostMapping("/")
    public Map<String, String> login(@RequestBody RequestLoginDTO Data) {
        Map<String, String> loginData = Data.request();
        User user = userRepo.findById(loginData.get("username")).orElseThrow();

        if (encoder.matches(loginData.get("password"), user.getPassword())) {
            String token = jwtUtils.generateToken(user.getUsername());
            log.info("take token ");
            return Map.of("token", token);
        } else {
            log.error("wrong password");
            throw new RuntimeException("密碼錯誤");
        }
    }
}