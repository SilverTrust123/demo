package com.example.demo.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.db.entity.User;

public interface UserRepository extends JpaRepository<User, String> {
}