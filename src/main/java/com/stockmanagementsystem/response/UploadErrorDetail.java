package com.stockmanagementsystem.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UploadErrorDetail {
    private Integer rowNumber;
    private String columnName;
    private String errorMessage;
}

