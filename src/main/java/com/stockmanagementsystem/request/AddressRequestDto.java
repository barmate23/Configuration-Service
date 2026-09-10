package com.stockmanagementsystem.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class AddressRequestDto {

    @NotBlank(message = "Country is required")
    private String country;

    private String digipin;

    @NotBlank(message = "Pincode is required")
    @Pattern(
        regexp = "^[0-9]{6}$",
        message = "Pincode must be exactly 6 digits"
    )
    private String pincode;

    @NotBlank(message = "State is required")
    private String state;

    @NotBlank(message = "District is required")
    private String district;

    private String city;

    private String taluka;

    private String town;

    private String village;

    private String locality;

    private String subLocality;

    private String street;

    private String googleMapLink;
}