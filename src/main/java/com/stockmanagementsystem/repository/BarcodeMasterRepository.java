package com.stockmanagementsystem.repository;

import com.stockmanagementsystem.entity.BarcodeMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BarcodeMasterRepository extends JpaRepository<BarcodeMaster,Integer> {
    BarcodeMaster findByIsDeletedAndLabelFor(boolean b, String type);
}
