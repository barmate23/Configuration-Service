package com.stockmanagementsystem.response;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserShiftsResponse {
    private ShiftsResponse shift;
    private List<UserShiftResponse> userShiftResponse;

}
