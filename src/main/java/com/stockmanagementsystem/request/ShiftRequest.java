package com.stockmanagementsystem.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.*;
import java.sql.Time;
import java.util.Date;
import java.util.List;

@Data
public class ShiftRequest {

    @NotBlank(message = "shiftName  cannot be blank")
    private String shiftName;

    @NotNull(message = "Year must not be null")
    @JsonFormat(pattern = "yyyy")
    private Date year;

    @NotNull(message = "Shift start time cannot be null")
    private Time shiftStart;

    @NotNull(message = "Shift end time cannot be null")
    private Time shiftEnd;

    @NotNull(message = "day type cannot be null")
    private List<Integer> day;

}
