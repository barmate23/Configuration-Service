package com.stockmanagementsystem.controller;

import com.stockmanagementsystem.entity.DeviceMaster;
import com.stockmanagementsystem.entity.SubModule;
import com.stockmanagementsystem.request.DeviceMasterRequest;
import com.stockmanagementsystem.response.BaseResponse;
import com.stockmanagementsystem.service.DeviceMasterService;
import com.stockmanagementsystem.service.DeviceRequest;
import com.stockmanagementsystem.utils.APIConstants;
import com.stockmanagementsystem.utils.ServiceConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping({APIConstants.BASE_REQUEST + APIConstants.SERVICENAME + APIConstants.DEVICE_MASTER_CONTROLLER})
public class DeviceMasterController {
    @Autowired
    DeviceMasterService deviceMasterService;

    @PostMapping(APIConstants.SAVE_DEVICE)
    public BaseResponse<DeviceMaster> saveDeviceMaster(@RequestBody DeviceRequest deviceMasterRequest){
        return deviceMasterService.saveDeviceMaster(deviceMasterRequest);
    }

    @DeleteMapping(APIConstants.DELETE_DEVICE)
    public BaseResponse<DeviceMaster> deleteDeviceMasterById(@PathVariable Integer deviceId){
        return deviceMasterService.deleteDeviceMasterById(deviceId);
    }

    @PutMapping(APIConstants.UPDATE_DEVICE)
    public BaseResponse<DeviceMaster> updateDeviceMaster(@PathVariable Integer deviceId,
                                         @RequestBody DeviceRequest deviceMasterRequest){
        return deviceMasterService.updateDeviceMaster(deviceId,deviceMasterRequest);
    }

    @GetMapping(APIConstants.GET_DEVICE_BY_ID)
    public BaseResponse<DeviceMaster> getDeviceById(@PathVariable Integer deviceId){
        return deviceMasterService.getDeviceMasterById(deviceId);
    }

    @GetMapping(APIConstants.GET_ALL_DEVICE)
    public BaseResponse<DeviceMaster> getAllDevice(){
        return deviceMasterService.getAllDevice();
    }

//    @GetMapping(APIConstants.GET_DEVICE_WITH_PAGINATION)
//    public BaseResponse<DeviceMaster> getdeviceWithPagination(@RequestParam(defaultValue = "0") Integer page,
//                                                       @RequestParam(defaultValue = "10") Integer size){
//        return deviceMasterService.getDeviceWithPagination(page,size);
//    }

    @GetMapping(APIConstants.GET_DEVICE_WITH_PAGINATION)
    public BaseResponse<DeviceMaster> searchDeviceMasters(
            @RequestParam(value = "page", defaultValue = "0", required = false) Integer page,
            @RequestParam(value = "size", defaultValue = "10", required = false) Integer size,
            @RequestParam(required = false) List<String> deviceIp,
            @RequestParam(required = false) List<String> deviceName,
            @RequestParam(required = false) List<String> deviceBrandName
    ) {
        return deviceMasterService.searchDeviceMaster(page, size, deviceIp, deviceName,deviceBrandName);
    }

    @DeleteMapping(APIConstants.ACTIVE_DEVICE_BY_ID)
    public BaseResponse<DeviceMaster> activeDeviceMasterById(@RequestParam Integer deviceId,
                                              @RequestParam Boolean status) {
        return deviceMasterService.activeDeviceMasterById(deviceId, status);
    }

    @GetMapping(APIConstants.GET_ALL_SUB_MODULE)
    public BaseResponse<SubModule> getAllSubModule(){
        return deviceMasterService.getAllSubModule();
    }
}
