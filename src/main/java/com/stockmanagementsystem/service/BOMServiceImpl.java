package com.stockmanagementsystem.service;

import com.stockmanagementsystem.entity.*;
import com.stockmanagementsystem.repository.BomHeadRepository;
import com.stockmanagementsystem.repository.BomLineRepository;
import com.stockmanagementsystem.repository.ItemRepository;
import com.stockmanagementsystem.request.BOMHeadRequest;
import com.stockmanagementsystem.request.BOMLineRequest;
import com.stockmanagementsystem.response.BaseResponse;
import com.stockmanagementsystem.utils.ResponseKeyConstant;
import com.stockmanagementsystem.validation.Validations;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.stockmanagementsystem.utils.GlobalMessages.getResponseMessages;
@Slf4j
@Service
public class BOMServiceImpl implements BOMService {

    @Autowired
    BomLineRepository bomLineRepository;

    @Autowired
    BomHeadRepository bomHeadRepository;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    LoginUser loginUser;
    @Autowired
    Validations validations;

    @Override
    public BaseResponse<BoMHead> getAllBomHeadWithPagination(Integer pageNo, Integer pageSize,List<String> bomERPCode,List<String> varient,List<String> model,Date date){
        long startTime = System.currentTimeMillis();
        log.info("LogId:{} - BOMServiceImpl - getAllBomHeadWithPagination - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId()," GET ALL BOM HEAD WITH PAGINATION METHOD START");
        BaseResponse<BoMHead> baseResponse = new BaseResponse<>();
        List<BoMHead> boMHeads=new ArrayList<>();
        Page<BoMHead> pageResult=null;
        try {
            final Pageable pageable = (Pageable) PageRequest.of((int) pageNo, (int) pageSize);
            if(bomERPCode==null && varient==null && model==null && date==null) {
                pageResult = this.bomHeadRepository.findByIsDeletedAndSubOrganizationId(false,loginUser.getSubOrgId(), pageable);
                baseResponse.setTotalPageCount(pageResult.getTotalPages());
                boMHeads=pageResult.getContent();
                baseResponse.setTotalRecordCount(pageResult.getTotalElements());
            } else if (bomERPCode!=null) {
                pageResult = this.bomHeadRepository.findByIsDeletedAndSubOrganizationIdAndBomERPCodeIn(false,loginUser.getSubOrgId(),bomERPCode, pageable);
                baseResponse.setTotalPageCount(pageResult.getTotalPages());
                boMHeads=pageResult.getContent();
                baseResponse.setTotalRecordCount(pageResult.getTotalElements());
            }
            else if (model!=null ) {
                pageResult = this.bomHeadRepository.findByIsDeletedAndSubOrganizationIdAndModelIn(false,loginUser.getSubOrgId(),model, pageable);
                baseResponse.setTotalPageCount(pageResult.getTotalPages());
                boMHeads=pageResult.getContent();
                baseResponse.setTotalRecordCount(pageResult.getTotalElements());
            }
            else if (varient!=null ) {
                pageResult = this.bomHeadRepository.findByIsDeletedAndSubOrganizationIdAndVariantIn(false,loginUser.getSubOrgId(),varient, pageable);
                baseResponse.setTotalPageCount(pageResult.getTotalPages());
                boMHeads=pageResult.getContent();
                baseResponse.setTotalRecordCount(pageResult.getTotalElements());
            }else {
                pageResult = this.bomHeadRepository.findByIsDeletedAndSubOrganizationId(false,loginUser.getSubOrgId(), pageable);
                baseResponse.setTotalPageCount(pageResult.getTotalPages());
                boMHeads=pageResult.getContent().stream().filter(k->k.getDate().equals(date)).collect(Collectors.toList());
                baseResponse.setTotalRecordCount((long) boMHeads.size());
            }
            ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10073S);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setData(boMHeads);
            baseResponse.setLogId(loginUser.getLogId());
            log.info("LogId:{} - BOMServiceImpl - getAllBomHeadWithPagination - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage());
            return baseResponse;
        }catch (Exception ex){
            ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10070F);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setLogId(loginUser.getLogId());
            long endTime = System.currentTimeMillis();
            log.error("LogId:{} - BOMServiceImpl - getAllBomHeadWithPagination - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage()+ (endTime - startTime),ex);
        }
        long endTime = System.currentTimeMillis();
        log.info("LogId:{} - BOMServiceImpl - getAllBomHeadWithPagination - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId()," GET ALL BOM HEAD WITH PAGINATION METHOD EXECUTED TIME :" + (endTime - startTime));
        return baseResponse;
    }

    @Override
    public BaseResponse<BOMLine> getAllBomLineWithPagination(Integer id,Integer pageNo, Integer pageSize){
        long startTime = System.currentTimeMillis();
        log.info("LogId:{} - BOMServiceImpl - getAllBomLineWithPagination - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId()," GET ALL BOM LINE WITH PAGINATION METHOD START");
        BaseResponse<BOMLine> baseResponse = new BaseResponse<>();
        List<BOMLine> bomLines=new ArrayList<>();
        try {
            final Pageable pageable = (Pageable) PageRequest.of((int) pageNo, (int) pageSize);
            Page<BOMLine> pageResult = this.bomLineRepository.findByIsDeletedAndSubOrganizationIdAndBomHeadId(false,loginUser.getSubOrgId(),id, pageable);
            baseResponse.setTotalPageCount(pageResult.getTotalPages());
            bomLines=pageResult.getContent();
            baseResponse.setTotalRecordCount(pageResult.getTotalElements());
            ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10074S);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setData(bomLines);
            baseResponse.setLogId(loginUser.getLogId());
            log.info("LogId:{} - BOMServiceImpl - getAllBomLineWithPagination - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage());
            return baseResponse;
        }catch (Exception ex){
            ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10071F);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setLogId(loginUser.getLogId());
            long endTime = System.currentTimeMillis();
            log.error("LogId:{} - BOMServiceImpl - getAllBomLineWithPagination - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage()+ (endTime - startTime),ex);
        }
        long endTime = System.currentTimeMillis();
        log.info("LogId:{} - BOMServiceImpl - getAllBomLineWithPagination - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId()," GET ALL BOM LINE WITH PAGINATION METHOD EXECUTED TIME :" + (endTime - startTime));
        return baseResponse;
    }
    @Override
    public BaseResponse<BOMLine> getAllBomLineByBomId(Integer id){
        long startTime = System.currentTimeMillis();
        log.info("LogId:{} - BOMServiceImpl - getAllBomLineByBomId - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId()," GET ALL BOM LINE BY BOM HEAD ID METHOD START");
        BaseResponse<BOMLine> baseResponse = new BaseResponse<>();
        try {
            List<BOMLine> bomLines= this.bomLineRepository.findByIsDeletedAndSubOrganizationIdAndBomHeadId(false,loginUser.getSubOrgId(),id);
            ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10074S);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setData(bomLines);
            baseResponse.setLogId(loginUser.getLogId());
            log.info("LogId:{} - BOMServiceImpl - getAllBomLineByBomId - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage());
            return baseResponse;
        }catch (Exception ex){
            ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10071F);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setLogId(loginUser.getLogId());
            long endTime = System.currentTimeMillis();
            log.error("LogId:{} - BOMServiceImpl - getAllBomLineByBomId - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage()+ (endTime - startTime),ex);
        }
        long endTime = System.currentTimeMillis();
        log.info("LogId:{} - BOMServiceImpl - getAllBomLineByBomId - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId()," GET ALL BOM LINE BY BOM HEAD ID METHOD EXECUTED TIME :" + (endTime - startTime));
        return baseResponse;
    }
    @Override
    public BaseResponse<BoMHead> saveBom(BOMHeadRequest bomHeadRequest){
        long startTime = System.currentTimeMillis();
        log.info("LogId:{} - BOMServiceImpl - saveBom - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId()," SAVE BOM METHOD START");
        BaseResponse<BoMHead> baseResponse = new BaseResponse<>();
        BoMHead boMHead=new BoMHead();
        List<BoMHead> boMHeads=new ArrayList<>();
        try {

            boMHead.setModel(bomHeadRequest.getModel());
            boMHead.setVariant(bomHeadRequest.getVariant());
            boMHead.setColour(bomHeadRequest.getColour());
            boMHead.setBomERPCode(bomHeadRequest.getBomERPCode());
            boMHead.setBomId(validations.bomIdGenerator());
            boMHead.setProduct(bomHeadRequest.getProduct());
            boMHead.setDate(bomHeadRequest.getDate());
            boMHead.setVersion(bomHeadRequest.getVersion());
            boMHead.setLifecyclePhase(bomHeadRequest.getLifecyclePhase());
            boMHead.setOrganizationId(loginUser.getOrgId());
            boMHead.setSubOrganizationId(loginUser.getSubOrgId());
            boMHead.setIsDeleted(false);
            boMHead.setCreatedBy(loginUser.getUserId());
            boMHead.setCreatedOn(new Date());
            boMHead.setModifiedOn(new Date());
            boMHead.setModifiedBy(loginUser.getUserId());
            bomHeadRepository.save(boMHead);
            for (BOMLineRequest bomLineRequest:bomHeadRequest.getBomLineRequests()) {
                BOMLine bomLine =new BOMLine();
                bomLine.setBomHead(boMHead);
                bomLine.setLevel(bomLineRequest.getLevel());
                bomLine.setLineNumber(bomLineRequest.getLineNumber());
                Optional<Item> itemOptional=itemRepository.findByIsDeletedAndSubOrganizationIdAndId(false,loginUser.getSubOrgId(),bomLineRequest.getItemId());
                if(itemOptional.isPresent()){
                    bomLine.setItem(itemOptional.get());
                }else {
                    ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10048E);
                    baseResponse.setCode(responseMessage.getCode());
                    baseResponse.setStatus(responseMessage.getStatus());
                    baseResponse.setMessage(responseMessage.getMessage());
                    baseResponse.setData(new ArrayList<>());
                    baseResponse.setLogId(loginUser.getLogId());
                    return baseResponse;
                }
                bomLine.setStage(bomLineRequest.getStage());
                bomLine.setQuantity(bomLineRequest.getQuantity());
                bomLine.setDependency(bomLineRequest.getDependency());
                bomLine.setReferenceDesignators(bomLineRequest.getReferenceDesignators());
                bomLine.setBomNotes(bomLineRequest.getBomNotes());
                bomLine.setIsDeleted(false);
                bomLine.setIsActive(true);
                bomLine.setOrganizationId(loginUser.getOrgId());
                bomLine.setSubOrganizationId(loginUser.getSubOrgId());
                bomLine.setCreatedBy(loginUser.getUserId());
                bomLine.setCreatedOn(new Date());
                bomLine.setModifiedOn(new Date());
                bomLine.setModifiedBy(loginUser.getUserId());
                bomLineRepository.save(bomLine);
            }
            ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10075S);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setData(boMHeads);
            baseResponse.setLogId(loginUser.getLogId());
            log.info("LogId:{} - BOMServiceImpl - saveBom - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage());

        }catch (Exception ex){
            ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10072F);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setLogId(loginUser.getLogId());
            bomHeadRepository.deleteById(boMHead.getId());
            long endTime = System.currentTimeMillis();
            log.error("LogId:{} - BOMServiceImpl - saveBom - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage()+ (endTime - startTime),ex);
            return baseResponse;
        }
        long endTime = System.currentTimeMillis();
        log.info("LogId:{} - BOMServiceImpl - saveBom - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId()," SAVE BOM METHOD EXECUTED TIME :" + (endTime - startTime));
        return baseResponse;
    }

    @Override
    public BaseResponse<BoMHead> updateBom(Integer bomId, BOMHeadRequest bomHeadRequest){
        long startTime = System.currentTimeMillis();
        log.info("LogId:{} - BOMServiceImpl - updateBom - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId()," UPDATE BOM METHOD START");
        BaseResponse<BoMHead> baseResponse = new BaseResponse<>();
        List<BoMHead> boMHeads=new ArrayList<>();
        try {
            BoMHead boMHead=bomHeadRepository.findByIsDeletedAndSubOrganizationIdAndId(false,loginUser.getSubOrgId(),bomId);
            boMHead.setModel(bomHeadRequest.getModel());
            boMHead.setVariant(bomHeadRequest.getVariant());
            boMHead.setColour(bomHeadRequest.getColour());
            boMHead.setDate(bomHeadRequest.getDate());
            boMHead.setVersion(bomHeadRequest.getVersion());
            boMHead.setLifecyclePhase(bomHeadRequest.getLifecyclePhase());
            boMHead.setIsDeleted(false);
            boMHead.setCreatedBy(loginUser.getUserId());
            boMHead.setCreatedOn(new Date());
            boMHead.setModifiedOn(new Date());
            boMHead.setModifiedBy(loginUser.getUserId());
            bomHeadRepository.save(boMHead);
            for (BOMLineRequest bomLineRequest:bomHeadRequest.getBomLineRequests()) {
                BOMLine bomLine=null;
                if(bomLineRequest.getId()!=null){
                    bomLine=bomLineRepository.findByIsDeletedAndSubOrganizationIdAndId(false,loginUser.getSubOrgId(),bomLineRequest.getId());
                }else {
                   bomLine =new BOMLine();
                }
                bomLine.setBomHead(boMHead);
                bomLine.setLevel(bomLineRequest.getLevel());
                bomLine.setLineNumber(bomLineRequest.getLineNumber());
                Optional<Item> itemOptional=itemRepository.findByIsDeletedAndSubOrganizationIdAndId(false,loginUser.getSubOrgId(),bomLineRequest.getItemId());
                if(itemOptional.isPresent()){
                    bomLine.setItem(itemOptional.get());
                }else {
                    ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10049E);
                    baseResponse.setCode(responseMessage.getCode());
                    baseResponse.setStatus(responseMessage.getStatus());
                    baseResponse.setMessage(responseMessage.getMessage());
                    baseResponse.setData(new ArrayList<>());
                    baseResponse.setLogId(loginUser.getLogId());
                    return baseResponse;
                }
                bomLine.setQuantity(bomLineRequest.getQuantity());
                bomLine.setStage(bomLineRequest.getStage());
                bomLine.setDependency(bomLineRequest.getDependency());
                bomLine.setReferenceDesignators(bomLineRequest.getReferenceDesignators());
                bomLine.setBomNotes(bomLineRequest.getBomNotes());
                bomLine.setIsDeleted(false);
                bomLine.setCreatedBy(loginUser.getUserId());
                bomLine.setCreatedOn(new Date());
                bomLine.setModifiedOn(new Date());
                bomLine.setModifiedBy(loginUser.getUserId());
                bomLine.setOrganizationId(loginUser.getOrgId());
                bomLine.setSubOrganizationId(loginUser.getSubOrgId());
                bomLineRepository.save(bomLine);
            }
            ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10076S);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setData(boMHeads);
            baseResponse.setLogId(loginUser.getLogId());
            log.info("LogId:{} - BOMServiceImpl - updateBom - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage());
            return baseResponse;
        }catch (Exception ex){
            ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10073F);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setLogId(loginUser.getLogId());
            long endTime = System.currentTimeMillis();
            log.error("LogId:{} - BOMServiceImpl - updateBom - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage()+ (endTime - startTime),ex);
        }
        long endTime = System.currentTimeMillis();
        log.info("LogId:{} - BOMServiceImpl - updateBom - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId()," UPDATE BOM METHOD EXECUTED TIME :" + (endTime - startTime));
        return baseResponse;
    }

    @Override
    public BaseResponse<BoMHead> deleteBomHeadsById(Integer id){
        long startTime = System.currentTimeMillis();
        log.info("LogId:{} - BOMServiceImpl - deleteBomHeadsById - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId()," DELETE BOM HEADS BY ID METHOD START");
        BaseResponse<BoMHead> baseResponse = new BaseResponse<>();
        List<BoMHead> boMHeads=new ArrayList<>();
        try {
            BoMHead boMHead=bomHeadRepository.findByIsDeletedAndSubOrganizationIdAndId(false,loginUser.getSubOrgId(),id);
            boMHead.setIsDeleted(true);
            List<BOMLine> bomLines=bomLineRepository.findByIsDeletedAndSubOrganizationIdAndBomHeadId(false,loginUser.getSubOrgId(),id);
            bomHeadRepository.save(boMHead);
            ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10077S);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setData(boMHeads);
            baseResponse.setLogId(loginUser.getLogId());
            log.info("LogId:{} - BOMServiceImpl - deleteBomHeadsById - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage());
            return baseResponse;
        }catch (Exception ex){
            ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10074F);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setLogId(loginUser.getLogId());
            long endTime = System.currentTimeMillis();
            log.error("LogId:{} - BOMServiceImpl - deleteBomHeadsById - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage()+ (endTime - startTime),ex);
        }
        long endTime = System.currentTimeMillis();
        log.info("LogId:{} - BOMServiceImpl - deleteBomHeadsById - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId()," DELETE BOM HEADS BY ID METHOD EXECUTED TIME :" + (endTime - startTime));
        return baseResponse;
    }

    @Override
    public BaseResponse<BOMLine> deleteBomLineById(Integer id){
        long startTime = System.currentTimeMillis();
        log.info("LogId:{} - BOMServiceImpl - deleteBomLineById - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId()," DELETE BOM LINE BY ID METHOD START");
        BaseResponse<BOMLine> baseResponse = new BaseResponse<>();
        List<BOMLine> bomLines=new ArrayList<>();
        try {
            BOMLine bomLine=bomLineRepository.findByIsDeletedAndSubOrganizationIdAndId(false,loginUser.getSubOrgId(),id);
            bomLine.setIsDeleted(true);
            bomLineRepository.save(bomLine);
            ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10078S);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setData(bomLines);
            baseResponse.setLogId(loginUser.getLogId());
            log.info("LogId:{} - BOMServiceImpl - deleteBomLineById - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage());
            return baseResponse;
        }catch (Exception ex){
            ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10075F);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setLogId(loginUser.getLogId());
            long endTime = System.currentTimeMillis();
            log.error("LogId:{} - BOMServiceImpl - deleteBomLineById - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage()+ (endTime - startTime),ex);
        }
        long endTime = System.currentTimeMillis();
        log.info("LogId:{} - BOMServiceImpl - deleteBomLineById - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId()," DELETE BOM LINE BY ID METHOD EXECUTED TIME :" + (endTime - startTime));
        return baseResponse;
    }

    @Override
    public BaseResponse<BoMHead> getAllBoMHead(){
        long startTime = System.currentTimeMillis();
        log.info("LogId:{} - BOMServiceImpl - getAllBoMHead - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId()," GET ALL BOM HEAD METHOD START");

        BaseResponse<BoMHead> baseResponse = new BaseResponse<>();
        List<BOMLine> bomLines=new ArrayList<>();
        try {
            List<BoMHead> boMHeads=bomHeadRepository.findByIsDeletedAndSubOrganizationId(false,loginUser.getSubOrgId());
            ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10079S);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setData(boMHeads);
            baseResponse.setLogId(loginUser.getLogId());
            log.info("LogId:{} - BOMServiceImpl - getAllBoMHead - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage());
            return baseResponse;
        }catch (Exception ex){
            ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10076F);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setLogId(loginUser.getLogId());
            long endTime = System.currentTimeMillis();
            log.error("LogId:{} - BOMServiceImpl - getAllBoMHead - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage()+ (endTime - startTime),ex);
        }
        long endTime = System.currentTimeMillis();
        log.info("LogId:{} - BOMServiceImpl - getAllBoMHead - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId()," GET ALL BOM HEAD METHOD EXECUTED TIME :" + (endTime - startTime));
        return baseResponse;
    }

}
