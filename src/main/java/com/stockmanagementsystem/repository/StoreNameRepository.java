package com.stockmanagementsystem.repository;

import com.stockmanagementsystem.entity.StoreName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StoreNameRepository extends JpaRepository<StoreName,Integer> {
    List<StoreName> findByIsDeletedAndIsUsedAndSubOrganizationId(boolean b, boolean b1, Integer subOrgId);

    StoreName findByIsDeletedAndIsUsedAndSubOrganizationIdAndStoreName(boolean b, boolean b1, Integer subOrgId, String storeName);

    StoreName findByIsDeletedAndSubOrganizationIdAndStoreName(boolean b, Integer subOrgId, String storeName);
}
