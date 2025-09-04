package com.stockmanagementsystem.service;
import com.stockmanagementsystem.entity.Item;
import com.stockmanagementsystem.entity.Supplier;
import com.stockmanagementsystem.entity.SupplierItemMapper;
import com.stockmanagementsystem.request.ItemSupplierMapperRequest;
import com.stockmanagementsystem.request.SupplierRequest;
import com.stockmanagementsystem.response.BaseResponse;
import com.stockmanagementsystem.response.ItemNameResponse;
import com.stockmanagementsystem.response.SupplierNameResponse;
import com.stockmanagementsystem.response.SupplierResponse;

import java.util.Date;
import java.util.List;

public interface SupplierService {
    BaseResponse<Supplier> saveSupplier(SupplierRequest supplierRequest );

    String generateSupplierId(Integer count);

    BaseResponse deleteBySupplierId(Integer id);

    BaseResponse<Supplier> updateSupplier(Integer id, SupplierRequest supplierRequest);


    BaseResponse<Supplier> searchSuppliers(

            Integer pageNumber, Integer pageSize, List<String> supplierName, List<String> supplierCategory, List<String> supplierGroup, Date startDate,Date endDate
    );

    BaseResponse<List<SupplierNameResponse>> getSupplierWithIds();

    BaseResponse getSupplierById(Integer id);
    BaseResponse<List<ItemNameResponse>> getItemIdWithName();

    BaseResponse<Supplier> getAllSuppliers();


    BaseResponse<Supplier> changeItemBySuppliersId(Integer id, List<Integer> itemId);

    BaseResponse<SupplierItemMapper> getItemBySupplier(Integer id);

    BaseResponse<Item> removeItemById(Integer supplierId, Integer itemId);

    BaseResponse<SupplierItemMapper> mapItemBySupplier(List<ItemSupplierMapperRequest> itemSupplierMapperRequests);
}
