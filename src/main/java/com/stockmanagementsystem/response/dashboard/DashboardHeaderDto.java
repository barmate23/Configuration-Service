package com.stockmanagementsystem.response.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardHeaderDto {
    private Integer organizationId;
    private String organizationName;
    private String userName;
    private Long totalRecordsConfigured;
    private Integer activeMasterServicesCount;
    private String lastSyncedTime;
}
