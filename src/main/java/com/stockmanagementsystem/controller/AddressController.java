package com.stockmanagementsystem.controller;

import com.stockmanagementsystem.request.AddressRequestDto;
import com.stockmanagementsystem.response.AddressResponseDto;
import com.stockmanagementsystem.response.BaseResponse;
import com.stockmanagementsystem.service.AddressService;
import com.stockmanagementsystem.utils.APIConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping({APIConstants.BASE_REQUEST + APIConstants.SERVICENAME + APIConstants.ADDRESS_CONTROLLER})
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    /**
     * Create Address
     */
    @PostMapping("/createAddress")
    public BaseResponse<AddressResponseDto> createAddress(
            @Valid @RequestBody AddressRequestDto request
    ) {
        return addressService.createAddress(request);
    }

    /**
     * Get Address By ID
     */
    @GetMapping("/getAddressById/{addressId}")
    public BaseResponse<AddressResponseDto> getAddressById(
            @PathVariable Long addressId
    ) {
        return addressService.getAddressById(addressId);
    }

    /**
     * Get All Addresses
     */
    @GetMapping("/getAllAddresses")
    public BaseResponse<AddressResponseDto> getAllAddresses() {
        return addressService.getAllAddresses();
    }

    /**
     * Update Address
     */
    @PutMapping("/updateAddress/{addressId}")
    public BaseResponse<AddressResponseDto> updateAddress(
            @PathVariable Long addressId,
            @Valid @RequestBody AddressRequestDto request
    ) {
        return addressService.updateAddress(
                addressId,
                request
        );
    }

    /**
     * Delete Address
     */
    @DeleteMapping("/{addressId}")
    public BaseResponse<AddressResponseDto> deleteAddress(
            @PathVariable Long addressId
    ) {
        return addressService.deleteAddress(addressId);
    }

    /**
     * Get Address By Pincode
     */
    @GetMapping("/pincode/{pincode}")
    public BaseResponse<AddressResponseDto> getByPincode(
            @PathVariable String pincode
    ) {
        return addressService.getAddressesByPincode(pincode);
    }
}