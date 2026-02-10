package com.stockmanagementsystem.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PackingProfileDetailDTO {

    private Long configId;
    private String configCode;

    private String itemCode;
    private String itemName;

    private String supplierCode;
    private String supplierName;

    private String packingLevelCount;

    private String primaryUom;
    private Integer primaryUnits;

    private String secondaryUom;
    private Integer secondaryUnits;

    private String tertiaryUom;
    private Integer tertiaryUnits;


    private Boolean isActive;
}
