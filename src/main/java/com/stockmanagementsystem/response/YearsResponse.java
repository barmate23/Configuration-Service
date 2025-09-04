package com.stockmanagementsystem.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class YearsResponse {

    @JsonFormat(pattern = "yyyy")
    private Date year;
}
