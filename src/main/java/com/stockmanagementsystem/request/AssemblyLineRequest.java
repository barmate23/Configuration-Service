package com.stockmanagementsystem.request;

import lombok.Data;

import java.util.List;


@Data
public class AssemblyLineRequest {

    private String assemblyLineId;
    private String lineNumber;
    List<StageRequest> stageRequests;

}
