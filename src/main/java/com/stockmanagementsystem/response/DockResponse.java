package com.stockmanagementsystem.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DockResponse {
    private Integer id;
    private String dockId;
    private String dockName;
    private String attribute;
    private StoreWithIdResponse store;
    private UserResponse dockSupervisor;
}
