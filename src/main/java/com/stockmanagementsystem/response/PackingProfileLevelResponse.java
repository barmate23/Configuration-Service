package com.stockmanagementsystem.response;

import lombok.Data;

@Data
public class PackingProfileLevelResponse {
    private Long id;
    private String levelCode;
    private Integer levelOrder;
    private String packagingCode;
    private String packagingName;
    private Integer unitsPerParent;
}
