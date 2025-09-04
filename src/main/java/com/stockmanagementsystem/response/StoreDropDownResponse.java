package com.stockmanagementsystem.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StoreDropDownResponse {

        private List<String> storeId;
        private List<String> storeName;
        private List<String> storeAddress;
        private List<String> storeManager;

}
