package com.stockmanagementsystem.repository;

import com.stockmanagementsystem.entity.ASNLine;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AsnLineRepository extends JpaRepository<ASNLine,Integer> {
    ASNLine findByIsDeletedFalseAndId(int i);
}
