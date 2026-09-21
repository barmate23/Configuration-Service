package com.stockmanagementsystem.controller;

import com.stockmanagementsystem.response.BaseResponse;
import com.stockmanagementsystem.response.dashboard.DashboardResponseDto;
import com.stockmanagementsystem.service.DashboardService;
import com.stockmanagementsystem.utils.APIConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping({APIConstants.BASE_REQUEST + APIConstants.SERVICENAME + APIConstants.DASHBOARD_CONTROLLER})
public class DashboardController {

    private final DashboardService dashboardService;

    /**
     * Single Consolidated API for Configuration & Master Data Hub Dashboard
     * Exposes the complete dashboard response cleanly divided by sections.
     */
    @GetMapping(APIConstants.GET_DASHBOARD_DATA)
    public BaseResponse<DashboardResponseDto> getDashboardData() {
        return dashboardService.getDashboardData();
    }
}
