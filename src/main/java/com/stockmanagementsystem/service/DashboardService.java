package com.stockmanagementsystem.service;

import com.stockmanagementsystem.response.BaseResponse;
import com.stockmanagementsystem.response.dashboard.DashboardResponseDto;

public interface DashboardService {

    /**
     * Fetch complete configuration & master data hub dashboard in a single unified API response.
     */
    BaseResponse<DashboardResponseDto> getDashboardData();
}
