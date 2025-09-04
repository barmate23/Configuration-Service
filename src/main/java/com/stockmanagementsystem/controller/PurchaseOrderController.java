package com.stockmanagementsystem.controller;

import com.stockmanagementsystem.entity.PurchaseOrderHead;
import com.stockmanagementsystem.entity.PurchaseOrderLine;
import com.stockmanagementsystem.request.PurchaseOrderHeadRequest;
import com.stockmanagementsystem.response.BaseResponse;
import com.stockmanagementsystem.service.PurchaseOrderService;
import com.stockmanagementsystem.utils.APIConstants;
import com.stockmanagementsystem.utils.ServiceConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping({APIConstants.BASE_REQUEST + APIConstants.SERVICENAME + ServiceConstants.PURCHASE_ORDER_CONTROLLER})
public class PurchaseOrderController {

    @Autowired
    PurchaseOrderService purchaseOrderService;

    @GetMapping("/getAllPurchaseOrderHeadWithPagination")
    public BaseResponse<PurchaseOrderHead> getAllPurchaseOrderHeadWithPagination(
            @RequestParam(required = false) List<String> orderNumber,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd")Date orderDate,
            @RequestParam(required = false)List<Integer> supplier,
            @RequestParam(required = false)@DateTimeFormat(pattern = "yyyy-MM-dd")Date deliveryDate,
            @RequestParam(defaultValue = "0") Integer pageNo,
            @RequestParam(defaultValue = "10") Integer pageSize){
        return purchaseOrderService.getAllPurchaseOrderHeadWithPagination(orderNumber,orderDate,supplier,deliveryDate,pageNo,pageSize);
    }

    @PostMapping("/savePurchaseOrder")
    public BaseResponse<PurchaseOrderHead> savePurchaseOrder(@RequestBody PurchaseOrderHeadRequest purchaseOrderHeadRequest){
        return purchaseOrderService.savePurchaseOrder(purchaseOrderHeadRequest);
    }

    @PostMapping("/updatePurchaseOrder/{id}")
    public BaseResponse<PurchaseOrderHead> updatePurchaseOrder(@PathVariable Integer id,@RequestBody PurchaseOrderHeadRequest purchaseOrderHeadRequest){
        return purchaseOrderService.updatePurchaseOrder(id,purchaseOrderHeadRequest);
    }


    @GetMapping("/getPurchaseOrderLineByPoId/{id}")
    public BaseResponse<PurchaseOrderLine> getPurchaseOrderLineByPoId(@PathVariable Integer id){
        return purchaseOrderService.getPurchaseOrderLineByPoId(id);
    }

    @DeleteMapping("/deletePurchaseOrderHeadById/{id}")
    public BaseResponse<PurchaseOrderHead> deletePurchaseOrderHeadById(@PathVariable Integer id){
        return purchaseOrderService.deletePurchaseOrderHeadById(id);
    }
    @DeleteMapping("/deletePurchaseOrderLineById/{id}")
    public BaseResponse<PurchaseOrderLine> deletePurchaseOrderLineById(@PathVariable Integer id){
        return purchaseOrderService.deletePurchaseOrderLineById(id);
    }

    @GetMapping("/getAllPurchaseOrderHead")
    public BaseResponse<PurchaseOrderHead> getAllPurchaseOrderHead(){
        return purchaseOrderService.getAllPurchaseOrderHead();
    }
}
