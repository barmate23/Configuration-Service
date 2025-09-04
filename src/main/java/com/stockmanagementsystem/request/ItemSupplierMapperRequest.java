package com.stockmanagementsystem.request;

import lombok.Data;

@Data
public class ItemSupplierMapperRequest {
    private Integer id;
    private Integer itemId;
    private Integer supplierId;
    private Boolean isDay;
    private Integer leadTime;

}
