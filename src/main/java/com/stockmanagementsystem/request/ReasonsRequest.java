package com.stockmanagementsystem.request;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReasonsRequest {

    private String rejectedReason;
    private Integer reasonCategoryId;

}
