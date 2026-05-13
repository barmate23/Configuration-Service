package com.stockmanagementsystem.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseOrderLineResponseV2 {
    private Integer id;
    private String purchaseOrderLineId;
    private Integer purchaseOrderHeadId;
    private String purchaseOrderNumber;
    private Integer lineNumber;
    private Integer itemId;
    private String itemCode;
    private String itemName;
    private String uom;
    private String currency;
    private Float unitPrice;
    private Integer numberOfContainer;
    private Float purchaseOrderQuantity;
    private Float subTotalRs;
    private Float stateGSTPercentage;
    private Float stateGSTAmount;
    private Float centralGSTPercentage;
    private Float centralGSTAmount;
    private Float interStateGSTPercentage;
    private Float interStateGSTAmount;
    private Float totalAmountRs;
    private Integer leadTime;
    private Boolean isDay;
    private Integer statusId;
    private String statusName;
    private Float remainingQuantity;
    private Float remainingAmount;
}
