package com.example.demo.db.repository;

import com.example.demo.db.entity.Log;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface LogRepository extends JpaRepository<Log, Long> {
    List<Log> findAllByOrderByTimestampDesc();

    List<Log> findByLogLevelOrderByTimestampDesc(String logLevel);

    List<Log> findByTimestampBetweenOrderByTimestampDesc(int start, int end);

    @Transactional
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("DELETE FROM Log")
    void deleteAllLogs();
}