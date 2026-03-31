package com.stockmanagementsystem.controller;

import com.stockmanagementsystem.request.ProductionShopRequest;
import com.stockmanagementsystem.response.*;
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

    @PostMapping(APIConstants.SAVE_PRODUCTION_SHOP)
    public BaseResponse<ProductionShopResponse> saveProductionShop(@RequestBody ProductionShopRequest request) {
        return productionShopService.saveProductionShop(request);
    }

    @PostMapping(APIConstants.UPDATE_PRODUCTION_SHOP)
    public BaseResponse<ProductionShopResponse> updateProductionShop(@PathVariable Integer id, @RequestBody ProductionShopRequest request) {
        return productionShopService.updateProductionShop(id, request);
    }

    @GetMapping(APIConstants.GET_ALL_PRODUCTION_SHOPS)
    public BaseResponse<ProductionShopResponse> getAllProductionShops() {
        return productionShopService.getAllProductionShops();
    }

    @GetMapping(APIConstants.GET_ALL_PRODUCTION_SHOPS_WITH_PAGINATION)
    public BaseResponse<ProductionShopResponse> getAllProductionShopsWithPagination(
            @RequestParam(defaultValue = "0") Integer pageNo,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return productionShopService.getAllProductionShopsWithPagination(pageNo, pageSize);
    }

    @DeleteMapping(APIConstants.DELETE_PRODUCTION_SHOP_BY_ID)
    public BaseResponse<ProductionShopResponse> deleteProductionShopById(@PathVariable Integer id) {
        return productionShopService.deleteProductionShopById(id);
    }

    @GetMapping(APIConstants.GET_PRODUCTION_LINES_BY_SHOP_ID)
    public BaseResponse<AssemblyLineResponse> getProductionLinesByShopId(@PathVariable Integer shopId) {
        return productionShopService.getProductionLinesByShopId(shopId);
    }

    @GetMapping(APIConstants.GET_PRODUCTION_SHOP_HIERARCHY)
    public BaseResponse<ProductionShopResponse> getProductionShopHierarchy() {
        return productionShopService.getProductionShopHierarchy();
    }

    @GetMapping(APIConstants.GET_PRODUCTION_LINE_BY_ID)
    public BaseResponse<AssemblyLineResponse> getProductionLineById(@PathVariable Integer id) {
        return productionShopService.getProductionLineById(id);
    }

    @GetMapping(APIConstants.GET_STAGES_BY_LINE_ID)
    public BaseResponse<StageResponse> getStagesByLineId(@PathVariable Integer lineId) {
        return productionShopService.getStagesByLineId(lineId);
    }
}
