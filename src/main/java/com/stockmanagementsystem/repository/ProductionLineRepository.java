package com.stockmanagementsystem.repository;

import com.stockmanagementsystem.entity.ProductionLine;
import com.stockmanagementsystem.entity.ProductionShop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductionLineRepository extends JpaRepository<ProductionLine, Integer> {
    List<ProductionLine> findByIsDeletedFalse();
    List<ProductionLine> findByProductionShopAndIsDeletedFalseOrderBySequenceNumberAsc(ProductionShop productionShop);
}
