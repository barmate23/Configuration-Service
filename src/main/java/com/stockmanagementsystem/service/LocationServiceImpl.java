package com.stockmanagementsystem.service;

import com.itextpdf.text.pdf.PdfWriter;
import com.stockmanagementsystem.entity.*;
import com.itextpdf.text.Document;
import com.stockmanagementsystem.exception.ExceptionLogger;
import com.stockmanagementsystem.repository.*;
import com.stockmanagementsystem.request.LocationRequest;
import com.stockmanagementsystem.response.BaseResponse;
import com.stockmanagementsystem.response.ExcellHeaderValidatorResponse;
import com.stockmanagementsystem.response.ValidationResultResponse;
import com.stockmanagementsystem.utils.BarcodeGenerator;
import com.stockmanagementsystem.utils.ResponseKeyConstant;
import com.stockmanagementsystem.utils.ServiceConstants;
import com.stockmanagementsystem.validation.Validations;
import io.micrometer.core.instrument.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.*;

import static com.stockmanagementsystem.utils.GlobalMessages.getResponseMessages;

@Service
@Slf4j
public class LocationServiceImpl extends Validations implements LocationService{
    @Autowired
    LocationRepository locationRepository;

    @Autowired
    LoginUser loginUser;

    @Autowired
    ItemRepository itemRepository;
    @Autowired
    ZoneRepository zoneRepository;

    @Autowired
    AreaRepository areaRepository;

    @Autowired
    Validations validations;

    @Autowired
    StoreRepository storeRepository;

    @Autowired
    StoreDockMapperRepository storeDockMapperRepository;

