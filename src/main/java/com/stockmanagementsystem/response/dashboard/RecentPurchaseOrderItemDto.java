package com.stockmanagementsystem.response.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecentPurchaseOrderItemDto {
    private Integer id;
    private String poNumber;
    private String supplierName;
    private String orderDate;
    private String deliverByDate;
    private Double amount;
    private String formattedAmount;
    private String status;
}
