package com.stockmanagementsystem.response;
import com.stockmanagementsystem.entity.Users;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserShiftResponse {
    private Users user;

}
