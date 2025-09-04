package com.stockmanagementsystem.controller;

import com.stockmanagementsystem.entity.Location;
import com.stockmanagementsystem.request.LocationRequest;
import com.stockmanagementsystem.response.BaseResponse;
import com.stockmanagementsystem.service.LocationService;
import com.stockmanagementsystem.utils.APIConstants;
import com.stockmanagementsystem.utils.ServiceConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping({APIConstants.BASE_REQUEST + APIConstants.SERVICENAME + ServiceConstants.LOCATION_CONTROLLER})
public class LocationMasterController {

    @Autowired
    LocationService locationService;

    @GetMapping("/getAllLocationWithPagination")
    public BaseResponse<Location> getAllLocationWithPagination(@RequestParam(defaultValue = "0") Integer pageNo,
                                                               @RequestParam(defaultValue = "10") Integer pageSize
                                                              ){
        return locationService.getAllLocationWithPagination(pageNo,pageSize);

    }

    @GetMapping("/getLocationWithFilter")
    public BaseResponse<Location> getAllLocationWithPagination(@RequestParam(defaultValue = "0") Integer pageNo,
                                                               @RequestParam(defaultValue = "10") Integer pageSize,
                                                               @RequestParam(required = false) List<Integer> storeId,
                                                               @RequestParam(required = false) List<Integer> areaId,
                                                               @RequestParam(required = false) List<Integer> zoneId,
                                                               @RequestParam(required = false) List<Integer> itemId,
                                                               @RequestParam(required = false) List<Integer> locationId){
        return locationService.getLocationWithFilter(storeId,areaId,zoneId,locationId,itemId,pageNo,pageSize);

    }

    @PostMapping("/saveLocation")
    public BaseResponse<Location> saveLocation(@RequestBody LocationRequest locationRequest){
        return locationService.saveLocations(locationRequest);

    }

    @PostMapping("/updateLocation/{id}")
    public BaseResponse<Location> updateLocation(@PathVariable Integer id,@RequestBody LocationRequest locationRequest){
        return locationService.updateLocations(id,locationRequest);

    }


    @GetMapping("/getAllLocation/{zoneId}")
    public BaseResponse<Location> getAllLocation(@PathVariable Integer zoneId){
        return locationService.getAllLocation(zoneId);

    }
    @DeleteMapping("/deleteLocationById/{id}")
    public BaseResponse<Location> deleteLocationById(@PathVariable Integer id){
        return locationService.deleteLocationById(id);

    }
    @GetMapping(APIConstants.GET_LOCATION_BARCODE)
    public ResponseEntity<byte[]> getLocationBarcode(@RequestParam(required = false) Integer storeId,
                                                     @RequestParam(required = false) Integer areaId,
                                                     @RequestParam(required = false) Integer zoneId,
                                                     @RequestParam String locationId) {
        byte[] pdfFile =  locationService.getLocationBarcode(storeId, areaId, zoneId, locationId);
        if(pdfFile != null){
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF).body(pdfFile);
        } else {
            return ResponseEntity.notFound().build();
        }

    }
    @GetMapping("/downloadLocationExcelFile/{zoneId}")
    public ResponseEntity<byte[]> downloadExcelLocationFile(@PathVariable Integer zoneId){
     return locationService.downloadExcelLocationFile(zoneId);
    }
    @PostMapping("/uploadExcelLocationFile")
    public ResponseEntity<BaseResponse> uploadExcelLocationFile(@RequestPart("file") MultipartFile file, @RequestParam("zoneId") Integer zoneId) throws IOException {
        return locationService.uploadLocationDetail(file,zoneId);
    }


}
