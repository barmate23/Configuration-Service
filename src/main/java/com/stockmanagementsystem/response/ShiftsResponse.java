package com.stockmanagementsystem.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShiftsResponse {
    private Integer shiftId;
    private String shiftName;
   // @JsonFormat(pattern = "hh:mm a", locale = "en_US")
    private Date shiftStart;
  //  @JsonFormat(pattern = "hh:mm a", locale = "en_US")
    private Date shiftEnd;

    public ShiftsResponse(ShiftResponse shift) {
    }
}
