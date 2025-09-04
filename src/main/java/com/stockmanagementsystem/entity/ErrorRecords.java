package com.stockmanagementsystem.entity;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ErrorRecords {
    private Integer rowNum;
    private Integer cellNum;
    private String errorMessage;
}
