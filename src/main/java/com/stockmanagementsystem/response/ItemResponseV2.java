package com.stockmanagementsystem.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemResponseV2 {
    private Integer id;
    private String itemId;
    private String itemCode;
    private String erpItemId;
    private String name;
    private String description;
    private String itemGroup;
    private String itemCategory;
    private String itemSubcategory;
    private String typeDirectIndirect;
    private String typeSerialBatchNone;
    private String issueType;
    private String classABC;
    private String attribute;
    private String source;
    private String uom;
    private String purchaseUom;
    private Float itemUnitWeight;
    private String physicalForm;
    private Float itemUnitRate;
    private String currency;
    private Integer optimumLevel;
    private Integer reorderLevel;
    private Integer safetyStockLevel;
    private Integer criticalLevel;
    private Boolean alternativeItem;
    private Boolean qcRequired;
    private Boolean inspection;
    private Integer dockId;
    private String dockName;
    private String dockCode;
    private Integer alternativeItemId;
    private String alternativeItemName;
    private Boolean isActive;
}
