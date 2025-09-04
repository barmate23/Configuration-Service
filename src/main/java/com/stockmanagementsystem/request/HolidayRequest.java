package com.stockmanagementsystem.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.util.Date;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HolidayRequest {

    @NotBlank(message = "Holiday name cannot be blank")
    private String holidayName;

    @NotNull(message = "Date cannot be null")
    @JsonFormat(pattern = "dd/MM/yyyy")
    @PastOrPresent(message = "Date must be in the past or present")
    private Date date;

    @NotNull(message = "Holiday type cannot be null")
    private Integer holidayType;

}
