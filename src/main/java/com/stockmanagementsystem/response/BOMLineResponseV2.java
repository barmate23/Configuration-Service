package com.stockmanagementsystem.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BOMLineResponseV2 {
    private Integer id;
    private Integer bomHeadId;
    private String bomCode;
    private Integer organizationId;
    private Integer subOrganizationId;
    private Integer level;
    private Integer lineNumber;
    private Integer itemId;
    private String itemCode;
    private String itemName;
    private Float quantity;
    private String unitOfMeasure;
    private String classType;
    private String issueType;
    private String dependency;
    private String referenceDesignators;
    private String bomNotes;
    private Integer stageId;
    private String stageCode;
}
