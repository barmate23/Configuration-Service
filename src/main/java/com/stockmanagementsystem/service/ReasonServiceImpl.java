package com.stockmanagementsystem.service;

import com.stockmanagementsystem.entity.*;
import com.stockmanagementsystem.exception.ExcelGenerationException;
import com.stockmanagementsystem.repository.ItemRepository;
import com.stockmanagementsystem.repository.ReasonCategoryMasterRepository;
import com.stockmanagementsystem.repository.ReasonRepository;
import com.stockmanagementsystem.response.*;
import com.stockmanagementsystem.utils.ResponseKeyConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.stockmanagementsystem.utils.GlobalMessages.getResponseMessages;

@Service
@Slf4j
public class ReasonServiceImpl implements ReasonService {

    @Autowired
    ReasonRepository reasonRepository;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    LoginUser loginUser;

    @Autowired
    ReasonCategoryMasterRepository categoryMasterRepository;


    @Override
    public BaseResponse saveReason(String rejectedReason,Integer reasonCategoryId) {

        long startTime = System.currentTimeMillis();
        log.info("LogId:{} - ReasonServiceImpl - saveReason - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId()," SAVED REASON START");
        BaseResponse  baseResponse=new BaseResponse();

        try {
            Reason reason=new Reason();
            reason.setReasonId(generateReasonId(1));
            reason.setRejectedReason(rejectedReason);
            ReasonCategoryMaster reasonCategoryMaster=this.categoryMasterRepository.findByIsDeletedAndId(false,reasonCategoryId);
            if(reasonCategoryMaster!=null) {
                reason.setReasonCategoryMaster(reasonCategoryMaster);
            }
            reason.setIsDeleted(false);
            reason.setSubOrganizationId(loginUser.getSubOrgId());
            reason.setOrganizationId(loginUser.getOrgId());
            reason.setCreatedBy(loginUser.getUserId());
            reason.setCreatedOn(new Date());
            reasonRepository.save(reason);
            ReasonResponse reasonResponse=new ReasonResponse();
            List<Object> responseData = new ArrayList<>();
            responseData.add(reason);
            ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10065S);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());

          //  baseResponse.setData((List) reasonResponse);
            baseResponse.setData(responseData);
            baseResponse.setLogId(loginUser.getLogId());
            log.info("LogId:{} - ReasonServiceImpl - saveReason - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage());
        }catch (Exception e){
            ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10062F);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setData(new ArrayList<>());
            baseResponse.setLogId(loginUser.getLogId());
            long endTime = System.currentTimeMillis();
            log.error("LogId:{} - ReasonServiceImpl - saveReason - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage()+ (endTime - startTime),e);

        }
        long endTime = System.currentTimeMillis();
        log.info("LogId:{} - ReasonServiceImpl - saveReason - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId()," SAVE REASON TIME" + (endTime - startTime));
        return baseResponse;
    }
@Override
    public String generateReasonId(Integer count) {
    List<Reason> reasons = reasonRepository.findBySubOrganizationId(loginUser.getSubOrgId());
    String reasonId = null;
    if (reasons != null && reasons.size()!=0) {
        int itmNumber = reasons.size();
        // Use String.format to ensure the number is always padded to 3 digits
        reasonId = String.format("%s-RS%06d", loginUser.getSubOrganizationCode(), itmNumber+count);
    }else{
        reasonId = String.format("%s-RS%06d", loginUser.getSubOrganizationCode(), count);
    }
    return reasonId;
    }

    @Override
    public BaseResponse deleteReasonById(Integer id) {

        long startTime = System.currentTimeMillis();
        log.info("LogId:{} - ReasonServiceImpl - deleteReasonById - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId()," DELETE REASON BY ID METHOD START");
        BaseResponse baseResponse=new BaseResponse();
        try {
           // Optional<Reason> reasons=reasonRepository.findByOrganizationIdAndSubOrganizationIdAndIsDeletedAndId(loginUser.getOrgId(), loginUser.getSubOrgId(),false,id);
            Optional<Reason> reasons=reasonRepository.findBySubOrganizationIdAndIsDeletedAndId( loginUser.getSubOrgId(),false,id);
            reasons.get().setIsDeleted(true);
            reasonRepository.save(reasons.get());
            ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10066S);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setLogId(loginUser.getLogId());
            baseResponse.setData(new ArrayList<>());
            log.info("LogId:{} - ReasonServiceImpl - deleteReasonById - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage());
        }catch (Exception e){
            ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10063F);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setLogId(loginUser.getLogId());
            baseResponse.setData(new ArrayList<>());

            long endTime = System.currentTimeMillis();
            log.error("LogId:{} - ReasonServiceImpl - deleteReasonById - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage() + (endTime - startTime),e);

        }
        long endTime = System.currentTimeMillis();
        log.info("LogId:{} - ReasonServiceImpl - deleteReasonById - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId()," DELETE REASON METHOD TIME" + (endTime - startTime));
        return baseResponse;
    }

    @Override
    public BaseResponse updateReason(Integer id,String rejectedReason,Integer reasonCategoryId) {

        long startTime = System.currentTimeMillis();
        log.info("LogId:{} - ReasonServiceImpl - updateReason - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId()," UPDATE REASON METHOD START");
        BaseResponse baseResponse = new BaseResponse();
        try {
            Optional<Reason> optionalReasons = reasonRepository.findById(id);
            if (optionalReasons.isPresent()) {
                Reason reason = optionalReasons.get();
                //  reason.setReasonId(reasonsRequest.getReasonId());
                reason.setRejectedReason(rejectedReason);
                ReasonCategoryMaster reasonCategoryMaster=this.categoryMasterRepository.findByIsDeletedAndId(false,reasonCategoryId);
                if(reasonCategoryMaster!=null) {
                    reason.setReasonCategoryMaster(reasonCategoryMaster);
                }
                reasonRepository.save(reason);
                ReasonResponse reasonResponse=new ReasonResponse();
                List<Object> responseData = new ArrayList<>();
                responseData.add(reason);
                ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10067S);
                baseResponse.setCode(responseMessage.getCode());
                baseResponse.setStatus(responseMessage.getStatus());
                baseResponse.setMessage(responseMessage.getMessage());
                baseResponse.setData(responseData);
                baseResponse.setLogId(loginUser.getLogId());
                log.info("LogId:{} - ReasonServiceImpl - updateReason - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage());
            } else {
                ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10047E);
                baseResponse.setCode(responseMessage.getCode());
                baseResponse.setStatus(responseMessage.getStatus());
                baseResponse.setMessage(responseMessage.getMessage());
                baseResponse.setData(new ArrayList<>());
                baseResponse.setLogId(loginUser.getLogId());
            }
        } catch (Exception e) {
            ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10064F);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setData(new ArrayList<>());
            baseResponse.setLogId(loginUser.getLogId());
            long endTime = System.currentTimeMillis();
            log.error("LogId:{} - ReasonServiceImpl - updateReason - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage() + (endTime - startTime),e);

        }

        long endTime = System.currentTimeMillis();
        log.info("LogId:{} - ReasonServiceImpl - updateReason - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId()," UPDATE REASON METHOD TIME" + (endTime - startTime));

        return baseResponse;
    }

    @Override
    public BaseResponse<List<ItemNameResponse>> getItemIdWithName() {

        long startTime = System.currentTimeMillis();
        log.info("LogId:{} - ReasonServiceImpl - getItemIdWithName - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId()," FETCHED ITEMS WITH NAME METHOD START");
        //BaseResponse<List<ItemNameResponse>> baseResponse = new BaseResponse<>();
        BaseResponse baseResponse = new BaseResponse<>();
        try {
            List<ItemNameResponse> itemResponseList = itemRepository.findByIsDeletedAndSubOrganizationId(false,loginUser.getSubOrgId()).stream()
                    .map(item -> new ItemNameResponse(item.getId(),item.getItemCode(), item.getName()))
                    .collect(Collectors.toList());

            ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10068S);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());

            baseResponse.setData(itemResponseList);
            baseResponse.setLogId(loginUser.getLogId());
            log.info("LogId:{} - ReasonServiceImpl - getItemIdWithName - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage());
        } catch (Exception e) {
            ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10065F);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setData(new ArrayList<>());
            baseResponse.setLogId(loginUser.getLogId());
            long endTime = System.currentTimeMillis();
            log.error("LogId:{} - ReasonServiceImpl - getItemIdWithName - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage() + (endTime - startTime),e);

        }
        long endTime = System.currentTimeMillis();
        log.info("LogId:{} - ReasonServiceImpl - getItemIdWithName - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId()," FETCHED ITEMS WITH NAME METHOD TIME" + (endTime - startTime));
        return baseResponse;
    }

    @Override
    public BaseResponse<List<ReasonResponse>> searchReasons(Integer pageNumber, Integer pageSize, List<String> reasonId, List<String> reasonCategory, List<String> itemName, Boolean userCreatedReason) {


        long startTime = System.currentTimeMillis();
        log.info("LogId:{} - ReasonServiceImpl - searchReasons - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),"SEARCH REASON METHOD START");
        //   BaseResponse<List<ReasonResponse>> response = new BaseResponse<>();
        BaseResponse response = new BaseResponse<>();
        try {
            Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("id").descending());
            // Pageable pageable = PageRequest.of(pageNumber, pageSize);
            List<ReasonResponse> reasonResponseList = new ArrayList<>();

            Specification<Reason> specification = ReasonSpecifications.withFilters(reasonId, reasonCategory,itemName, userCreatedReason, true,loginUser.getSubOrgId());

            Page<Reason> reasonPage = reasonRepository.findAll(specification, pageable);
            List<Reason> reasonList = reasonPage.getContent();

            for (Reason reasons : reasonList) {
                ReasonResponse  reasonResponse = new ReasonResponse();
                reasonResponse.setId(reasons.getId());
                reasonResponse.setReasonId(reasons.getReasonId());
                //  reasonResponse.setReasonName(reasons.getReasonName());
                reasonResponse.setCategoryMaster(reasons.getReasonCategoryMaster());
                reasonResponse.setRejectedReason(reasons.getRejectedReason());
                reasonResponseList.add(reasonResponse);
            }
            ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10069S);
            response.setCode(responseMessage.getCode());
            response.setStatus(responseMessage.getStatus());
            response.setMessage(responseMessage.getMessage());

            response.setData(reasonResponseList);
            response.setTotalRecordCount(reasonPage.getTotalElements());
            response.setTotalPageCount(reasonPage.getTotalPages());
            response.setLogId(loginUser.getLogId());
            log.info("LogId:{} - ReasonServiceImpl - searchReasons - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage());

        } catch (Exception e) {
            ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10066F);
            response.setCode(responseMessage.getCode());
            response.setStatus(responseMessage.getStatus());
            response.setMessage(responseMessage.getMessage());
            response.setData(new ArrayList<>());
            long endTime = System.currentTimeMillis();
            log.error("LogId:{} - ReasonServiceImpl - searchReasons - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage() + (endTime - startTime),e);
            e.printStackTrace();
            response.setLogId(loginUser.getLogId());
        }
        long endTime = System.currentTimeMillis();
        log.info("LogId:{} - ReasonServiceImpl - searchReasons - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId()," SEARCH REASONS METHOD TIME" + (endTime - startTime));
        return response;
    }

    @Override
    public BaseResponse<List<ReasonResponse>> getAllReasons(int page, int pageSize) {
        long startTime = System.currentTimeMillis();
        log.info("LogId:{} - ReasonServiceImpl - getAllReasons - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),"FETCHED REASON METHOD START");
        //BaseResponse<List<ReasonResponse>> response = new BaseResponse<>();
        BaseResponse response = new BaseResponse<>();
        try {
            Pageable pageable = PageRequest.of(page, pageSize);
            List<ReasonResponse> reasonResponseList = new ArrayList<>();

            Page<Reason> reasonPage = reasonRepository.findByOrganizationIdAndSubOrganizationIdAndIsDeleted(loginUser.getOrgId(), loginUser.getSubOrgId(),false, pageable);

            for (Reason reasons : reasonPage.getContent()) {
                ReasonResponse reasonResponse = new ReasonResponse();
                reasonResponse.setId(reasons.getId());
                reasonResponse.setReasonId(reasons.getReasonId());
                //reasonResponse.setReasonName(reasons.getReasonName());
                reasonResponse.setCategoryMaster(reasons.getReasonCategoryMaster());
                reasonResponse.setRejectedReason(reasons.getRejectedReason());
                reasonResponseList.add(reasonResponse);
            }
            ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10070S);
            response.setCode(responseMessage.getCode());
            response.setStatus(responseMessage.getStatus());
            response.setMessage(responseMessage.getMessage());
            response.setData(reasonResponseList);
            response.setTotalRecordCount(reasonPage.getTotalElements());
            response.setTotalPageCount(reasonPage.getTotalPages());
            response.setLogId(loginUser.getLogId());
            log.info("LogId:{} - ReasonServiceImpl - getAllReasons - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage());
        } catch (Exception e) {
            ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10067F);
            response.setCode(responseMessage.getCode());
            response.setStatus(responseMessage.getStatus());
            response.setMessage(responseMessage.getMessage());
            response.setData(new ArrayList<>());
            response.setLogId(loginUser.getLogId());
            long endTime = System.currentTimeMillis();
            log.error("LogId:{} - ReasonServiceImpl - getAllReasons - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage()+ (endTime - startTime),e);

        }
        long endTime = System.currentTimeMillis();
        log.info("LogId:{} - ReasonServiceImpl - getAllReasons - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId()," FETCH REASONS METHOD TIME " + (endTime - startTime));

        return response;
    }

    @Override
    public byte[] generateExcelForAllReasons() {
        long startTime = System.currentTimeMillis();
        log.info("LogId:{} - ReasonServiceImpl - generateExcelForAllReasons - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),"DOWNLOAD REASONS EXCEL START");
        List<Reason> reasonList = reasonRepository.findByOrganizationIdAndSubOrganizationIdAndIsDeleted(loginUser.getOrgId(), loginUser.getSubOrgId(),false);
        long endTime = System.currentTimeMillis();
        log.info("LogId:{} - ReasonServiceImpl - generateExcelForAllReasons - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId()," DOWNLOAD REASONS EXCEL TIME " + (endTime - startTime));
        return generateExcelForDocks(reasonList);
    }



    public static byte[] generateExcelForDocks(List<Reason> reasonList) {

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Reason Data");

            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Reason ID");
            headerRow.createCell(1).setCellValue("Rejection Reason");
            headerRow.createCell(2).setCellValue("Reason Category");

            int rowNum = 1;
            for (Reason reason : reasonList) {
                Row dataRow = sheet.createRow(rowNum++);
                dataRow.createCell(0).setCellValue(reason.getReasonId());
                dataRow.createCell(1).setCellValue(reason.getRejectedReason());
                dataRow.createCell(2).setCellValue(reason.getReasonCategoryMaster().getReasonCategoryName());

            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        } catch (IOException e) {

            throw new ExcelGenerationException("Failed to generate Excel file for reasons ", e);
        }
    }


    @Override
    public BaseResponse<List<ReasonCategoryResponse>> getReasonCategoryWithId() {
        long startTime = System.currentTimeMillis();
        log.info("LogId:{} - ReasonServiceImpl - getReasonCategoryWithId - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId()," FETCHED REASON WITH ID METHOD START");

        //  BaseResponse<List<ReasonCategoryResponse>> baseResponse = new BaseResponse<>();
        BaseResponse baseResponse = new BaseResponse<>();
        try {

            List<ReasonCategoryResponse> responseList = reasonRepository.findBySubOrganizationIdAndIsDeleted( loginUser.getSubOrgId(),false).stream()
                    .map(reason -> new ReasonCategoryResponse(reason.getId(),reason.getReasonId(), reason.getReasonCategoryMaster().getReasonCategoryName(), reason.getRejectedReason()))
                    .collect(Collectors.toList());

            ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10071S);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());

            baseResponse.setData(responseList);
            baseResponse.setLogId(loginUser.getLogId());

            log.info("LogId:{} - ReasonServiceImpl - getReasonCategoryWithId - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage());
        } catch (Exception e) {
            ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10068F);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());

            baseResponse.setData(new ArrayList<>());
            baseResponse.setLogId(loginUser.getLogId());
            long endTime = System.currentTimeMillis();
            log.error("LogId:{} - ReasonServiceImpl - getReasonCategoryWithId - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage() + (endTime - startTime),e);
        }
        long endTime = System.currentTimeMillis();
        log.info("LogId:{} - ReasonServiceImpl - getReasonCategoryWithId - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId()," FETCHED REASON WITH ID METHOD TIME " + (endTime - startTime));

        return baseResponse;
    }

    @Override
    public BaseResponse<Reason> getAllReasonsWithoutPagination(){
        long startTime = System.currentTimeMillis();
        log.info("LogId:{} - ReasonServiceImpl - getAllReasonsWithoutPagination - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId()," REASONS LIST FETCHED START");

        BaseResponse<Reason> baseResponse=new BaseResponse<>();
        try{
            List<Reason> reasonList=reasonRepository.findByIsDeletedAndSubOrganizationId(false,loginUser.getSubOrgId());
            ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10072S);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setData(reasonList);
            baseResponse.setLogId(loginUser.getLogId());
            log.info("LogId:{} - ReasonServiceImpl - getAllReasonsWithoutPagination - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage());
            return baseResponse;
        }catch (Exception ex){
            ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10069F);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setLogId(loginUser.getLogId());
            long endTime = System.currentTimeMillis();
            log.error("LogId:{} - ReasonServiceImpl - getAllReasonsWithoutPagination - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage()+ (endTime - startTime),ex);

        }
        long endTime = System.currentTimeMillis();
        log.info("LogId:{} - ReasonServiceImpl - getAllReasonsWithoutPagination - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId()," FETCHED REASON TIME" + (endTime - startTime));

        return baseResponse;
    }

    @Override
    public BaseResponse<ReasonCategoryMaster> getAllCategory() {
        long startTime = System.currentTimeMillis();
        log.info("LogId:{} - ReasonServiceImpl - getAllReasonByCategory - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId()," getAllReasonByCategory Method Started");

        BaseResponse<ReasonCategoryMaster> baseResponse=new BaseResponse<>();
        try{
            List<ReasonCategoryMaster> reasonCategoryMasters=categoryMasterRepository.findByIsDeleted(false);
            ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10072S);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setData(reasonCategoryMasters);
            baseResponse.setLogId(loginUser.getLogId());
            log.info("LogId:{} - ReasonServiceImpl - getAllReasonsWithoutPagination - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage());
            return baseResponse;
        }catch (Exception ex){
            ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10069F);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setLogId(loginUser.getLogId());
            long endTime = System.currentTimeMillis();
            log.error("LogId:{} - ReasonServiceImpl - getAllReasonsWithoutPagination - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage()+ (endTime - startTime),ex);

        }
        long endTime = System.currentTimeMillis();
        log.info("LogId:{} - ReasonServiceImpl - getAllReasonsWithoutPagination - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId()," FETCHED REASON TIME" + (endTime - startTime));

        return baseResponse;
    }

    @Override
    public BaseResponse<Reason> getAllReasonByCategory(String categoryCode) {
        long startTime = System.currentTimeMillis();
        log.info("LogId:{} - ReasonServiceImpl - getAllReasonByCategory - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId()," getAllReasonByCategory Method Started");

        BaseResponse<Reason> baseResponse=new BaseResponse<>();
        try{
            List<Reason> reasons=reasonRepository.findByIsDeletedAndSubOrganizationIdAndReasonCategoryMasterReasonCategoryCode(false,loginUser.getSubOrgId(),categoryCode);
            ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10072S);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setData(reasons);
            baseResponse.setLogId(loginUser.getLogId());
            log.info("LogId:{} - ReasonServiceImpl - getAllReasonsWithoutPagination - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage());
            return baseResponse;
        }catch (Exception ex){
            ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10069F);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setLogId(loginUser.getLogId());
            long endTime = System.currentTimeMillis();
            log.error("LogId:{} - ReasonServiceImpl - getAllReasonsWithoutPagination - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage()+ (endTime - startTime),ex);

        }
        long endTime = System.currentTimeMillis();
        log.info("LogId:{} - ReasonServiceImpl - getAllReasonsWithoutPagination - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId()," FETCHED REASON TIME" + (endTime - startTime));

        return baseResponse;
    }
}