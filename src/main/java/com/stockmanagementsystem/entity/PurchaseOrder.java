package com.stockmanagementsystem.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseOrder {
    private String purchaseOrderNumber;

    private Date purchaseOrderDate;

    private Integer lineNumber;

    private String itemCode;

    private String itemName;

    private String uom;

    private Float unitPrice;

    private Integer purchaseOrderQuantity;

    private Float subTotal;

    private Float stateGstPercent;

    private Float stateGstAmount;

    private Float centralGstPercent;

    private Float centralGstAmount;

    private Float interStateGstPercent;

    private Float interStateGstAmount;

    private Float totalAmount;

    private Date deliverByDate;

    private String supplierId;

    private String supplierName;

}
