package com.stockmanagementsystem.repository;

import com.stockmanagementsystem.entity.PPELine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PPELineRepository extends JpaRepository<PPELine,Integer> {
    List<PPELine> findByIsDeletedAndSubOrganizationIdAndItemIdAndPPEHeadPpeId(boolean b, Integer subOrgId, String itemId, String planId);

    List<PPELine> findByIsDeletedAndSubOrganizationIdAndItemItemIdAndPPEHeadPlanOrderId(boolean b, Integer subOrgId, String itemId, String planId);
}
