package com.stockmanagementsystem.response.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModuleStatusDto {
    private String moduleName;
    private String moduleCode;
    private Long recordCount;
    private Boolean isConfigured;
    private Double percentage;
}
