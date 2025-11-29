package com.stockmanagementsystem.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemRequest {

//private String itemId;
    private String itemCode;
    private String erpItemId;
    private String itemName;
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
    private Float itemUnitWeight;
    private String physicalForm;
    private String containerCapacityUom;
    private Float containerCapacity;
    private Float itemUnitRate;
    private String currency;
    private Integer alternativeItemId;
    private boolean qcRequired;
    private Integer optimumLevel;
    private Integer reorderLevel;
    private Integer safetyStockLevel;
    private Integer criticalLevel;
    private Boolean isDays;
    private Boolean inspection;
    private Integer leadTime;
    private Integer dockId;
    private ContainerRequest containerRequest;
}
