package com.stockmanagementsystem.response;

import lombok.Data;

import java.util.Date;

@Data
public class CreateYearResponse {
    private String year;
    private Date startDate;
    private Date endDate;
}
