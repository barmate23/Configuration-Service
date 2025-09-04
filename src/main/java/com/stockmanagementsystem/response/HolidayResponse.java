package com.stockmanagementsystem.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HolidayResponse {

    private Integer holidayId;
    private String holidayName;
    private Date date;
    private HolidayTypeResponse holidayType;
}
