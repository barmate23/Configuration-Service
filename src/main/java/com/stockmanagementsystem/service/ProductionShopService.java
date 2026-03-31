package com.stockmanagementsystem.service;

import com.stockmanagementsystem.entity.AssemblyLine;
import com.stockmanagementsystem.entity.ProductionShop;
import com.stockmanagementsystem.entity.Stage;
import com.stockmanagementsystem.request.ProductionShopRequest;
import com.stockmanagementsystem.response.AssemblyLineResponse;
import com.stockmanagementsystem.response.BaseResponse;
import com.stockmanagementsystem.response.ProductionShopResponse;
import com.stockmanagementsystem.response.StageResponse;

import java.util.List;

public interface ProductionShopService {
    BaseResponse<ProductionShopResponse> saveProductionShop(ProductionShopRequest request);
    BaseResponse<ProductionShopResponse> updateProductionShop(Integer id, ProductionShopRequest request);
    BaseResponse<ProductionShopResponse> getAllProductionShops();
    BaseResponse<ProductionShopResponse> getProductionShopById(Integer id);
    BaseResponse<ProductionShopResponse> deleteProductionShopById(Integer id);
    BaseResponse<ProductionShopResponse> getAllProductionShopsWithPagination(Integer pageNo, Integer pageSize);
    BaseResponse<ProductionShopResponse> getProductionShopHierarchy();
    BaseResponse<AssemblyLineResponse> getProductionLinesByShopId(Integer shopId);
    BaseResponse<AssemblyLineResponse> getProductionLineById(Integer id);
    BaseResponse<StageResponse> getStagesByLineId(Integer lineId);
}
