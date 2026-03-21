package com.example.demo.service;

import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.DTO.responseDTO.ResponseTodoDTO;
import com.example.demo.db.entity.Todo;
import com.example.demo.db.repository.TodoRepository;

import jakarta.transaction.Transactional;

@Service

public class ServiceTodo {
    @Autowired
    private TodoRepository todoRepository;

    private static final Logger log = LoggerFactory.getLogger(ServiceTodo.class);

    public void record(String message) {
        log.info("input todo to db show as follow " + message);
        Todo todo = new Todo();
        todo.setMessage(message);
        todoRepository.save(todo);
    }

    public void justRecord(String newMessage) {
        log.info("refresh new todo : {}", newMessage);
        todoRepository.deleteAll();
        Todo todo = new Todo();
        todo.setMessage(newMessage);
        todoRepository.save(todo);
    }

    public List<ResponseTodoDTO> getMessage() {
        return todoRepository.findAll().stream()
                .map(entity -> new ResponseTodoDTO(entity.getMessage())).collect(Collectors.toList());
    }

    @Transactional
    public List<ResponseTodoDTO> refreshTodo(String newMessage) {
        log.info("refresh new todo : {}", newMessage);
        todoRepository.deleteAll();
        Todo todo = new Todo();
        todo.setMessage(newMessage);
        todoRepository.save(todo);
        return todoRepository.findAll().stream()
                .map(entity -> new ResponseTodoDTO(entity.getMessage())).collect(Collectors.toList());
    }
}
