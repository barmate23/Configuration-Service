package com.stockmanagementsystem.service;

import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfWriter;
import com.stockmanagementsystem.entity.*;
import com.stockmanagementsystem.repository.EquipmentRepository;
import com.stockmanagementsystem.repository.StoreRepository;
import com.stockmanagementsystem.request.EquipmentRequest;
import com.stockmanagementsystem.response.BaseResponse;
import com.stockmanagementsystem.utils.BarcodeGenerator;
import com.stockmanagementsystem.utils.ResponseKeyConstant;
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

@Service
@Slf4j
public class EquipmentServiceImpl implements EquipmentService {

    @Autowired
    EquipmentRepository equipmentRepository;
    @Autowired
    private StoreRepository storeRepository;
    @Autowired
    LoginUser loginUser;

    @Autowired
    Validations validations;
    @Override
    public BaseResponse<Equipment> saveEquipment(EquipmentRequest equipmentRequest){
        long startTime = System.currentTimeMillis();
        log.info("LogId:{} - EquipmentServiceImpl - saveEquipment - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId()," SAVE EQUIPMENT START");
        BaseResponse<Equipment> baseResponse=new BaseResponse<>();
        List<Equipment> equipments=new ArrayList<>();
        try {
          Equipment equipment=new Equipment();
          equipment.setTrolleyId(equipmentGenerator(1));
            List<Equipment>equipmentList=equipmentRepository.findByIsDeletedAndSubOrganizationId(false,loginUser.getSubOrgId());
            if(!equipmentList.stream().anyMatch(e->e.getAssetId().equals(equipmentRequest.getAssetId()))) {
                equipment.setAssetId(equipmentRequest.getAssetId());
            }else{
                ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10050E);
                baseResponse.setCode(responseMessage.getCode());
                baseResponse.setStatus(responseMessage.getStatus());
                baseResponse.setMessage(responseMessage.getMessage());
                baseResponse.setLogId(loginUser.getLogId());
                return baseResponse;
            }
          equipment.setTrolleyType(equipmentRequest.getTrolleyType());
          equipment.setOrganizationId(loginUser.getOrgId());
          equipment.setEquipmentName(equipmentRequest.getEquipmentName());
          equipment.setSubOrganizationId(loginUser.getSubOrgId());
          equipment.setIsDeleted(false);
          equipment.setCreatedBy(loginUser.getUserId());
          equipment.setCreatedOn(new Date());
          equipment.setModifiedOn(new Date());
          equipment.setModifiedBy(loginUser.getUserId());
            Optional<Store> storeOptional=storeRepository.findByIsDeletedAndIdAndSubOrganizationId(false,equipmentRequest.getStoreId(),loginUser.getSubOrgId());
            if(storeOptional.isPresent()){
                equipment.setStore(storeOptional.get());
            }else {
                ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10078F);
                baseResponse.setCode(responseMessage.getCode());
                baseResponse.setStatus(responseMessage.getStatus());
                baseResponse.setMessage(responseMessage.getMessage());
                baseResponse.setLogId(loginUser.getLogId());
                return baseResponse;
            }
            equipmentRepository.save(equipment);
            equipments.add(equipment);

            ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10080S);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setData(equipments);
            baseResponse.setLogId(loginUser.getLogId());
            log.info("LogId:{} - EquipmentServiceImpl - saveEquipment - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage());

        }catch (Exception ignored){
            ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10077F);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setLogId(loginUser.getLogId());
            long endTime = System.currentTimeMillis();
            log.error("LogId:{} - EquipmentServiceImpl - saveEquipment - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage() + (endTime - startTime),ignored);
        }
        long endTime = System.currentTimeMillis();
        log.info("LogId:{} - EquipmentServiceImpl - saveEquipment - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId()," SAVE EQUIPMENT TIME " + (endTime - startTime));
        return baseResponse;
    }

