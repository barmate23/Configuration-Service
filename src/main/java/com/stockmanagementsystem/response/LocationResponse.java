package com.stockmanagementsystem.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LocationResponse {
    private Integer id;
    private String itemName;
    private String itemCode;
    private String zoneCode;
    private String zoneName;
    private String zoneId;
    private String areaCode;
    private String areaName;
    private String areaId;
    private String storeCode;
    private String storeName;
    private String storeId;
    private Integer itemQty;
    private String level;
    private String row;
    private String rackFloor;
    private String rackNo;
    private String shelfNo;
    private String erpLocationId;
    private String locationId;
    private String locationType;
    private Float length;
    private Float width;
    private Float height;
    private Float areaSqCm;
    private Float volumeCuCm;
    private Integer carryingCapacity;
    private Integer remainingItemQty;
}
