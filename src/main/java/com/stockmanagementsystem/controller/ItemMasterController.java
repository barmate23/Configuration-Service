package com.stockmanagementsystem.controller;


import com.stockmanagementsystem.entity.Item;
import com.stockmanagementsystem.entity.Zone;
import com.stockmanagementsystem.request.ItemRequest;
import com.stockmanagementsystem.response.BaseResponse;
import com.stockmanagementsystem.response.SupplierResponse;
import com.stockmanagementsystem.service.ItemService;
import com.stockmanagementsystem.utils.APIConstants;
import com.stockmanagementsystem.utils.ServiceConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping({APIConstants.BASE_REQUEST + APIConstants.SERVICENAME + ServiceConstants.ITEM_CONTROLLER})
public class ItemMasterController {

    @Autowired
    ItemService itemService;

    @GetMapping("/getAllItemWithPagination")
    public BaseResponse<Item> getAllItemWithPagination(@RequestParam(defaultValue = "0") Integer pageNo,
                                                       @RequestParam(defaultValue = "10") Integer pageSize){
        return itemService.getAllItemWithPagination(pageNo,pageSize);
    }
    @PostMapping("/saveItem")
    public BaseResponse<Item> saveItem(@RequestBody ItemRequest itemRequest){
        return itemService.saveItem(itemRequest);
    }

    @DeleteMapping("/deleteItemById/{itemId}")
    public BaseResponse<Item> deleteItemById(@PathVariable Integer itemId){
        return itemService.deleteItemById(itemId);
    }
    @GetMapping("/getItemById/{itemId}")
    public BaseResponse<Item> getItemById(@PathVariable Integer itemId){
        return itemService.getItemById(itemId);
    }
    @PutMapping("/updateItem/{itemId}")
    public BaseResponse<Item> updateItem(@PathVariable Integer itemId,
                                         @RequestBody ItemRequest itemRequest){
        return itemService.updateItem(itemId,itemRequest);
    }
    @GetMapping("/getAllItem")
    public BaseResponse<Item> getAllItem(){
        return itemService.getAllItem();
    }
    @GetMapping("/getAllAlternativeItem")
    public BaseResponse<Item> getAllAlternativeItem(){
        return itemService.getAllAlternativeItem();
    }
    @GetMapping(APIConstants.GET_ITEM_SEARCH)
    public BaseResponse<List<SupplierResponse>> searchItems(
            @RequestParam(value = "pageNumber", defaultValue = "0", required = false) Integer pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) Integer pageSize,
            @RequestParam(required = false) List<String> name,
            @RequestParam(required = false) List<String> itemGroup,
            @RequestParam(required = false) List<String> itemCategory,
            @RequestParam(required = false) List<String> issueType,
            @RequestParam(required = false) List<String> classABC,
            @RequestParam(required = false)@DateTimeFormat(pattern = "yyyy/MM/dd") Date startDate,
            @RequestParam(required = false)@DateTimeFormat(pattern = "yyyy/MM/dd")Date endDate
    ) {
        return itemService.searchItems(pageNumber, pageSize, name, itemGroup,itemCategory,issueType,classABC, startDate,endDate);
    }
}
