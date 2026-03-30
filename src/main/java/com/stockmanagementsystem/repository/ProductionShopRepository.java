package com.stockmanagementsystem.repository;

import com.stockmanagementsystem.entity.ProductionShop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductionShopRepository extends JpaRepository<ProductionShop, Integer> {
    List<ProductionShop> findByIsDeletedFalse();
}
