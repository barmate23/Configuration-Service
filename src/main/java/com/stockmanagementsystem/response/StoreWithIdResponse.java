package com.stockmanagementsystem.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StoreWithIdResponse {

        private Integer id;
        private String storeId;
        private String storeName;


}
