package com.stockmanagementsystem.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ZoneResponse {
    private Integer id;
    private String areaCode;
    private String areaName;
    private Integer areaId;
    private String storeCode;
    private String storeName;
    private String storeId;
    private String erpZoneId;
    private String zoneId;
    private String zoneName;
    private Integer zoneCategoryId;
    private String zoneCategoryName;
}
