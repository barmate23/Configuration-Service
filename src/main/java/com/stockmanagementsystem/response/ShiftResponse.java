package com.stockmanagementsystem.response;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.stockmanagementsystem.entity.Shift;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShiftResponse {

    private ShiftsResponse shift;
    private List<ShiftMapperResponse> shiftMapperResponses;
    @JsonFormat(pattern = "yyyy")
    private Date yearResponse;

}
