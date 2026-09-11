package com.stockmanagementsystem.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AddressResponseDto {

    private Long addressId;

    private String country;

    private String digipin;

    private String pincode;

    private String state;

    private String district;

    private String city;

    private String taluka;

    private String town;

    private String village;

    private String locality;

    private String subLocality;

    private String street;

    private String googleMapLink;

    private String areaCode;

    private LocalDateTime createdOn;

    private LocalDateTime updatedOn;
}