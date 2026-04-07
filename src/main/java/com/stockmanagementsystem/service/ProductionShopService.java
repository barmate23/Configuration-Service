package com.stockmanagementsystem.service;

import com.stockmanagementsystem.entity.AssemblyLine;
import com.stockmanagementsystem.entity.Stage;
import com.stockmanagementsystem.entity.ProductionShop;
import com.stockmanagementsystem.request.AssemblyLineRequest;
import com.stockmanagementsystem.request.ProductionShopRequest;
import com.stockmanagementsystem.request.StageRequest;
import com.stockmanagementsystem.response.BaseResponse;

import java.util.List;

public interface ProductionShopService {
    BaseResponse<ProductionShop> saveProductionShop(ProductionShopRequest shop);
    BaseResponse<ProductionShop> updateProductionShop(Integer id, ProductionShopRequest shop);
    BaseResponse<ProductionShop> deleteProductionShop(Integer id);
    BaseResponse<ProductionShop> getAllProductionShops();
    BaseResponse<ProductionShop> getProductionShopById(Integer id);

    BaseResponse<AssemblyLine> saveProductionLine(AssemblyLineRequest line);
    BaseResponse<AssemblyLine> updateProductionLine(Integer id, AssemblyLineRequest line);
    BaseResponse<AssemblyLine> deleteProductionLine(Integer id);
    BaseResponse<AssemblyLine> getAllProductionLines();
    BaseResponse<AssemblyLine> getProductionLinesByShopId(Integer shopId);

    BaseResponse<Stage> saveProductionLineStage(StageRequest stage);
    BaseResponse<Stage> updateProductionLineStage(Integer id, StageRequest stage);
    BaseResponse<Stage> deleteProductionLineStage(Integer id);
    BaseResponse<Stage> getAllProductionLineStages();
    BaseResponse<Stage> getStagesByLineId(Integer lineId);
}
