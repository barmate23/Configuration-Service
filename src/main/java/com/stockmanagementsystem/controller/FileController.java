package com.stockmanagementsystem.controller;


import com.stockmanagementsystem.entity.Location;
import com.stockmanagementsystem.exception.ExceptionLogger;
import com.stockmanagementsystem.exception.ValidationFailureException;
import com.stockmanagementsystem.repository.ItemRepository;
import com.stockmanagementsystem.repository.LocationRepository;
import com.stockmanagementsystem.response.BaseResponse;
import com.stockmanagementsystem.service.UploadExcelService;
import com.stockmanagementsystem.utils.APIConstants;
import com.stockmanagementsystem.utils.ServiceConstants;
import com.stockmanagementsystem.validation.Validations;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@RestController
@Slf4j
@RequestMapping({APIConstants.BASE_REQUEST + APIConstants.SERVICENAME+APIConstants.FILE_CONTROLLER})
public class FileController {

    @Autowired
    private UploadExcelService uploadExcelService;

    @Autowired
    private Validations validations;

    @Autowired
    private LocationRepository locationRepository;

    @Value("${baseFilePath}")
    private String baseFilePath;

    @GetMapping({APIConstants.FILE_DOWNLOAD})
    public ResponseEntity<byte[]> downloadExcelFile(@PathVariable String type,String logId) throws IOException {

        try {
            log.info("FileController----downloadExcelFile Method Exceution start with input parameter :" + type);

            String filePath = null;
            Object[] dropdownList =null;
            List<Integer> columnIndex=new ArrayList<>();
            switch (type) {
                case ServiceConstants.LOCATION:
                    filePath = baseFilePath + ServiceConstants.LOCATION_FILE;
                    break;
                case ServiceConstants.ITEM:
                    filePath = baseFilePath + ServiceConstants.ITEM_FILE;
                    break;
                case ServiceConstants.SUPPLIER:
                    filePath = baseFilePath + ServiceConstants.SUPPLIER_FILE;
                    break;
                case ServiceConstants.PURCHASEORDER:
                    filePath = baseFilePath + ServiceConstants.PURCHASE_FILE;
                    break;
                case ServiceConstants.REASON:
                    filePath = baseFilePath + ServiceConstants.REASON_FILE;
                    break;
                case ServiceConstants.STORE:
                    filePath = baseFilePath + ServiceConstants.STORE_FILE;
                    break;
                case ServiceConstants.EQUIPMENT:
                    filePath = baseFilePath + ServiceConstants.EQUIPMENT_FILE;
                    break;
                case ServiceConstants.DOCKS:
                    filePath = baseFilePath + ServiceConstants.DOCKS_FILE;
                    break;
                case ServiceConstants.BOM:
                    filePath = baseFilePath + ServiceConstants.BOM_FILE;
                    break;
                case ServiceConstants.PPE_HEAD:
                    filePath = baseFilePath + ServiceConstants.PPE_HEAD_FILE;
                    break;
                case ServiceConstants.USR:
                    filePath = baseFilePath + ServiceConstants.USR_FILE;
                    break;
                case ServiceConstants.USRLST:
                    filePath = baseFilePath + ServiceConstants.USR_LIST_FILE;
                    break;
            }

            log.info("FileController----downloadExcelFile Method----filePath :" + filePath);

            if (filePath != null) {
                // Read the Excel file content into a byte array
                File file = new File(filePath);
                byte[] excelBytes = Files.readAllBytes(file.toPath());
                // Set response headers for downloading the file
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
                headers.setContentDispositionFormData("attachment", type + ".xlsx");

                log.info("FileController----downloadExcelFile Method Exceuted");

                return ResponseEntity.ok()
                        .headers(headers)
                        .body(excelBytes);
            } else {
                log.error("Error occurred while downloading Excel file");
                return ResponseEntity.notFound().build();
            }
        }catch(Exception e){
            log.error("Error occurred while downloading Excel file: " + e.getMessage());
            ExceptionLogger.logException(e,null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


    @PostMapping({APIConstants.FILE_UPLOAD})
    public ResponseEntity<BaseResponse> uploadExcelFile(@RequestPart("file") MultipartFile file,@RequestParam("type") String type) throws IOException, ValidationFailureException {

        log.info("FileController----uploadExcelFile Method Execution start with input parameter :" + type);
        String logID=null;
        // Validate the file
        if (file == null || file.isEmpty()) {
           return ResponseEntity.ok(new BaseResponse(200, ServiceConstants.FILE_NOT_FOUND, null, 0,logID));
        }

        String filename = file.getOriginalFilename();
        if (filename == null || (!filename.endsWith(".xlsx") && !filename.endsWith(".csv") && !filename.endsWith(".xls"))) {
            return ResponseEntity.ok(new BaseResponse(ServiceConstants.STATUS_CODE_500, ServiceConstants.INVALID_FILE_FORMAT, null, 0, logID));
        }


        ResponseEntity<BaseResponse> stringResponseEntity = null;
        switch (type) {

            case ServiceConstants.LOCATION:
                //Calling uploadLocationDetail service
                stringResponseEntity =  this.uploadExcelService.uploadLocationDetail(file,type);
                break;

            case ServiceConstants.ITEM:

                //Calling uploadItemDetail service
                stringResponseEntity = this.uploadExcelService.uploadItemDetail(file,type);
                break;

            case ServiceConstants.SUPPLIER:
                stringResponseEntity = this.uploadExcelService.uploadSupplierDetail(file,type);
                break;

            case ServiceConstants.PURCHASEORDER:
                stringResponseEntity = this.uploadExcelService.uploadPurchaseOrders(file,type);
                break;

            case ServiceConstants.STORE:
                stringResponseEntity =this.uploadExcelService.uploadStoreDetail(file,type,logID);
                break;

            case ServiceConstants.REASON:
                stringResponseEntity =this.uploadExcelService.uploadReasonDetails(file,type);
                break;

            case ServiceConstants.DOCKS:
                stringResponseEntity =this.uploadExcelService.uploadDocksDetails(file,type);
                break;
            case ServiceConstants.EQUIPMENT:
                stringResponseEntity =this.uploadExcelService.uploadEquipmentDetail(file,type,logID);
                break;
            case ServiceConstants.PPE_HEAD:
                stringResponseEntity =this.uploadExcelService.uploadPpeDetails(file,type);
                break;
            case ServiceConstants.BOM:
                stringResponseEntity =this.uploadExcelService.uploadBomDetail(file,type,logID);
                break;
            case ServiceConstants.DEVICEMASTER:
                stringResponseEntity =this.uploadExcelService.uploadDeviceMasterDetails(file,type);
                break;
            case ServiceConstants.USERLIST:
                stringResponseEntity =this.uploadExcelService.uploadUserListDetails(file,type);
                break;
        }

        log.info("FileController----uploadExcelFile Method Executed");
        return stringResponseEntity;
    }

}

