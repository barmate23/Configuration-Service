package com.stockmanagementsystem.response.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecentPurchaseOrdersDto {
    private Long totalPOs;
    private List<RecentPurchaseOrderItemDto> purchaseOrders;
}
