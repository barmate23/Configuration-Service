package com.stockmanagementsystem.controller;

import com.stockmanagementsystem.entity.AssemblyLine;
import com.stockmanagementsystem.entity.Stage;
import com.stockmanagementsystem.entity.ProductionShop;
import com.stockmanagementsystem.response.BaseResponse;
import com.stockmanagementsystem.service.ProductionShopService;
import com.stockmanagementsystem.utils.APIConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping({APIConstants.BASE_REQUEST + APIConstants.SERVICENAME + APIConstants.PRODUCTION_SHOP_CONTROLLER})
public class ProductionShopController {

    @Autowired
    private ProductionShopService productionShopService;

    // SHOP APIS
    @PostMapping(APIConstants.SAVE_PRODUCTION_SHOP)
    public BaseResponse<ProductionShop> saveProductionShop(@RequestBody ProductionShop shop) {
        return productionShopService.saveProductionShop(shop);
    }

    @PostMapping(APIConstants.UPDATE_PRODUCTION_SHOP)
    public BaseResponse<ProductionShop> updateProductionShop(@PathVariable Integer id, @RequestBody ProductionShop shop) {
        return productionShopService.updateProductionShop(id, shop);
    }

    @DeleteMapping(APIConstants.DELETE_PRODUCTION_SHOP)
    public BaseResponse<ProductionShop> deleteProductionShop(@PathVariable Integer id) {
        return productionShopService.deleteProductionShop(id);
    }

    @GetMapping(APIConstants.GET_ALL_PRODUCTION_SHOPS)
    public BaseResponse<ProductionShop> getAllProductionShops() {
        return productionShopService.getAllProductionShops();
    }

    @GetMapping(APIConstants.GET_PRODUCTION_SHOP_BY_ID)
    public BaseResponse<ProductionShop> getProductionShopById(@PathVariable Integer id) {
        return productionShopService.getProductionShopById(id);
    }

    // LINE APIS
    @PostMapping(APIConstants.SAVE_PRODUCTION_LINE)
    public BaseResponse<AssemblyLine> saveProductionLine(@RequestBody AssemblyLine line) {
        return productionShopService.saveProductionLine(line);
    }

    @PostMapping(APIConstants.UPDATE_PRODUCTION_LINE)
    public BaseResponse<AssemblyLine> updateProductionLine(@PathVariable Integer id, @RequestBody AssemblyLine line) {
        return productionShopService.updateProductionLine(id, line);
    }

    @DeleteMapping(APIConstants.DELETE_PRODUCTION_LINE)
    public BaseResponse<AssemblyLine> deleteProductionLine(@PathVariable Integer id) {
        return productionShopService.deleteProductionLine(id);
    }

    @GetMapping(APIConstants.GET_ALL_PRODUCTION_LINES)
    public BaseResponse<AssemblyLine> getAllProductionLines() {
        return productionShopService.getAllProductionLines();
    }

    @GetMapping(APIConstants.GET_PRODUCTION_LINES_BY_SHOP_ID)
    public BaseResponse<AssemblyLine> getProductionLinesByShopId(@PathVariable Integer shopId) {
        return productionShopService.getProductionLinesByShopId(shopId);
    }

    // STAGE APIS
    @PostMapping(APIConstants.SAVE_PRODUCTION_LINE_STAGE)
    public BaseResponse<Stage> saveProductionLineStage(@RequestBody Stage stage) {
        return productionShopService.saveProductionLineStage(stage);
    }

    @PostMapping(APIConstants.UPDATE_PRODUCTION_LINE_STAGE)
    public BaseResponse<Stage> updateProductionLineStage(@PathVariable Integer id, @RequestBody Stage stage) {
        return productionShopService.updateProductionLineStage(id, stage);
    }

    @DeleteMapping(APIConstants.DELETE_PRODUCTION_LINE_STAGE)
    public BaseResponse<Stage> deleteProductionLineStage(@PathVariable Integer id) {
        return productionShopService.deleteProductionLineStage(id);
    }

    @GetMapping(APIConstants.GET_ALL_PRODUCTION_LINE_STAGES)
    public BaseResponse<Stage> getAllProductionLineStages() {
        return productionShopService.getAllProductionLineStages();
    }

    @GetMapping(APIConstants.GET_STAGES_BY_LINE_ID)
    public BaseResponse<Stage> getStagesByLineId(@PathVariable Integer lineId) {
        return productionShopService.getStagesByLineId(lineId);
    }
}
