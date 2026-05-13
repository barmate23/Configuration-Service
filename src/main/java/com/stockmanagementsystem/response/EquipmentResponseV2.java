package com.stockmanagementsystem.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EquipmentResponseV2 {
    private Integer id;
    private String trolleyId;
    private String equipmentName;
    private String assetId;
    private String trolleyType;
    private Integer storeId;
    private String storeName;
    private String storeCode;
}
