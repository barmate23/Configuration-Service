package com.stockmanagementsystem.repository;

import com.stockmanagementsystem.entity.StoreKeeperMapper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StoreKeeperMapperRepository extends JpaRepository<StoreKeeperMapper,Integer> {
    List<StoreKeeperMapper> findByIsDeletedAndSubOrganizationId(boolean b, Integer subOrgId);
}
