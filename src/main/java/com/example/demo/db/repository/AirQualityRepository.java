package com.example.demo.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.db.entity.AirQuality;
import com.example.demo.db.entity.AirQualityId;

public interface AirQualityRepository extends JpaRepository<AirQuality, AirQualityId> {

}
