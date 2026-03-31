package com.stockmanagementsystem.request;

import lombok.Data;

@Data
public class ProductionLineStageRequest {
    private String erpStageCode;
    private String stageCode;
    private String stageName;
    private Integer sequenceNumber;
    private Integer productionLineId;
}
