package com.stockmanagementsystem.repository;

import com.stockmanagementsystem.entity.SerialBatchNumber;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface SerialBatchNumberRepository extends JpaRepository<SerialBatchNumber,Integer> {
    List<SerialBatchNumber> findByIsDeletedFalseAndAsnLineId(Integer id);

    List<SerialBatchNumber> findByIsDeletedFalseAndAsnLineIdOrderByAcceptedRejectedContainerBarcodePackingSlipNumberDesc(Integer requestId);
}
