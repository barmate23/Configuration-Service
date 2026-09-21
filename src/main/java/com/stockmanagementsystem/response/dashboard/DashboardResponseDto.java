package com.stockmanagementsystem.response.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardResponseDto {
    private DashboardHeaderDto header;
    private KpiCardsDto kpiCards;
    private ConfigurationProgressDto configurationProgress;
    private WorkforceDistributionDto departmentWorkforceDistribution;
    private RecentPurchaseOrdersDto recentPurchaseOrders;
    private ConnectedHardwareFleetDto connectedHardwareFleet;
    private BottomQuickStatsDto bottomQuickStats;
}
