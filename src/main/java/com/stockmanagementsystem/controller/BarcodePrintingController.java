package com.stockmanagementsystem.controller;

import com.stockmanagementsystem.response.BaseResponse;
import com.stockmanagementsystem.service.BarcodePrintService;
import com.stockmanagementsystem.utils.APIConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping({APIConstants.BASE_REQUEST + APIConstants.SERVICENAME+APIConstants.BARCODE_CONTROLLER})

public class BarcodePrintingController {

    @Autowired
    BarcodePrintService barcodePrintService;

    @PostMapping(APIConstants.BARCODE_PRINT)
    public BaseResponse barcodePrint(@RequestParam Integer deviceId,
                                     @RequestParam String type,
                                     @RequestParam(required = false) Integer id,
                                     @RequestParam(required = false) Integer zoneId,
                                     @RequestParam(required = false) Integer asnLineId,
                                     @RequestParam(required = false) Integer poLineId,
                                     @RequestParam(required = false) Boolean isAccepted){
        return barcodePrintService.barcodePrinting(deviceId,type,id,zoneId,asnLineId,poLineId,isAccepted);
    }

    @PostMapping("/printCrrBarcode")
    public BaseResponse printCrrBarcode(@RequestParam String crrNumber,Integer deviceId){
        return barcodePrintService.printCrrBarcode(crrNumber,deviceId);
    }
}
