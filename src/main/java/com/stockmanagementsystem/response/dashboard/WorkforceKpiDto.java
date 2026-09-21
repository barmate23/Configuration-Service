package com.stockmanagementsystem.response.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkforceKpiDto {
    private Long totalUsers;
    private Long activeUsers;
    private Double activePercentage;
    private Long totalDepartments;
}
