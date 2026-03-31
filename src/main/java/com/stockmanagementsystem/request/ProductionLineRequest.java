package com.stockmanagementsystem.request;

import lombok.Data;
import java.util.List;

@Data
public class ProductionLineRequest {
    private String erpLineCode;
    private String lineCode;
    private String lineName;
    private String description;
    private Integer sequenceNumber;
    private Integer shopId;
    private List<ProductionLineStageRequest> stageRequests;
}