    @Override
    public BaseResponse<Equipment> updateEquipment(Integer id, EquipmentRequest equipmentRequest){
        long startTime = System.currentTimeMillis();
        log.info("LogId:{} - EquipmentServiceImpl - updateEquipment - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId()," UPDATE EQUIPMENT START");
        BaseResponse<Equipment> baseResponse=new BaseResponse<>();
        List<Equipment> equipments=new ArrayList<>();
        try {
            Equipment equipment=equipmentRepository.findByIsDeletedAndId(false,id);
            if(equipment.getAssetId().equalsIgnoreCase(equipmentRequest.getAssetId())) {
                equipment.setAssetId(equipmentRequest.getAssetId());
            }else {
                List<Equipment>equipmentList=equipmentRepository.findByIsDeletedAndOrganizationId(false,loginUser.getSubOrgId());
                if(equipmentList!=null && equipmentList.size()!=0 && !equipmentList.stream().anyMatch(e->e.getAssetId().equalsIgnoreCase(equipmentRequest.getAssetId()))) {
                    equipment.setAssetId(equipmentRequest.getAssetId());
                }else{
                    ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10051E);
                    baseResponse.setCode(responseMessage.getCode());
                    baseResponse.setStatus(responseMessage.getStatus());
                    baseResponse.setMessage(responseMessage.getMessage());
                    baseResponse.setLogId(loginUser.getLogId());
                    return baseResponse;
                }
            }
            equipment.setTrolleyType(equipmentRequest.getTrolleyType());
            equipment.setEquipmentName(equipmentRequest.getEquipmentName());
            equipment.setOrganizationId(loginUser.getOrgId());
            equipment.setSubOrganizationId(loginUser.getSubOrgId());
            equipment.setIsDeleted(false);
            equipment.setCreatedBy(loginUser.getUserId());
            equipment.setCreatedOn(new Date());
            equipment.setModifiedOn(new Date());
            Optional<Store> storeOptional=storeRepository.findByIsDeletedAndIdAndSubOrganizationId(false,equipmentRequest.getStoreId(),loginUser.getSubOrgId());
            if(storeOptional.isPresent()){
                equipment.setStore(storeOptional.get());
            }else {
                ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10079F);
                baseResponse.setCode(responseMessage.getCode());
                baseResponse.setStatus(responseMessage.getStatus());
                baseResponse.setMessage(responseMessage.getMessage());
                baseResponse.setLogId(loginUser.getLogId());
                return baseResponse;
            }
            equipmentRepository.save(equipment);
            equipments.add(equipment);

            ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10081S);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setData(equipments);
            baseResponse.setLogId(loginUser.getLogId());
            log.info("LogId:{} - EquipmentServiceImpl - updateEquipment - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage());

        }catch (Exception ignored){
            ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10080F);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setLogId(loginUser.getLogId());
            long endTime = System.currentTimeMillis();
            log.error("LogId:{} - EquipmentServiceImpl - updateEquipment - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage() + (endTime - startTime),ignored);
        }
        long endTime = System.currentTimeMillis();
        log.info("LogId:{} - EquipmentServiceImpl - updateEquipment - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId()," UPDATE EQUIPMENT TIME " + (endTime - startTime));
        return baseResponse;
    }
    @Override
    public BaseResponse<Equipment> getAllEquipmentWithPagination(Integer pageNo, Integer pageSize,List<Integer> storeId,List<String> trolleyType){
        long startTime = System.currentTimeMillis();
        log.info("LogId:{} - EquipmentServiceImpl - getAllEquipmentWithPagination - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId()," GET ALL EQUIPMENT WITH PAGINATION START");

        BaseResponse<Equipment> baseResponse=new BaseResponse<>();
        Page<Equipment> pageResult=null;
        try {
            final Pageable pageable = (Pageable) PageRequest.of((int) pageNo, (int) pageSize);
            if(storeId==null && trolleyType==null) {
                pageResult = equipmentRepository.findByIsDeletedAndSubOrganizationId(false,loginUser.getSubOrgId(), pageable);
            } else if (storeId!=null && trolleyType!=null) {
                pageResult = equipmentRepository.findByIsDeletedAndSubOrganizationIdAndTrolleyTypeInAndStoreIdIn(false,loginUser.getSubOrgId(),trolleyType,storeId, pageable);
            }else if (storeId != null){
                pageResult = equipmentRepository.findByIsDeletedAndSubOrganizationIdAndStoreIdIn(false,loginUser.getSubOrgId(),storeId, pageable);
            }else {
                pageResult = equipmentRepository.findByIsDeletedAndSubOrganizationIdAndTrolleyTypeIn(false,loginUser.getSubOrgId(),trolleyType, pageable);
            }
            ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10082S);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setData(pageResult.getContent());
            baseResponse.setTotalRecordCount(pageResult.getTotalElements());
            baseResponse.setTotalPageCount(pageResult.getTotalPages());
            baseResponse.setLogId(loginUser.getLogId());
            log.info("LogId:{} - EquipmentServiceImpl - getAllEquipmentWithPagination - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage());
            return baseResponse;
        }catch (Exception ex){
            ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10081F);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setLogId(loginUser.getLogId());
            long endTime = System.currentTimeMillis();
            log.error("LogId:{} - EquipmentServiceImpl - getAllEquipmentWithPagination - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage() + (endTime - startTime),ex);
        }
        long endTime = System.currentTimeMillis();
        log.info("LogId:{} - EquipmentServiceImpl - getAllEquipmentWithPagination - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId()," GET ALL EQUIPMENT WITH PAGINATION TIME " + (endTime - startTime));
        return baseResponse;
    }
    @Override
    public BaseResponse<Equipment> deleteEquipmentById(Integer id){
        long startTime = System.currentTimeMillis();
        log.info("LogId:{} - EquipmentServiceImpl - deleteEquipmentById - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId()," DELETE EQUIPMENT BY ID START");
        BaseResponse<Equipment> baseResponse=new BaseResponse<>();
        try {
                Equipment equipment=equipmentRepository.findByIsDeletedAndSubOrganizationIdAndId(false,loginUser.getSubOrgId(),id);
                equipment.setIsDeleted(true);
                equipmentRepository.save(equipment);
                List<Equipment> equipments=new ArrayList<>();
                equipments.add(equipment);

            ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10083S);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());


                baseResponse.setData(equipments);
            baseResponse.setLogId(loginUser.getLogId());
            log.info("LogId:{} - EquipmentServiceImpl - deleteEquipmentById - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage());
            return baseResponse;
        }catch (Exception ex){
            ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10082F);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setLogId(loginUser.getLogId());
            long endTime = System.currentTimeMillis();
            log.error("LogId:{} - EquipmentServiceImpl - deleteEquipmentById - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage() + (endTime - startTime),ex);

        }
        long endTime = System.currentTimeMillis();
        log.info("LogId:{} - EquipmentServiceImpl - deleteEquipmentById - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId()," DELETE EQUIPMENT BY ID TIME " + (endTime - startTime));
        return baseResponse;
    }

    @Override
    public BaseResponse<Equipment> getAllEquipment(){
        long startTime = System.currentTimeMillis();
        log.info("LogId:{} - EquipmentServiceImpl - getAllEquipment - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId()," GET ALL EQUIPMENT START");
        BaseResponse<Equipment> baseResponse=new BaseResponse<>();
        try {
            List<Equipment> equipment=equipmentRepository.findByIsDeletedAndSubOrganizationId(false,loginUser.getSubOrgId());

            ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10084S);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setData(equipment);
            baseResponse.setLogId(loginUser.getLogId());
            log.info("LogId:{} - EquipmentServiceImpl - getAllEquipment - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage());
            return baseResponse;
        }catch (Exception ex){
            ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10083F);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setLogId(loginUser.getLogId());
            long endTime = System.currentTimeMillis();
            log.error("LogId:{} - EquipmentServiceImpl - getAllEquipment - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage() + (endTime - startTime),ex);
        }
        long endTime = System.currentTimeMillis();
        log.info("LogId:{} - EquipmentServiceImpl - getAllEquipment - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId()," GET ALL EQUIPMENT TIME " + (endTime - startTime));
        return baseResponse;
    }

    @Override
    public ByteArrayOutputStream generateBarcodePDF() {
        long startTime = System.currentTimeMillis();
        try {
            log.info("LogId:{} - equipmentService - generateBarcodePDF - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId()," GET ZONE BARCODE START");

            log.info("LogId:{} - equipmentService - generateBarcodePDF - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId()," GET ZONES FOR BARCODE DB CALL");
            List<Equipment>  equipmentList = equipmentRepository.findByIsDeletedAndSubOrganizationId(false, loginUser.getSubOrgId());
            Document document = new Document();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, outputStream);
            document.open();

            for (Equipment equipment : equipmentList) {
                byte[] barcodeImageBytes = BarcodeGenerator.generateBarcode(equipment.getTrolleyId());
                com.itextpdf.text.Image barcodeImage = com.itextpdf.text.Image.getInstance(barcodeImageBytes);
                document.add(barcodeImage);
            }
            document.close();
            long endTime = System.currentTimeMillis();
            log.info("LogId:{} - equipmentService - generateBarcodePDF - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),"SUCCESSFULLY DOWNLOAD DOCKS BARCODE PDF TIME :" + (endTime - startTime));

            return outputStream;
        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            log.info("LogId:{} - equipmentService - generateBarcodePDF - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),"FAILED DOWNLOAD DOCK BARCODE PDF TIME :" + (endTime - startTime));
            e.printStackTrace();
            return null;
        }
    }
    @Override
    public String equipmentGenerator(Integer count) {
        List<Equipment> equipment = equipmentRepository.findBySubOrganizationId(loginUser.getSubOrgId());
        String equipmentId = null;
        if (equipment != null && equipment.size()!=0) {
            equipmentId = String.format("%s-EQ%03d", loginUser.getSubOrganizationCode(), equipment.size()+count);
        }else{
            equipmentId = String.format("%s-EQ%03d", loginUser.getSubOrganizationCode(), count);
        }
        return equipmentId;
    }
}
