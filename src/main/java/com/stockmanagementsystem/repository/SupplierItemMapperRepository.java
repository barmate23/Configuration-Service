package com.stockmanagementsystem.repository;

import com.stockmanagementsystem.entity.SupplierItemMapper;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SupplierItemMapperRepository extends JpaRepository<SupplierItemMapper,Integer> {
    SupplierItemMapper findByIsDeletedAndSupplier_Id(boolean isDeleted, Integer supplierId);

    List<SupplierItemMapper> findAllByIsDeletedAndSupplier_Id(boolean b, Integer id);

    List<SupplierItemMapper> findByIsDeletedAndSubOrganizationIdAndSupplierId(boolean b, Integer subOrgId, Integer id);

    SupplierItemMapper findByIsDeletedAndSubOrganizationIdAndItemId(boolean b, Integer subOrgId, Integer id);

    SupplierItemMapper findByIsDeletedAndSubOrganizationIdAndItemIdAndSupplierId(boolean b, Integer subOrgId, Integer itemId, Integer supplierId);

    SupplierItemMapper findByIsDeletedAndSupplier_IdAndSubOrganizationId(boolean b, Integer id, Integer subOrgId);

    List<SupplierItemMapper> findAllByIsDeletedAndSupplier_IdAndSubOrganizationId(boolean b, Integer id, Integer subOrgId);

    SupplierItemMapper findByIsDeletedAndSubOrganizationIdAndSupplierItemId(boolean b, Integer subOrgId, Integer id);

    Optional<SupplierItemMapper> findByIsDeletedAndSubOrganizationIdAndItemItemIdAndSupplierSupplierId(boolean b, Integer subOrgId, String itemId, String supplierId);
}
