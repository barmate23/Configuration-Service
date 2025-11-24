package com.stockmanagementsystem.request;

import lombok.Data;

import java.util.List;

@Data
public class WeeklyOffRequest {
    private List<String> days;
}
