package com.stockmanagementsystem.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReasonCategoryResponse {

    private Integer id;
    private String reasonId;
    private String ReasonCategory;
}
