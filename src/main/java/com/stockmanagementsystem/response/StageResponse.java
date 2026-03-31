package com.stockmanagementsystem.response;

import lombok.Data;
import java.util.List;

@Data
public class StageResponse {
    private Integer id;
    private String erpStageCode;
    private String stageCode;
    private String stageName;
    private Integer sequenceNumber;
}
