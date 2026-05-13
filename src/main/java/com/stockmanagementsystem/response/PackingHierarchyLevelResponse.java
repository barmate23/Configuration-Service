package com.stockmanagementsystem.response;

import lombok.Data;

@Data
public class PackingHierarchyLevelResponse {
    private Long id;
    private Integer organizationId;
    private Integer subOrganizationId;
    private String levelCode;
    private Integer levelOrder;
}
