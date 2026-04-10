package com.stockmanagementsystem.request;

import lombok.Data;
import java.util.List;

@Data
public class PackingProfileConfigRequest {
    private Long id;
    private String description;
    private Integer itemId;
    private Integer supplierId;
    private String moqLevel;
    private Integer moqQty;
    private List<PackingProfileLevelRequest> levels;
}
