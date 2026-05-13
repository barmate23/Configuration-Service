package com.stockmanagementsystem.service;

import com.stockmanagementsystem.entity.Item;
import com.stockmanagementsystem.request.ItemRequest;
import com.stockmanagementsystem.response.BaseResponse;
import com.stockmanagementsystem.response.ItemResponseV2;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public interface ItemService {
    BaseResponse<Item> getAllItemWithPagination(Integer pageNo, Integer pageSize);

    BaseResponse<Item>saveItem(ItemRequest itemRequest);

    BaseResponse<Item>deleteItemById(Integer itemId);

    BaseResponse<Item> getItemById(Integer itemId);

    BaseResponse<Item>updateItem(Integer itemId, ItemRequest itemRequest);

    BaseResponse<Item> getAllItem();

    BaseResponse<Item> getAllAlternativeItem();

    BaseResponse searchItems(
            // public BaseResponse<List<ItemResponse>> searchItems(

            Integer pageNumber, Integer pageSize, List<String> name, List<String> itemGroup, List<String> itemCategory, List<String> issueType, List<String> classABC, Date startDate,Date endDate
    );

    String generateItemId(Integer count);

    BaseResponse<ItemResponseV2> getAllItemWithPaginationV2(Integer pageNo, Integer pageSize);

    BaseResponse<ItemResponseV2> getItemByIdV2(Integer itemId);

    BaseResponse<ItemResponseV2> getAllItemV2();

    BaseResponse<ItemResponseV2> getAllAlternativeItemV2();

    BaseResponse<ItemResponseV2> searchItemsV2(Integer pageNumber, Integer pageSize, List<String> name, List<String> itemGroup, List<String> itemCategory, List<String> issueType, List<String> classABC, Date startDate, Date endDate);
}
