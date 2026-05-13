package com.stockmanagementsystem.response;

import lombok.Data;
import java.util.List;

@Data
public class ProductionShopResponse {
    private Integer id;
    private String erpShopCode;
    private String shopCode;
    private String shopName;
    private String description;
    private String shopType;
    private List<AssemblyLineResponse> assemblyLines;
}
