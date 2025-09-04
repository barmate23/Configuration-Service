package com.stockmanagementsystem.repository;

import com.stockmanagementsystem.entity.PpeStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PPEStatusRepository extends JpaRepository<PpeStatus,Integer> {


    PpeStatus findByIsDeletedAndSubOrganizationIdAndStatusName(boolean b, Integer subOrgId, String created);

    PpeStatus findByIsDeletedAndStatusName(boolean b, String created);
}
