package com.stockmanagementsystem.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupplierNameResponse {
    private Integer id;
    private String supplierId;
    private String supplierName;
    private String erpSupplier;
    private String supplierCategory;
    private String supplierGroup;


}
