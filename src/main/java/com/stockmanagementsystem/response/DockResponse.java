package com.stockmanagementsystem.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DockResponse {
    private Integer id;
    private String dockId;
    private String dockName;
    private List<UserResponse> supervisors;
    private String attribute;
    private List<StoreResponseDto> storeResponseList;
}
