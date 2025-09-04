package com.stockmanagementsystem.repository;

import com.stockmanagementsystem.entity.ReasonCategoryMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReasonCategoryMasterRepository extends JpaRepository<ReasonCategoryMaster,Integer> {

    ReasonCategoryMaster findByIsDeletedAndId(boolean b, Integer reasonCategoryId);

    ReasonCategoryMaster findByIsDeletedAndReasonCategoryName(boolean b, String reasonCategory);

    List<ReasonCategoryMaster> findByIsDeleted(boolean b);
}
