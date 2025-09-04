package com.stockmanagementsystem.response;

import com.stockmanagementsystem.entity.Store;
import lombok.Data;

import java.util.List;

@Data
public class StoreResponse {
    private List<Store> stores;
    private Integer pageCount;
    private Long recordCount;

    public StoreResponse(Integer id, String storeId) {
    }

    public StoreResponse() {

    }
}
