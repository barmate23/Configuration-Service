package com.stockmanagementsystem.service;

import com.stockmanagementsystem.entity.NewAddress;
import com.stockmanagementsystem.repository.NewAddressRepository;
import com.stockmanagementsystem.request.AddressRequestDto;
import com.stockmanagementsystem.response.AddressResponseDto;
import com.stockmanagementsystem.response.BaseResponse;
import com.stockmanagementsystem.utils.ServiceConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AddressServiceImpl implements AddressService {

    private final NewAddressRepository addressRepository;

    @Override
    public BaseResponse<AddressResponseDto> createAddress(AddressRequestDto request) {
        BaseResponse<AddressResponseDto> response = new BaseResponse<>();
        NewAddress address = new NewAddress();

        mapRequestToEntity(request, address);
        NewAddress savedAddress = addressRepository.save(address);

        response.setStatus(HttpStatus.CREATED.value());
        response.setMessage("Address created successfully");
        response.setData(Collections.singletonList(mapToResponse(savedAddress)));
        response.setCode(ServiceConstants.SUCCESS_CODE);
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public BaseResponse<AddressResponseDto> getAddressById(Long addressId) {
        BaseResponse<AddressResponseDto> response = new BaseResponse<>();
        NewAddress address = addressRepository
                .findByAddressIdAndIsDeletedFalse(addressId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Address not found with id: " + addressId
                        )
                );

        response.setStatus(HttpStatus.OK.value());
        response.setMessage("Address fetched successfully");
        response.setData(Collections.singletonList(mapToResponse(address)));
        response.setCode(ServiceConstants.SUCCESS_CODE);
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public BaseResponse<AddressResponseDto> getAllAddresses() {
        BaseResponse<AddressResponseDto> response = new BaseResponse<>();
        List<AddressResponseDto> list = addressRepository
                .findAllByIsDeletedFalseOrderByAddressIdDesc()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        response.setStatus(HttpStatus.OK.value());
        response.setMessage("All addresses fetched successfully");
        response.setData(list);
        response.setCode(ServiceConstants.SUCCESS_CODE);
        response.setTotalRecordCount((long) list.size());
        return response;
    }

    @Override
    public BaseResponse<AddressResponseDto> updateAddress(
            Long addressId,
            AddressRequestDto request
    ) {
        BaseResponse<AddressResponseDto> response = new BaseResponse<>();
        NewAddress address = addressRepository
                .findByAddressIdAndIsDeletedFalse(addressId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Address not found with id: " + addressId
                        )
                );

        mapRequestToEntity(request, address);
        NewAddress updatedAddress = addressRepository.save(address);

        response.setStatus(HttpStatus.OK.value());
        response.setMessage("Address updated successfully");
        response.setData(Collections.singletonList(mapToResponse(updatedAddress)));
        response.setCode(ServiceConstants.SUCCESS_CODE);
        return response;
    }

    @Override
    public BaseResponse<AddressResponseDto> deleteAddress(Long addressId) {
        BaseResponse<AddressResponseDto> response = new BaseResponse<>();
        NewAddress address = addressRepository
                .findByAddressIdAndIsDeletedFalse(addressId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Address not found with id: " + addressId
                        )
                );

        // Soft delete
        address.setIsDeleted(true);
        addressRepository.save(address);

        response.setStatus(HttpStatus.OK.value());
        response.setMessage("Address deleted successfully");
        response.setData(Collections.emptyList());
        response.setCode(ServiceConstants.SUCCESS_CODE);
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public BaseResponse<AddressResponseDto> getAddressesByPincode(
            String pincode
    ) {
        BaseResponse<AddressResponseDto> response = new BaseResponse<>();
        List<AddressResponseDto> list = addressRepository
                .findAllByIsDeletedFalseOrderByAddressIdDesc()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        response.setStatus(HttpStatus.OK.value());
        response.setMessage("Addresses fetched successfully");
        response.setData(list);
        response.setCode(ServiceConstants.SUCCESS_CODE);
        response.setTotalRecordCount((long) list.size());
        return response;
    }

    private void mapRequestToEntity(
            AddressRequestDto request,
            NewAddress address
    ) {

        address.setCountry(request.getCountry());
        address.setDigipin(request.getDigipin());
        address.setPincode(request.getPincode());
        address.setState(request.getState());
        address.setDistrict(request.getDistrict());
        address.setCity(request.getCity());
        address.setTaluka(request.getTaluka());
        address.setTown(request.getTown());
        address.setVillage(request.getVillage());
        address.setLocality(request.getLocality());
        address.setSubLocality(request.getSubLocality());
        address.setStreet(request.getStreet());
        address.setGoogleMapLink(request.getGoogleMapLink());
        address.setAreaCode(request.getAreaCode());
    }

    private AddressResponseDto mapToResponse(NewAddress address) {

        return AddressResponseDto.builder()
                .addressId(address.getAddressId())
                .country(address.getCountry())
                .digipin(address.getDigipin())
                .pincode(address.getPincode())
                .state(address.getState())
                .district(address.getDistrict())
                .city(address.getCity())
                .taluka(address.getTaluka())
                .town(address.getTown())
                .village(address.getVillage())
                .locality(address.getLocality())
                .subLocality(address.getSubLocality())
                .street(address.getStreet())
                .googleMapLink(address.getGoogleMapLink())
                .areaCode(address.getAreaCode())
                .createdOn(address.getCreatedOn())
                .updatedOn(address.getUpdatedOn())
                .build();
    }
}