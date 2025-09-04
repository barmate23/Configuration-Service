package com.stockmanagementsystem.response;

import lombok.Data;

import java.util.List;
@Data
public class AddressResponse {
    // private Integer addressId;
    private Integer pincode;
    private List<String> stateName;
    private List<String> districtName;
    private List<String> subDistrictName;
    private List<String> townName;
    private List<String> villageName;
}
