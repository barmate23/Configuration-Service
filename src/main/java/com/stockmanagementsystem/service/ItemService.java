package com.stockmanagementsystem.service;

import com.stockmanagementsystem.entity.Item;
import com.stockmanagementsystem.request.ItemRequest;
import com.stockmanagementsystem.response.BaseResponse;
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
}
