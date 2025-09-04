package com.stockmanagementsystem.request;

import lombok.Data;

import java.util.List;

@Data
public class StoreRequest {
    private String storeName;
    private List<String> storeManagerName;
    private List<Integer> additionalAreaLicenceKeyId;
    private String erpStoreId;

}
