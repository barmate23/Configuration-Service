package com.stockmanagementsystem.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExcellHeaderValidatorResponse {

    private Boolean isValid;
    private String errorMessage;

}
