package com.stockmanagementsystem.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PackingProfileListDTO {

    private Long configId;
    private String configCode;

    private String itemDescription;
    private String supplierName;

    private String packingLevel;   // PRIMARY / SECONDARY / TERTIARY / QUATERNARY

    private Boolean isActive;
}
