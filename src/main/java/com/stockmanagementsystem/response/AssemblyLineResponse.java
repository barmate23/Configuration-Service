package com.stockmanagementsystem.response;

import lombok.Data;
import java.util.List;

@Data
public class AssemblyLineResponse {
    private Integer id;
    private String erpLineCode;
    private String lineCode;
    private String lineName;
    private String lineNumber;
    private String assemblyLineId;
    private String description;
    private Integer sequenceNumber;
    private List<StageResponse> stages;
}
