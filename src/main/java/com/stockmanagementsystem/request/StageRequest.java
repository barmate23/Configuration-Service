package com.stockmanagementsystem.request;


import lombok.Data;

@Data
public class StageRequest {
    private Integer id;
    private String erpStageCode;
    private String stageCode;
    private String stageName;
    private Integer sequenceNumber;
    private Integer assemblyLineId;
}
