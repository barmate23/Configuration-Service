package com.stockmanagementsystem.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AreaResponseDto {
    private Integer id;
    private String storeCode;
    private String storeName;
    private Integer storeId;
    private String erpAreaId;
    private String areaId;
    private String areaName;
}
