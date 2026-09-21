package com.stockmanagementsystem.response.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KpiCardsDto {
    private WorkforceKpiDto workforceAndAccess;
    private ItemCatalogKpiDto itemMasterCatalog;
    private InwardProcurementKpiDto inwardProcurement;
    private HardwareFleetKpiDto hardwareFleet;
}
