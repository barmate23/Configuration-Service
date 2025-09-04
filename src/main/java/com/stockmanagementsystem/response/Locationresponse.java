package com.stockmanagementsystem.response;

import com.stockmanagementsystem.entity.Location;
import com.stockmanagementsystem.entity.Store;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Locationresponse {

    private List<Location> locations;
    private Integer pageCount;
    private Long recordCount;


}
