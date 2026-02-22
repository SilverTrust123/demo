package com.example.demo.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.db.entity.AirParticulatesId;
import com.example.demo.db.entity.*;

public interface AirParticulatesRepository extends JpaRepository<AirParticulates, AirParticulatesId> {

}
