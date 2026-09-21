package com.stockmanagementsystem.response.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BottomQuickStatsDto {
    private Long storesCount;
    private Long bomActiveAssemblies;
    private Long shopsCount;
    private Long docksCount;
    private Long calendarEventsCount;
    private Long reasonCodesCount;
}
