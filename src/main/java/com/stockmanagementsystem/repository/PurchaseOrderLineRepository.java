package com.stockmanagementsystem.repository;

import com.stockmanagementsystem.entity.PurchaseOrderLine;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PurchaseOrderLineRepository extends JpaRepository<PurchaseOrderLine,Integer> {


    List<PurchaseOrderLine> findByIsDeletedAndPurchaseOrderHeadId(boolean b, Integer poId);

    PurchaseOrderLine findByIsDeletedAndId(boolean b, Integer id);

    PurchaseOrderLine findByIsDeletedAndSubOrganizationIdAndId(boolean b, Integer subOrgId, Integer id);

    List<PurchaseOrderLine> findByIsDeletedAndSubOrganizationIdAndPurchaseOrderHeadId(boolean b, Integer subOrgId, Integer poId);
}
