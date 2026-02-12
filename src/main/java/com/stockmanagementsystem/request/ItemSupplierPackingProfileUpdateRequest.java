package com.stockmanagementsystem.request;

import lombok.Data;

@Data
public class ItemSupplierPackingProfileUpdateRequest {

    private Long packingProfileId;          // PackingProfileConfigMaster ID
    private Long packingHierarchyLevelId;   // PackingHierarchyLevel ID

    private String primaryUom;
    private Integer primaryUnits;

    private String secondaryUom;
    private Integer secondaryUnits;

    private String tertiaryUom;
    private Integer tertiaryUnits;

    private String moqLevel;   // PRIMARY / SECONDARY / TERTIARY
    private Integer moqQty;

}
