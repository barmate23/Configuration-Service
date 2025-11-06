package com.stockmanagementsystem.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PackingSummaryResponse {
    private String itemName;
    private String itemCode;
    private Integer totalContainers;
    private Integer totalQuantity;
    private List<ContainerSummary> packingSummary;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ContainerSummary {
        private String containerNo;
        private List<String> serialNumbers;
    }
}
