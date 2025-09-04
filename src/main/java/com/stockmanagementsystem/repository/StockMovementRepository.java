package com.stockmanagementsystem.repository;

import com.stockmanagementsystem.entity.StockMovement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StockMovementRepository extends JpaRepository<StockMovement, Integer> {

    List<StockMovement> findByIsDeletedAndSubOrganizationIdAndIsAcceptedAndAcceptedRejectedContainerBarcodeAcceptedRejectedContainerId(boolean isDeleted, Integer subOrgId, boolean isAccepted, Integer acceptedRejectedContainerId);

    List<StockMovement> findByIsDeletedAndSubOrganizationIdAndIsAcceptedAndItemIdOrderByIdDesc(boolean b, Integer subOrgId, boolean b1, Integer itemId);

    StockMovement findByIsDeletedAndSubOrganizationIdAndId(boolean b, Integer subOrgId, Integer stockMovementId);

    List<StockMovement> findBySubOrganizationIdAndIsDeletedAndPpeLineId(Integer subOrgId, boolean b, Integer ppeLineId);

    List<StockMovement> findByIsDeletedAndSubOrganizationIdAndCrrGrrBarcodeIn(boolean b, Integer subOrgId, List<String> grrNumber);

    List<StockMovement> findByIsDeletedAndSubOrganizationIdAndTransactionStatusStatus(boolean b, Integer subOrgId, String stagingput);
    List<StockMovement> findByIsDeletedAndSubOrganizationIdAndItemIdOrderByIdDesc(boolean b, Integer subOrgId , Integer id);
    List<StockMovement> findByIsDeletedAndSubOrganizationIdAndTransactionStatusStatusAndAcceptedRejectedContainerStoreOperatorId(boolean b, Integer subOrgId, String stagingput, Integer userId);

    List<StockMovement> findByIsDeletedAndIsAcceptedAndSubOrganizationIdAndAcceptedRejectedContainerAsnLineId(boolean b, Boolean isAccepted, Integer subOrgId, Integer asnLineId);

    List<StockMovement> findByIsDeletedAndIsAcceptedAndSubOrganizationIdAndAcceptedRejectedContainerPurchaseOrderLineId(boolean b, Boolean isAccepted, Integer subOrgId, Integer poLineId);
}

