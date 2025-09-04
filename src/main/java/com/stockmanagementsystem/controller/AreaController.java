package com.stockmanagementsystem.controller;

import com.stockmanagementsystem.entity.Address;
import com.stockmanagementsystem.entity.Area;
import com.stockmanagementsystem.response.AddressResponse;
import com.stockmanagementsystem.response.BaseResponse;
import com.stockmanagementsystem.service.AreaServices;
import com.stockmanagementsystem.utils.APIConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.hibernate.sql.InFragment.NULL;

@RestController
@Slf4j
@RequestMapping({APIConstants.BASE_REQUEST + APIConstants.SERVICENAME+APIConstants.AREA_CONTROLLER})
public class AreaController {

    @Autowired
    AreaServices areaServices;

    @GetMapping(APIConstants.GET_ALL_AREA_WITH_PAGINATION)
    public BaseResponse<Area> getALlAreasWithPagination(@RequestParam(defaultValue = "0") Integer pageNo,
                                                        @RequestParam(defaultValue = "10") Integer pageSize ,
                                                        @RequestParam(required = false ) List<Integer> storeId,@RequestParam(required = false) List<Integer> areaId){
        return areaServices.getALlAreas(pageNo,pageSize,storeId,areaId);
    }

    @GetMapping(APIConstants.GET_ALL_AREA_BY_STORE_ID)
    public BaseResponse<Area> getALlAreasByStoreId(@PathVariable List<Integer> id){
        return areaServices.getAllAreaByStore(id);
    }
    @PostMapping(APIConstants.SAVE_AREA)
    public BaseResponse<Area> saveArea(@RequestParam Integer storeId,@RequestParam String erpAreaId,@RequestParam String areaName ){
        return areaServices.saveArea(storeId,erpAreaId,areaName);
    }
    @PostMapping(APIConstants.UPDATE_AREA)
    public BaseResponse<Area> updateArea(@PathVariable Integer id,@RequestParam Integer storeId,@RequestParam String erpAreaId,@RequestParam String areaName ){
        return areaServices.updateAreaById(id,storeId,erpAreaId,areaName);
    }
    @DeleteMapping(APIConstants.DELETED_BY_AREA_ID)
    public BaseResponse<Area> deleteAreaById(@PathVariable Integer id){
        return areaServices.deleteAreaById(id);
    }


    @GetMapping({APIConstants.GET_ADDRESS_BY_PINCODE})
    public BaseResponse<AddressResponse> getAddressDetailsByPincode(@PathVariable Integer pincode) {
        BaseResponse<AddressResponse> response = areaServices.getAddressByPincodes(pincode);
        return response;
    }

}
