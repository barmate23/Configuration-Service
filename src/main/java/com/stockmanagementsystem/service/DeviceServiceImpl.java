package com.stockmanagementsystem.service;


import com.stockmanagementsystem.entity.DeviceMaster;
import com.stockmanagementsystem.entity.LoginUser;
import com.stockmanagementsystem.repository.DeviceMasterRepository;
import com.stockmanagementsystem.repository.SubModuleRepository;
import com.stockmanagementsystem.response.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
@Slf4j
@Service
public class DeviceServiceImpl implements DeviceService{
    @Autowired
    LoginUser loginUser;
    @Autowired
    private DeviceMasterRepository deviceMasterRepository;
    @Autowired
    private SubModuleRepository subModuleRepository;

    @Override
    public BaseResponse<DeviceMaster> saveDevice(DeviceRequest deviceRequest) {
        // Start the timer to measure method execution time
        long startTime = System.currentTimeMillis();

        // Log method entry with UserId and LogId
        log.info("LogId:{} - DeviceServiceImpl - saveDevice - UserId:{} - {}",
                loginUser.getLogId(), loginUser.getUserId(), "SAVE DEVICE METHOD START");

        // Initialize BaseResponse and DeviceMaster objects
        BaseResponse<DeviceMaster> baseResponse = new BaseResponse<>();
        DeviceMaster deviceMaster = new DeviceMaster();

        try {
            // Set device details from request
            deviceMaster.setDeviceIp(deviceRequest.getDeviceIp());
            deviceMaster.setDeviceName(deviceRequest.getDeviceName());
            deviceMaster.setDeviceBrandName(deviceRequest.getDeviceBrand());
            deviceMaster.setDevicePort(deviceRequest.getDevicePort());

            // Retrieve and set role using roleId from deviceRequest
            deviceMaster.setRole(subModuleRepository.findByIsDeletedAndId(false, deviceRequest.getRoleId()).get());

            // Set organization details and audit fields
            deviceMaster.setOrganizationId(loginUser.getOrgId());
            deviceMaster.setSubOrganizationId(loginUser.getSubOrgId());
            deviceMaster.setCreatedBy(loginUser.getUserId());
            deviceMaster.setCreatedOn(new Date());
            deviceMaster.setModifiedBy(loginUser.getUserId());
            deviceMaster.setModifiedOn(new Date());

            // Set flags for activity and deletion
            deviceMaster.setIsDeleted(false);
            deviceMaster.setIsActive(true);

            // Save the device master data in the repository
            deviceMasterRepository.save(deviceMaster);

            // Prepare response on successful save
            baseResponse.setLogId(loginUser.getLogId());
            baseResponse.setCode(1);
            baseResponse.setMessage("DEVICE SAVE SUCCESSFULLY");
            baseResponse.setStatus(200);

            // Add the saved device to the response
            List<DeviceMaster> deviceMasters = new ArrayList<>();
            deviceMasters.add(deviceMaster);
            baseResponse.setData(deviceMasters);

            // Log successful save operation
            log.info("LogId:{} - DeviceServiceImpl - saveDevice - UserId:{} - {}",
                    loginUser.getLogId(), loginUser.getUserId(), "DEVICE SAVED SUCCESSFULLY");

        } catch (Exception e) {
            // Log the exception details
            log.error("LogId:{} - DeviceServiceImpl - saveDevice - UserId:{} - Error: {}",
                    loginUser.getLogId(), loginUser.getUserId(), e.getMessage(), e);

            // Set response in case of failure
            baseResponse.setLogId(loginUser.getLogId());
            baseResponse.setCode(0);
            baseResponse.setMessage("DEVICE SAVE FAILED: " + e.getMessage());
            baseResponse.setStatus(500);
        } finally {
            // End the timer and log the method execution time
            long endTime = System.currentTimeMillis();
            log.info("LogId:{} - DeviceServiceImpl - saveDevice - UserId:{} - {}",
                    loginUser.getLogId(), loginUser.getUserId(),
                    "SAVE DEVICE METHOD EXECUTED IN :" + (endTime - startTime) + " ms");
        }

        // Return the response
        return baseResponse;
    }

