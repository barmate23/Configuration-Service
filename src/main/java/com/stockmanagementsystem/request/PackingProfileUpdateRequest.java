package com.stockmanagementsystem.request;

import lombok.Data;

@Data
public class PackingProfileUpdateRequest {

    private String description;

    private Integer packingLevel;

    private String primaryUom;
    private Integer primaryUnits;

    private String secondaryUom;
    private Integer secondaryUnits;

    private String tertiaryUom;
    private Integer tertiaryUnits;

    private String moqLevel; // PRIMARY / SECONDARY / TERTIARY
    private Integer moqQty;

    private Boolean isActive;
}
