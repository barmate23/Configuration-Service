package com.stockmanagementsystem.service;

import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfWriter;
import com.stockmanagementsystem.entity.*;
import com.stockmanagementsystem.repository.AreaRepository;
import com.stockmanagementsystem.repository.CommonMasterRepository;
import com.stockmanagementsystem.repository.LocationRepository;
import com.stockmanagementsystem.repository.ZoneRepository;
import com.stockmanagementsystem.response.BaseResponse;
import com.stockmanagementsystem.utils.BarcodeGenerator;
import com.stockmanagementsystem.utils.ResponseKeyConstant;
import com.stockmanagementsystem.utils.ServiceConstants;
import com.stockmanagementsystem.validation.Validations;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static com.stockmanagementsystem.utils.GlobalMessages.getResponseMessages;

@Slf4j
@Service
public class ZoneServiceImpl implements ZoneService{
    @Autowired
    ZoneRepository zoneRepository;
    @Autowired
    LoginUser loginUser;

    @Autowired
    AreaRepository areaRepository;
    @Autowired
    Validations validations;

    @Autowired
    LocationRepository locationRepository;
    @Autowired
    LocationService locationService;

    @Autowired
    CommonMasterRepository commonMasterRepository;

    @Override
    public BaseResponse<Zone> updateZone(Integer id,Integer areaId, String erpZoneId, String zoneName,Integer statusId){
        long startTime = System.currentTimeMillis();
        log.info("LogId:{} - ZoneServiceImpl - updateZone - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId()," UPDATE ZONE METHOD START");
        BaseResponse<Zone> baseResponse=new BaseResponse<>();
        try{
            Optional<Zone> zone=zoneRepository.findByIsDeletedAndIdAndSubOrganizationId(false,id,loginUser.getSubOrgId());
            Area area=areaRepository.findByIsDeletedAndIdAndSubOrganizationId(false,areaId,loginUser.getSubOrgId());
            List<Zone>zoneList =new ArrayList<>();
                zone.get().setArea(area);
                if(validations.isValidPassword(zoneName) && validations.isValidPassword(erpZoneId)){
                    zone.get().setZoneName(zoneName);
                    zone.get().setErpZoneId(erpZoneId);
                }else {
                    ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10020E);
                    baseResponse.setCode(responseMessage.getCode());
                    baseResponse.setStatus(responseMessage.getStatus());
                    baseResponse.setMessage(responseMessage.getMessage());
                    baseResponse.setData(new ArrayList<>());
                    baseResponse.setLogId(loginUser.getLogId());
                    long endTime = System.currentTimeMillis();
                    log.info("LogId:{} - ZoneServiceImpl - updateZone - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage() + (endTime - startTime));
                    return baseResponse;
                }
                zone.get().setZoneCategory(commonMasterRepository.findByIsDeletedAndTypeAndId(false, ServiceConstants.ZONEC,statusId));
                zone.get().setCreatedBy(loginUser.getUserId());
                zone.get().setCreatedOn(new Date());
                zone.get().setModifiedBy(loginUser.getUserId());
                zone.get().setModifiedOn(new Date());
                zone.get().setIsDeleted(false);
                zoneRepository.save(zone.get());
                ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10033S);
                baseResponse.setCode(responseMessage.getCode());
                baseResponse.setStatus(responseMessage.getStatus());
                baseResponse.setMessage(responseMessage.getMessage());
                baseResponse.setData(zoneList);
                baseResponse.setLogId(loginUser.getLogId());
            log.info("LogId:{} - ZoneServiceImpl - updateZone - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage());
            return baseResponse;
        }catch (Exception ex){
            ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10032F);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setLogId(loginUser.getLogId());
            long endTime = System.currentTimeMillis();
            log.error("LogId:{} - ZoneServiceImpl - updateZone - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage() + (endTime - startTime),ex);

        }
        long endTime = System.currentTimeMillis();
        log.info("LogId:{} - ZoneServiceImpl - updateZone - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId()," UPDATE ZONE TIME :" + (endTime - startTime));
        return baseResponse;
    }

    @Override
    public BaseResponse<Zone> createZone(Area area,Integer sq){
        long startTime = System.currentTimeMillis();
        log.info("LogId:{} - ZoneServiceImpl - createZone - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId()," CREATE ZONE METHOD START");
        BaseResponse<Zone> baseResponse=new BaseResponse<>();
        try{
            Zone zone=new Zone();
            if(sq<10){
                zone.setZoneId(area.getAreaId()+"-Z0"+sq);
            }else{
                zone.setZoneId(area.getAreaId()+"-Z"+sq);
            }
            zone.setArea(area);
            zone.setSubOrganizationId(loginUser.getSubOrgId());
            zone.setOrganizationId(loginUser.getOrgId());
            zone.setCreatedBy(loginUser.getUserId());
            zone.setCreatedOn(new Date());
            zone.setModifiedBy(loginUser.getUserId());
            zone.setModifiedOn(new Date());
            zone.setIsDeleted(false);
            zoneRepository.save(zone);
            ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10034S);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setLogId(loginUser.getLogId());
            for(Integer i=1;i<=64;i++){
            locationService.createLocations(zone,i);
            }
            log.info("LogId:{} - ZoneServiceImpl - createZone - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage());

            return baseResponse;
        }catch (Exception ex){
            ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10033F);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setLogId(loginUser.getLogId());
            long endTime = System.currentTimeMillis();
            log.error("LogId:{} - ZoneServiceImpl - createZone - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage() + (endTime - startTime),ex);

        }
        long endTime = System.currentTimeMillis();
        log.info("LogId:{} - ZoneServiceImpl - createZone - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId()," CREATE ZONE TIME :" + (endTime - startTime));
        return baseResponse;
    }

    @Override
    public BaseResponse<Zone> getAllZones(List<Integer> areaId){
        long startTime = System.currentTimeMillis();
        log.info("LogId:{} - ZoneServiceImpl - getAllZones - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId()," GET ZONE METHOD START");

        BaseResponse<Zone> baseResponse=new BaseResponse<>();
        try{
            List<Zone> zoneList=zoneRepository.findByIsDeletedAndSubOrganizationIdAndAreaIdInOrderByIdAsc(false,loginUser.getSubOrgId(),areaId);
            ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10035S);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setData(zoneList);
            baseResponse.setLogId(loginUser.getLogId());
            log.info("LogId:{} - ZoneServiceImpl - getAllZones - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage());
            return baseResponse;
        }catch (Exception ex){
            ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10034F);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setLogId(loginUser.getLogId());
            long endTime = System.currentTimeMillis();
            log.error("LogId:{} - ZoneServiceImpl - getAllZones - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage() + (endTime - startTime),ex);

        }
        long endTime = System.currentTimeMillis();
        log.info("LogId:{} - ZoneServiceImpl - getAllZones - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId()," GET ZONE TIME :" + (endTime - startTime));
        return baseResponse;
    }
    @Override
    public BaseResponse<Zone> getAllZonesWithPagination(Integer pageNo, Integer pageSize,List<Integer> storeId,List<Integer> areaId,List<Integer> zoneId,Date startDate,Date endDate) {
        long startTime = System.currentTimeMillis();
        log.info("LogId:{} - ZoneServiceImpl - getAllZonesWithPagination - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId()," GET ZONE METHOD START");

        BaseResponse<Zone> baseResponse = new BaseResponse<>();
        List<Zone> zoneList=new ArrayList<>();
        try {
            Page<Zone> pageResult=null;
            final Pageable pageable = PageRequest.of(pageNo, pageSize);
            if(storeId==null && areaId==null && zoneId==null){
                pageResult = this.zoneRepository.findByIsDeletedAndSubOrganizationIdOrderByIdAsc(false,loginUser.getSubOrgId(), pageable);
            } else if (storeId!=null && areaId!=null && zoneId!=null) {
                pageResult = this.zoneRepository.findByIsDeletedAndSubOrganizationIdAndAreaStoreIdInAndIdInAndAreaIdInOrderByIdAsc(false,loginUser.getSubOrgId(),storeId,zoneId,areaId, pageable);
            } else if (storeId!=null && areaId==null && zoneId==null) {
                pageResult = this.zoneRepository.findByIsDeletedAndSubOrganizationIdAndAreaStoreIdInOrderByIdAsc(false,loginUser.getSubOrgId(),storeId, pageable);
            } else if (storeId==null && areaId==null && zoneId!=null) {
                pageResult = this.zoneRepository.findByIsDeletedAndIdIn(false,zoneId, pageable);
            }else {
                pageResult = this.zoneRepository.findByIsDeletedAndAreaIdIn(false,areaId, pageable);
            }
            baseResponse.setTotalPageCount(pageResult.getTotalPages());
            zoneList=(pageResult.getContent());
            baseResponse.setTotalRecordCount(pageResult.getTotalElements());
            ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10036S);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setData(zoneList);
            baseResponse.setLogId(loginUser.getLogId());
            log.info("LogId:{} - ZoneServiceImpl - getAllZonesWithPagination - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage());
            return baseResponse;

        }catch (Exception ex){
            ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10035F);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setLogId(loginUser.getLogId());
            long endTime = System.currentTimeMillis();
            log.error("LogId:{} - ZoneServiceImpl - getAllZonesWithPagination - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage() + (endTime - startTime),ex);

        }
        long endTime = System.currentTimeMillis();
        log.info("LogId:{} - ZoneServiceImpl - getAllZonesWithPagination - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId()," GET ZONE TIME :" + (endTime - startTime));

        return baseResponse;
    }

    @Override
    public BaseResponse<Zone> deleteZoneById(Integer zoneId){
        long startTime = System.currentTimeMillis();
        log.info("LogId:{} - ZoneServiceImpl - deleteZoneById - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId()," DELETE ZONE BY ID METHOD START");

        BaseResponse<Zone> baseResponse=new BaseResponse<>();
        try {
            Optional<Zone> zone=zoneRepository.findByIsDeletedAndId(false,zoneId);
            zone.get().setIsDeleted(true);
            List<Zone> zoneList=new ArrayList<>();
            zoneList.add(zone.get());
            List<Location>locations=locationRepository.findByIsDeletedAndSubOrganizationIdAndZoneId(false,loginUser.getSubOrgId(),zoneId);
            if(locations!=null && !locations.isEmpty()){
                ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10021E);
                baseResponse.setCode(responseMessage.getCode());
                baseResponse.setStatus(responseMessage.getStatus());
                baseResponse.setMessage(responseMessage.getMessage());
                baseResponse.setData(zoneList);
                baseResponse.setLogId(loginUser.getLogId());
                log.info("LogId:{} - ZoneServiceImpl - deleteZoneById - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage());
            }else {
                zoneRepository.save(zone.get());
                ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10037S);
                baseResponse.setCode(responseMessage.getCode());
                baseResponse.setStatus(responseMessage.getStatus());
                baseResponse.setMessage(responseMessage.getMessage());
                baseResponse.setData(zoneList);
                baseResponse.setLogId(loginUser.getLogId());
                log.info("LogId:{} - ZoneServiceImpl - deleteZoneById - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage());
            }
            return baseResponse;
        }catch (Exception ex){
            ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10036F);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setLogId(loginUser.getLogId());
            long endTime = System.currentTimeMillis();
            log.error("LogId:{} - ZoneServiceImpl - deleteZoneById - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage() + (endTime - startTime),ex);
        }
        long endTime = System.currentTimeMillis();
        log.info("LogId:{} - ZoneServiceImpl - deleteZoneById - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId()," DELETE ZONE BY ID TIME :" + (endTime - startTime));

        return baseResponse;
    }

    @Override
    public ByteArrayOutputStream generateBarcodePDF(String zoneId, Boolean getAll) {
        long startTime = System.currentTimeMillis();
        try {
            log.info("LogId:{} - ZoneService - generateBarcodePDF - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId()," GET ZONE BARCODE START");
            List<Zone> zoneList = new ArrayList<>();
            if (getAll) {
                log.info("LogId:{} - ZoneService - generateBarcodePDF - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId()," GET ZONES FOR BARCODE DB CALL");
                zoneList = zoneRepository.findByIsDeletedAndSubOrganizationId(false, loginUser.getSubOrgId());
            }
            else{
                Zone z = new Zone();
                z.setZoneId(zoneId);
                zoneList.add(z);
            }
            Document document = new Document();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, outputStream);
            document.open();

            int barcodesPerPage = 5;
            int barcodeCount = 0;

            for (Zone zone : zoneList) {
                byte[] barcodeImageBytes = BarcodeGenerator.generateBarcode(zone.getZoneId());
                com.itextpdf.text.Image barcodeImage = com.itextpdf.text.Image.getInstance(barcodeImageBytes);
                document.add(barcodeImage);

                barcodeCount++;

                if (barcodeCount % barcodesPerPage == 0 && barcodeCount < zoneList.size()) {
                    document.newPage();
                }
            }
            document.close();
            long endTime = System.currentTimeMillis();
            log.info("LogId:{} - ZoneService - generateBarcodePDF - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),"SUCCESSFULLY DOWNLOAD DOCKS BARCODE PDF TIME :" + (endTime - startTime));

            return outputStream;
        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            log.info("LogId:{} - ZoneService - generateBarcodePDF - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),"FAILED DOWNLOAD DOCK BARCODE PDF TIME :" + (endTime - startTime));
            e.printStackTrace();
            return null;
        }
    }
}
