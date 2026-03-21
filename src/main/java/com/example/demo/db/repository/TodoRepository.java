package com.example.demo.db.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.db.entity.Todo;

public interface TodoRepository extends JpaRepository<Todo, String> {
    Optional<Todo> findByMessage(String message);
}