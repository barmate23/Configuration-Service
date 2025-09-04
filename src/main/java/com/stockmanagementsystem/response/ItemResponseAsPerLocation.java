package com.stockmanagementsystem.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemResponseAsPerLocation {


    private String storeName;

    private String storeCode;

    private String area;

    private String locationCode;

    private String locationName;


}
