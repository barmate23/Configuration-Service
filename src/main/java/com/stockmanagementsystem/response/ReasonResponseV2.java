package com.stockmanagementsystem.response;

import com.stockmanagementsystem.entity.ReasonCategoryMaster;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReasonResponseV2 {
    private Integer id;
    private String reasonId;
    private String rejectedReason;
    private Boolean isApproved;
    private ReasonCategoryMaster reasonCategoryMaster;
    private Boolean isUserCreated;
}
