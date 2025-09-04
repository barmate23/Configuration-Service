package com.stockmanagementsystem.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SupplierDropdownResponse {

    private List<String> supplierName;
    private List<String> supplierCategory;
    private List<String> supplierType;
    private List<String> supplierId;
    private List<SupplierPair> uniqueSupplierPairs;

}
