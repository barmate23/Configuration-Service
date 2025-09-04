package com.stockmanagementsystem.service;

import com.stockmanagementsystem.entity.DeviceMaster;
import com.stockmanagementsystem.entity.SubModule;
import com.stockmanagementsystem.request.DeviceMasterRequest;
import com.stockmanagementsystem.response.BaseResponse;
import com.stockmanagementsystem.response.ReasonResponse;

import java.util.Date;
import java.util.List;

public interface DeviceMasterService {

    BaseResponse<DeviceMaster> saveDeviceMaster(DeviceRequest deviceMasterRequest);

    BaseResponse<DeviceMaster>deleteDeviceMasterById(Integer deviceId);

    BaseResponse<DeviceMaster> getDeviceMasterById(Integer deviceId);

    BaseResponse<DeviceMaster>updateDeviceMaster(Integer deviceId, DeviceRequest deviceMasterRequest);
    BaseResponse<DeviceMaster> getAllDevice();
    BaseResponse<DeviceMaster> getDeviceWithPagination(Integer pageNo, Integer pageSize);
    BaseResponse<DeviceMaster> searchDeviceMaster(
            Integer pageNumber, Integer pageSize, List<String> deviceIp,List<String> deviceName, List<String> deviceBrandName
    );

    BaseResponse<DeviceMaster> activeDeviceMasterById(Integer deviceId, Boolean status);

    BaseResponse<SubModule> getAllSubModule();
}
