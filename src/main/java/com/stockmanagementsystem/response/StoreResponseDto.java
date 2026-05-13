package com.stockmanagementsystem.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StoreResponseDto {
    private Integer id;
    private String storeId;
    private String storeName;
    private String storeAddress;
    private String storeManagerName;
    private String contactNumber;
    private String emailId;
    private Boolean isActive;
    private String erpStoreId;
}
