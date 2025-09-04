package com.stockmanagementsystem.repository;

import com.stockmanagementsystem.entity.PPEHead;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;
@Repository
public interface PPEHeadRepository extends JpaRepository<PPEHead,Integer> {

    Optional<PPEHead> findByIsDeletedAndSubOrganizationIdAndPpeId(boolean b,Integer subOrgId, String planId);

    List<PPEHead> findByIsDeletedAndSubOrganizationIdOrderByIdAsc(boolean b, Integer subOrgId);

    Optional<PPEHead> findByIsDeletedAndSubOrganizationIdAndPlanOrderId(boolean b, Integer subOrgId, String planId);

    Optional<PPEHead> findByIsDeletedAndStartDateAndStartTimeAndProductionShop(boolean b, Date startDate, Date startTimeDate, String productionShop);
}
