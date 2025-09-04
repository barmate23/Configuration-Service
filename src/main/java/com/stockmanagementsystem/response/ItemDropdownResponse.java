package com.stockmanagementsystem.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemDropdownResponse {

    private List<String> itemCode;
    private List<String> itemName;
//    private List<LocationPair> locationPair;
    private List<StorePair> storePair;
    private List<ItemPair> itemPairs;

}
