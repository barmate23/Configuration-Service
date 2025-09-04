package com.stockmanagementsystem.request;

import lombok.Data;

@Data
public class ContainerRequest {
    private String code;
    private String type;
    private String dimensionUOM;
    private Float width;
    private Float height;
    private Float length;
    private Float circumference;
    private Float weight;
    private Integer itemQty;
    private Integer minimumOrderQty;

}
