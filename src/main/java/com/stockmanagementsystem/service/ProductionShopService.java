package com.stockmanagementsystem.service;

import com.stockmanagementsystem.entity.AssemblyLine;
import com.stockmanagementsystem.entity.Stage;
import com.stockmanagementsystem.entity.ProductionShop;
import com.stockmanagementsystem.response.BaseResponse;

import java.util.List;

public interface ProductionShopService {
    BaseResponse<ProductionShop> saveProductionShop(ProductionShop shop);
    BaseResponse<ProductionShop> updateProductionShop(Integer id, ProductionShop shop);
    BaseResponse<ProductionShop> deleteProductionShop(Integer id);
    BaseResponse<ProductionShop> getAllProductionShops();
    BaseResponse<ProductionShop> getProductionShopById(Integer id);

    BaseResponse<AssemblyLine> saveProductionLine(AssemblyLine line);
    BaseResponse<AssemblyLine> updateProductionLine(Integer id, AssemblyLine line);
    BaseResponse<AssemblyLine> deleteProductionLine(Integer id);
    BaseResponse<AssemblyLine> getAllProductionLines();
    BaseResponse<AssemblyLine> getProductionLinesByShopId(Integer shopId);

    BaseResponse<Stage> saveProductionLineStage(Stage stage);
    BaseResponse<Stage> updateProductionLineStage(Integer id, Stage stage);
    BaseResponse<Stage> deleteProductionLineStage(Integer id);
    BaseResponse<Stage> getAllProductionLineStages();
    BaseResponse<Stage> getStagesByLineId(Integer lineId);
}
