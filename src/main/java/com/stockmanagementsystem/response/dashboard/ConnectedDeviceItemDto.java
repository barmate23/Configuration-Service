package com.stockmanagementsystem.response.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConnectedDeviceItemDto {
    private Integer id;
    private String terminal;
    private String ipAddress;
    private String role;
    private String status;
    private Boolean isActive;
}
