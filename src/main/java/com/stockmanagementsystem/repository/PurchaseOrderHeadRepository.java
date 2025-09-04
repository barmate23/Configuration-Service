package com.stockmanagementsystem.repository;

import com.stockmanagementsystem.entity.PurchaseOrderHead;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface PurchaseOrderHeadRepository extends JpaRepository<PurchaseOrderHead,Integer> {



    List<PurchaseOrderHead> findByIsDeletedAndPurchaseOrderNumberAndOrganizationId(boolean b, String purchaseOrderNumber,Integer orgId);

    Optional<PurchaseOrderHead> findByIsDeletedAndPurchaseOrderNumber(boolean b, String purchaseOrderNumber);

    Page<PurchaseOrderHead> findByIsDeleted(boolean b, Pageable pageable);
    List<PurchaseOrderHead> findByIsDeleted(boolean b);

    PurchaseOrderHead findByIsDeletedAndId(boolean b, Integer poId);

    Page<PurchaseOrderHead> findByIsDeletedAndPurchaseOrderNumberIn(boolean b, List<String> orderNumber, Pageable pageable);

    Page<PurchaseOrderHead> findByIsDeletedAndSupplierIdIn(boolean b, List<Integer> supplier, Pageable pageable);

    List<PurchaseOrderHead> findByIsDeletedAndOrganizationId(boolean b, Integer subOrgId);

    List<PurchaseOrderHead> findByIsDeletedAndSubOrganizationId(boolean b, Integer subOrgId);
    Page<PurchaseOrderHead> findByIsDeletedAndSubOrganizationId(boolean b, Integer subOrgId,Pageable pageable);

    Page<PurchaseOrderHead> findByIsDeletedAndDeliveryType(boolean b, Date deliveryDate, Pageable pageable);

    Page<PurchaseOrderHead> findByIsDeletedAndSubOrganizationIdAndPurchaseOrderNumberIn(boolean b, Integer subOrgId, List<String> orderNumber, Pageable pageable);

    Page<PurchaseOrderHead> findByIsDeletedAndSubOrganizationIdAndSupplierIdIn(boolean b, Integer subOrgId, List<Integer> supplier, Pageable pageable);

    PurchaseOrderHead findByIsDeletedAndSubOrganizationIdAndId(boolean b, Integer subOrgId, Integer id);

    Optional<PurchaseOrderHead> findByIsDeletedAndSubOrganizationIdAndPurchaseOrderNumber(boolean b, Integer subOrgId, String purchaseOrderNumber);

    List<PurchaseOrderHead> findByIsDeletedAndSubOrganizationIdAndSupplierId(boolean b, Integer subOrgId, String supplierId);
}
