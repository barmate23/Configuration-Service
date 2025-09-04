package com.stockmanagementsystem.service;
import com.stockmanagementsystem.entity.*;
import com.stockmanagementsystem.repository.DeviceMasterRepository;
import com.stockmanagementsystem.repository.SubModuleRepository;
import com.stockmanagementsystem.request.DeviceMasterRequest;
import com.stockmanagementsystem.response.BaseResponse;
import com.stockmanagementsystem.utils.ResponseKeyConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.stockmanagementsystem.utils.GlobalMessages.getResponseMessages;

@Service
@Slf4j
public class DeviceMasterServiceImpl implements DeviceMasterService {
    @Autowired
    LoginUser loginUser;
    @Autowired
    DeviceMasterRepository deviceMasterRepository;
    @Autowired
    SubModuleRepository subModuleRepository;

    @Override
    public BaseResponse<DeviceMaster> saveDeviceMaster(DeviceRequest deviceMasterRequest) {
        long startTime = System.currentTimeMillis();
        log.info("LogId:{} - DeviceMasterServiceImpl - saveDeviceMaster - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId()," SAVE DEVICE MASTER METHOD START");
        BaseResponse<DeviceMaster> baseResponse=new BaseResponse<>();
        List<DeviceMaster> deviceMasterList=new ArrayList<>();
     try {
         DeviceMaster deviceMaster=new DeviceMaster();

         DeviceMaster existingDevice = deviceMasterRepository.findBySubOrganizationIdAndIsDeletedAndDeviceNameOrSubOrganizationIdAndIsDeletedAndDeviceIp( loginUser.getSubOrgId(),false,deviceMasterRequest.getDeviceName(),  loginUser.getSubOrgId(),false,deviceMasterRequest.getDeviceIp());
         if (existingDevice != null) {
             if(existingDevice.getDeviceName().equals(deviceMasterRequest.getDeviceName())) {
                 ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10064E);
                 baseResponse.setCode(responseMessage.getCode());
                 baseResponse.setStatus(responseMessage.getStatus());
                 baseResponse.setMessage(responseMessage.getMessage());
                 baseResponse.setLogId(loginUser.getLogId());
                 baseResponse.setData(new ArrayList<>());
             } else if (existingDevice.getDeviceIp().equals(deviceMasterRequest.getDeviceIp())) {
                 ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10065E);
                 baseResponse.setCode(responseMessage.getCode());
                 baseResponse.setStatus(responseMessage.getStatus());
                 baseResponse.setMessage(responseMessage.getMessage());
                 baseResponse.setLogId(loginUser.getLogId());
                 baseResponse.setData(new ArrayList<>());
             }
             return baseResponse;
         }

         deviceMaster.setDeviceIp(deviceMasterRequest.getDeviceIp());
         deviceMaster.setDeviceName(deviceMasterRequest.getDeviceName());
         deviceMaster.setDevicePort(deviceMasterRequest.getDevicePort());
         deviceMaster.setDeviceBrandName(deviceMasterRequest.getDeviceBrand());
         Optional<SubModule> optionalSubModule= subModuleRepository.findByIsDeletedAndId(false,deviceMasterRequest.getRoleId());
         if (optionalSubModule.isPresent()) {
             deviceMaster.setRole(optionalSubModule.get());
         }else {
             ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10062E);
             baseResponse.setCode(responseMessage.getCode());
             baseResponse.setStatus(responseMessage.getStatus());
             baseResponse.setMessage(responseMessage.getMessage());
             baseResponse.setData(new ArrayList<>());
             baseResponse.setLogId(loginUser.getLogId());
             log.info("LogId:{} - DeviceMasterServiceImpl - saveDeviceMaster - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage());
             return baseResponse;
         }
         deviceMaster.setIsDeleted(false);
         deviceMaster.setIsActive(true);
         deviceMaster.setCreatedBy(loginUser.getUserId());
         deviceMaster.setCreatedOn(new Date());
         deviceMaster.setOrganizationId(loginUser.getOrgId());
         deviceMaster.setSubOrganizationId(loginUser.getSubOrgId());
         deviceMasterRepository.save(deviceMaster);

         deviceMasterList.add(deviceMaster);

         ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10104S);
         baseResponse.setCode(responseMessage.getCode());
         baseResponse.setStatus(responseMessage.getStatus());
         baseResponse.setData(deviceMasterList);
         baseResponse.setMessage(responseMessage.getMessage());
         baseResponse.setLogId(loginUser.getLogId());
         log.info("LogId:{} - DeviceMasterServiceImpl - saveDeviceMaster - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage());

     }catch (Exception ex){
         ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10127F);
         baseResponse.setCode(responseMessage.getCode());
         baseResponse.setStatus(responseMessage.getStatus());
         baseResponse.setMessage(responseMessage.getMessage());
         baseResponse.setLogId(loginUser.getLogId());
         long endTime = System.currentTimeMillis();
         log.error("LogId:{} - DeviceMasterServiceImpl - saveDeviceMaster - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage() + (endTime - startTime),ex);

     }
        long endTime = System.currentTimeMillis();
        log.info("LogId:{} - DeviceMasterServiceImpl - saveDeviceMaster - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId()," SAVE DEVICE MASTER EXECUTED TIME :" + (endTime - startTime));
        return baseResponse;
    }

