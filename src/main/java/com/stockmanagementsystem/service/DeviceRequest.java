package com.stockmanagementsystem.service;

import lombok.Data;

import javax.validation.constraints.*;
import java.util.Date;

@Data
public class DeviceRequest {
    @NotBlank(message = "Device name cannot be empty")
    @Size(max = 100, message = "Device name cannot exceed 100 characters")
    private String deviceName;

    @NotBlank(message = "Device brand cannot be empty")
    @Size(max = 50, message = "Device brand cannot exceed 50 characters")
    private String deviceBrand;

    @NotBlank(message = "Device IP cannot be empty")
    @Pattern(regexp = "^(\\d{1,3}\\.){3}\\d{1,3}$", message = "Invalid IP address format")
    private String deviceIp;

    @NotNull(message = "Device port cannot be null")
    @Min(value = 1, message = "Port number must be greater than 0")
    @Max(value = 65535, message = "Port number must be less than or equal to 65535")
    private Integer devicePort;

    @NotNull(message = "Role ID cannot be null")
    private Integer roleId;

}
