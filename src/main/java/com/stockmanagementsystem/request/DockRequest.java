package com.stockmanagementsystem.request;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DockRequest {

 /*   @NotNull(message = "Dock ID cannot be null")
    private String dockId;*/

    @NotNull(message = "Dock Name cannot be null")
    @Size(min = 1, max = 255, message = "Dock Name must be between 1 and 255 characters")
    private String dockName;

    @NotNull(message = "Attribute cannot be null")
    @Size(max = 255, message = "Attribute length cannot exceed 255 characters")
    private String attribute;

    @NotNull(message = "Store ID cannot be null")
    private List<Integer> store;

    @NotNull(message = "Dock Supervisor ID cannot be null")
    private Integer dockSupervisor;

}