    @Override
    public BaseResponse<DeviceMaster> getDeviceById(Integer id) {
        long startTime = System.currentTimeMillis();
        log.info("LogId:{} - DeviceServiceImpl - getDeviceById - UserId:{} - {}",
                loginUser.getLogId(), loginUser.getUserId(), "GET DEVICE BY ID METHOD START");

        BaseResponse<DeviceMaster> baseResponse = new BaseResponse<>();

        try {
            // Attempt to fetch the device by its ID
            DeviceMaster deviceMaster = deviceMasterRepository.findByIsDeletedAndSubOrganizationIdAndId(false,loginUser.getSubOrgId(),id)
                    .orElseThrow(() -> new RuntimeException("Device not found with ID: " + id));

            // Prepare the success response
            baseResponse.setLogId(loginUser.getLogId());
            baseResponse.setCode(1);
            baseResponse.setMessage("DEVICE FOUND SUCCESSFULLY");
            baseResponse.setStatus(200);
            baseResponse.setData(Collections.singletonList(deviceMaster));

            log.info("LogId:{} - DeviceServiceImpl - getDeviceById - UserId:{} - {}",
                    loginUser.getLogId(), loginUser.getUserId(), "DEVICE FOUND SUCCESSFULLY");

        } catch (RuntimeException e) {
            // Log the exception if the device is not found
            log.error("LogId:{} - DeviceServiceImpl - getDeviceById - UserId:{} - Error: {}",
                    loginUser.getLogId(), loginUser.getUserId(), e.getMessage(), e);

            // Prepare the failure response
            baseResponse.setLogId(loginUser.getLogId());
            baseResponse.setCode(0);
            baseResponse.setMessage("DEVICE NOT FOUND: " + e.getMessage());
            baseResponse.setStatus(404);
            baseResponse.setData(null);
        } finally {
            long endTime = System.currentTimeMillis();
            log.info("LogId:{} - DeviceServiceImpl - getDeviceById - UserId:{} - {}",
                    loginUser.getLogId(), loginUser.getUserId(),
                    "GET DEVICE BY ID METHOD EXECUTED IN :" + (endTime - startTime) + " ms");
        }

        return baseResponse;
    }

    @Override
    public BaseResponse<DeviceMaster> getAllDevicesWithPagination(int page, int size) {
        long startTime = System.currentTimeMillis();
        log.info("LogId:{} - DeviceServiceImpl - getDevicesBySubOrganizationWithPagination - UserId:{} - {}",
                loginUser.getLogId(), loginUser.getUserId(), "GET DEVICES WITH PAGINATION METHOD START");

        BaseResponse<DeviceMaster> baseResponse = new BaseResponse<>();

        try {
            // Create Pageable object to handle pagination
            Pageable pageable = PageRequest.of(page, size);

            // Fetch paginated devices by sub-organization ID and non-deleted devices
            Page<DeviceMaster> pagedResult = deviceMasterRepository.findByIsDeletedAndSubOrganizationId(
                    false, loginUser.getSubOrgId(), pageable);

            // Check if there are results
            if (pagedResult.hasContent()) {
                List<DeviceMaster> deviceMasters = pagedResult.getContent();

                // Prepare the success response
                baseResponse.setLogId(loginUser.getLogId());
                baseResponse.setCode(1);
                baseResponse.setMessage("DEVICES FOUND SUCCESSFULLY");
                baseResponse.setStatus(200);
                baseResponse.setData(deviceMasters);
            } else {
                // Handle the case when no devices are found
                baseResponse.setLogId(loginUser.getLogId());
                baseResponse.setCode(0);
                baseResponse.setMessage("NO DEVICES FOUND");
                baseResponse.setStatus(500);
                baseResponse.setData(Collections.emptyList());
            }

            log.info("LogId:{} - DeviceServiceImpl - getDevicesBySubOrganizationWithPagination - UserId:{} - {}",
                    loginUser.getLogId(), loginUser.getUserId(), "DEVICES FOUND SUCCESSFULLY");

        } catch (Exception e) {
            // Log the exception if something goes wrong
            log.error("LogId:{} - DeviceServiceImpl - getDevicesBySubOrganizationWithPagination - UserId:{} - Error: {}",
                    loginUser.getLogId(), loginUser.getUserId(), e.getMessage(), e);

            // Prepare the failure response
            baseResponse.setLogId(loginUser.getLogId());
            baseResponse.setCode(0);
            baseResponse.setMessage("ERROR FETCHING DEVICES: " + e.getMessage());
            baseResponse.setStatus(500);
            baseResponse.setData(null);
        } finally {
            long endTime = System.currentTimeMillis();
            log.info("LogId:{} - DeviceServiceImpl - getDevicesBySubOrganizationWithPagination - UserId:{} - {}",
                    loginUser.getLogId(), loginUser.getUserId(),
                    "GET DEVICES WITH PAGINATION METHOD EXECUTED IN :" + (endTime - startTime) + " ms");
        }

        return baseResponse;
    }


