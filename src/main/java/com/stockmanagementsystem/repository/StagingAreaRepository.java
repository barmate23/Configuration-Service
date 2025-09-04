package com.stockmanagementsystem.repository;

import com.stockmanagementsystem.entity.StagingArea;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StagingAreaRepository extends JpaRepository<StagingArea,Integer> {
}
