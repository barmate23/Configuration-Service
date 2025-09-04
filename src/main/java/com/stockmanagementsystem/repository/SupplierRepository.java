package com.stockmanagementsystem.repository;

import com.stockmanagementsystem.entity.Supplier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier,Integer> {



    Supplier findByIsDeletedAndSupplierIdAndOrganizationId(boolean b, String supplierId,Integer orgId);



    Supplier findByIsDeletedAndSupplierNameAndOrganizationId(boolean b, String supplierName,Integer orgId);


    Optional<Supplier> findByIsDeletedAndSupplierId(boolean b, String supplierId);

    Supplier findByOrganizationIdAndSubOrganizationIdAndIsDeletedAndId(Integer orgId, Integer subOrgId, boolean b, Integer id);

    Page<Supplier> findAll(Specification<Supplier> specification, Pageable pageable);


    List<Supplier> findByIsDeleted(boolean b);


    Supplier findBySupplierPANNumberAndIsDeleted(String supplierPANNumber, boolean b);

    Supplier findBySupplierGSTRegistrationNumberAndIsDeleted(String supplierGSTRegistrationNumber, boolean b);

    boolean existsByIsDeletedAndSupplierId(boolean b, String generatedDockId);

    Supplier findBySupplierGSTRegistrationNumberAndIdNotAndIsDeleted(String supplierGSTRegistrationNumber, Integer id, boolean b);

    Supplier findBySupplierPANNumberAndIdNotAndIsDeleted(String supplierPANNumber, Integer id, boolean b);

    Optional<Supplier> findByIsDeletedAndId(boolean b, Integer supplierId);

    List<Supplier> findByIsDeletedAndSubOrganizationId(boolean b, Integer subOrgId);

    Supplier findByIsDeletedAndSubOrganizationIdAndId(boolean b, Integer subOrgId, Integer id);

    Optional<Supplier> findByIsDeletedAndSubOrganizationIdAndSupplierId(boolean b, Integer subOrgId, String supplierId);

    Supplier findByIsDeletedAndSupplierIdAndSubOrganizationId(boolean b, String supplierId, Integer subOrgId);

    Supplier findByIsDeletedAndSupplierNameAndSubOrganizationId(boolean b, String supplierName, Integer orgId);

    Supplier findBySupplierGSTRegistrationNumberAndIdNotAndIsDeletedAndSubOrganizationId(String supplierGSTRegistrationNumber, Integer id, boolean b, Integer subOrgId);

    Supplier findBySupplierPANNumberAndIdNotAndIsDeletedAndSubOrganizationId(String supplierPANNumber, Integer id, boolean b, Integer subOrgId);

    boolean existsByIsDeletedAndSubOrganizationIdAndSupplierId(boolean b, Integer subOrgId, String generatedDockId);

    List<Supplier> findBySubOrganizationIdOrderByIdAsc(Integer subOrgId);

    Optional<Supplier> findByIsDeletedAndSubOrganizationIdAndErpSupplierId(boolean b, Integer subOrgId, String supplierId);

    Optional<Supplier> findByIsDeletedAndSubOrganizationIdIsAndErpSupplierId(boolean b, Integer subOrgId, String erpSupplierId);
}
