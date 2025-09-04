package com.stockmanagementsystem.service;

import com.stockmanagementsystem.entity.CommonMaster;
import com.stockmanagementsystem.entity.LoginUser;
import com.stockmanagementsystem.entity.ResponseMessage;
import com.stockmanagementsystem.repository.CommonMasterRepository;
import com.stockmanagementsystem.response.BaseResponse;
import com.stockmanagementsystem.utils.ResponseKeyConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.stockmanagementsystem.utils.GlobalMessages.getResponseMessages;
@Slf4j
@Service
public class CommonServiceImpl implements CommonService {

    @Autowired
    CommonMasterRepository commonMasterRepository;

    @Autowired
    LoginUser loginUser;

    @Override
    public BaseResponse<CommonMaster> getMasterData(String type){
        long startTime = System.currentTimeMillis();
        log.info("LogId:{} - CommonServiceImpl - getMasterData - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId()," GET MASTER DATA METHOD START");

        BaseResponse<CommonMaster> baseResponse = new BaseResponse<>();
        List<CommonMaster> commonMasters=new ArrayList<>();
        try {
            commonMasters = this.commonMasterRepository.findByIsDeletedAndType(false,type);
            ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10085S);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setData(commonMasters);
            baseResponse.setLogId(loginUser.getLogId());
            log.info("LogId:{} - CommonServiceImpl - getMasterData - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage());
            return baseResponse;
        }catch (Exception ex){
            ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10084F);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setLogId(loginUser.getLogId());
            long endTime = System.currentTimeMillis();
            log.error("LogId:{} - CommonServiceImpl - getMasterData - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage()+ (endTime - startTime),ex);
        }
        long endTime = System.currentTimeMillis();
        log.info("LogId:{} - CommonServiceImpl - getMasterData - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId()," GET MASTER DATA METHOD EXECUTED TIME :" + (endTime - startTime));
        return baseResponse;
    }
}
