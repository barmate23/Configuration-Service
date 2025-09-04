package com.stockmanagementsystem.service;

import com.stockmanagementsystem.entity.*;
import com.stockmanagementsystem.repository.AssemblyLineRepository;
import com.stockmanagementsystem.repository.StageRepository;
import com.stockmanagementsystem.request.AssemblyLineRequest;
import com.stockmanagementsystem.request.StageRequest;
import com.stockmanagementsystem.response.BaseResponse;
import com.stockmanagementsystem.utils.ResponseKeyConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.stockmanagementsystem.utils.GlobalMessages.getResponseMessages;
@Slf4j
@Service
public class AssemblyLineImpl implements  AssemblyLineService{
    @Autowired
    private AssemblyLineRepository assemblyLineRepository;
    @Autowired
    private StageRepository stageRepository;
    @Autowired
    LoginUser loginUser;
    @Override
    public BaseResponse<AssemblyLine> saveAssemblyLine(AssemblyLineRequest assemblyLineRequest) {
        long startTime = System.currentTimeMillis();
        log.info("LogId:{} - AssemblyLineImpl - saveAssemblyLine - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId()," SAVE ASSEMBLY LINE IMPL METHOD START");
        BaseResponse<AssemblyLine> response = new BaseResponse<>();
        try {
            AssemblyLine assemblyLine = new AssemblyLine();
            assemblyLine.setLineNumber(assemblyLineRequest.getLineNumber());
            List<AssemblyLine> assemblyLines=assemblyLineRepository.findByIsDeletedAndSubOrganizationId(false,loginUser.getSubOrgId());
            assemblyLine.setAssemblyLineId(assemblyLineIdGenerator());
            assemblyLine.setIsDeleted(false);
            assemblyLine.setCreatedOn(new Date());
            assemblyLine.setCreatedBy(loginUser.getUserId());
            assemblyLine.setModifiedBy(loginUser.getUserId());
            assemblyLine.setModifiedOn(new Date());
            assemblyLine.setOrganizationId(loginUser.getOrgId());
            assemblyLine.setSubOrganizationId(loginUser.getSubOrgId());
            assemblyLineRepository.save(assemblyLine);
            if (assemblyLine.getId() != null) {
                List<Stage> stages = new ArrayList<>();
                Integer st=1;
                for (StageRequest stageRequest : assemblyLineRequest.getStageRequests()) {
                    Stage stage = new Stage();

                    String stageCode=stageIdGenerator(assemblyLine.getId(),st);
                    st++;
                    stage.setStageCode(stageCode);
                    stage.setStageId(assemblyLine.getAssemblyLineId()+"-"+stageCode);
                    stage.setAssemblyLine(assemblyLine);
                    stage.setStageName(stageRequest.getStageName());
                    stage.setStageId(stageRequest.getStageId());
                    stage.setIsDeleted(false);
                    stage.setCreatedOn(new Date());
                    stage.setCreatedBy(loginUser.getUserId());
                    stage.setModifiedBy(loginUser.getUserId());
                    stage.setModifiedOn(new Date());
                    stage.setOrganizationId(loginUser.getOrgId());
                    stage.setSubOrganizationId(loginUser.getSubOrgId());
                    stages.add(stage);
                }
                stageRepository.saveAll(stages);
                List<AssemblyLine> assemblyLineList = new ArrayList<>();
                assemblyLineList.add(assemblyLine);
                ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10024S);
                response.setCode(responseMessage.getCode());
                response.setStatus(responseMessage.getStatus());
                response.setMessage(responseMessage.getMessage());
                response.setData(assemblyLineList);
                log.info("LogId:{} - AssemblyLineImpl - saveAssemblyLine - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage());

            } else {
                ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10023F);
                response.setCode(responseMessage.getCode());
                response.setStatus(responseMessage.getStatus());
                response.setMessage(responseMessage.getMessage());
                response.setData(null);
                log.info("LogId:{} - AssemblyLineImpl - saveAssemblyLine - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage());

            }
        } catch (Exception e) {

            ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10023F);
            response.setCode(responseMessage.getCode());
            response.setStatus(responseMessage.getStatus());
            response.setMessage(responseMessage.getMessage());
            response.setData(null);
            long endTime = System.currentTimeMillis();
            log.error("LogId:{} - AssemblyLineImpl - saveAssemblyLine - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage() + (endTime - startTime),e);

        }
        long endTime = System.currentTimeMillis();
        log.info("LogId:{} - AssemblyLineImpl - saveAssemblyLine - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId()," SAVE ASSEMBLY LINE IMPL METHOD EXECUTED TIME :" + (endTime - startTime));
        return response;
    }

    @Override
    public BaseResponse<AssemblyLine> updateAssemblyLine(Integer id,AssemblyLineRequest assemblyLineRequest) {
        long startTime = System.currentTimeMillis();
        log.info("LogId:{} - AssemblyLineImpl - updateAssemblyLine - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId()," UPDATE ASSEMBLY LINE IMPL METHOD START");
       // LoginUser loginUser = new LoginUser();
        BaseResponse<AssemblyLine> response = new BaseResponse<>();
        try {
            AssemblyLine assemblyLine = assemblyLineRepository.findByIsDeletedAndId(false,id);
            assemblyLine.setLineNumber(assemblyLineRequest.getLineNumber());
            assemblyLine.setAssemblyLineId(assemblyLineRequest.getAssemblyLineId());
            assemblyLine.setIsDeleted(false);
            assemblyLine.setCreatedOn(new Date());
            assemblyLine.setCreatedBy(loginUser.getUserId());
            assemblyLine.setModifiedBy(loginUser.getUserId());
            assemblyLine.setModifiedOn(new Date());
            assemblyLine.setOrganizationId(loginUser.getOrgId());
            assemblyLine.setSubOrganizationId(loginUser.getSubOrgId());
            assemblyLineRepository.save(assemblyLine);
            if (assemblyLine.getId() != null) {
                List<Stage> stages = new ArrayList<>();
                Integer st=1;
                for (StageRequest stageRequest : assemblyLineRequest.getStageRequests()) {
                    Stage stage = null;
                    if (stageRequest.getId()!=null){
                        stage=stageRepository.findByIsDeletedAndId(false,stageRequest.getId());
                    }else {
                        stage=new Stage();
                        stage.setStageCode(stageIdGenerator(assemblyLine.getId(),st));
                        st++;
                    }
                    stage.setAssemblyLine(assemblyLine);
                    stage.setStageName(stageRequest.getStageName());
                    stage.setStageId(stageRequest.getStageId());
                    stage.setIsDeleted(false);
                    stage.setCreatedOn(new Date());
                    stage.setCreatedBy(loginUser.getUserId());
                    stage.setModifiedBy(loginUser.getUserId());
                    stage.setModifiedOn(new Date());
                    stage.setOrganizationId(loginUser.getOrgId());
                    stage.setSubOrganizationId(loginUser.getSubOrgId());
                    stages.add(stage);
                }
                stageRepository.saveAll(stages);
                List<AssemblyLine> assemblyLineList = new ArrayList<>();
                assemblyLineList.add(assemblyLine);

                ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10023S);
                response.setCode(responseMessage.getCode());
                response.setStatus(responseMessage.getStatus());
                response.setMessage(responseMessage.getMessage());
                response.setData(assemblyLineList);
                log.info("LogId:{} - AssemblyLineImpl - updateAssemblyLine - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage());
            } else {
                ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10022F);
                response.setCode(responseMessage.getCode());
                response.setStatus(responseMessage.getStatus());
                response.setMessage(responseMessage.getMessage());
                response.setData(null);
                log.info("LogId:{} - AssemblyLineImpl - updateAssemblyLine - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage());
            }
        } catch (Exception e) {
            ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10022F);
            response.setCode(responseMessage.getCode());
            response.setStatus(responseMessage.getStatus());
            response.setMessage(responseMessage.getMessage());
            response.setData(null);
            long endTime = System.currentTimeMillis();
            log.error("LogId:{} - AssemblyLineImpl - updateAssemblyLine - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage() + (endTime - startTime),e);

        }
        long endTime = System.currentTimeMillis();
        log.info("LogId:{} - AssemblyLineImpl - updateAssemblyLine - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId()," UPDATE ASSEMBLY LINE IMPL METHOD EXECUTED TIME :" + (endTime - startTime));
        return response;
    }
    @Override
    public BaseResponse<AssemblyLine> getAllAssemblyLineWithPagination(List<Integer> id, Integer pageNo, Integer pageSize){
        long startTime = System.currentTimeMillis();
        log.info("LogId:{} - AssemblyLineImpl - getAllAssemblyLineWithPagination - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId()," GET ALL ASSEMBLY LINE WITH PAGINATION METHOD START");

        BaseResponse<AssemblyLine> baseResponse = new BaseResponse<>();
        List<AssemblyLine> assemblyLines=new ArrayList<>();
        Page<AssemblyLine> pageResult=null;
        try {
            final Pageable pageable = (Pageable) PageRequest.of((int) pageNo, (int) pageSize);
            if(id!=null){
                pageResult= this.assemblyLineRepository.findByIsDeletedAndSubOrganizationIdAndIdIn(false,loginUser.getSubOrgId(),id, pageable);
            }else {
                pageResult= this.assemblyLineRepository.findByIsDeletedAndSubOrganizationId(false,loginUser.getSubOrgId(), pageable);
            }
            baseResponse.setTotalPageCount(pageResult.getTotalPages());
            assemblyLines=pageResult.getContent();
            baseResponse.setTotalRecordCount(pageResult.getTotalElements());

            ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10022S);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setData(assemblyLines);
            baseResponse.setLogId(loginUser.getLogId());
            log.info("LogId:{} - AssemblyLineImpl - getAllAssemblyLineWithPagination - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage());

            return baseResponse;
        }catch (Exception ex){

            ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10021F);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setLogId(loginUser.getLogId());
            long endTime = System.currentTimeMillis();
            log.error("LogId:{} - AssemblyLineImpl - getAllAssemblyLineWithPagination - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage() + (endTime - startTime),ex);
        }
        long endTime = System.currentTimeMillis();
        log.info("LogId:{} - AssemblyLineImpl - getAllAssemblyLineWithPagination - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId()," GET ALL ASSEMBLY LINE WITH PAGINATION METHOD EXECUTED TIME :" + (endTime - startTime));
        return baseResponse;
    }
    @Override
    public BaseResponse<AssemblyLine> getAllAssemblyLines(){
        long startTime = System.currentTimeMillis();
        log.info("LogId:{} - AssemblyLineImpl - getAllAssemblyLines - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId()," GET ALL ASSEMBLY LINE METHOD START");

        BaseResponse<AssemblyLine> baseResponse = new BaseResponse<>();
        List<AssemblyLine> assemblyLines=new ArrayList<>();
        try {
            assemblyLines = this.assemblyLineRepository.findByIsDeletedAndSubOrganizationId(false,loginUser.getSubOrgId());
            ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10021S);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setData(assemblyLines);
            baseResponse.setLogId(loginUser.getLogId());
            log.info("LogId:{} - AssemblyLineImpl - getAllAssemblyLines - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage());
            return baseResponse;
        }catch (Exception ex){
            ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10020F);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setLogId(loginUser.getLogId());
            long endTime = System.currentTimeMillis();
            log.error("LogId:{} - AssemblyLineImpl - getAllAssemblyLines - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage() + (endTime - startTime),ex);
        }
        long endTime = System.currentTimeMillis();
        log.info("LogId:{} - AssemblyLineImpl - getAllAssemblyLines - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId()," GET ALL ASSEMBLY LINE METHOD EXECUTED TIME :" + (endTime - startTime));
        return baseResponse;
    }
    @Override
    public BaseResponse<Stage> getAllStageWithPagination(Integer id,Integer pageNo, Integer pageSize){
        long startTime = System.currentTimeMillis();
        log.info("LogId:{} - AssemblyLineImpl - getAllStageWithPagination - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId()," GET ALL STAGE WITH PAGINATION METHOD START");

        BaseResponse<Stage> baseResponse = new BaseResponse<>();
        List<Stage> stages=new ArrayList<>();
        try {
            final Pageable pageable = (Pageable) PageRequest.of((int) pageNo, (int) pageSize);
            Page<Stage> pageResult = this.stageRepository.findByIsDeletedAndAssemblyLineId(false,id, pageable);
            baseResponse.setTotalPageCount(pageResult.getTotalPages());
            stages=pageResult.getContent();
            baseResponse.setTotalRecordCount(pageResult.getTotalElements());
            ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10020S);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setData(stages);
            baseResponse.setLogId(loginUser.getLogId());
            log.info("LogId:{} - AssemblyLineImpl - getAllStageWithPagination - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage());
            return baseResponse;
        }catch (Exception ex){
            ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10019F);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setData(new ArrayList<>());
            baseResponse.setLogId(loginUser.getLogId());
            long endTime = System.currentTimeMillis();
            log.error("LogId:{} - AssemblyLineImpl - getAllStageWithPagination - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage() + (endTime - startTime),ex);
        }
        long endTime = System.currentTimeMillis();
        log.info("LogId:{} - AssemblyLineImpl - getAllStageWithPagination - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId()," GET ALL STAGE WITH PAGINATION METHOD EXECUTED TIME :" + (endTime - startTime));

        return baseResponse;
    }
    @Override
    public BaseResponse<AssemblyLine> deleteAssemblyLineById(Integer id) {
        long startTime = System.currentTimeMillis();
        log.info("LogId:{} - AssemblyLineImpl - deleteAssemblyLineById - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId()," DELETE ASSEMBLY LINE BY ID METHOD START");

        BaseResponse<AssemblyLine> baseResponse=new BaseResponse<>();
        AssemblyLine assemblyLine = assemblyLineRepository.findById(id).get();
        List<AssemblyLine> assemblyLines=new ArrayList<>();
        List<Stage> stages= stageRepository.findByIsDeletedAndAssemblyLineId(false,id);
        if (stages!=null && stages.size()==0){
            assemblyLine.setIsDeleted(true);
            assemblyLineRepository.save(assemblyLine);
            assemblyLines.add(assemblyLine);
            ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10019S);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setData(assemblyLines);
            baseResponse.setLogId(loginUser.getLogId());
            log.info("LogId:{} - AssemblyLineImpl - deleteAssemblyLineById - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage());
        }else {
            ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10012E);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setData(new ArrayList<>());
            baseResponse.setLogId(loginUser.getLogId());
            log.info("LogId:{} - AssemblyLineImpl - deleteAssemblyLineById - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage());
        }
        long endTime = System.currentTimeMillis();
        log.info("LogId:{} - AssemblyLineImpl - deleteAssemblyLineById - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId()," DELETE ASSEMBLY LINE BY ID METHOD EXECUTED TIME :" + (endTime - startTime));
        return baseResponse;
    }
    @Override
    public BaseResponse<Stage> deleteStageById(Integer id) {
        long startTime = System.currentTimeMillis();
        log.info("LogId:{} - AssemblyLineImpl - deleteStageById - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId()," DELETE STAGE BY ID METHOD START");

        BaseResponse<Stage> baseResponse=new BaseResponse<>();
        List<Stage> stages=new ArrayList<>();
        Stage stage= stageRepository.findByIsDeletedAndId(false,id);
        try {
            stage.setIsDeleted(true);
            stageRepository.save(stage);
            stages.add(stage);

            ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10018S);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setData(stages);
            baseResponse.setLogId(loginUser.getLogId());
            log.info("LogId:{} - AssemblyLineImpl - deleteStageById - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage());
        }catch (Exception e){
            ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10018F);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setData(new ArrayList<>());
            baseResponse.setLogId(loginUser.getLogId());
            long endTime = System.currentTimeMillis();
            log.error("LogId:{} - AssemblyLineImpl - deleteStageById - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage() + (endTime - startTime),e);

        }
        long endTime = System.currentTimeMillis();
        log.info("LogId:{} - AssemblyLineImpl - deleteStageById - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId()," DELETE STAGE BY ID METHOD EXECUTED TIME :" + (endTime - startTime));
        return baseResponse;
    }
    @Override
    public BaseResponse<Stage> getAllStage(){
        long startTime = System.currentTimeMillis();
        log.info("LogId:{} - AssemblyLineImpl - getAllStage - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId()," GET ALL STAGE METHOD START");

        BaseResponse<Stage> baseResponse = new BaseResponse<>();
        try {
            List<Stage> stages = this.stageRepository.findByIsDeletedAndSubOrganizationId(false,loginUser.getSubOrgId()).stream().distinct().collect(Collectors.toList());
            ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10021S);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setData(stages);
            baseResponse.setLogId(loginUser.getLogId());
            log.info("LogId:{} - AssemblyLineImpl - getAllStage - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage());
            return baseResponse;
        }catch (Exception ex){
            ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10020F);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setLogId(loginUser.getLogId());
            long endTime = System.currentTimeMillis();
            log.error("LogId:{} - AssemblyLineImpl - getAllStage - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage() + (endTime - startTime),ex);
        }
        long endTime = System.currentTimeMillis();
        log.info("LogId:{} - AssemblyLineImpl - getAllStage - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId()," GET ALL STAGE METHOD EXECUTED TIME :" + (endTime - startTime));
        return baseResponse;
    }

    public String assemblyLineIdGenerator() {
        List<AssemblyLine> assemblyLines = assemblyLineRepository.findBySubOrganizationId(loginUser.getSubOrgId());
        String assemblyLineId = null;
        if (assemblyLines != null && assemblyLines.size()!=0 ) {
            // Extract the last assemblyLineId and parse the numerical part
            int itmNumber = assemblyLines.size();
            // Use String.format to ensure the number is always padded to 3 digits
            assemblyLineId = String.format("%s-ASM%03d", loginUser.getSubOrganizationCode(), itmNumber++);
        }else{
            assemblyLineId=loginUser.getSubOrganizationCode()+"-ASM001";
        }
        return assemblyLineId;
    }
    public String stageIdGenerator(Integer asmlId,Integer count) {
        List<Stage> stages = stageRepository.findBySubOrganizationIdAndAssemblyLineId(loginUser.getSubOrgId(),asmlId);
        String stageId = null;
        if (stages != null) {
            // Extract the last assemblyLineId and parse the numerical part
            int itmNumber = stages.size();
            // Use String.format to ensure the number is always padded to 3 digits
            stageId = String.format("STG%03d",itmNumber+count);
        }else{
            stageId = String.format("STG%03d",count);
        }
        return stageId;
    }


}
