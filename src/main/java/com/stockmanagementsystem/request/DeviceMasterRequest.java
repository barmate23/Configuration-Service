package com.stockmanagementsystem.request;

import com.stockmanagementsystem.entity.SubModule;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeviceMasterRequest {

    private String deviceIp;
    private String deviceName;
    private String deviceBrandName;
    private Integer devicePort;

    private Integer role;
}
