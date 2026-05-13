package com.stockmanagementsystem.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeviceMasterResponseV2 {
    private Integer id;
    private String deviceIp;
    private String deviceName;
    private String deviceBrandName;
    private Integer roleId;
    private String roleCode;
    private String roleName;
    private Integer devicePort;
    private Boolean isActive;
}
