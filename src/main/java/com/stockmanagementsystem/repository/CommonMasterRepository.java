package com.stockmanagementsystem.repository;

import com.stockmanagementsystem.entity.CommonMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommonMasterRepository extends JpaRepository<CommonMaster,Integer> {


    List<CommonMaster> findByIsDeletedAndType(boolean b, String type);

    CommonMaster findByIsDeletedAndTypeAndId(boolean b, String zonec, Integer statusId);
}
