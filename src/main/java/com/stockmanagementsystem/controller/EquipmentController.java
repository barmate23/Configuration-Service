package com.stockmanagementsystem.controller;

import com.stockmanagementsystem.entity.Equipment;
import com.stockmanagementsystem.entity.LoginUser;
import com.stockmanagementsystem.entity.Zone;
import com.stockmanagementsystem.request.EquipmentRequest;
import com.stockmanagementsystem.response.BaseResponse;
import com.stockmanagementsystem.service.EquipmentService;
import com.stockmanagementsystem.utils.APIConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.util.List;

@RestController
@Slf4j
@RequestMapping({APIConstants.BASE_REQUEST + APIConstants.SERVICENAME+APIConstants.EQUIPMENT_CONTROLLER})
public class EquipmentController {

    @Autowired
    EquipmentService equipmentService;

    @Autowired
    LoginUser loginUser;


    @GetMapping("/getAllEquipmentWithPagination")
    public BaseResponse<Equipment> getAllEquipmentWithPagination(@RequestParam(defaultValue = "0") Integer pageNo,
                                                                 @RequestParam(defaultValue = "10") Integer pageSize,
                                                                 @RequestParam(required = false ) List<Integer> storeId,
                                                                 @RequestParam(required = false) List<String> trolleyType){
        return equipmentService.getAllEquipmentWithPagination(pageNo,pageSize,storeId,trolleyType);
        }
    @PostMapping("/saveEquipment")
    public BaseResponse<Equipment> saveEquipment(@RequestBody EquipmentRequest equipmentRequest){
        return equipmentService.saveEquipment(equipmentRequest);
    }

    @PostMapping("/updateEquipment/{id}")
    public BaseResponse<Equipment> updateEquipment(@PathVariable Integer id,@RequestBody EquipmentRequest equipmentRequest){
        return equipmentService.updateEquipment(id,equipmentRequest);
    }
    @DeleteMapping("/deleteEquipmentById/{id}")
    public BaseResponse<Equipment> deleteEquipmentById(@PathVariable Integer id){
        return equipmentService.deleteEquipmentById(id);
    }
    @GetMapping("/getAllEquipment")
    public BaseResponse<Equipment> deleteEquipmentById(){
        return equipmentService.getAllEquipment();
    }

    @GetMapping(APIConstants.GET_BARCODE_EQUIPMENT)
    public ResponseEntity<byte[]> generateBarcodePDF() {
        try {
            log.info("LogId:{} - EquipmentController - generateBarcodePDF - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId()," GET STAGE BARCODE START");
            ByteArrayOutputStream outputStream = equipmentService.generateBarcodePDF();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("filename", "Stage_barcode_list.pdf");
            headers.setContentLength(outputStream.size());
            log.info("LogId:{} - EquipmentController - generateBarcodePDF - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId()," GET STAGE BARCODE END");

            return new ResponseEntity<>(outputStream.toByteArray(), headers, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