    @Value("${baseFilePath}")
    private String baseFilePath;
    @Override
    public BaseResponse<Location> saveLocations(LocationRequest locationRequest) {
        long startTime = System.currentTimeMillis();
        log.info("LogId:{} - LocationServiceImpl - saveLocations - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId()," SAVE LOCATION METHOD START");
        BaseResponse<Location> baseResponse = new BaseResponse<>();
        Zone zone=null;
        try {
            Location location = new Location();
            location.setErpLocationId(locationRequest.getErpLocationId());
            location.setLevel(locationRequest.getLevel());
            location.setRow(locationRequest.getRow());
            location.setRackFloor(locationRequest.getRackFloor());
            location.setRackNo(locationRequest.getRackNo());
            location.setShelfNo(locationRequest.getShelfNo());
            location.setWidth(locationRequest.getWidth());
            location.setLength(locationRequest.getLength());
            location.setLocationType(locationRequest.getLocationType());
            location.setHeight(locationRequest.getHeight());
            location.setVolumeCuCm(locationRequest.getVolumeCuCm());
            location.setAreaSqCm(locationRequest.getAreaSqCm());
            location.setCarryingCapacity(locationRequest.getCarryingCapacity());
            Optional<Item> itemOptional = itemRepository.findByIsDeletedAndId(false, locationRequest.getItemId());
            if (itemOptional.isPresent()) {
                location.setItem(itemOptional.get());
            } else {
                ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10022E);
                baseResponse.setCode(responseMessage.getCode());
                baseResponse.setStatus(responseMessage.getStatus());
                baseResponse.setMessage(responseMessage.getMessage());
                baseResponse.setLogId(loginUser.getLogId());
                return baseResponse;
            }
            Optional<Store> store = storeRepository.findByIsDeletedAndId(false, locationRequest.getStoreId());
            Area area = null;
            if (locationRequest.getAreaId() != null) {
                area = areaRepository.findByIsDeletedAndId(false, locationRequest.getAreaId());
            } else {
                List<Area> areas = areaRepository.findByIsDeletedAndStoreId(false, locationRequest.getStoreId());
                if (areas.size() < 4) {
                    area = new Area();
                    area.setAreaId(validations.areaIdGeneration(locationRequest.getStoreId()));
                    if(validations.isValidPassword(locationRequest.getErpAreaId()) && validations.isValidPassword(locationRequest.getAreaName())){
                        area.setErpAreaId(locationRequest.getErpAreaId());
                        area.setAreaName(locationRequest.getAreaName());
                    }else {
                        ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10023E);
                        baseResponse.setCode(responseMessage.getCode());
                        baseResponse.setStatus(responseMessage.getStatus());
                        baseResponse.setMessage(responseMessage.getMessage());
                        baseResponse.setData(new ArrayList<>());
                        baseResponse.setLogId(loginUser.getLogId());
                        return baseResponse;
                    }
                    area.setStore(store.get());
                    area.setOrganizationId(loginUser.getOrgId());
                    area.setSubOrganizationId(loginUser.getSubOrgId());
                    area.setIsDeleted(false);
                    area.setModifiedOn(new Date());
                    area.setModifiedBy(loginUser.getUserId());
                    area.setCreatedBy(loginUser.getUserId());
                    area.setCreatedOn(new Date());
                    areaRepository.save(area);
                } else {
                    ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10025E);
                    baseResponse.setCode(responseMessage.getCode());
                    baseResponse.setStatus(responseMessage.getStatus());
                    baseResponse.setMessage(responseMessage.getMessage());
                    baseResponse.setLogId(loginUser.getLogId());
                    return baseResponse;
                }
            }
            Optional<Zone> zoneOptional = zoneRepository.findByIsDeletedAndId(false, locationRequest.getZoneId());
            if (locationRequest.getZoneId() != null) {
                if (zoneOptional.isPresent()) {
                    location.setZone(zoneOptional.get());
                    location.setLocationId(validations.locationIdGeneration(zoneOptional.get().getId()));
                } else {
                    ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10026E);
                    baseResponse.setCode(responseMessage.getCode());
                    baseResponse.setStatus(responseMessage.getStatus());
                    baseResponse.setMessage(responseMessage.getMessage());
                    baseResponse.setLogId(loginUser.getLogId());
                    return baseResponse;
                }
            } else {
                zone = new Zone();
                List<Zone> zones = zoneRepository.findByIsDeletedAndAreaId(false, area.getId());
                if (zones.size() < 8) {
                    zone.setArea(area);
                    if(validations.isValidPassword(locationRequest.getZoneName()) && validations.isValidPassword(locationRequest.getErpZoneId())){
                        zone.setZoneName(locationRequest.getZoneName());
                        zone.setErpZoneId(locationRequest.getErpZoneId());
                    }else {
                        ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10027E);
                        baseResponse.setCode(responseMessage.getCode());
                        baseResponse.setStatus(responseMessage.getStatus());
                        baseResponse.setMessage(responseMessage.getMessage());
                        baseResponse.setData(new ArrayList<>());
                        baseResponse.setLogId(loginUser.getLogId());
                        return baseResponse;
                    }
                    zone.setZoneId(validations.ZoneIdGeneration(area.getId()));
                    zone.setCreatedBy(loginUser.getUserId());
                    zone.setOrganizationId(loginUser.getOrgId());
                    zone.setSubOrganizationId(loginUser.getSubOrgId());
                    zone.setIsDeleted(false);
                    zone.setCreatedOn(new Date());
                    zone.setModifiedBy(loginUser.getUserId());
                    zone.setModifiedOn(new Date());
                    zone.setIsDeleted(false);
                    zoneRepository.save(zone);
                    location.setZone(zone);
                    location.setLocationId(validations.locationIdGeneration(zone.getId()));
                } else {
                    ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10024E);
                    baseResponse.setCode(responseMessage.getCode());
                    baseResponse.setStatus(responseMessage.getStatus());
                    baseResponse.setMessage(responseMessage.getMessage());
                    baseResponse.setLogId(loginUser.getLogId());
                    return baseResponse;
                }
            }
                location.setOrganizationId(loginUser.getOrgId());
                location.setSubOrganizationId(loginUser.getSubOrgId());
                location.setIsDeleted(false);
                location.setCreatedBy(loginUser.getUserId());
                location.setCreatedOn(new Date());
            List<Location>locationList=new ArrayList<>();
            if(zoneOptional.isPresent()){
                locationList = locationRepository.findByIsDeletedAndZoneId(false, zoneOptional.get().getId());
            }else {
                locationList = locationRepository.findByIsDeletedAndZoneId(false, zone.getId());
            }
            if(locationList!=null && locationList.size()<64){
                    locationRepository.save(location);
                }else {
                ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10028E);
                baseResponse.setCode(responseMessage.getCode());
                baseResponse.setStatus(responseMessage.getStatus());
                baseResponse.setMessage(responseMessage.getMessage());
                baseResponse.setLogId(loginUser.getLogId());
                    return baseResponse;
                }
                List<Location> locations = new ArrayList<>();
                locations.add(location);
            ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10038S);
                baseResponse.setCode(responseMessage.getCode());
                baseResponse.setStatus(responseMessage.getStatus());
                baseResponse.setMessage(responseMessage.getMessage());
                baseResponse.setData(locations);
                baseResponse.setLogId(loginUser.getLogId());
            log.info("LogId:{} - LocationServiceImpl - saveLocations - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage());

            return baseResponse;
            }catch(Exception e){
            ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10037F);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setLogId(loginUser.getLogId());
            long endTime = System.currentTimeMillis();
            log.error("LogId:{} - LocationServiceImpl - saveLocations - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage() + (endTime - startTime),e);
            log.error("",e);
            }
        long endTime = System.currentTimeMillis();
        log.info("LogId:{} - LocationServiceImpl - saveLocations - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId()," SAVE LOCATIONS TIME " + (endTime - startTime));
        return baseResponse;
    }

    @Override
    public BaseResponse<Location> updateLocations(Integer id, LocationRequest locationRequest) {
        long startTime = System.currentTimeMillis();
        log.info("LogId:{} - LocationServiceImpl - updateLocations - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), " UPDATE LOCATION METHOD START");
        BaseResponse<Location> baseResponse = new BaseResponse<>();
        try {
            Location location = locationRepository.findByIsDeletedAndId(false, id);
            location.setErpLocationId(locationRequest.getErpLocationId());
            location.setLevel(locationRequest.getLevel());
            location.setRow(locationRequest.getRow());
            location.setRackFloor(locationRequest.getRackFloor());
            location.setRackNo(locationRequest.getRackNo());
            location.setShelfNo(locationRequest.getShelfNo());
            location.setWidth(locationRequest.getWidth());
            location.setLength(locationRequest.getLength());
            location.setLocationType(locationRequest.getLocationType());
            location.setHeight(locationRequest.getHeight());
            location.setVolumeCuCm(locationRequest.getVolumeCuCm());
            location.setAreaSqCm(locationRequest.getAreaSqCm());
            location.setCarryingCapacity(locationRequest.getCarryingCapacity());
            location.setItemQty(locationRequest.getItemQty());
            Optional<Item> itemOptional = itemRepository.findByIsDeletedAndId(false, locationRequest.getItemId());
            if (itemOptional.isPresent()) {
                location.setItem(itemOptional.get());
            } else {
                ResponseMessage responseMessage = getResponseMessages(ResponseKeyConstant.UPLD10029E);
                baseResponse.setCode(responseMessage.getCode());
                baseResponse.setStatus(responseMessage.getStatus());
                baseResponse.setMessage(responseMessage.getMessage());
                long endTime = System.currentTimeMillis();
                log.info("LogId:{} - LocationServiceImpl - updateLocations - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), ResponseKeyConstant.SPACE + responseMessage.getMessage() + (endTime - startTime));
                return baseResponse;
            }
            if (locationRequest.getZoneId() != null) {
                Optional<Zone> zoneOptional = zoneRepository.findByIsDeletedAndId(false, locationRequest.getZoneId());
                if (zoneOptional.isPresent()) {
                    location.setZone(zoneOptional.get());
                    zoneOptional.get().setZoneName(locationRequest.getZoneName());
                    zoneOptional.get().setErpZoneId(locationRequest.getErpZoneId());
                    zoneRepository.save(zoneOptional.get());
                } else {
                    ResponseMessage responseMessage = getResponseMessages(ResponseKeyConstant.UPLD10030E);
                    baseResponse.setCode(responseMessage.getCode());
                    baseResponse.setStatus(responseMessage.getStatus());
                    baseResponse.setMessage(responseMessage.getMessage());
                    baseResponse.setLogId(loginUser.getLogId());
                    long endTime = System.currentTimeMillis();
                    log.info("LogId:{} - LocationServiceImpl - updateLocations - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), ResponseKeyConstant.SPACE + responseMessage.getMessage() + (endTime - startTime));
                    return baseResponse;
                }
            }
            if (locationRequest.getAreaId() != null) {
                Area area = areaRepository.findByIsDeletedAndId(false, locationRequest.getAreaId());
                area.setAreaName(locationRequest.getAreaName());
                area.setErpAreaId(locationRequest.getErpAreaId());
                areaRepository.save(area);
            }
            location.setOrganizationId(loginUser.getOrgId());
            location.setSubOrganizationId(loginUser.getSubOrgId());
            location.setIsDeleted(false);
            location.setCreatedBy(loginUser.getUserId());
            location.setCreatedOn(new Date());
            locationRepository.save(location);
            List<Location> locations = new ArrayList<>();
            locations.add(location);
            ResponseMessage responseMessage = getResponseMessages(ResponseKeyConstant.UPLD10039S);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setData(locations);
            baseResponse.setLogId(loginUser.getLogId());
            log.info("LogId:{} - LocationServiceImpl - updateLocations - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), ResponseKeyConstant.SPACE + responseMessage.getMessage());
            return baseResponse;
        } catch (Exception e) {
            ResponseMessage responseMessage = getResponseMessages(ResponseKeyConstant.UPLD10038F);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setLogId(loginUser.getLogId());
            long endTime = System.currentTimeMillis();
            log.error("LogId:{} - LocationServiceImpl - updateLocations - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), ResponseKeyConstant.SPACE + responseMessage.getMessage() + (endTime - startTime), e);

            return baseResponse;
        }
    }
        @Override
        public BaseResponse<Location> createLocations (Zone zone, Integer sq){
            long startTime = System.currentTimeMillis();
            log.info("LogId:{} - LocationServiceImpl - createLocations - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), " AUTO CREATE LOCATION METHOD START");
            BaseResponse<Location> baseResponse = new BaseResponse<>();
            try {
                Location location = new Location();
                location.setLocationId(String.format("%s-L%03d", zone.getZoneId(), sq));
                location.setZone(zone);
                location.setCarryingCapacity(10);
                location.setItemQty(0);
                location.setRemainingItemQty(10);
                location.setOrganizationId(loginUser.getOrgId());
                location.setSubOrganizationId(loginUser.getSubOrgId());
                location.setIsDeleted(false);
                location.setCreatedBy(loginUser.getUserId());
                location.setCreatedOn(new Date());
                locationRepository.save(location);
                List<Location> locations = new ArrayList<>();
                locations.add(location);
                ResponseMessage responseMessage = getResponseMessages(ResponseKeyConstant.UPLD10040S);
                baseResponse.setCode(responseMessage.getCode());
                baseResponse.setStatus(responseMessage.getStatus());
                baseResponse.setMessage(responseMessage.getMessage());
                baseResponse.setData(locations);
                baseResponse.setLogId(loginUser.getLogId());
                log.info("LogId:{} - LocationServiceImpl - createLocations - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), ResponseKeyConstant.SPACE + responseMessage.getMessage());
                return baseResponse;
            } catch (Exception e) {
                ResponseMessage responseMessage = getResponseMessages(ResponseKeyConstant.UPLD10039F);
                baseResponse.setCode(responseMessage.getCode());
                baseResponse.setStatus(responseMessage.getStatus());
                baseResponse.setMessage(responseMessage.getMessage());
                baseResponse.setLogId(loginUser.getLogId());
                long endTime = System.currentTimeMillis();
                log.error("LogId:{} - LocationServiceImpl - createLocations - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), ResponseKeyConstant.SPACE + responseMessage.getMessage() + (endTime - startTime), e);
            }
            long endTime = System.currentTimeMillis();
            log.info("LogId:{} - LocationServiceImpl - createLocations - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), " CREATE LOCATIONS TIME " + (endTime - startTime));
            return baseResponse;
        }
        @Override
        public BaseResponse<Location> getAllLocationWithPagination (Integer pageNo, Integer pageSize){
            long startTime = System.currentTimeMillis();
            log.info("LogId:{} - LocationServiceImpl - getAllLocationWithPagination - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), " GET ALL LOCATION WITH PAGINATION START");
            BaseResponse<Location> baseResponse = new BaseResponse<>();
            List<Location> locations = new ArrayList<>();
            try {
                Page<Location> pageResult = null;
                final Pageable pageable = (Pageable) PageRequest.of((int) pageNo, (int) pageSize);
                pageResult = this.locationRepository.findByIsDeletedAndSubOrganizationIdOrderByIdAsc(false, loginUser.getSubOrgId(), pageable);
                baseResponse.setTotalPageCount(pageResult.getTotalPages());
                locations = (pageResult.getContent());
                baseResponse.setTotalRecordCount(pageResult.getTotalElements());

                ResponseMessage responseMessage = getResponseMessages(ResponseKeyConstant.UPLD10041S);
                baseResponse.setCode(responseMessage.getCode());
                baseResponse.setStatus(responseMessage.getStatus());
                baseResponse.setMessage(responseMessage.getMessage());
                baseResponse.setData(locations);
                baseResponse.setLogId(loginUser.getLogId());
                log.info("LogId:{} - LocationServiceImpl - getAllLocationWithPagination - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), ResponseKeyConstant.SPACE + responseMessage.getMessage());
                return baseResponse;
            } catch (Exception ex) {
                ResponseMessage responseMessage = getResponseMessages(ResponseKeyConstant.UPLD10040F);
                baseResponse.setCode(responseMessage.getCode());
                baseResponse.setStatus(responseMessage.getStatus());
                baseResponse.setMessage(responseMessage.getMessage());
                baseResponse.setLogId(loginUser.getLogId());
                long endTime = System.currentTimeMillis();
                log.error("LogId:{} - LocationServiceImpl - getAllLocationWithPagination - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), ResponseKeyConstant.SPACE + responseMessage.getMessage() + (endTime - startTime), ex);
            }
            long endTime = System.currentTimeMillis();
            log.info("LogId:{} - LocationServiceImpl - getAllLocationWithPagination - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), " GET ALL LOCATION WITH PAGINATION TIME " + (endTime - startTime));
            return baseResponse;
        }

        @Override
        public BaseResponse<Location> getLocationWithFilter
        (List < Integer > storeId, List < Integer > areaId, List < Integer > zoneId, List < Integer > locationId, List < Integer > itemId, Integer
        pageNo, Integer pageSize){
            long startTime = System.currentTimeMillis();
            log.info("LogId:{} - LocationServiceImpl - getLocationWithFilter - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), " GET LOCATION WITH FILTER START");

            BaseResponse<Location> baseResponse = new BaseResponse<>();
            List<Location> locations = new ArrayList<>();
            try {
                final Pageable pageable = (Pageable) PageRequest.of((int) pageNo, (int) pageSize);
                Page<Location> pageResult = null;

                if (storeId != null) {
                    pageResult = this.locationRepository.findByIsDeletedAndZoneAreaStoreIdInOrderByIdAsc(false, storeId, pageable);
                } else if (storeId != null) {
                    pageResult = this.locationRepository.findByIsDeletedAndZoneAreaStoreIdInOrderByIdAsc(false, storeId, pageable);
                } else if (areaId != null) {
                    pageResult = this.locationRepository.findByIsDeletedAndZoneAreaIdInOrderByIdAsc(false, areaId, pageable);
                } else if (zoneId != null) {
                    pageResult = this.locationRepository.findByIsDeletedAndZoneIdInOrderByIdAsc(false, zoneId, pageable);
                } else if (locationId != null) {
                    pageResult = this.locationRepository.findByIsDeletedAndIdInOrderByIdAsc(false, locationId, pageable);
                } else if (locationId != null && itemId != null) {
                    pageResult = this.locationRepository.findByIsDeletedAndIdInOrItemIdInOrderByIdAsc(false, locationId, itemId, pageable);
                } else {
                    pageResult = this.locationRepository.findByIsDeletedAndItemIdInOrderByIdAsc(false, itemId, pageable);
                }
                baseResponse.setTotalPageCount(pageResult.getTotalPages());
                locations = (pageResult.getContent());
                baseResponse.setTotalRecordCount(pageResult.getTotalElements());

                ResponseMessage responseMessage = getResponseMessages(ResponseKeyConstant.UPLD10042S);
                baseResponse.setCode(responseMessage.getCode());
                baseResponse.setStatus(responseMessage.getStatus());
                baseResponse.setMessage(responseMessage.getMessage());
                baseResponse.setData(locations);
                baseResponse.setLogId(loginUser.getLogId());
                log.info("LogId:{} - LocationServiceImpl - getLocationWithFilter - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), ResponseKeyConstant.SPACE + responseMessage.getMessage());
                return baseResponse;
            } catch (Exception ex) {
                ResponseMessage responseMessage = getResponseMessages(ResponseKeyConstant.UPLD10041F);
                baseResponse.setCode(responseMessage.getCode());
                baseResponse.setStatus(responseMessage.getStatus());
                baseResponse.setMessage(responseMessage.getMessage());
                baseResponse.setLogId(loginUser.getLogId());
                long endTime = System.currentTimeMillis();
                log.error("LogId:{} - LocationServiceImpl - getLocationWithFilter - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), ResponseKeyConstant.SPACE + responseMessage.getMessage() + (endTime - startTime), ex);
            }
            long endTime = System.currentTimeMillis();
            log.info("LogId:{} - LocationServiceImpl - getLocationWithFilter - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), " GET LOCATION WITH FILTER TIME " + (endTime - startTime));
            return baseResponse;
        }
        @Override
        public BaseResponse<Location> getAllLocation (Integer zoneId){
            long startTime = System.currentTimeMillis();
            log.info("LogId:{} - LocationServiceImpl - getAllLocation - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), " GET ALL LOCATION START");
            BaseResponse<Location> baseResponse = new BaseResponse<>();
            try {
                List<Location> locations = locationRepository.findByIsDeletedAndSubOrganizationIdAndZoneId(false, loginUser.getSubOrgId(), zoneId);
                ResponseMessage responseMessage = getResponseMessages(ResponseKeyConstant.UPLD10043S);
                baseResponse.setCode(responseMessage.getCode());
                baseResponse.setStatus(responseMessage.getStatus());
                baseResponse.setMessage(responseMessage.getMessage());

                baseResponse.setCode(1);
                baseResponse.setStatus(200);
                baseResponse.setData(locations);
                baseResponse.setMessage(" LOCATIONS LIST FETCHED SUCCESSFULLY ");
                baseResponse.setLogId(loginUser.getLogId());
                log.info("LogId:{} - LocationServiceImpl - getAllLocation - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), ResponseKeyConstant.SPACE + responseMessage.getMessage());
                return baseResponse;
            } catch (Exception ex) {
                ResponseMessage responseMessage = getResponseMessages(ResponseKeyConstant.UPLD10042F);
                baseResponse.setCode(responseMessage.getCode());
                baseResponse.setStatus(responseMessage.getStatus());
                baseResponse.setMessage(responseMessage.getMessage());

                baseResponse.setCode(0);
                baseResponse.setStatus(500);
                baseResponse.setMessage(" FAILED TO FETCHED LOCATIONS LIST ");
                baseResponse.setLogId(loginUser.getLogId());
                long endTime = System.currentTimeMillis();
                log.error("LogId:{} - LocationServiceImpl - getAllLocation - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), ResponseKeyConstant.SPACE + responseMessage.getMessage() + (endTime - startTime), ex);

            }
            long endTime = System.currentTimeMillis();
            log.info("LogId:{} - LocationServiceImpl - getAllLocation - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), " GET ALL LOCATION TIME " + (endTime - startTime));
            return baseResponse;
        }
        @Override
        public BaseResponse<Location> deleteLocationById (Integer locationId){
            long startTime = System.currentTimeMillis();
            log.info("LogId:{} - LocationServiceImpl - deleteLocationById - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), " DELETE LOCATION BY ID START");
            BaseResponse<Location> baseResponse = new BaseResponse<>();
            try {
                Location location = locationRepository.findByIsDeletedAndSubOrganizationIdAndId(false, loginUser.getSubOrgId(), locationId);
                if (location.getItem() != null) {
                    ResponseMessage responseMessage = getResponseMessages(ResponseKeyConstant.UPLD10031E);
                    baseResponse.setCode(responseMessage.getCode());
                    baseResponse.setStatus(responseMessage.getStatus());
                    baseResponse.setMessage(responseMessage.getMessage());
                    baseResponse.setData(new ArrayList<>());
                    baseResponse.setLogId(loginUser.getLogId());
                    return baseResponse;
                }
                location.setIsDeleted(true);
                List<Location> locations = new ArrayList<>();
                locations.add(location);
                locationRepository.save(location);
                ResponseMessage responseMessage = getResponseMessages(ResponseKeyConstant.UPLD10044S);
                baseResponse.setCode(responseMessage.getCode());
                baseResponse.setStatus(responseMessage.getStatus());
                baseResponse.setMessage(responseMessage.getMessage());
                baseResponse.setData(locations);
                baseResponse.setLogId(loginUser.getLogId());
                log.info("LogId:{} - LocationServiceImpl - deleteLocationById - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), ResponseKeyConstant.SPACE + responseMessage.getMessage());
                return baseResponse;
            } catch (Exception ex) {
                ResponseMessage responseMessage = getResponseMessages(ResponseKeyConstant.UPLD10043F);
                baseResponse.setCode(responseMessage.getCode());
                baseResponse.setStatus(responseMessage.getStatus());
                baseResponse.setMessage(responseMessage.getMessage());
                baseResponse.setLogId(loginUser.getLogId());
                long endTime = System.currentTimeMillis();
                log.error("LogId:{} - LocationServiceImpl - deleteLocationById - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), ResponseKeyConstant.SPACE + responseMessage.getMessage() + (endTime - startTime), ex);
            }
            long endTime = System.currentTimeMillis();
            log.info("LogId:{} - LocationServiceImpl - deleteLocationById - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), " DELETE LOCATION BY ID TIME " + (endTime - startTime));
            return baseResponse;
        }
        @Override
        public byte[] getLocationBarcode (Integer storeId, Integer areaId, Integer zoneId, String locationId){
            try {
                long startTime = System.currentTimeMillis();
                List<String> locationIds = new ArrayList<>();
                if (!StringUtils.isEmpty(locationId)) {
                    locationIds.add(locationId);
                } else {
                    List<Location> locationList = null;

                    if (storeId != null && storeId > 0) {
                        locationList = this.locationRepository.findByIsDeletedAndZoneAreaStoreId(false, storeId);
                    } else if (areaId != null && areaId > 0) {
                        locationList = this.locationRepository.findByIsDeletedAndZoneAreaId(false, areaId);
                    } else if (zoneId != null && zoneId > 0) {
                        locationList = this.locationRepository.findByIsDeletedAndZoneId(false, zoneId);
                    } else {
                        locationList = locationRepository.findByIsDeletedAndSubOrganizationId(false, loginUser.getSubOrgId());
                    }
                    for (Location location : locationList) {
                        if (!StringUtils.isEmpty(location.getLocationId())) {
                            locationIds.add(location.getLocationId());
                        }
                    }
                }
                log.info(String.valueOf(loginUser.getLogId() + " DOWNLOAD DOCKS BARCODE PDF "));
                Document document = new Document();
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                PdfWriter.getInstance(document, outputStream);
                document.open();
                for (String locId : locationIds) {
                    byte[] barcodeImageBytes = BarcodeGenerator.generateBarcode(locId);
                    com.itextpdf.text.Image locationImage = com.itextpdf.text.Image.getInstance(barcodeImageBytes);
                    document.add(locationImage);
                }

                document.close();
                long endTime = System.currentTimeMillis();
                log.info(String.valueOf(loginUser.getLogId() + "SUCCESSFULLY DOWNLOAD DOCKS BARCODE PDF TIME" + (endTime - startTime)));
                return outputStream.toByteArray();
            } catch (Exception e) {
                log.info(String.valueOf(loginUser.getLogId() + "FAILED DOWNLOAD DOCK BARCODE PDF"), e);
                e.printStackTrace();
                return null;
            }
        }
        @Override
        public ResponseEntity<byte[]> downloadExcelLocationFile (Integer zoneId){
            HttpHeaders headers = new HttpHeaders();
            byte[] excelBytes = null;
            try {
                String templateFilePath = baseFilePath + ServiceConstants.LOCATION_FILE;
                FileInputStream fis = new FileInputStream(templateFilePath);
                Workbook workbook = new XSSFWorkbook(fis);
                Sheet sheet = workbook.getSheetAt(ServiceConstants.SHEET_INDEX);
                List<Location> locations = locationRepository.findByIsDeletedAndSubOrganizationIdAndZoneId(false, loginUser.getSubOrgId(), zoneId);
                for (int i = 0; i < locations.size(); i++) {
                    Row row = sheet.getRow(i + 2);
                    if (row == null) {
                        row = sheet.createRow(i + 2);
                    }
                    Cell storeId = row.getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    Cell storeName = row.getCell(1, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    Cell erpAreaId = row.getCell(2, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    Cell areaId = row.getCell(3, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    Cell areaName = row.getCell(4, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    Cell erpZoneId = row.getCell(5, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    Cell zoneIds = row.getCell(6, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    Cell zoneName = row.getCell(7, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    Cell erpLocationId = row.getCell(8, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    Cell locationIds = row.getCell(9, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    Cell itemId = row.getCell(10, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    Cell itemName = row.getCell(11, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    Cell locationType = row.getCell(12, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    Cell rows = row.getCell(13, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    Cell rackFloor = row.getCell(14, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    Cell rackNo = row.getCell(15, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    Cell shelfNo = row.getCell(16, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    Cell length = row.getCell(17, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    Cell width = row.getCell(18, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    Cell height = row.getCell(19, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    Cell area = row.getCell(20, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    Cell volume = row.getCell(21, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    Cell carryingCapacityKg = row.getCell(22, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    storeId.setCellValue(locations.get(i).getZone().getArea().getStore().getStoreId());
                    storeName.setCellValue(locations.get(i).getZone().getArea().getStore().getStoreName());
                    if (locations.get(i).getZone().getArea().getErpAreaId() != null) {
                        erpAreaId.setCellValue(locations.get(i).getZone().getArea().getErpAreaId());
                    }
                    if (locations.get(i).getZone().getArea().getAreaName() != null) {
                        areaName.setCellValue(locations.get(i).getZone().getArea().getAreaName());
                    }
                    areaId.setCellValue(locations.get(i).getZone().getArea().getAreaId());
                    if (locations.get(i).getZone().getErpZoneId() != null) {
                        erpZoneId.setCellValue(locations.get(i).getZone().getErpZoneId());
                    }
                    if (locations.get(i).getZone().getZoneName() != null) {
                        zoneName.setCellValue(locations.get(i).getZone().getZoneName());
                    }
                    if (locations.get(i).getZone().getZoneId() != null) {
                        zoneIds.setCellValue(locations.get(i).getZone().getZoneId());
                    }
                    locationIds.setCellValue(locations.get(i).getLocationId());

                    if (locations.get(i).getErpLocationId() != null) {
                        erpLocationId.setCellValue(locations.get(i).getErpLocationId());
                    }

                    if (locations.get(i).getItem() != null) {
                        itemId.setCellValue(locations.get(i).getItem().getItemId());
                        itemName.setCellValue(locations.get(i).getItem().getName());
                    }
                    if (locations.get(i).getLocationType() != null) {
                        locationType.setCellValue(locations.get(i).getLocationType());
                    }
                    if (locations.get(i).getRow() != null) {
                        rows.setCellValue(locations.get(i).getRow());
                    }
                    if (locations.get(i).getRackFloor() != null) {
                        rackFloor.setCellValue(locations.get(i).getRackFloor());
                    }
                    if (locations.get(i).getRackNo() != null) {
                        rackNo.setCellValue(locations.get(i).getRackNo());
                    }
                    if (locations.get(i).getShelfNo() != null) {
                        shelfNo.setCellValue(locations.get(i).getShelfNo());
                    }
                    if (locations.get(i).getLength() != null) {
                        length.setCellValue(locations.get(i).getLength());
                    }
                    if (locations.get(i).getWidth() != null) {
                        width.setCellValue(locations.get(i).getWidth());
                    }
                    if (locations.get(i).getHeight() != null) {
                        height.setCellValue(locations.get(i).getHeight());
                    }
                    if (locations.get(i).getAreaSqCm() != null) {
                        area.setCellValue(locations.get(i).getAreaSqCm());
                    }
                    if (locations.get(i).getVolumeCuCm() != null) {
                        volume.setCellValue(locations.get(i).getVolumeCuCm());
                    }
                    if (locations.get(i).getCarryingCapacity() != null) {
                        carryingCapacityKg.setCellValue(locations.get(i).getCarryingCapacity());
                    }

                }
                // Write the workbook to a ByteArrayOutputStream
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                workbook.write(outputStream);
                workbook.close();
                // Convert ByteArrayOutputStream to byte array
                excelBytes = outputStream.toByteArray();
                // Set response headers for downloading the file
                headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
                headers.setContentDispositionFormData("attachment", "location.xlsx");
                log.info("FileController----downloadExcelFile Method Executed");
            } catch (Exception e) {
                log.error("FileController----downloadExcelFile Method Executed", e);
            }
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(excelBytes);

        }
        @Override
        public ResponseEntity<BaseResponse> uploadLocationDetail (MultipartFile file, Integer zoneId) throws IOException
        {
            try {
                long startTime = System.currentTimeMillis();
                String type = "Location";

                log.info(String.valueOf(new StringBuilder().append(loginUser.getLogId()).append(ServiceConstants.SPACE).append(ServiceConstants.UPLOAD_LOCATION_DETAIL_METHOD_STARTED)));
                // Read the Excel file and perform validation
                Workbook workbook = WorkbookFactory.create(file.getInputStream());
                Sheet sheet = workbook.getSheetAt(ServiceConstants.SHEET_INDEX); // Assuming the data is in the first sheet

                List<Location> locations = new ArrayList<>();
                List<ValidationResultResponse> resultResponses = new ArrayList<>();
                List<String> headerNames = new ArrayList<>();
                Integer count = 0;
                List<String> expectedColumns = Arrays.asList(
                        ServiceConstants.STORE_ID,
                        ServiceConstants.STORE_NAME,
                        ServiceConstants.ERP_AREA_ID,
                        ServiceConstants.AREA_ID,
                        ServiceConstants.AREA_NAME,
                        ServiceConstants.ERP_ZONE_ID,
                        ServiceConstants.Zone_ID,
                        ServiceConstants.Zone_NAME,
                        ServiceConstants.ERP_LOCATION_ID,
                        ServiceConstants.LOCATION_ID,
                        ServiceConstants.ITEM_ID,
                        ServiceConstants.ITEM_NAME,
                        ServiceConstants.LOCATION_TYPE,
                        ServiceConstants.ROW,
                        ServiceConstants.RACK_FLOOR,
                        ServiceConstants.RACK_NO,
                        ServiceConstants.SHELF_NO,
                        ServiceConstants.LENGTH,
                        ServiceConstants.WIDTH,
                        ServiceConstants.HEIGHT,
                        ServiceConstants.AREA_SQ_CM,
                        ServiceConstants.VOLUME_CU_CM,
                        ServiceConstants.CARRYING_CAPACITY
                );
                List<ExcellHeaderValidatorResponse> excellHeaderValidatorResponse = validateExcelHeader(sheet, expectedColumns);
                if (!excellHeaderValidatorResponse.get(0).getIsValid()) {
                    return ResponseEntity.ok(new BaseResponse<>(ServiceConstants.INTERNAL_SERVER_ERROR, excellHeaderValidatorResponse.get(0).getErrorMessage(), excellHeaderValidatorResponse, ServiceConstants.ERROR_CODE, loginUser.getLogId()));
                }
                Row headerRow = sheet.getRow(1);
                for (int cellIndex = 0; cellIndex < headerRow.getLastCellNum(); cellIndex++) {
                    Cell headerCell = headerRow.getCell(cellIndex, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                    if (headerCell != null) {
                        String headerName = headerCell.getStringCellValue();
                        headerNames.add(headerName);
                    }
                }
                for (Row data : sheet) {
                    int emptyCellCount = 0;
                    for (int i = 0; i < data.getLastCellNum(); i++) {
                        Cell emptyCell = data.getCell(i, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                        if (emptyCell == null) {
                            emptyCellCount++;
                        }
                    }
                    if (data.getLastCellNum() != emptyCellCount) {
                        // Assuming the data starts from the third row
                        if (data.getRowNum() <= ServiceConstants.LOCATION_COLUMN_HEADER_ROW_INDEX) {
                            // Skip the header row
                            continue;
                        }
                        String storeId = getCellStringValue(data, 0, resultResponses, type, headerNames);
                        String storeName = getCellStringValue(data, 1, resultResponses, type, headerNames);
                        String erpAreaId = getCellStringValue(data, 2, resultResponses, type, headerNames);
                        String areaId = getCellStringValue(data, 3, resultResponses, type, headerNames);
                        String areaName = getCellStringValue(data, 4, resultResponses, type, headerNames);
                        String erpZoneId = getCellStringValue(data, 5, resultResponses, type, headerNames);
                        String zoneIds = getCellStringValue(data, 6, resultResponses, type, headerNames);
                        String zoneName = getCellStringValue(data, 7, resultResponses, type, headerNames);
                        String erpLocationId = getCellStringValue(data, 8, resultResponses, type, headerNames);
                        String locationId = getCellStringValue(data, 9, resultResponses, type, headerNames);
                        String itemId = getCellStringValue(data, 10, resultResponses, type, headerNames);
                        String itemName = getCellStringValue(data, 11, resultResponses, type, headerNames);
                        String locationType = getCellStringValue(data, 12, resultResponses, type, headerNames);
                        String row = getCellStringValue(data, 13, resultResponses, type, headerNames);
                        String rackFloor = getCellStringValue(data, 14, resultResponses, type, headerNames);
                        String rackNo = getCellStringValue(data, 15, resultResponses, type, headerNames);
                        String shelfNo = getCellStringValue(data, 16, resultResponses, type, headerNames);
                        Float length = getCellFloatValue(data, 17, resultResponses, type, headerNames);
                        Float width = getCellFloatValue(data, 18, resultResponses, type, headerNames);
                        Float height = getCellFloatValue(data, 19, resultResponses, type, headerNames);
                        Float area = getCellFloatValue(data, 20, resultResponses, type, headerNames);
                        Float volume = getCellFloatValue(data, 21, resultResponses, type, headerNames);
                        Integer carryingCapacityKg = getCellIntegerValue(data, 22, resultResponses, type, headerNames);
                        // Create a new Location object and set its properties
//                    Area areas =areaRepository.findByIsDeletedAndSubOrganizationIdAndAreaIdAndStoreStoreId(false,loginUser,areaId,storeId);
//
//                    areas.setErpAreaId(erpAreaId);
//                    areas.setAreaName(areaName);
//                    Optional<Store> store = storeRepository.findByIsDeletedAndStoreId(false, storeId);
//                    if (!store.isEmpty()) {
//                        areas.setStore(store.get());
//                    }else {
//                        resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), headerNames.get(ServiceConstants.CELL_INDEX_0), "This Store Id not Present in Database"));
//                    }
//                    areas.setIsDeleted(false);
//                    areas.setCreatedBy(loginUser.getUserId());
//                    areas.setCreatedOn(new Date());
//                    Optional<Area> areaOptional=areaRepository.findByIsDeletedAndAreaId(false,areaId);
//                    if(areaOptional.isEmpty()){
//                        areaRepository.save(areas);
//                    }
//                    Zone zone = new Zone();
//                    zone.setZoneId(zoneIds);
//                    if(areaOptional.isPresent()){
//                        zone.setArea(areaOptional.get());
//                    } else if (areas.getId()!=null) {
//                        zone.setArea(areas);
//                    } else {
//                        resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), headerNames.get(ServiceConstants.CELL_INDEX_6), "This Store Id not Present in Database"));
//                    }
//                    zone.setErpZoneId(erpZoneId);
//                    zone.setZoneName(zoneName);
//                    zone.setOrganizationId(loginUser.getOrgId());
//                    zone.setSubOrganizationId(loginUser.getOrgId());
//                    zone.setIsDeleted(false);
//                    zone.setCreatedBy(loginUser.getUserId());
//                    zone.setCreatedOn(new Date());
//
//                    if (zoneOptional.isEmpty()){
//                        zoneRepository.save(zone);
//                    }
                        // Create a new Location object and set its properties

                        Optional<Zone> zoneOptional = zoneRepository.findByIsDeletedAndSubOrganizationIdAndIdAndZoneId(false, loginUser.getSubOrgId(), zoneId, zoneIds);
                        if (zoneOptional.isPresent()) {
                            Optional<Location> optionalLocation = locationRepository.findByIsDeletedAndSubOrganizationIdAndLocationIdAndZoneZoneId(false, loginUser.getSubOrgId(), locationId, zoneIds);
                            if (optionalLocation.isPresent()) {
                                optionalLocation.get().setLocationId(locationId);
                                optionalLocation.get().setErpLocationId(erpLocationId);
                                optionalLocation.get().setRow(row);
                                optionalLocation.get().setRackFloor(rackFloor);
                                optionalLocation.get().setRackNo(rackNo);
                                optionalLocation.get().setShelfNo(shelfNo);
                                Optional<Item> itemOption = itemRepository.findByIsDeletedAndSubOrganizationIdAndItemCode(false, loginUser.getSubOrgId(), itemId);

                                if (itemOption.isPresent()) {
                                    StoreDockMapper storeDockMapper = storeDockMapperRepository.findByIsDeletedAndSubOrganizationIdAndStoreIdAndDockId(false, loginUser.getSubOrgId(), optionalLocation.get().getZone().getArea().getStore().getId(), itemOption.get().getDockId().getId());
                                    if (storeDockMapper != null) {
                                        optionalLocation.get().setItem(itemOption.get());
                                    } else {
                                        resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), headerNames.get(ServiceConstants.CELL_INDEX_10), "This Item not belong in this dock"));
                                    }

                                } else {
                                    resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), headerNames.get(ServiceConstants.CELL_INDEX_10), "This Item Id not Present in Database"));
                                }
                                optionalLocation.get().setLocationType(locationType);
                                optionalLocation.get().setLength(length);
                                optionalLocation.get().setHeight(height);
                                optionalLocation.get().setWidth(width);
                                optionalLocation.get().setAreaSqCm(area);
                                optionalLocation.get().setVolumeCuCm(volume);
                                optionalLocation.get().setCarryingCapacity(carryingCapacityKg);
                                optionalLocation.get().setIsDeleted(false);
                                optionalLocation.get().setCreatedBy(loginUser.getUserId());
                                optionalLocation.get().setCreatedOn(new Date());
                                locationRepository.save(optionalLocation.get());
                                log.info(String.valueOf(new StringBuilder().append(loginUser.getLogId()).append(ServiceConstants.TOTAL_ROWS_SCANNED).append(count)));
                            } else {
                                resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), headerNames.get(ServiceConstants.CELL_INDEX_6), "This Store Id not Present in Database"));
                            }
                        } else {
                            resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), headerNames.get(ServiceConstants.CELL_INDEX_6), "This St Id not Present in Database"));
                        }
                    }
                }
                long endTime = System.currentTimeMillis();
                // Close the workbook
                workbook.close();
                if (resultResponses.size() == 0) {
                    List<Location> locationList = locationRepository.findByIsDeletedAndSubOrganizationIdAndZoneId(false, loginUser.getSubOrgId(), zoneId);

                    locationList = locations;
                    this.locationRepository.saveAllAndFlush(locationList);
                    log.info(String.valueOf(new StringBuilder().append(loginUser.getLogId()).append(ServiceConstants.SPACE).append(ServiceConstants.UPLOAD_LOCATION_DETAIL_METHOD_EXECUTED)
                            .append(ServiceConstants.EXEC_TIME).append(endTime - startTime)));

                    return ResponseEntity.ok(new BaseResponse<>(HttpStatus.OK.value(), ServiceConstants.FILE_UPLOADED_SUCCESSFULLY, null, ServiceConstants.SUCCESS_CODE, loginUser.getLogId()));
                } else {
                    log.info(String.valueOf(new StringBuilder().append(loginUser.getLogId()).append(ServiceConstants.SPACE).append(ServiceConstants.LOCATION_DATA_UPLOAD_FAILED)
                            .append(ServiceConstants.EXEC_TIME).append(endTime - startTime)));
                    return ResponseEntity.ok(new BaseResponse<>(500, ServiceConstants.LOCATION_DATA_UPLOAD_FAILED, resultResponses, ServiceConstants.ERROR_CODE, loginUser.getLogId()));
                }
            } catch (Exception e) {
                log.error(String.valueOf(new StringBuilder().append(loginUser.getLogId()).append(ServiceConstants.LOCATION_DATA_UPLOAD_FAILED)));
                ExceptionLogger.logException(e, loginUser.getLogId());
                return ResponseEntity.ok(new BaseResponse<>(ServiceConstants.STATUS_CODE_500, ServiceConstants.LOCATION_DATA_UPLOAD_FAILED, null, ServiceConstants.ERROR_CODE, loginUser.getLogId()));
            }
        }
}
