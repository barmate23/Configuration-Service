package com.stockmanagementsystem.repository;

import com.stockmanagementsystem.entity.StockBalance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StockBalanceRepository extends JpaRepository<StockBalance,Integer> {
}
