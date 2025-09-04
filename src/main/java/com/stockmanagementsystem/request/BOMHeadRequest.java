package com.stockmanagementsystem.request;

import com.stockmanagementsystem.entity.BOMLine;
import lombok.Data;

import javax.persistence.Column;
import java.util.Date;
import java.util.List;

@Data
public class BOMHeadRequest {
    private String model;
    private String product;
    private String variant;
    private String colour;
    private Integer bomId;
    private String bomERPCode;
    private Date date;
    private Float version;
    private String lifecyclePhase;
    List<BOMLineRequest> bomLineRequests;
}
