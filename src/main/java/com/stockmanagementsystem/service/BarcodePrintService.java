package com.stockmanagementsystem.service;

import com.stockmanagementsystem.response.BaseResponse;
import org.springframework.stereotype.Service;

@Service
public interface BarcodePrintService {
    BaseResponse barcodePrinting(Integer deviceId, String type, Integer id,Integer zoneId,Integer asnLineId,Integer poLineId,Boolean isAccepted);

    BaseResponse printCrrBarcode(String barcodeNumber, Integer deviceId);
}
