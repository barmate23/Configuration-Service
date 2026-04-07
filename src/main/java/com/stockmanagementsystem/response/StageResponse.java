package com.stockmanagementsystem.response;

import lombok.Data;

@Data
public class StageResponse {
    private Integer id;
    private String erpStageCode;
    private String stageCode;
    private String stageName;
    private String stageId;
    private Integer sequenceNumber;
}
