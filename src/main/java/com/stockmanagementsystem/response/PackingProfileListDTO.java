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

    private String itemName;
    private String itemCode;
    private String supplierName;
    private String supplierCode;


    private Boolean isActive;
}
