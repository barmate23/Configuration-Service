package com.stockmanagementsystem.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocksRequest {

    private List<Integer> attribute;
    private List<Integer> dockName;
    private Date year;

}
