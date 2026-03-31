package com.stockmanagementsystem.repository;

import com.stockmanagementsystem.entity.Item;
import com.stockmanagementsystem.entity.ItemSupplierPackingProfileMap;
import com.stockmanagementsystem.entity.Supplier;
import com.stockmanagementsystem.response.PackingProfileListProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ItemSupplierPackingProfileMapRepository extends JpaRepository<ItemSupplierPackingProfileMap,Integer> {
    Optional<ItemSupplierPackingProfileMap> findByOrganizationIdAndSubOrganizationIdAndItemAndSupplierAndIsDeleted(Integer orgId, Integer subOrgId, Item item, Supplier supplier, boolean b);

    @Query(
            "SELECT " +
                    "   m.id AS id, " +
                    "   p.id AS packingProfileId, " +
                    "   i.name AS itemName, " +
                    "   i.itemCode AS itemCode, " +
                    "   s.supplierName AS supplierName, " +
                    "   s.erpSupplierId AS erpSupplierId, " +
                    "   h.levelCode AS packingHierarchyLevelCode, " +
                    "   p.isActive AS isActive, " +
                    "   p.modifiedOn AS modifiedOn " +
                    "FROM ItemSupplierPackingProfileMap m " +
                    "JOIN m.packingProfile p " +
                    "JOIN p.packingHierarchyLevel h " +
                    "JOIN m.item i " +
                    "JOIN m.supplier s " +
                    "WHERE m.organizationId = :orgId " +
                    "AND m.subOrganizationId = :subOrgId "
    )
    Page<PackingProfileListProjection> findAllPackingProfiles(
            @Param("orgId") Integer orgId,
            @Param("subOrgId") Integer subOrgId,
            Pageable pageable
    );



    @Query(
            "SELECT m " +
                    "FROM ItemSupplierPackingProfileMap m " +
                    "JOIN FETCH m.packingProfile p " +
                    "JOIN FETCH m.item i " +
                    "JOIN FETCH m.supplier s " +
                    "WHERE m.id = :configId " +
                    "AND p.organizationId = :orgId " +
                    "AND p.subOrganizationId = :subOrgId " +
                    "AND p.isDeleted = false"
    )
    Optional<ItemSupplierPackingProfileMap> findPackingProfileById(
            @Param("configId") Long configId,
            @Param("orgId") Integer orgId,
            @Param("subOrgId") Integer subOrgId
    );

}

