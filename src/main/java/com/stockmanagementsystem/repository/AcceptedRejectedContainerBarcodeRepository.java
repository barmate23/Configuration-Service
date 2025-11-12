package com.stockmanagementsystem.repository;


import com.stockmanagementsystem.entity.AcceptedRejectedContainerBarcode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AcceptedRejectedContainerBarcodeRepository extends JpaRepository<AcceptedRejectedContainerBarcode,Integer> {

    List<AcceptedRejectedContainerBarcode> findByIsDeletedAndIsAcceptedAndSubOrganizationIdAndAcceptedRejectedContainerAsnLineId(boolean b, Boolean isAccepted, Integer subOrgId, Integer asnLineId);

    List<AcceptedRejectedContainerBarcode> findByIsDeletedAndIsAcceptedAndSubOrganizationIdAndAcceptedRejectedContainerPurchaseOrderLineId(boolean b, Boolean isAccepted, Integer subOrgId, Integer poLineId);

    boolean existsByPackingSlipNumber(String nextSlip);
}
