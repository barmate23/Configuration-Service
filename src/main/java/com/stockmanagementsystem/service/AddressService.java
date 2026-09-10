package com.stockmanagementsystem.service;

import com.stockmanagementsystem.request.AddressRequestDto;
import com.stockmanagementsystem.response.AddressResponseDto;
import com.stockmanagementsystem.response.BaseResponse;

public interface AddressService {

    BaseResponse<AddressResponseDto> createAddress(AddressRequestDto request);

    BaseResponse<AddressResponseDto> getAddressById(Long addressId);

    BaseResponse<AddressResponseDto> getAllAddresses();

    BaseResponse<AddressResponseDto> updateAddress(
            Long addressId,
            AddressRequestDto request
    );

    BaseResponse<AddressResponseDto> deleteAddress(Long addressId);

    BaseResponse<AddressResponseDto> getAddressesByPincode(String pincode);
}