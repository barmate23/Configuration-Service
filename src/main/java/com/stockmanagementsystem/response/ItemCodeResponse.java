package com.stockmanagementsystem.response;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemCodeResponse {
    private Integer id;
    private String itemId;
    private String itemCode;
    private String name;
}
