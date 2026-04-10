package com.stockmanagementsystem.response;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class PackagingMasterResponse {
    private Long id;
    private String packagingCode;
    private String packagingName;
    private Long packagingSubtypeId;
    private String packagingSubtypeName;
    private String uom;
    private BigDecimal length;
    private BigDecimal width;
    private BigDecimal height;
    private BigDecimal diameter;
    private BigDecimal weight;
    private BigDecimal volume;
    private Boolean isStackable;
    private BigDecimal numberOfStackLevel;
    private BigDecimal maxWeightCapacity;
}
