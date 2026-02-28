package com.example.demo.db.repository;

import com.example.demo.db.entity.Log;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LogRepository extends JpaRepository<Log, Long> {
    List<Log> findAllByOrderByTimestampDesc();

    List<Log> findByLogLevelOrderByTimestampDesc(String logLevel);

    List<Log> findByTimestampBetweenOrderByTimestampDesc(int start, int end);
}