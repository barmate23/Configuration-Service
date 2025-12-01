package com.stockmanagementsystem.repository;


import com.stockmanagementsystem.entity.AcceptedRejectedContainerBarcode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AcceptedRejectedContainerBarcodeRepository extends JpaRepository<AcceptedRejectedContainerBarcode,Integer> {

    List<AcceptedRejectedContainerBarcode> findByIsDeletedAndIsAcceptedAndSubOrganizationIdAndAcceptedRejectedContainerAsnLineId(boolean b, Boolean isAccepted, Integer subOrgId, Integer asnLineId);

    List<AcceptedRejectedContainerBarcode> findByIsDeletedAndIsAcceptedAndSubOrganizationIdAndAcceptedRejectedContainerPurchaseOrderLineId(boolean b, Boolean isAccepted, Integer subOrgId, Integer poLineId);

    boolean existsByPackingSlipNumber(String nextSlip);

    @Query("SELECT MAX(CAST(SUBSTRING(a.packingSlipNumber, LENGTH(:prefix) + 1) AS int)) " +
            "FROM AcceptedRejectedContainerBarcode a " +
            "WHERE a.packingSlipNumber LIKE CONCAT(:prefix, '%')")
    Integer findMaxSequenceForPrefix(@Param("prefix") String prefix);
}
