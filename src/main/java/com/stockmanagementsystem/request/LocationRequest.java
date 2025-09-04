package com.stockmanagementsystem.request;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;


@Data
public class LocationRequest {
    private Integer itemId;
    private Integer storeId;
    private Integer zoneId;
    private String zoneName;
    private String erpZoneId;
    private Integer areaId;
    private String erpAreaId;
    private String areaName;
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
    private Integer itemQty;
    private Integer carryingCapacity;
}
