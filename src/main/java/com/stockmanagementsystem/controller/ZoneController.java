package com.stockmanagementsystem.controller;

import com.stockmanagementsystem.entity.Area;
import com.stockmanagementsystem.entity.CommonMaster;
import com.stockmanagementsystem.entity.LoginUser;
import com.stockmanagementsystem.entity.Zone;
import com.stockmanagementsystem.repository.ZoneRepository;
import com.stockmanagementsystem.response.BaseResponse;
import com.stockmanagementsystem.service.ZoneService;
import com.stockmanagementsystem.utils.APIConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.GET;
import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.List;

@RestController
@Slf4j
@RequestMapping({APIConstants.BASE_REQUEST + APIConstants.SERVICENAME+APIConstants.ZONE_CONTROLLER})
public class ZoneController {

    @Autowired
    ZoneService zoneService;
    @Autowired
    LoginUser loginUser;
    @GetMapping("/getAllZoneByAreaId/{id}")
    public BaseResponse<Zone> getAllZoneByAreaId(@PathVariable List<Integer> id){
        return zoneService.getAllZones(id);
    }
    @GetMapping("/getAllZoneWithPagination")
    public BaseResponse<Zone> getAllZoneWithPagination(@RequestParam(defaultValue = "0") Integer pageNo,
                                                       @RequestParam(defaultValue = "10") Integer pageSize,
                                                       @RequestParam(required = false ) List<Integer> storeId,
                                                       @RequestParam(required = false) List<Integer> areaId,
                                                       @RequestParam(required = false) List<Integer> zoneId,
                                                       @RequestParam(required = false)@DateTimeFormat(pattern = "yyyy/MM/dd") Date startDate,
                                                       @RequestParam(required = false)@DateTimeFormat(pattern = "yyyy/MM/dd")Date endDate){
        return zoneService.getAllZonesWithPagination(pageNo,pageSize,storeId,areaId,zoneId,startDate,endDate);
    }
    @PostMapping("/updateZone/{id}")
    public BaseResponse<Zone>updateZone(@PathVariable Integer id,
                                        @RequestParam Integer areaId,
                                        @RequestParam String erpZoneId,
                                        @RequestParam String zoneId,
                                        @RequestParam Integer statusId
    ){
        return zoneService.updateZone(id,areaId,erpZoneId,zoneId,statusId);
    }
    @DeleteMapping("/deleteZoneById/{id}")
    public BaseResponse<Zone> deleteZoneById(@PathVariable Integer id){
        return zoneService.deleteZoneById(id);
    }

    @GetMapping(APIConstants.GET_BARCODE_ZONE)
    public ResponseEntity<byte[]> generateBarcodePDF(@RequestParam String zoneId,
                                                     @RequestParam Boolean getAll) {
        try {
            log.info("LogId:{} - ZoneController - generateBarcodePDF - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId()," GET ZONE BARCODE START");
            ByteArrayOutputStream outputStream = zoneService.generateBarcodePDF(zoneId, getAll);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("filename", "Zone_barcode_list.pdf");
            headers.setContentLength(outputStream.size());
            log.info("LogId:{} - ZoneController - generateBarcodePDF - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId()," GET ZONE BARCODE END");

            return new ResponseEntity<>(outputStream.toByteArray(), headers, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
