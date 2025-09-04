package com.stockmanagementsystem.request;
import lombok.Data;

@Data
public class BOMLineRequest {
    private Integer id;
    private Integer level;
    private String stage;
    private Integer lineNumber;
    private Integer itemId;
    private Integer quantity;
    private String dependency;
    private String referenceDesignators;
    private String bomNotes;
}
