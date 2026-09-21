package com.stockmanagementsystem.response.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemCatalogKpiDto {
    private Long totalItems;
    private Long classACount;
    private Long directCount;
    private Long indirectCount;
}
