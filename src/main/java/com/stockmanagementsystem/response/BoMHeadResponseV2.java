package com.stockmanagementsystem.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BoMHeadResponseV2 {
    private Integer id;
    private String model;
    private String brand;
    private String product;
    private String variant;
    private String colour;
    private String bomId;
    private String bomERPCode;
    private Date date;
    private Float version;
    private String lifecyclePhase;
    private Integer assemblyLine;
    private Boolean isActive;
}
