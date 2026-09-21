package com.stockmanagementsystem.response.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HardwareFleetKpiDto {
    private Long onlineDevices;
    private Long totalDevices;
    private Double onlinePercentage;
    private Long totalDocks;
    private Long totalShops;
}
