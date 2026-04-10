package com.stockmanagementsystem.response;

import lombok.Data;
import java.util.List;

@Data
public class PackingProfileResponse {
    private Long id;
    private String description;
    private String itemCode;
    private String itemName;
    private String supplierCode;
    private String supplierName;
    private String moqLevel;
    private Integer moqQty;
    private Boolean isActive;
    private List<PackingProfileLevelResponse> levels;
}
