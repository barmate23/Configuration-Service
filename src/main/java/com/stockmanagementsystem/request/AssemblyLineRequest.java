package com.stockmanagementsystem.request;

import lombok.Data;
import java.util.List;

@Data
public class AssemblyLineRequest {
    private String erpLineCode;
    private String lineCode;
    private String lineName;
    private String lineNumber;
    private String assemblyLineId;
    private Integer shopId;
    private String description;
    private Integer sequenceNumber;
    private List<StageRequest> stageRequests;
}
