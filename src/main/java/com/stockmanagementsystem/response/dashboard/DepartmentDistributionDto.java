package com.stockmanagementsystem.response.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentDistributionDto {
    private String departmentName;
    private String moduleCode;
    private Long userCount;
    private Long totalLicenses;
    private Long availableLicenses;
    private Double percentage;
    private Double workforcePercentage;
    private Boolean isUserCreated;
}
