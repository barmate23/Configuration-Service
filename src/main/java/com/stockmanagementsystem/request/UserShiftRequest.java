package com.stockmanagementsystem.request;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserShiftRequest {

   // private Integer shiftMapperId;
    private Integer shiftId;
    private List<Integer> userId;
}
