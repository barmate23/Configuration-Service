package com.stockmanagementsystem.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemNameResponse {

    private Integer id;
    private String itemId;
    private String name;
}
