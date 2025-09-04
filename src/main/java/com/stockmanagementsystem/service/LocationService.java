package com.stockmanagementsystem.service;

import com.stockmanagementsystem.entity.Location;
import com.stockmanagementsystem.entity.Zone;
import com.stockmanagementsystem.request.LocationRequest;
import com.stockmanagementsystem.response.BaseResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public interface LocationService {
    BaseResponse<Location> saveLocations(LocationRequest locationRequest);

    BaseResponse<Location> updateLocations(Integer id, LocationRequest locationRequest);

    BaseResponse<Location> createLocations(Zone zone, Integer sq);

    BaseResponse<Location> getAllLocationWithPagination(Integer pageNo, Integer pageSize);

    BaseResponse<Location> getLocationWithFilter(List<Integer> storeId, List<Integer> areaId, List<Integer> zoneId, List<Integer> locationId, List<Integer> itemId, Integer pageNo, Integer pageSize);


    BaseResponse<Location> getAllLocation(Integer zoneId);

    BaseResponse<Location> deleteLocationById(Integer storeId);

    byte[] getLocationBarcode(Integer storeId, Integer areaId, Integer zoneId, String locationId);

    ResponseEntity<byte[]> downloadExcelLocationFile(Integer zoneId);

    ResponseEntity<BaseResponse> uploadLocationDetail(MultipartFile file, Integer zoneId) throws IOException;
}