    @Override
    public BaseResponse<DeviceMaster> updateDeviceById(Integer id,DeviceRequest deviceRequest) {
        // Start the timer to measure method execution time
        long startTime = System.currentTimeMillis();

        // Log method entry with UserId and LogId
        log.info("LogId:{} - DeviceServiceImpl - updateDevice - UserId:{} - {}",
                loginUser.getLogId(), loginUser.getUserId(), "UPDATE DEVICE METHOD START");

        // Initialize BaseResponse object
        BaseResponse<DeviceMaster> baseResponse = new BaseResponse<>();

        try {
            // Fetch the existing device by ID from the database
            DeviceMaster deviceMaster = deviceMasterRepository.findByIsDeletedAndSubOrganizationIdAndId(false,loginUser.getSubOrgId(),id)
                    .orElseThrow(() -> new RuntimeException("Device not found with ID: " + id));

            // Update device details from the request
            deviceMaster.setDeviceIp(deviceRequest.getDeviceIp());
            deviceMaster.setDeviceName(deviceRequest.getDeviceName());
            deviceMaster.setDeviceBrandName(deviceRequest.getDeviceBrand());
            deviceMaster.setDevicePort(deviceRequest.getDevicePort());

            // Update role using roleId from deviceRequest
            deviceMaster.setRole(subModuleRepository.findByIsDeletedAndId(false, deviceRequest.getRoleId()).get());

            // Update audit fields
            deviceMaster.setModifiedBy(loginUser.getUserId());
            deviceMaster.setModifiedOn(new Date());

            // Save the updated device master data in the repository
            deviceMasterRepository.save(deviceMaster);

            // Prepare response on successful update
            baseResponse.setLogId(loginUser.getLogId());
            baseResponse.setCode(1);
            baseResponse.setMessage("DEVICE UPDATED SUCCESSFULLY");
            baseResponse.setStatus(200);

            // Add the updated device to the response
            List<DeviceMaster> deviceMasters = new ArrayList<>();
            deviceMasters.add(deviceMaster);
            baseResponse.setData(deviceMasters);

            // Log successful update operation
            log.info("LogId:{} - DeviceServiceImpl - updateDevice - UserId:{} - {}",
                    loginUser.getLogId(), loginUser.getUserId(), "DEVICE UPDATED SUCCESSFULLY");

        } catch (Exception e) {
            // Log the exception details
            log.error("LogId:{} - DeviceServiceImpl - updateDevice - UserId:{} - Error: {}",
                    loginUser.getLogId(), loginUser.getUserId(), e.getMessage(), e);

            // Set response in case of failure
            baseResponse.setLogId(loginUser.getLogId());
            baseResponse.setCode(0);
            baseResponse.setMessage("DEVICE UPDATE FAILED: " + e.getMessage());
            baseResponse.setStatus(500);
        } finally {
            // End the timer and log the method execution time
            long endTime = System.currentTimeMillis();
            log.info("LogId:{} - DeviceServiceImpl - updateDevice - UserId:{} - {}",
                    loginUser.getLogId(), loginUser.getUserId(),
                    "UPDATE DEVICE METHOD EXECUTED IN :" + (endTime - startTime) + " ms");
        }

        // Return the response
        return baseResponse;
    }

    @Override
    public BaseResponse<DeviceMaster> deleteDeviceById(Integer id) {
        long startTime = System.currentTimeMillis();
        log.info("LogId:{} - DeviceServiceImpl - getDeviceById - UserId:{} - {}",
                loginUser.getLogId(), loginUser.getUserId(), "GET DEVICE BY ID METHOD START");

        BaseResponse<DeviceMaster> baseResponse = new BaseResponse<>();

        try {
            // Attempt to fetch the device by its ID
            DeviceMaster deviceMaster = deviceMasterRepository.findByIsDeletedAndSubOrganizationIdAndId(false,loginUser.getSubOrgId(),id)
                    .orElseThrow(() -> new RuntimeException("Device not found with ID: " + id));
            deviceMaster.setIsDeleted(true);
            deviceMasterRepository.save(deviceMaster);

            // Prepare the success response
            baseResponse.setLogId(loginUser.getLogId());
            baseResponse.setCode(1);
            baseResponse.setMessage("DEVICE FOUND SUCCESSFULLY");
            baseResponse.setStatus(200);
            baseResponse.setData(Collections.singletonList(deviceMaster));

            log.info("LogId:{} - DeviceServiceImpl - getDeviceById - UserId:{} - {}",
                    loginUser.getLogId(), loginUser.getUserId(), "DEVICE FOUND SUCCESSFULLY");

        } catch (RuntimeException e) {
            // Log the exception if the device is not found
            log.error("LogId:{} - DeviceServiceImpl - getDeviceById - UserId:{} - Error: {}",
                    loginUser.getLogId(), loginUser.getUserId(), e.getMessage(), e);

            // Prepare the failure response
            baseResponse.setLogId(loginUser.getLogId());
            baseResponse.setCode(0);
            baseResponse.setMessage("DEVICE NOT FOUND: " + e.getMessage());
            baseResponse.setStatus(404);
            baseResponse.setData(null);
        } finally {
            long endTime = System.currentTimeMillis();
            log.info("LogId:{} - DeviceServiceImpl - getDeviceById - UserId:{} - {}",
                    loginUser.getLogId(), loginUser.getUserId(),
                    "GET DEVICE BY ID METHOD EXECUTED IN :" + (endTime - startTime) + " ms");
        }

        return baseResponse;
    }
}
