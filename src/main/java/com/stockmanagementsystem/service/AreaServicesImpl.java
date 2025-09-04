package com.stockmanagementsystem.service;

import com.stockmanagementsystem.entity.*;
import com.stockmanagementsystem.repository.AddressRepository;
import com.stockmanagementsystem.repository.AreaRepository;
import com.stockmanagementsystem.repository.StoreRepository;
import com.stockmanagementsystem.repository.ZoneRepository;
import com.stockmanagementsystem.response.AddressResponse;
import com.stockmanagementsystem.response.BaseResponse;
import com.stockmanagementsystem.utils.ResponseKeyConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.stockmanagementsystem.utils.GlobalMessages.getResponseMessages;

@Slf4j
@Service
public class AreaServicesImpl implements AreaServices {
    @Autowired
    AreaRepository areaRepository;
    @Autowired
    LoginUser loginUser;
    @Autowired
    StoreRepository storeRepository;

    @Autowired
    AddressRepository addressRepository;

    @Autowired
    ZoneRepository zoneRepository;

    @Autowired
    ZoneService zoneService;

    @Override
    public BaseResponse<Area> getALlAreas(Integer pageNo, Integer pageSize, List<Integer> storeId, List<Integer> areaId) {
        long startTime = System.currentTimeMillis();
        log.info("LogId:{} - AreaServicesImpl - getALlAreas - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId()," GET ALL AREA METHOD START");
        BaseResponse<Area> baseResponse = new BaseResponse<>();
        List<Area> areaList = new ArrayList<>();
        Page<Area>  pageResult =null;
        try {
            final Pageable pageable = (Pageable) PageRequest.of((int) pageNo, (int) pageSize);
            if (storeId==null && areaId==null){
                pageResult = this.areaRepository.findByIsDeletedAndSubOrganizationIdOrderById(false,loginUser.getSubOrgId(), pageable);
            } else if(storeId!=null && areaId!=null) {
                pageResult = this.areaRepository.findByIsDeletedAndStoreIdInAndIdInOrderById(false, pageable, storeId, areaId);
            } else if (storeId!=null) {
                pageResult = this.areaRepository.findByIsDeletedAndSubOrganizationIdAndStoreIdInOrderById(false,loginUser.getSubOrgId(),storeId,pageable );
            } else if (areaId!=null) {
                pageResult = this.areaRepository.findByIsDeletedAndSubOrganizationIdAndIdInOrderById(false,loginUser.getSubOrgId(),areaId,pageable);
            }

            ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10011S);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());

            baseResponse.setTotalPageCount(pageResult.getTotalPages());
            areaList = (pageResult.getContent());
            baseResponse.setTotalRecordCount(pageResult.getTotalElements());
            baseResponse.setData(areaList);
            baseResponse.setLogId(loginUser.getLogId());
            log.info("LogId:{} - AreaServicesImpl - getALlAreas - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage());
        } catch (Exception ex) {

            ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10011F);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setLogId(loginUser.getLogId());
            long endTime = System.currentTimeMillis();
            log.error("LogId:{} - AreaServicesImpl - getALlAreas - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage() + (endTime - startTime),ex);
        }
        long endTime = System.currentTimeMillis();
        log.info("LogId:{} - AreaServicesImpl - getALlAreas - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId()," GET ALL AREA METHOD EXECUTED TIME :" + (endTime - startTime));
        return baseResponse;
    }

    @Override
    public BaseResponse<Area> updateAreaById(Integer areaId, Integer storeId, String erpAreaId, String areaName) {
        long startTime = System.currentTimeMillis();
        log.info("LogId:{} - AreaServicesImpl - updateAreaById - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),"UPDATE AREA BY ID METHOD START");
        BaseResponse<Area> baseResponse = new BaseResponse<>();
        try {
            Area area = areaRepository.findByIsDeletedAndSubOrganizationIdAndId(false,loginUser.getSubOrgId(),areaId);
            List<Area> areaList = areaRepository.findByIsDeletedAndSubOrganizationIdAndStoreId(false, loginUser.getSubOrgId(), storeId);
            if(area.getAreaName()!=null && !area.getAreaName().equalsIgnoreCase(areaName)) {
                // Fetch the area list for the specified store and sub-organization
                // Check if an area with the same name already exists
                boolean areaExists = areaList.stream()
                        .anyMatch(a -> a.getAreaName() != null && a.getAreaName().equalsIgnoreCase(areaName));

                if (!areaExists) {
                    // If the area doesn't exist, update the area name
                    area.setAreaName(areaName);
                } else {
                    // If the area exists, return an error response
                    ResponseMessage responseMessage = getResponseMessages(ResponseKeyConstant.UPLD10008E);
                    baseResponse.setCode(responseMessage.getCode());
                    baseResponse.setStatus(responseMessage.getStatus());
                    baseResponse.setMessage(responseMessage.getMessage());
                    baseResponse.setData(new ArrayList<>());
                    baseResponse.setLogId(loginUser.getLogId());

                    log.info("LogId:{} - AreaServicesImpl - updateAreaById - UserId:{} - {}",
                            loginUser.getLogId(), loginUser.getUserId(), responseMessage.getMessage());

                    return baseResponse;
                }
            }else {
                area.setAreaName(areaName);
            }

            if(!areaList.stream().anyMatch(a->a.getErpAreaId()!=null && a.getErpAreaId().equalsIgnoreCase(erpAreaId))){
                area.setErpAreaId(erpAreaId);
            }else{
                ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10009E);
                baseResponse.setCode(responseMessage.getCode());
                baseResponse.setStatus(responseMessage.getStatus());
                baseResponse.setMessage(responseMessage.getMessage());
                baseResponse.setData(new ArrayList<>());
                baseResponse.setLogId(loginUser.getLogId());
                log.info("LogId:{} - AreaServicesImpl - updateAreaById - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage());
                return baseResponse;
            }
            area.setSubOrganizationId(loginUser.getSubOrgId());
            area.setOrganizationId(loginUser.getOrgId());
            area.setCreatedOn(new Date());
            area.setCreatedBy(loginUser.getUserId());
            area.setIsDeleted(false);
            area.setModifiedBy(loginUser.getUserId());
            area.setModifiedOn(new Date());
            areaRepository.save(area);
            List<Area> areas = new ArrayList<>();
            areas.add(area);

            ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10012S);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setData(areas);
            baseResponse.setLogId(loginUser.getLogId());
            log.info("LogId:{} - AreaServicesImpl - updateAreaById - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage());
        } catch (Exception ex) {
            ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10012F);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setLogId(loginUser.getLogId());
            long endTime = System.currentTimeMillis();
            log.error("LogId:{} - AreaServicesImpl - updateAreaById - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage() + (endTime - startTime),ex);
        }
        long endTime = System.currentTimeMillis();
        log.info("LogId:{} - AreaServicesImpl - updateAreaById - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId()," UPDATE AREA BY ID METHOD EXECUTED TIME :" + (endTime - startTime));
        return baseResponse;

    }

    @Override
    public BaseResponse<Area> createArea(Store store, Integer sq) {
        long startTime = System.currentTimeMillis();
        log.info("LogId:{} - AreaServicesImpl - createArea - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),"  AUTO CREATE AREA METHOD START");
        BaseResponse<Area> baseResponse = new BaseResponse<>();
        try {
            Area area = new Area();
            if(sq<10) {
                area.setAreaId(store.getStoreId() + "-A0" + sq);
            }
            else {
                area.setAreaId(store.getStoreId() + "-A" + sq);
            }
            area.setStore(store);
            area.setSubOrganizationId(loginUser.getSubOrgId());
            area.setOrganizationId(loginUser.getOrgId());
            area.setCreatedOn(new Date());
            area.setCreatedBy(loginUser.getUserId());
            area.setIsDeleted(false);
            area.setModifiedBy(loginUser.getUserId());
            area.setModifiedOn(new Date());
            areaRepository.save(area);
            List<Area> areas = new ArrayList<>();
            areas.add(area);
            ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10013S);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setData(areas);
            baseResponse.setLogId(loginUser.getLogId());
            for (Integer i=1;i<=8;i++){
                zoneService.createZone(area,i);
            }
        } catch (Exception ex) {

            ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10013F);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setLogId(loginUser.getLogId());
            long endTime = System.currentTimeMillis();
            log.error("LogId:{} - AreaServicesImpl - createArea - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage() + (endTime - startTime),ex);
            return baseResponse;
        }
        long endTime = System.currentTimeMillis();
        log.info("LogId:{} - AreaServicesImpl - createArea - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId()," AUTO CREATE AREA METHOD EXECUTED :" + (endTime - startTime));
        return baseResponse;
    }

    @Override
    public BaseResponse<Area> saveArea(Integer storeId, String erpAreaId, String areaName) {

        long startTime = System.currentTimeMillis();
        log.info("LogId:{} - AreaServicesImpl - saveArea - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),"  SAVE AREA METHOD START");
        BaseResponse<Area> baseResponse = new BaseResponse<>();
        try {
            Area area = new Area();
            Optional<Store> store = storeRepository.findByIsDeletedAndId(false, storeId);
            if (store.isPresent()) {
                area.setStore(store.get());
            } else {
                ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10010E);
                baseResponse.setCode(responseMessage.getCode());
                baseResponse.setStatus(responseMessage.getStatus());
                baseResponse.setMessage(responseMessage.getMessage());
                baseResponse.setData(new ArrayList<>());
                baseResponse.setLogId(loginUser.getLogId());
                log.info("LogId:{} - AreaServicesImpl - saveArea - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage());
                return baseResponse;
            }
            Optional<List<Area>> areaList = areaRepository.findByIsDeleted(false);
            area.setAreaId(store.get().getStoreId() + "A0" + areaList.get().size() + 1);
            area.setAreaName(areaName);
            area.setErpAreaId(erpAreaId);
            area.setSubOrganizationId(loginUser.getSubOrgId());
            area.setOrganizationId(loginUser.getOrgId());
            area.setCreatedOn(new Date());
            area.setCreatedBy(loginUser.getUserId());
            area.setIsDeleted(false);
            area.setModifiedBy(loginUser.getUserId());
            area.setModifiedOn(new Date());
            areaRepository.save(area);
            List<Area> areas = new ArrayList<>();
            areas.add(area);
            ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10014S);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setData(areas);
            log.info("LogId:{} - AreaServicesImpl - saveArea - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage());
            baseResponse.setLogId(loginUser.getLogId());
        } catch (Exception ex) {
            ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10014F);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setLogId(loginUser.getLogId());
            long endTime = System.currentTimeMillis();
            log.error("LogId:{} - AreaServicesImpl - createArea - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage() + (endTime - startTime),ex);
            return baseResponse;
        }
        long endTime = System.currentTimeMillis();
        log.info("LogId:{} - AreaServicesImpl - createArea - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId()," SAVE AREA METHOD EXECUTED TIME :" + (endTime - startTime));
        return baseResponse;
    }

    @Override
    public BaseResponse<Area> getAllAreaByStore(List<Integer> storeId){
        long startTime = System.currentTimeMillis();
        log.info("LogId:{} - AreaServicesImpl - getAllAreaByStore - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),"  GET ALL AREA BY STORE METHOD START");
        BaseResponse<Area> baseResponse=new BaseResponse<>();
        try {
            List<Area> areas=areaRepository.findByIsDeletedAndStoreIdIn(false,storeId);

            ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10017S);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setData(areas);
            baseResponse.setLogId(loginUser.getLogId());
            log.info("LogId:{} - AreaServicesImpl - getAllAreaByStore - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage());
        }catch (Exception ex){
            ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10017F);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setLogId(loginUser.getLogId());
            long endTime = System.currentTimeMillis();
            log.error("LogId:{} - AreaServicesImpl - getAllAreaByStore - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage()+ (endTime - startTime),ex);
            return baseResponse;
        }
        long endTime = System.currentTimeMillis();
        log.info("LogId:{} - AreaServicesImpl - getAllAreaByStore - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId()," GET ALL AREA BY STORE METHOD EXECUTED TIME :" + (endTime - startTime));
        return baseResponse;
    }

    @Override
    public BaseResponse<Area> deleteAreaById(Integer areaId){
        long startTime = System.currentTimeMillis();
        log.info("LogId:{} - AreaServicesImpl - deleteAreaById - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),"  DELETE AREA BY ID METHOD START");

        BaseResponse<Area> baseResponse=new BaseResponse<>();
        try {
            Area area=areaRepository.findByIsDeletedAndId(false,areaId);
            area.setIsDeleted(true);
            List<Area> areas=new ArrayList<>();
            areas.add(area);
            List<Zone>zones =zoneRepository.findByIsDeletedAndAreaId(false,areaId);
            if(zones!=null && zones.size()!=0){
                ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10011E);
                baseResponse.setCode(responseMessage.getCode());
                baseResponse.setStatus(responseMessage.getStatus());
                baseResponse.setMessage(responseMessage.getMessage());
                baseResponse.setData(new ArrayList<>());
                log.info("LogId:{} - AreaServicesImpl - deleteAreaById - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage());
            }else {
                areaRepository.save(area);

                ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10015S);
                baseResponse.setCode(responseMessage.getCode());
                baseResponse.setStatus(responseMessage.getStatus());
                baseResponse.setMessage(responseMessage.getMessage());
                baseResponse.setData(areas);
                baseResponse.setLogId(loginUser.getLogId());
                log.info("LogId:{} - AreaServicesImpl - deleteAreaById - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage());
            }
        }catch (Exception ex){
            ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10015F);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setLogId(loginUser.getLogId());
            long endTime = System.currentTimeMillis();
            log.error("LogId:{} - AreaServicesImpl - deleteAreaById - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage()+ (endTime - startTime),ex);

        }
        long endTime = System.currentTimeMillis();
        log.info("LogId:{} - AreaServicesImpl - deleteAreaById - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId()," DELETE AREA BY ID METHOD EXECUTED TIME :" + (endTime - startTime));
        return baseResponse;
    }

    @Override
    public BaseResponse<AddressResponse> getAddressByPincodes(Integer pincode) {
        long startTime = System.currentTimeMillis();
        log.info("LogId:{} - AreaServicesImpl - getAddressByPincodes - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),"  GET ADDRESS BY PINCODE METHOD METHOD START");
        BaseResponse<AddressResponse> baseResponse = new BaseResponse<>();
        List<AddressResponse> uniqueAddresses = null;
        try {
            List<Address> addressList = addressRepository.findByPincode(pincode);
            uniqueAddresses = new ArrayList<>();
            Set<Integer> uniquePincodes = new HashSet<>();
            Set<String> uniqueStates = new HashSet<>();
            Set<String> uniqueDistricts = new HashSet<>();
            Set<String> uniqueSubDistricts = new HashSet<>();
            Set<String> uniqueTowns = new HashSet<>();
            Set<String> uniqueVillages = new HashSet<>();
            for (Address address : addressList) {
                uniquePincodes.add(address.getPincode());
                uniqueStates.add(address.getStateName());
                uniqueDistricts.add(address.getDistrictName());
                uniqueSubDistricts.add(address.getSubDistrictName());
                uniqueTowns.add(address.getTownName());
                uniqueVillages.add(address.getVillageName());
            }
            AddressResponse addressResponseDTO = new AddressResponse();
            addressResponseDTO.setPincode(uniquePincodes.stream().findFirst().orElse(null));
            addressResponseDTO.setStateName(new ArrayList<>(uniqueStates));
            addressResponseDTO.setDistrictName(new ArrayList<>(uniqueDistricts));
            addressResponseDTO.setSubDistrictName(new ArrayList<>(uniqueSubDistricts));
            addressResponseDTO.setTownName(new ArrayList<>(uniqueTowns));
            addressResponseDTO.setVillageName(new ArrayList<>(uniqueVillages));
            uniqueAddresses.add(addressResponseDTO);
            ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10016S);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());


            baseResponse.setData(uniqueAddresses);
            baseResponse.setLogId(loginUser.getLogId());
            log.info("LogId:{} - AreaServicesImpl - getAddressByPincodes - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage());


        } catch (Exception e) {
            ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10016F);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setData(new ArrayList<>());
            baseResponse.setLogId(loginUser.getLogId());
            long endTime = System.currentTimeMillis();
            log.error("LogId:{} - AreaServicesImpl - getAddressByPincodes - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage()+ (endTime - startTime),e);

        }
        long endTime = System.currentTimeMillis();
        log.info("LogId:{} - AreaServicesImpl - getAddressByPincodes - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId()," GET ADDRESS BY PINCODE METHOD EXECUTED TIME :" + (endTime - startTime));
        return baseResponse;
    }
}
