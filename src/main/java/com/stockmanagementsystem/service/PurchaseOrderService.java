package com.stockmanagementsystem.service;

import com.stockmanagementsystem.entity.PurchaseOrderHead;
import com.stockmanagementsystem.entity.PurchaseOrderLine;
import com.stockmanagementsystem.request.PurchaseOrderHeadRequest;
import com.stockmanagementsystem.response.BaseResponse;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public interface PurchaseOrderService {
    BaseResponse<PurchaseOrderHead> getAllPurchaseOrderHeadWithPagination(List<String> orderNumber, Date orderDate, List<Integer> supplier, Date deliveryDate, Integer pageNo, Integer pageSize);

    BaseResponse<PurchaseOrderHead>savePurchaseOrder(PurchaseOrderHeadRequest purchaseOrderHeadRequest);

    BaseResponse<PurchaseOrderHead>updatePurchaseOrder(Integer poId, PurchaseOrderHeadRequest purchaseOrderHeadRequest);

    BaseResponse<PurchaseOrderLine> getPurchaseOrderLineByPoId(Integer poId);

    BaseResponse<PurchaseOrderLine> deletePurchaseOrderLineById(Integer id);

    BaseResponse<PurchaseOrderHead> deletePurchaseOrderHeadById(Integer id);

    BaseResponse<PurchaseOrderHead> getAllPurchaseOrderHead();
}
