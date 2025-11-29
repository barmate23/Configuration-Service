package com.stockmanagementsystem.service;

import com.stockmanagementsystem.entity.Reason;
import com.stockmanagementsystem.entity.ReasonCategoryMaster;
import com.stockmanagementsystem.response.BaseResponse;
import com.stockmanagementsystem.response.ItemNameResponse;
import com.stockmanagementsystem.response.ReasonCategoryResponse;
import com.stockmanagementsystem.response.ReasonResponse;

import java.util.List;

public interface ReasonService {

    BaseResponse saveReason(String rejectedReason,Integer reasonCategoryId);

    String generateReasonId(Integer count);

    BaseResponse deleteReasonById(Integer id);

    BaseResponse updateReason(Integer id, String rejectedReason,Integer reasonCategoryId);
    BaseResponse<List<ItemNameResponse>> getItemIdWithName();
    BaseResponse<List<ReasonResponse>> searchReasons(
            Integer pageNumber, Integer pageSize, List<String> reasonId, String reasonCategory, List<String> itemName, Boolean createdYear
    );
    BaseResponse<List<ReasonResponse>> getAllReasons(int page, int pageSize);

    byte[] generateExcelForAllReasons();

    BaseResponse<List<ReasonCategoryResponse>> getReasonCategoryWithId();

    BaseResponse<Reason> getAllReasonsWithoutPagination();

    BaseResponse<ReasonCategoryMaster> getAllCategory();

    BaseResponse<Reason> getAllReasonByCategory(String categoryCode);
}
