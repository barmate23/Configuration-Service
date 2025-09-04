package com.stockmanagementsystem.controller;

import com.stockmanagementsystem.entity.CommonMaster;
import com.stockmanagementsystem.response.BaseResponse;
import com.stockmanagementsystem.service.CommonService;
import com.stockmanagementsystem.utils.APIConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({APIConstants.BASE_REQUEST + APIConstants.SERVICENAME + APIConstants.COMMON_MASTER_CONTROLLER})
public class CommonMasterController {
    @Autowired
    CommonService commonService;

    @GetMapping("/getMasterData/{type}")
    public BaseResponse<CommonMaster> getMasterData(@PathVariable String type){
        return  commonService.getMasterData(type);
    }

}
