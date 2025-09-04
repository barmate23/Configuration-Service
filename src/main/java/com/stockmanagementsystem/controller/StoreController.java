package com.stockmanagementsystem.controller;

import com.stockmanagementsystem.entity.Store;
import com.stockmanagementsystem.entity.StoreName;
import com.stockmanagementsystem.entity.Users;
import com.stockmanagementsystem.request.StoreRequest;
import com.stockmanagementsystem.response.BaseResponse;
import com.stockmanagementsystem.response.CreateYearResponse;
import com.stockmanagementsystem.response.StoreResponse;
import com.stockmanagementsystem.service.StoreService;
import com.stockmanagementsystem.utils.APIConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@Slf4j
@RequestMapping({APIConstants.BASE_REQUEST + APIConstants.SERVICENAME+APIConstants.FILE_CONTROLLER})
public class StoreController {
    @Autowired
    StoreService storeService;
    @GetMapping("/getStoresWithPagination")
    public BaseResponse<StoreResponse> getStoresWithPagination(@RequestParam(defaultValue = "0") Integer pageNo,
                                                               @RequestParam(defaultValue = "10") Integer pageSize){
        return storeService.getStoresWithPagination(pageNo,pageSize);
    }
    @PostMapping("/saveStore")
    public BaseResponse<Store> saveStore(@RequestBody StoreRequest storeRequest){
        return storeService.saveStore(storeRequest);
    }
    @PostMapping("/updateStore/{storeId}")
    public BaseResponse<Store> updateStore(@PathVariable Integer storeId,@RequestBody StoreRequest storeRequest){
        return storeService.updateStore(storeId,storeRequest);
    }

    @GetMapping("/getStoreListByERPStoreId")
    public BaseResponse<StoreResponse> getStoreListByERPStoreId(@RequestParam(defaultValue = "0") Integer pageNo,
                                                        @RequestParam(defaultValue = "10") Integer pageSize,
                                                        @RequestParam(required = false) List<String> erpStoreId,
                                                                @RequestParam(required = false) List<Integer> storeId,
                                                                @RequestParam(required = false)@DateTimeFormat(pattern = "yyyy/MM/dd") Date startDate,
                                                                @RequestParam(required = false)@DateTimeFormat(pattern = "yyyy/MM/dd")Date endDate
    ){
        return storeService.getStoreListByERPStoreId(pageNo,pageSize,storeId,erpStoreId,startDate, endDate);
    }
    @GetMapping("/getAllStores")
    public BaseResponse<Store> getAllStores(){
        return storeService.getAllStores();
    }
//    @GetMapping("/getAllStoresAcceptItemAssignInLocation")
//    public BaseResponse<Store> getAllStoresAcceptItemAssignInLocation(){
//        return storeService.getAllStoresAcceptItemAssignInLocation();
//    }

    @GetMapping("/getAllYears/{type}")
    public BaseResponse<CreateYearResponse> getAllYears(@PathVariable String type){
        return storeService.getAllYears(type);
    }

    @GetMapping("/getAllStoresName")
    public BaseResponse<StoreName> getAllStoresName(){
        return storeService.getAllStoresName();
    }

    @DeleteMapping("/deleteStoreById/{storeId}")
    public BaseResponse<Store> deleteStoreById(@PathVariable Integer storeId){
        return storeService.deleteStoreById(storeId);
    }

    @GetMapping(APIConstants.GET_BARCODE_STORES)
    public ResponseEntity<byte[]> generateBarcodePDF() {
        byte[] pdfFile =  storeService.generateStoreBarcodePDF();
        if(pdfFile != null){
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF).body(pdfFile);
        } else {
            return ResponseEntity.notFound().build();
        }

    }

    @GetMapping("/getAllStoreKeeper")
    public BaseResponse<Users> getAllStoreKeeper(){
        return storeService.getAllStoreKeeper();
    }

    @GetMapping("/getAreaLicense")
    public BaseResponse<Users> getAreaLicense(){
        return storeService.getAreaLicense();
    }
}
