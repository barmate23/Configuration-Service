package com.stockmanagementsystem.request;

import lombok.Data;

@Data
public class PackingProfileLevelRequest {
    private Long id;
    private Long hierarchyLevelId;
    private Long packagingId;
    private Integer levelOrder;
    private Integer unitsPerParent;
}
