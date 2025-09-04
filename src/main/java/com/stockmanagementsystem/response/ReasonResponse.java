package com.stockmanagementsystem.response;

import com.stockmanagementsystem.entity.ReasonCategoryMaster;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReasonResponse {

    private Integer id;
    private String reasonId;
    private String rejectedReason;
    private ReasonCategoryMaster categoryMaster;

}
