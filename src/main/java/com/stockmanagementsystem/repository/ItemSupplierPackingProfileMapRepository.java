package com.stockmanagementsystem.repository;

import com.stockmanagementsystem.entity.Item;
import com.stockmanagementsystem.entity.ItemSupplierPackingProfileMap;
import com.stockmanagementsystem.entity.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ItemSupplierPackingProfileMapRepository extends JpaRepository<ItemSupplierPackingProfileMap,Integer> {
    Optional<ItemSupplierPackingProfileMap> findByOrganizationIdAndSubOrganizationIdAndItemAndSupplierAndIsDeleted(Integer orgId, Integer subOrgId, Item item, Supplier supplier, boolean b);
}
