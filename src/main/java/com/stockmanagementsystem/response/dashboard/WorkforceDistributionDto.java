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
public class WorkforceDistributionDto {
    private Long totalUsers;
    private Long permanentCount;
    private Long contractCount;
    private Long materialCount;
    private Long serviceCount;
    private Long totalConfiguredDepartments;
    private Integer totalModulesChecked;
    private List<DepartmentDistributionDto> departments;
}
