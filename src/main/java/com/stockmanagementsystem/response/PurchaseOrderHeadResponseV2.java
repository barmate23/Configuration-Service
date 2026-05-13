package com.stockmanagementsystem.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseOrderHeadResponseV2 {
    private Integer id;
    private String purchaseOrderId;
    private String purchaseOrderNumber;
    private Date purchaseOrderDate;
    private Integer supplierId;
    private String supplierName;
    private String supplierCode;
    private String deliveryType;
    private Double totalAmount;
    private Date deliverByDate;
    private Date startDate;
    private Date endDate;
    private String mobileNumber;
    private Integer statusId;
    private String statusName;
}
