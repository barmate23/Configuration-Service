package com.stockmanagementsystem.repository;

import com.stockmanagementsystem.entity.PrintQueueDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PrintQueueDetailsRepository extends JpaRepository<PrintQueueDetail,Integer> {
}
