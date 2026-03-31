package com.stockmanagementsystem.repository;

import com.stockmanagementsystem.entity.SerialBatchNumber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface SerialBatchNumberRepository extends JpaRepository<SerialBatchNumber,Integer> {
    List<SerialBatchNumber> findByIsDeletedFalseAndAsnLineId(Integer id);

    List<SerialBatchNumber> findByIsDeletedFalseAndAsnLineIdOrderByAcceptedRejectedContainerBarcodePackingSlipNumberDesc(Integer requestId);

    @Query("select s.serialBatchNumber " +
            "from SerialBatchNumber s " +
            "join s.asnLine l " +
            "join l.itemScheduleSupplier ism " +
            "where s.isDeleted = false " +
            "  and l.item.id = :itemId " +
            "  and ism.supplier.id = :supplierId " +
            "  and s.serialBatchNumber in :serials")
    List<String> findExistingSerialsForItemSupplierAndSerials(@Param("itemId") Integer itemId,
                                                              @Param("supplierId") Integer supplierId,
                                                              @Param("serials") List<String> serials);




}
