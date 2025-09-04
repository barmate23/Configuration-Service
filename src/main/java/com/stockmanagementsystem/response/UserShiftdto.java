package com.stockmanagementsystem.response;

import com.stockmanagementsystem.entity.ShiftMapper;
import com.stockmanagementsystem.entity.UserShiftMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserShiftdto {
    private ShiftMapper shiftMapper;
    private List<UserShiftMapper> userShiftResponse;
}
