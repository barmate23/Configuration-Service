package com.stockmanagementsystem.response.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfigurationProgressDto {
    private Integer totalModules;
    private Integer configuredModules;
    private Integer pendingModules;
    private Double progressPercentage;
    private Double pendingPercentage;
    private Long totalRecords;
    private String pendingAction;
    private List<ModuleStatusDto> modules;
}
