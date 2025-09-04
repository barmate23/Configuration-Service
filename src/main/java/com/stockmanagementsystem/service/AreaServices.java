package com.stockmanagementsystem.service;

import com.stockmanagementsystem.entity.Area;
import com.stockmanagementsystem.entity.Store;
import com.stockmanagementsystem.response.AddressResponse;
import com.stockmanagementsystem.response.BaseResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface AreaServices {

    BaseResponse<Area> getALlAreas(Integer pageNo, Integer pageSize, List<Integer> storeId, List<Integer> areaId);

    BaseResponse<Area> updateAreaById(Integer areaId, Integer storeId, String erpAreaId, String areaName);

    BaseResponse<Area> createArea(Store store,Integer sq);

    BaseResponse<Area> saveArea(Integer storeId, String erpAreaId, String areaName);

    BaseResponse<Area> getAllAreaByStore(List<Integer> storeId);

    BaseResponse<Area> deleteAreaById(Integer areaId);

    BaseResponse<AddressResponse> getAddressByPincodes(Integer pincode);
}
