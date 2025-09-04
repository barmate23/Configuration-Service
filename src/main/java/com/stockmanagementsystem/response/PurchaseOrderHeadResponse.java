package com.stockmanagementsystem.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseOrderHeadResponse {

    private List<String> purchaseOrderNumber;
    private List<String> supplierName;

}
