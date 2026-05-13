package com.stockmanagementsystem.response;

import lombok.Data;
import java.util.List;

@Data
public class PackagingTypeResponse {
    private Long id;
    private String typeName;
    private List<PackagingSubtypeResponse> subtypes;
}
