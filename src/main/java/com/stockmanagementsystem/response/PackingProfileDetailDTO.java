package com.stockmanagementsystem.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PackingProfileDetailDTO {

    private Long configId;
    private String configCode;

    private String itemCode;
    private String itemName;

    private String supplierCode;
    private String supplierName;

    private String packingLevelCount;

    private Integer primaryUnits;

    private Integer secondaryUnits;

    private Integer tertiaryUnits;


    private Boolean isActive;

    public PackingProfileDetailDTO(Long id, String s, String itemCode, String name, String supplierId, String supplierName, String s1, Integer primaryUnits, Integer secondaryUnits, Integer tertiaryUnits, Boolean isActive) {
    }
}
