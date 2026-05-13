package com.stockmanagementsystem.repository;

import com.stockmanagementsystem.entity.PackingProfileLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PackingProfileLevelRepository extends JpaRepository<PackingProfileLevel, Long> {
    List<PackingProfileLevel> findBySupplierItemMapper_SupplierIdAndSupplierItemMapper_ItemIdAndIsDeletedFalse(Integer supplierId, Integer itemId);
}
