package com.stockmanagementsystem.request;


import lombok.Data;

@Data
public class StageRequest {
    private Integer id;
    private String stageId;
    private String stageName;
    private String assemblyLineId;
}
