package com.stockmanagementsystem.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseOrderLineRequest {

    private Integer lineNumber;
    private Integer id;

    private Integer itemId;

    private String uom;


    private Integer leadTime;

    private Boolean isDay;

    private Float unitPrice;
    private Float purchaseOrderQuantity;

    private Float subTotal;

    private Float stateGstPercent;

    private Float stateGstAmount;

    private Float centralGstPercent;

    private Float centralGstAmount;

    private Float interStateGstPercent;

    private Float interStateGstAmount;

    private Float totalAmount;


}
