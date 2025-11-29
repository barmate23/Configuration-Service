package com.stockmanagementsystem.controller;

import com.stockmanagementsystem.entity.Reason;
import com.stockmanagementsystem.entity.ReasonCategoryMaster;
import com.stockmanagementsystem.entity.Supplier;
import com.stockmanagementsystem.request.ReasonsRequest;
import com.stockmanagementsystem.response.BaseResponse;
import com.stockmanagementsystem.response.ItemNameResponse;
import com.stockmanagementsystem.response.ReasonCategoryResponse;
import com.stockmanagementsystem.response.ReasonResponse;
import com.stockmanagementsystem.service.ReasonService;
import com.stockmanagementsystem.utils.APIConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping({APIConstants.BASE_REQUEST + APIConstants.SERVICENAME + APIConstants.REASON_CONTROLLER})
public class ReasonController {

    @Autowired
    ReasonService reasonService;

    @PostMapping(APIConstants.SAVE_REASON)
    public BaseResponse saveReason(@RequestParam String rejectedReason,@RequestParam Integer reasonCategoryId){
        return reasonService.saveReason(rejectedReason,reasonCategoryId);
    }

    @DeleteMapping(APIConstants.DELETE_REASON)
    public BaseResponse<Reason> deleteReasonById(@PathVariable Integer id){
        return reasonService.deleteReasonById(id);
    }

    @PutMapping(APIConstants.UPDATE_REASON)
    public ResponseEntity<BaseResponse> updateReason(@RequestParam Integer id, @RequestParam String rejectedReason,@RequestParam Integer reasonCategoryId) {
        BaseResponse baseResponse = reasonService.updateReason(id, rejectedReason,reasonCategoryId);
        return new ResponseEntity<>(baseResponse, HttpStatus.valueOf(baseResponse.getStatus()));
    }

    @GetMapping(APIConstants.GET_ITEM)
    public ResponseEntity<BaseResponse<List<ItemNameResponse>>> getItemsWithIds() {
        BaseResponse<List<ItemNameResponse>> response = reasonService.getItemIdWithName();
        HttpStatus status = HttpStatus.resolve(response.getCode());
        if (status == null) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping(APIConstants.GET_REASON_SEARCH)
    public BaseResponse<List<ReasonResponse>> searchReasons(
            @RequestParam(value = "pageNumber", defaultValue = "0", required = false) Integer pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) Integer pageSize,
            @RequestParam(required = false) List<String> reasonId,
            @RequestParam(required = false) String reasonCategory,
            @RequestParam(required = false) List<String> itemName,
            @RequestParam(required = false) Boolean userCreatedReason
    ) {

        return reasonService.searchReasons(pageNumber, pageSize, reasonId, reasonCategory, itemName,userCreatedReason);
    }

    @GetMapping(APIConstants.GET_REASON)
    public BaseResponse<List<ReasonResponse>> getAllDocks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int pageSize) {

        return reasonService.getAllReasons(page, pageSize);
    }


    @GetMapping(APIConstants.GET_EXCEL_REASONS)
    public ResponseEntity<byte[]> generateExcelReason() {
        byte[] excelContent = reasonService.generateExcelForAllReasons();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "reasons.xlsx");

        return new ResponseEntity<>(excelContent, headers, HttpStatus.OK);
    }

    @GetMapping(APIConstants.GET_All_REASONS)
    public ResponseEntity<BaseResponse<List<ReasonCategoryResponse>>> getReasonCategoryWithIds() {
        BaseResponse<List<ReasonCategoryResponse>> response = reasonService.getReasonCategoryWithId();
        HttpStatus status = HttpStatus.resolve(response.getCode());
        if (status == null) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/getAllReasonsWithoutPagination")
    public BaseResponse<Reason> getAllSuppliers(){
        return reasonService.getAllReasonsWithoutPagination();
    }

    @GetMapping(APIConstants.GET_REASON_BY_CATEGORY)
    public BaseResponse<ReasonCategoryMaster> getAllCategory() {

        return reasonService.getAllCategory();
    }

    @GetMapping("/getReasonByCategoryCode/{categoryCode}")
    public BaseResponse<Reason> getAllReasonByCategory(@PathVariable String categoryCode){
        return reasonService.getAllReasonByCategory(categoryCode);
    }

}

