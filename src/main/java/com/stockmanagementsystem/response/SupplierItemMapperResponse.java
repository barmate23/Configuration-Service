package com.stockmanagementsystem.response;

import com.stockmanagementsystem.entity.Item;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class SupplierItemMapperResponse {
    private Item item;
}
