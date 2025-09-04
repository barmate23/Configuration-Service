package com.stockmanagementsystem.controller;

import com.stockmanagementsystem.entity.Device;
import com.stockmanagementsystem.entity.DeviceMaster;
import com.stockmanagementsystem.response.BaseResponse;
import com.stockmanagementsystem.service.DeviceRequest;
import com.stockmanagementsystem.service.DeviceService;
import com.stockmanagementsystem.utils.APIConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping({APIConstants.BASE_REQUEST + APIConstants.SERVICENAME})
public class DeviceController {
    @Autowired
    private DeviceService deviceService;

    @PostMapping("/saveDevice")
    public BaseResponse<DeviceMaster> saveDevice(@RequestBody DeviceRequest deviceRequest){
        return deviceService.saveDevice(deviceRequest);
    }

    @PutMapping("/updateDeviceById/{id}")
    public BaseResponse<DeviceMaster>  updateDeviceById(@PathVariable Integer id,
                                                        @RequestBody DeviceRequest deviceRequest){
        return deviceService.updateDeviceById(id,deviceRequest);
    }
    @GetMapping("/getDeviceById/{id}")
    public BaseResponse<DeviceMaster>  getDeviceById(@PathVariable Integer id){
        return deviceService.getDeviceById(id);
    }
    @GetMapping("/getAllDevicesWithPagination")
    public BaseResponse<DeviceMaster>  getDeviceById(@RequestParam Integer page, Integer size){
        return deviceService.getAllDevicesWithPagination(page,size);
    }

    @DeleteMapping("/delete/{id}")
    public BaseResponse<DeviceMaster> deleteDeviceById(@PathVariable Integer id){
       return deviceService.deleteDeviceById(id);

    }
}
