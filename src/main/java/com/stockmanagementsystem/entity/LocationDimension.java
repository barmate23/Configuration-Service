package com.stockmanagementsystem.entity;

import lombok.Data;

@Data
public class LocationDimension {
    private Double storageLength;
    private Double storageBreadth;
    private Double storageHeight ;
    private Double storageWeightCapacity;
    private String Uom;
}
