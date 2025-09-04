package com.stockmanagementsystem.service;

import com.stockmanagementsystem.entity.Device;
import com.stockmanagementsystem.entity.DeviceMaster;
import com.stockmanagementsystem.response.BaseResponse;

import java.util.List;

public interface DeviceService {

    BaseResponse<DeviceMaster> saveDevice(DeviceRequest deviceRequest);

    BaseResponse<DeviceMaster> getDeviceById(Integer id);

    BaseResponse<DeviceMaster> getAllDevicesWithPagination(int page, int size);

    BaseResponse<DeviceMaster> updateDeviceById(Integer id, DeviceRequest deviceRequest);

    BaseResponse<DeviceMaster> deleteDeviceById(Integer id);
}
