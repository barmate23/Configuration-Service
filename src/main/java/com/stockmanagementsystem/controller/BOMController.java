package com.stockmanagementsystem.controller;

import com.stockmanagementsystem.entity.BOMLine;
import com.stockmanagementsystem.entity.BoMHead;
import com.stockmanagementsystem.entity.Stage;
import com.stockmanagementsystem.request.BOMHeadRequest;
import com.stockmanagementsystem.response.BaseResponse;
import com.stockmanagementsystem.service.BOMService;
import com.stockmanagementsystem.utils.APIConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping({APIConstants.BASE_REQUEST + APIConstants.SERVICENAME + APIConstants.BOM_CONTROLLER})
public class BOMController {

    @Autowired
    BOMService bomService;

    @GetMapping(APIConstants.GET_ALL_BOM_HEAD_WITH_PAGINATION)
    public BaseResponse<BoMHead> getAllBOMHeadsWithPagination(
            @RequestParam(defaultValue = "0") Integer pageNo,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) List<String> bomERPCode,
            @RequestParam(required = false) List<String> varient,
            @RequestParam(required = false) List<String> model,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date date){
        return bomService.getAllBomHeadWithPagination(pageNo,pageSize,bomERPCode,varient,model,date);
    }

    @GetMapping(APIConstants.GET_ALL_BOM_LINES_WITH_PAGINATION)
    public BaseResponse<BOMLine> getAllBomLineWithPagination(
            @RequestParam Integer id,
            @RequestParam(defaultValue = "0") Integer pageNo,
            @RequestParam(defaultValue = "10") Integer pageSize){
        return bomService.getAllBomLineWithPagination(id,pageNo,pageSize);
    }

    @GetMapping(APIConstants.GET_ALL_B0M_LINE_BY_BOM_ID)
    public BaseResponse<BOMLine> getAllBomLineByBomId(
            @PathVariable Integer id){
        return bomService.getAllBomLineByBomId(id);
    }

    @PostMapping("/saveBillOfMaterial")
    public BaseResponse<BoMHead> saveBom(@RequestBody BOMHeadRequest bomHeadRequest){
        return bomService.saveBom(bomHeadRequest);
    }
    @PostMapping("/updateBillOfMaterial/{id}")
    public BaseResponse<BoMHead> updateBom(@PathVariable Integer id,
                                           @RequestBody BOMHeadRequest bomHeadRequest){
        return bomService.updateBom(id,bomHeadRequest);
    }
    @DeleteMapping("/deleteBomHeadsById/{id}")
    public BaseResponse<BoMHead> deleteBomHeadsById(@PathVariable Integer id){
        return bomService.deleteBomHeadsById(id);
    }
    @DeleteMapping("/deleteBomLineById/{id}")
    public BaseResponse<BOMLine> deleteBomLineById(@PathVariable Integer id){
        return bomService.deleteBomLineById(id);
    }
    @GetMapping("/getAllBoMHead")
    public BaseResponse<BoMHead> getAllBoMHead(){
        return bomService.getAllBoMHead();
    }

}
