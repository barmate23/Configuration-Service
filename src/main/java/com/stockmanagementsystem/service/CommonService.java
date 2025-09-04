package com.stockmanagementsystem.service;

import com.stockmanagementsystem.entity.CommonMaster;
import com.stockmanagementsystem.response.BaseResponse;
import org.springframework.stereotype.Service;

@Service
public interface CommonService {
    BaseResponse<CommonMaster> getMasterData(String type);
}
