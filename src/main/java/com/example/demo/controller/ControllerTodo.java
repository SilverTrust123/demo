package com.example.demo.controller;

import java.util.concurrent.CompletableFuture;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.priorityQueueTask.QueueService;
import com.example.demo.DTO.requestDTO.RequestTodoDTO;
import org.slf4j.Logger;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/todo")
public class ControllerTodo {
    private static final Logger log = LoggerFactory.getLogger(ControllerTodo.class);
    @Value("${important}")
    private int IMPORTANT;
    @Value("${normal}")
    private int NORMAL;
    @Value("${urgent}")
    private int URGENT;
    @Autowired
    private QueueService queueService;

    @PostMapping("/")
    public CompletableFuture<Object> leaveMessage(@RequestBody RequestTodoDTO message) {
        log.info("Receive and transfer request for leave Message");
        return queueService.addRequestToQueue(NORMAL, message, "leaveMessage");
    }

    @GetMapping("/getMessage")
    public CompletableFuture<Object> getMessage() {
        log.info("Receive and transfer request for get Message");
        return queueService.addRequestToQueue(NORMAL, null, "getMessage");
    }

}
