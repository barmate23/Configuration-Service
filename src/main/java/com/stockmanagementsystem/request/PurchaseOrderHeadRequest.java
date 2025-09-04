package com.stockmanagementsystem.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseOrderHeadRequest {
    private String purchaseOrderNumber;
    private Date purchaseOrderDate;
    private Integer supplierId;
    private Integer itemId;
    private String deliveryType;
    private Date deliverByDate;
    private String mobileNumber;
    private List<PurchaseOrderLineRequest> purchaseOrderLineRequests;

}
