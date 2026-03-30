package com.stockmanagementsystem.repository;

import com.stockmanagementsystem.entity.ProductionLine;
import com.stockmanagementsystem.entity.ProductionLineStage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductionLineStageRepository extends JpaRepository<ProductionLineStage, Integer> {
    List<ProductionLineStage> findByIsDeletedFalse();
    List<ProductionLineStage> findByProductionLineAndIsDeletedFalseOrderBySequenceNumberAsc(ProductionLine productionLine);
}
