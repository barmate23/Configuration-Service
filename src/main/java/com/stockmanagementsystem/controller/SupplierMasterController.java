package com.stockmanagementsystem.controller;
import com.stockmanagementsystem.entity.Item;
import com.stockmanagementsystem.entity.Supplier;
import com.stockmanagementsystem.entity.SupplierItemMapper;
import com.stockmanagementsystem.request.ItemSupplierMapperRequest;
import com.stockmanagementsystem.request.SupplierRequest;
import com.stockmanagementsystem.response.*;
import com.stockmanagementsystem.service.SupplierService;
import com.stockmanagementsystem.utils.APIConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping({APIConstants.BASE_REQUEST + APIConstants.SERVICENAME + APIConstants.SUPPLIER_CONTROLLER})
public class SupplierMasterController {
   @Autowired
   SupplierService supplierService;

    @PostMapping(APIConstants.SAVE_SUPPLIER)
    public BaseResponse saveSupplier(@RequestBody SupplierRequest supplierRequest) {
        return supplierService.saveSupplier(supplierRequest);
    }

    @DeleteMapping(APIConstants.DELETE_SUPPLIER)
    public BaseResponse<Supplier> deleteSupplierById(@PathVariable Integer id){
        return supplierService.deleteBySupplierId(id);
    }

    @PutMapping(APIConstants.UPDATE_SUPPLIER)
    public ResponseEntity<BaseResponse> updateSupplier(@PathVariable Integer id, @RequestBody SupplierRequest supplierRequest) {
        BaseResponse baseResponse = supplierService.updateSupplier(id, supplierRequest);
        return new ResponseEntity<>(baseResponse, HttpStatus.valueOf(baseResponse.getStatus()));
    }

    @GetMapping(APIConstants.GET_SUPPLIER_SEARCH)
    public BaseResponse<Supplier> searchSupplier(
            @RequestParam(value = "pageNumber", defaultValue = "0", required = false) Integer pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) Integer pageSize,
            @RequestParam(required = false) List<String> supplierName,
            @RequestParam(required = false) List<String> supplierCategory,
            @RequestParam(required = false) List<String> supplierGroup,
            @RequestParam(required = false)@DateTimeFormat(pattern = "yyyy/MM/dd") Date startDate,
            @RequestParam(required = false)@DateTimeFormat(pattern = "yyyy/MM/dd")Date endDate
    ) {
        return supplierService.searchSuppliers(pageNumber, pageSize, supplierName, supplierCategory,supplierGroup,startDate,endDate);
    }
    @GetMapping(APIConstants.GET_SUPPLIERS)
    public ResponseEntity<BaseResponse<List<SupplierNameResponse>>> getAllSuppliersWithIds() {
        BaseResponse<List<SupplierNameResponse>> response = supplierService.getSupplierWithIds();
        HttpStatus status = HttpStatus.resolve(response.getCode());
        if (status == null) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping(APIConstants.GET_BY_SUPPLIER_ID)
    public ResponseEntity<BaseResponse> getShiftById(@PathVariable("id") Integer id) {
        BaseResponse response = supplierService.getSupplierById(id);
        return ResponseEntity.ok(response);
    }
    @GetMapping(APIConstants.GET_ITEM)
    public ResponseEntity<BaseResponse<List<ItemNameResponse>>> getItemsWithIds() {
        BaseResponse<List<ItemNameResponse>> response = supplierService.getItemIdWithName();
        HttpStatus status = HttpStatus.resolve(response.getCode());
        if (status == null) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/getAllSuppliers")
    public BaseResponse<Supplier> getAllSuppliers(){
        return supplierService.getAllSuppliers();
    }

    @GetMapping("/getItemBySupplier/{id}")
    public BaseResponse<SupplierItemMapper> getItemBySupplier(@PathVariable Integer id){
        return supplierService.getItemBySupplier(id);
    }
    @GetMapping("/changeItemBySuppliersId")
    public BaseResponse<Supplier> changeItemBySuppliersId(@RequestParam Integer id,@RequestParam List<Integer> itemId){
        return supplierService.changeItemBySuppliersId(id,itemId);
    }

    @DeleteMapping("/removeItemById")
    public BaseResponse<Item> removeItemById(@RequestParam Integer supplierId, @RequestParam Integer itemId){
        return supplierService.removeItemById(supplierId,itemId);
    }

    @PostMapping("/mapItemBySupplier")
    public BaseResponse<SupplierItemMapper> mapItemBySupplier(@RequestBody List<ItemSupplierMapperRequest> itemSupplierMapperRequest){
        return supplierService.mapItemBySupplier(itemSupplierMapperRequest);
    }
}
