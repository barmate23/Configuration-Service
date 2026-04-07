package com.stockmanagementsystem.request;

import lombok.Data;
import java.util.List;

@Data
public class ProductionShopRequest {
    private String erpShopCode;
    private String shopCode;
    private String shopName;
    private String description;
    private String shopType;
    private List<AssemblyLineRequest> assemblyLineRequests;
}
