package com.stockmanagementsystem.repository;

import com.stockmanagementsystem.entity.LocationIdGeneratorMapper;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocationIdGeneratorRepository extends JpaRepository<LocationIdGeneratorMapper,Integer> {
}
