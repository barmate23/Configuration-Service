package com.stockmanagementsystem.service;

import com.stockmanagementsystem.entity.BOMLine;
import com.stockmanagementsystem.entity.BoMHead;
import com.stockmanagementsystem.request.BOMHeadRequest;
import com.stockmanagementsystem.response.BaseResponse;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public interface BOMService {
    BaseResponse<BoMHead> getAllBomHeadWithPagination(Integer pageNo, Integer pageSize, List<String> bomERPCode, List<String> varient, List<String> model, Date date);

    BaseResponse<BOMLine> getAllBomLineWithPagination(Integer id, Integer pageNo, Integer pageSize);

    BaseResponse<BOMLine> getAllBomLineByBomId(Integer id);

    BaseResponse<BoMHead> saveBom(BOMHeadRequest bomHeadRequest);

    BaseResponse<BoMHead> updateBom(Integer bomId, BOMHeadRequest bomHeadRequest);

    BaseResponse<BoMHead> deleteBomHeadsById(Integer id);

    BaseResponse<BOMLine> deleteBomLineById(Integer id);

    BaseResponse<BoMHead> getAllBoMHead();
}
