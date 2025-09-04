package com.stockmanagementsystem.service;

import com.stockmanagementsystem.entity.Store;
import com.stockmanagementsystem.entity.StoreName;
import com.stockmanagementsystem.entity.Users;
import com.stockmanagementsystem.request.StoreRequest;
import com.stockmanagementsystem.response.BaseResponse;
import com.stockmanagementsystem.response.CreateYearResponse;
import com.stockmanagementsystem.response.StoreResponse;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public interface StoreService {
    BaseResponse<Store> saveStore(StoreRequest storeRequest);

    BaseResponse<Store> updateStore(Integer storeId, StoreRequest storeRequest);

    BaseResponse<StoreResponse> getStoresWithPagination(Integer pageNo, Integer pageSize);

    BaseResponse<StoreResponse> getStoreListByERPStoreId(Integer pageNo, Integer pageSize, List<Integer> storeId, List<String> erpStoreId , Date startDate, Date endDate);

    BaseResponse<Store> getAllStores();

//    BaseResponse<Store> getAllStoresAcceptItemAssignInLocation();

    BaseResponse<StoreName> getAllStoresName();

    BaseResponse<Store> deleteStoreById(Integer storeId);

    byte[] generateStoreBarcodePDF();

    BaseResponse<Users> getAllStoreKeeper();

    BaseResponse getAreaLicense();

    BaseResponse<CreateYearResponse> getAllYears(String type);
}
