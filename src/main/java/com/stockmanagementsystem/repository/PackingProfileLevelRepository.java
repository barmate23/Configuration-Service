package com.stockmanagementsystem.repository;

import com.stockmanagementsystem.entity.PackingProfileLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PackingProfileLevelRepository extends JpaRepository<PackingProfileLevel, Long> {
}