    @Override
    public BaseResponse<DeviceMaster> deleteDeviceMasterById(Integer deviceId) {
        long startTime = System.currentTimeMillis();
        log.info("LogId:{} - DeviceMasterServiceImpl - deleteDeviceMasterById - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),"  DEVICE MASTER DELETED START");
        BaseResponse<DeviceMaster> baseResponse=new BaseResponse<>();
        List<DeviceMaster> deviceMasterList=new ArrayList<>();
        try {
            Optional<DeviceMaster> deviceMaster =deviceMasterRepository.findByIsDeletedAndSubOrganizationIdAndId(false, loginUser.getSubOrgId(),deviceId);
            deviceMaster.get().setIsDeleted(true);
            deviceMasterRepository.save(deviceMaster.get());
            deviceMasterList.add(deviceMaster.get());
            ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10108S);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setData(deviceMasterList);
            baseResponse.setLogId(loginUser.getLogId());
            log.info("LogId:{} - DeviceMasterServiceImpl - deleteDeviceMasterById - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage());
            return baseResponse;
        }catch (Exception ex){
            ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10131F);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setLogId(loginUser.getLogId());
            long endTime = System.currentTimeMillis();
            log.error("LogId:{} - DeviceMasterServiceImpl - deleteDeviceMasterById - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage() + (endTime - startTime),ex);
        }
        long endTime = System.currentTimeMillis();
        log.info("LogId:{} - DeviceMasterServiceImpl - deleteDeviceMasterById - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId()," DEVICE MASTER DELETED TIME" + (endTime - startTime));
        return baseResponse;

    }

    @Override
    public BaseResponse<DeviceMaster> getDeviceMasterById(Integer deviceId) {
        long startTime = System.currentTimeMillis();
        log.info("LogId:{} - DeviceMasterServiceImpl - getDeviceMasterById - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), " GET DEVICE MASTER BY ID METHOD START");
        BaseResponse<DeviceMaster> baseResponse = new BaseResponse<>();

        try {
            Optional<DeviceMaster> optionalDeviceMaster = deviceMasterRepository.findByIsDeletedAndSubOrganizationIdAndId(false,loginUser.getSubOrgId(),deviceId);
            if (!optionalDeviceMaster.isPresent()) {
                ResponseMessage responseMessage = getResponseMessages(ResponseKeyConstant.UPLD10063E);
                baseResponse.setCode(responseMessage.getCode());
                baseResponse.setStatus(responseMessage.getStatus());
                baseResponse.setMessage(responseMessage.getMessage());
                baseResponse.setLogId(loginUser.getLogId());
                long endTime = System.currentTimeMillis();
                log.error("LogId:{} - DeviceMasterServiceImpl - getDeviceMasterById - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), "Device not found. EXECUTED TIME :" + (endTime - startTime));
                return baseResponse;
            }

            DeviceMaster deviceMaster = optionalDeviceMaster.get();
            List<DeviceMaster> deviceMasterList=new ArrayList<>();
            deviceMasterList.add(deviceMaster);

            ResponseMessage responseMessage = getResponseMessages(ResponseKeyConstant.UPLD10107S);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setLogId(loginUser.getLogId());
            baseResponse.setData(deviceMasterList);
            log.info("LogId:{} - DeviceMasterServiceImpl - getDeviceMasterById - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), ResponseKeyConstant.SPACE + responseMessage.getMessage());

        } catch (Exception ex) {
            ResponseMessage responseMessage = getResponseMessages(ResponseKeyConstant.UPLD10129F);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setLogId(loginUser.getLogId());
            long endTime = System.currentTimeMillis();
            log.error("LogId:{} - DeviceMasterServiceImpl - getDeviceMasterById - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), ResponseKeyConstant.SPACE + responseMessage.getMessage() + " EXECUTED TIME :" + (endTime - startTime), ex);
        }

        long endTime = System.currentTimeMillis();
        log.info("LogId:{} - DeviceMasterServiceImpl - getDeviceMasterById - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), " GET DEVICE MASTER BY ID EXECUTED TIME :" + (endTime - startTime));
        return baseResponse;
    }

    @Override
    public BaseResponse<DeviceMaster> updateDeviceMaster(Integer deviceId, DeviceRequest deviceMasterRequest) {
        long startTime = System.currentTimeMillis();
        log.info("LogId:{} - DeviceMasterServiceImpl - updateDeviceMaster - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId()," UPDATE DEVICE MASTER METHOD START");
        BaseResponse<DeviceMaster> baseResponse = new BaseResponse<>();
        List<DeviceMaster> deviceMasterList=new ArrayList<>();
        try {
            Optional<DeviceMaster> optionalDeviceMaster= deviceMasterRepository.findByIsDeletedAndSubOrganizationIdAndId(false,loginUser.getSubOrgId(),deviceId);
            if (!optionalDeviceMaster.isPresent()) {
                ResponseMessage responseMessage = getResponseMessages(ResponseKeyConstant.UPLD10063E);
                baseResponse.setCode(responseMessage.getCode());
                baseResponse.setStatus(responseMessage.getStatus());
                baseResponse.setMessage(responseMessage.getMessage());
                baseResponse.setLogId(loginUser.getLogId());
                long endTime = System.currentTimeMillis();
                log.error("LogId:{} - DeviceMasterServiceImpl - updateDeviceMaster - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), "Device not found. EXECUTED TIME :" + (endTime - startTime));
                return baseResponse;
            }
            DeviceMaster deviceMaster = optionalDeviceMaster.get();


            DeviceMaster existingDeviceByName = deviceMasterRepository.findBySubOrganizationIdAndIsDeletedAndDeviceName(loginUser.getSubOrgId(), false, deviceMasterRequest.getDeviceName());
            if (existingDeviceByName != null && !existingDeviceByName.getId().equals(deviceId)) {
                ResponseMessage responseMessage = getResponseMessages(ResponseKeyConstant.UPLD10064E);
                baseResponse.setCode(responseMessage.getCode());
                baseResponse.setStatus(responseMessage.getStatus());
                baseResponse.setMessage(responseMessage.getMessage());
                baseResponse.setLogId(loginUser.getLogId());
                baseResponse.setData(new ArrayList<>());
                return baseResponse;
            }

            DeviceMaster existingDeviceByIp = deviceMasterRepository.findBySubOrganizationIdAndIsDeletedAndDeviceIp(loginUser.getSubOrgId(), false, deviceMasterRequest.getDeviceIp());
            if (existingDeviceByIp != null && !existingDeviceByIp.getId().equals(deviceId)) {
                ResponseMessage responseMessage = getResponseMessages(ResponseKeyConstant.UPLD10065E);
                baseResponse.setCode(responseMessage.getCode());
                baseResponse.setStatus(responseMessage.getStatus());
                baseResponse.setMessage(responseMessage.getMessage());
                baseResponse.setLogId(loginUser.getLogId());
                baseResponse.setData(new ArrayList<>());
                return baseResponse;
            }
            deviceMaster.setDeviceIp(deviceMasterRequest.getDeviceIp());
            deviceMaster.setDevicePort(deviceMasterRequest.getDevicePort());
            deviceMaster.setDeviceName(deviceMasterRequest.getDeviceName());
            deviceMaster.setDeviceBrandName(deviceMasterRequest.getDeviceBrand());
            deviceMaster.setIsDeleted(false);
            deviceMaster.setModifiedOn(new Date());
            deviceMaster.setModifiedBy(loginUser.getUserId());
            deviceMaster.setOrganizationId(loginUser.getOrgId());
            deviceMaster.setSubOrganizationId(loginUser.getSubOrgId());

            Optional<SubModule> optionalSubModule= subModuleRepository.findByIsDeletedAndId(false,deviceMasterRequest.getRoleId());
            if (optionalSubModule.isPresent()) {
                deviceMaster.setRole(optionalSubModule.get());
            }else {
                ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10062E);
                baseResponse.setCode(responseMessage.getCode());
                baseResponse.setStatus(responseMessage.getStatus());
                baseResponse.setMessage(responseMessage.getMessage());
                baseResponse.setData(new ArrayList<>());
                baseResponse.setLogId(loginUser.getLogId());
                log.info("LogId:{} - DeviceMasterServiceImpl - updateDeviceMaster - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage());
                return baseResponse;
            }
            DeviceMaster save = deviceMasterRepository.save(deviceMaster);
            deviceMasterList.add(save);

            ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10105S);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setData(deviceMasterList);
            baseResponse.setLogId(loginUser.getLogId());
            log.info("LogId:{} - DeviceMasterServiceImpl - updateDeviceMaster - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage());
            return baseResponse;

        }catch (Exception ex){
            ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10128F);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setLogId(loginUser.getLogId());
            long endTime = System.currentTimeMillis();
            log.error("LogId:{} - DeviceMasterServiceImpl - updateDeviceMaster - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage()+ (endTime - startTime),ex);

        }
        long endTime = System.currentTimeMillis();
        log.info("LogId:{} - DeviceMasterServiceImpl - updateDeviceMaster - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId()," UPDATE DEVICE MASTER METHOD EXECUTED TIME :" + (endTime - startTime));
        return baseResponse;
    }

    @Override
    public BaseResponse<DeviceMaster> getAllDevice() {
        long startTime = System.currentTimeMillis();
        log.info("LogId:{} - DeviceMasterServiceImpl - getAllDevice - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),"DEVICE LIST FETCHED METHOD START ");
        BaseResponse<DeviceMaster> baseResponse=new BaseResponse<>();
        try{
            List<DeviceMaster> deviceMasterList=deviceMasterRepository.findByIsDeletedAndSubOrganizationId(false,loginUser.getSubOrgId());

            ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10106S);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setData(deviceMasterList);
            baseResponse.setLogId(loginUser.getLogId());
            log.info("LogId:{} - DeviceMasterServiceImpl - getAllDevice - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage());

        }catch (Exception ex){
            ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10130F);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setLogId(loginUser.getLogId());
            long endTime = System.currentTimeMillis();
            log.error("LogId:{} - DeviceMasterServiceImpl - getAllDevice - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage() + (endTime - startTime),ex);


        }
        long endTime = System.currentTimeMillis();
        log.info("LogId:{} - DeviceMasterServiceImpl - getAllDevice - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId()," DEVICE LIST FETCHED TIME :" + (endTime - startTime));
        return baseResponse;
    }

    @Override
    public BaseResponse<DeviceMaster> getDeviceWithPagination(Integer pageNo, Integer pageSize) {

        long startTime = System.currentTimeMillis();
        log.info("LogId:{} - DeviceMasterServiceImpl - getDeviceWithPagination - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId()," DEVICE LIST FETCHED START");
        BaseResponse<DeviceMaster> baseResponse = new BaseResponse<>();
        List<DeviceMaster> deviceMasterList=new ArrayList<>();

        try {
            final Pageable pageable = (Pageable) PageRequest.of((int) pageNo, (int) pageSize);
            Page<DeviceMaster> pageResult = this.deviceMasterRepository.findByIsDeletedAndSubOrganizationId(false,loginUser.getSubOrgId(), pageable);
            baseResponse.setTotalPageCount(pageResult.getTotalPages());
            deviceMasterList=pageResult.getContent();
            baseResponse.setTotalRecordCount(pageResult.getTotalElements());

            ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10106S);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setData(deviceMasterList);
            baseResponse.setLogId(loginUser.getLogId());
            log.info("LogId:{} - DeviceMasterServiceImpl - getDeviceWithPagination - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage());
            return baseResponse;
        }catch (Exception ex){
            ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10130F);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setLogId(loginUser.getLogId());
            long endTime = System.currentTimeMillis();
            log.error("LogId:{} - DeviceMasterServiceImpl - getDeviceWithPagination - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage() + (endTime - startTime),ex);

        }
        long endTime = System.currentTimeMillis();
        log.info("LogId:{} - DeviceMasterServiceImpl - getDeviceWithPagination - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId()," DEVICE LIST FETCHED TIME" + (endTime - startTime));
        return baseResponse;
    }

    @Override
    public BaseResponse<DeviceMaster> searchDeviceMaster(Integer page, Integer size, List<String> deviceIp, List<String> deviceName, List<String> deviceBrandName) {

        long startTime = System.currentTimeMillis();
        log.info("LogId:{} - DeviceMasterServiceImpl - searchDeviceMaster - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId()," SEARCH DEVICE MASTER START");
        BaseResponse<DeviceMaster> response = new BaseResponse<>();

        try {
            Pageable pageable = PageRequest.of(page, size);
            Specification<DeviceMaster> specification = DeviceMasterSpecifications.withFilters(deviceIp, deviceName, deviceBrandName,true, loginUser.getSubOrgId());

            Page<DeviceMaster> deviceMasterPage = deviceMasterRepository.findAll(specification, pageable);

            List<DeviceMaster> deviceMasterList = deviceMasterPage.getContent();
            response.setData(deviceMasterList);
            response.setTotalRecordCount(deviceMasterPage.getTotalElements());
            response.setTotalPageCount(deviceMasterPage.getTotalPages());

            ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10106S);
            response.setCode(responseMessage.getCode());
            response.setStatus(responseMessage.getStatus());
            response.setMessage(responseMessage.getMessage());
            response.setLogId(loginUser.getLogId());
            log.info("LogId:{} - DeviceMasterServiceImpl - searchDeviceMaster - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage());

        } catch (Exception e) {
            ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10130F);
            response.setCode(responseMessage.getCode());
            response.setStatus(responseMessage.getStatus());
            response.setMessage(responseMessage.getMessage());
            response.setData(Collections.emptyList());
            long endTime = System.currentTimeMillis();
            log.error("LogId:{} - DeviceMasterServiceImpl - searchDeviceMaster - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage()+ (endTime - startTime),e);
            response.setLogId(loginUser.getLogId());
        }
        long endTime = System.currentTimeMillis();
        log.info("LogId:{} - DeviceMasterServiceImpl - searchDeviceMaster - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),"  SEARCH DEVICE MASTER TIME" + (endTime - startTime));
        return response;
    }


    @Override
    public BaseResponse<DeviceMaster> activeDeviceMasterById(Integer deviceId, Boolean status){
        long startTime = System.currentTimeMillis();
        log.info(loginUser.getLogId()+" DEVICE MASTER ACTIVE AND DEACTIVATE METHOD START");
        BaseResponse response=new BaseResponse<>();
        try{
            Optional<DeviceMaster> deviceMasters = Optional.ofNullable(this.deviceMasterRepository.findByIsDeletedAndId(false, deviceId));
            if(status) {
                deviceMasters.get().setIsActive(status);
                deviceMasterRepository.save(deviceMasters.get());
                List<DeviceMaster> deviceMasterList = new ArrayList<>();
                deviceMasterList.add(deviceMasters.get());

                ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10109S);
                response.setCode(responseMessage.getCode());
                response.setStatus(responseMessage.getStatus());
                response.setMessage(responseMessage.getMessage());
                response.setData(deviceMasterList);
                response.setLogId(loginUser.getLogId());
                log.info(loginUser.getLogId() + " DEVICE MASTER ACTIVATED SUCCESSFULLY ");
            }else {
                deviceMasters.get().setIsActive(status);
                deviceMasterRepository.save(deviceMasters.get());
                List<DeviceMaster> deviceMasterList = new ArrayList<>();
                deviceMasterList.add(deviceMasters.get());

                ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10110S);
                response.setCode(responseMessage.getCode());
                response.setStatus(responseMessage.getStatus());
                response.setMessage(responseMessage.getMessage());
                response.setData(deviceMasterList);
                response.setLogId(loginUser.getLogId());
                log.info(loginUser.getLogId() + " DEVICE MASTER DEACTIVATED SUCCESSFULLY ");
            }
        }catch(Exception e){

            ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10132F);
            response.setCode(responseMessage.getCode());
            response.setStatus(responseMessage.getStatus());
            response.setMessage(responseMessage.getMessage());
            response.setData(new ArrayList<>());
            response.setLogId(loginUser.getLogId());
            long endTime = System.currentTimeMillis();
            log.error(String.valueOf(new StringBuilder(loginUser.getLogId()).append(" ERROR OCCURS AT DEACTIVATED & ACTIVATED DEVICE MASTER EXECUTED TIME ").append(endTime-startTime)),e);
        }
        long endTime = System.currentTimeMillis();
        log.info(String.valueOf(new StringBuilder(loginUser.getLogId()).append(" DEACTIVATED & ACTIVATED DEVICE MASTER METHOD  EXECUTED TIME ").append(endTime-startTime)));
        return response;
    }

    @Override
    public BaseResponse<SubModule> getAllSubModule() {
        long startTime = System.currentTimeMillis();
        log.info("LogId:{} - DeviceMasterServiceImpl - getAllSubModule - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),"SUB MODULE LIST FETCHED METHOD START ");
        BaseResponse<SubModule> baseResponse=new BaseResponse<>();
        try{
            List<SubModule> subModules=subModuleRepository.findByIsDeleted(false);

            baseResponse.setCode(1);
            baseResponse.setStatus(200);
            baseResponse.setMessage("SUCCESSFULLY FETCHED SUM MODULE LIST");
            baseResponse.setData(subModules);
            baseResponse.setLogId(loginUser.getLogId());
            log.info("LogId:{} - DeviceMasterServiceImpl - getAllSubModule - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE);

        }catch (Exception ex){
            baseResponse.setCode(0);
            baseResponse.setStatus(200);
            baseResponse.setMessage(" FAILED TO FETCHED SUM MODULE LIST");
            baseResponse.setLogId(loginUser.getLogId());
            long endTime = System.currentTimeMillis();
            log.error("LogId:{} - DeviceMasterServiceImpl - getAllSubModule - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+ (endTime - startTime),ex);


        }
        long endTime = System.currentTimeMillis();
        log.info("LogId:{} - DeviceMasterServiceImpl - getAllSubModule - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId()," SUB MODULE LIST FETCHED TIME :" + (endTime - startTime));
        return baseResponse;
    }

}
