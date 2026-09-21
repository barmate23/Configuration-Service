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
public class ConnectedHardwareFleetDto {
    private Long onlineCount;
    private Long totalCount;
    private Double onlinePercentage;
    private List<ConnectedDeviceItemDto> devices;
}
