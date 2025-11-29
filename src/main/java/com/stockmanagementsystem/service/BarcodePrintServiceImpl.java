package com.stockmanagementsystem.service;

import com.stockmanagementsystem.entity.*;
import com.stockmanagementsystem.repository.*;
import com.stockmanagementsystem.response.BaseResponse;
import com.stockmanagementsystem.utils.ServiceConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
@Slf4j
@Service
public class BarcodePrintServiceImpl implements BarcodePrintService{

    @Autowired
    DeviceMasterRepository deviceMasterRepository;

    @Autowired
    LoginUser loginUser;

    @Autowired
    ZoneRepository zoneRepository;
    @Autowired
    AreaRepository areaRepository;
    @Autowired
    StoreRepository storeRepository;
    @Autowired
    LocationRepository locationRepository;
    @Autowired
    BarcodeMasterRepository barcodeMasterRepository;
    @Autowired
    SubModuleRepository subModuleRepository;
    @Autowired
    PrintQueueDetailsRepository printQueueDetailsRepository;
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    AcceptedRejectedContainerBarcodeRepository acceptedRejectedContainerBarcodeRepository;
    @Autowired
    StockMovementRepository stockMovementRepository;

    @Override
    public BaseResponse barcodePrinting(Integer deviceId, String type, Integer id, Integer zoneId,Integer asnLineId,Integer poLineId,Boolean isAccepted) {
        BaseResponse baseResponse = new BaseResponse<>();
        // Log the start of the method execution
        long startTime = System.currentTimeMillis();
        log.info("LOGID:{} - BARCODEPRINTING - USERID:{} - {} - METHOD START", loginUser.getLogId(), loginUser.getUserId(), "DEVICEID:" + deviceId + ", TYPE:" + type + ", ID:" + id + ", ZONEID:" + zoneId);

        try {
            if (id != null) {
                // Prepare and save a print queue detail for a specific entity
                PrintQueueDetail printQueueDetail = new PrintQueueDetail();
                printQueueDetail.setBarcodeMaster(barcodeMasterRepository.findByIsDeletedAndLabelFor(false, type));
                printQueueDetail.setDeviceMaster(deviceMasterRepository.findByIsDeletedAndId(false, deviceId));
                printQueueDetail.setPrintJobStatus(ServiceConstants.QUEUED);
                printQueueDetail.setTimeStamp(new Date());
                printQueueDetail.setIsDeleted(false);
                printQueueDetail.setCreatedBy(loginUser.getUserId());
                printQueueDetail.setCreatedOn(new Date());
                printQueueDetail.setModifiedBy(loginUser.getUserId());
                printQueueDetail.setModifiedOn(new Date());
                printQueueDetail.setSubOrganizationId(loginUser.getSubOrgId());
                printQueueDetail.setOrganizationId(loginUser.getOrgId());
                switch (type) {
                    case "ZONE":
                        Optional<Zone> zone = zoneRepository.findByIsDeletedAndIdAndSubOrganizationId(false, id, loginUser.getSubOrgId());
                        printQueueDetail.setValue(zone.get().getZoneId());
                        printQueueDetail.setSubModules(subModuleRepository.findByIsDeletedAndSubModuleCode(false, "ZONE"));
                        printQueueDetailsRepository.save(printQueueDetail);
                        log.info("LOGID:{} - BARCODEPRINTING - USERID:{} - SAVED PRINT QUEUE DETAIL FOR ZONE WITH ID: {}", loginUser.getLogId(), loginUser.getUserId(), id);
                        break;
                    case "LOCATION":
                        Location location = locationRepository.findByIsDeletedAndSubOrganizationIdAndId(false, loginUser.getSubOrgId(), id);
                        printQueueDetail.setValue(location.getLocationId());
                        printQueueDetail.setSubModules(subModuleRepository.findByIsDeletedAndSubModuleCode(false, "LCSN"));
                        printQueueDetailsRepository.save(printQueueDetail);
                        log.info("LOGID:{} - BARCODEPRINTING - USERID:{} - SAVED PRINT QUEUE DETAIL FOR LOCATION WITH ID: {}", loginUser.getLogId(), loginUser.getUserId(), id);
                        break;
                    case "ITEM":
                        Optional<Item> item = itemRepository.findByIsDeletedAndSubOrganizationIdAndId(false, loginUser.getSubOrgId(), id);
                        printQueueDetail.setValue(item.get().getItemCode());
                        printQueueDetail.setSubModules(subModuleRepository.findByIsDeletedAndSubModuleCode(false, "LCSN"));
                        printQueueDetailsRepository.save(printQueueDetail);
                        log.info("LOGID:{} - BARCODEPRINTING - USERID:{} - SAVED PRINT QUEUE DETAIL FOR ITEM WITH ID: {}", loginUser.getLogId(), loginUser.getUserId(), id);
                        break;
                    default:
                        log.warn("LOGID:{} - BARCODEPRINTING - USERID:{} - UNKNOWN TYPE: {}", loginUser.getLogId(), loginUser.getUserId(), type);
                        break;
                }
            } else {
                // Prepare and save print queue details for all entities of a specific type
                BarcodeMaster barcodeMaster = barcodeMasterRepository.findByIsDeletedAndLabelFor(false, type);
                DeviceMaster deviceMaster = deviceMasterRepository.findByIsDeletedAndId(false, deviceId);
                Date date = new Date();
                switch (type) {
                    case "ZONE":
                        List<Zone> zones = zoneRepository.findByIsDeletedAndSubOrganizationId(false, loginUser.getSubOrgId());
                        SubModule zoneSubModule = subModuleRepository.findByIsDeletedAndSubModuleCode(false, "ZONE");
                        for (Zone zone : zones) {
                            PrintQueueDetail printQueueDetail = createPrintQueueDetail(barcodeMaster, deviceMaster, zone.getZoneId(), ServiceConstants.QUEUED, zoneSubModule, date);
                            printQueueDetailsRepository.save(printQueueDetail);
                            log.info("LOGID:{} - BARCODEPRINTING - USERID:{} - SAVED PRINT QUEUE DETAIL FOR ZONE WITH ID: {}", loginUser.getLogId(), loginUser.getUserId(), zone.getZoneId());
                        }
                        break;
                    case "LOCATION":
                        List<Location> locations = locationRepository.findByIsDeletedAndSubOrganizationIdAndZoneIdOrderByIdAsc(false, loginUser.getSubOrgId(), zoneId);
                        SubModule locationSubModule = subModuleRepository.findByIsDeletedAndSubModuleCode(false, "LCSN");
                        for (Location location : locations) {
                            PrintQueueDetail printQueueDetail = createPrintQueueDetail(barcodeMaster, deviceMaster, location.getLocationId(), ServiceConstants.QUEUED, locationSubModule, date);
                            printQueueDetailsRepository.save(printQueueDetail);
                            log.info("LOGID:{} - BARCODEPRINTING - USERID:{} - SAVED PRINT QUEUE DETAIL FOR LOCATION WITH ID: {}", loginUser.getLogId(), loginUser.getUserId(), location.getLocationId());
                        }
                        break;
                    case "ITEM":
                        List<Item> items = itemRepository.findByIsDeletedAndSubOrganizationId(false, loginUser.getSubOrgId());
                        SubModule itemSubModule = subModuleRepository.findByIsDeletedAndSubModuleCode(false, "ITEM");
                        for (Item item : items) {
                            PrintQueueDetail printQueueDetail = createPrintQueueDetail(barcodeMaster, deviceMaster, item.getItemCode(), ServiceConstants.QUEUED, itemSubModule, date);
                            printQueueDetailsRepository.save(printQueueDetail);
                            log.info("LOGID:{} - BARCODEPRINTING - USERID:{} - SAVED PRINT QUEUE DETAIL FOR ITEM WITH ID: {}", loginUser.getLogId(), loginUser.getUserId(), item.getItemId());
                        }
                        break;
                    case "CRR_CONTAINER":
                        List<AcceptedRejectedContainerBarcode> acceptedRejectedContainerBarcodes=new ArrayList<>();
                        if(asnLineId!=null) {
                             acceptedRejectedContainerBarcodes = acceptedRejectedContainerBarcodeRepository.findByIsDeletedAndIsAcceptedAndSubOrganizationIdAndAcceptedRejectedContainerAsnLineId(false,isAccepted ,loginUser.getSubOrgId(), asnLineId);
                        }else{
                           acceptedRejectedContainerBarcodes = acceptedRejectedContainerBarcodeRepository.findByIsDeletedAndIsAcceptedAndSubOrganizationIdAndAcceptedRejectedContainerPurchaseOrderLineId(false,isAccepted, loginUser.getSubOrgId(), poLineId);
                        }
                        SubModule crrSubModule = subModuleRepository.findByIsDeletedAndSubModuleCode(false, "DOSU");
                        for (AcceptedRejectedContainerBarcode acceptedRejectedContainerBarcode : acceptedRejectedContainerBarcodes) {
                            PrintQueueDetail printQueueDetail = createPrintQueueDetail(barcodeMaster, deviceMaster, acceptedRejectedContainerBarcode.getCrrContainerCode(), ServiceConstants.QUEUED, crrSubModule, date);
                            printQueueDetailsRepository.save(printQueueDetail);
                            log.info("LOGID:{} - BARCODEPRINTING - USERID:{} - SAVED PRINT QUEUE DETAIL FOR CRR_CONTAINER WITH ID: {}", loginUser.getLogId(), loginUser.getUserId(), acceptedRejectedContainerBarcode.getCrrContainerCode());
                        }
                        break;
                    case "GRR":
                        List<StockMovement> stockMovements=new ArrayList<>();
                        if(asnLineId!=null) {
                            stockMovements = stockMovementRepository.findByIsDeletedAndIsAcceptedAndSubOrganizationIdAndAcceptedRejectedContainerAsnLineId(false,isAccepted ,loginUser.getSubOrgId(), asnLineId);
                        }else{
                            stockMovements = stockMovementRepository.findByIsDeletedAndIsAcceptedAndSubOrganizationIdAndAcceptedRejectedContainerPurchaseOrderLineId(false,isAccepted, loginUser.getSubOrgId(), poLineId);
                        }
                        SubModule grrSubModule = subModuleRepository.findByIsDeletedAndSubModuleCode(false, "SOPR");
                        for (StockMovement stockMovement : stockMovements) {
                            PrintQueueDetail printQueueDetail = createPrintQueueDetail(barcodeMaster, deviceMaster, stockMovement.getCrrGrrBarcode(), ServiceConstants.QUEUED, grrSubModule, date);
                            printQueueDetailsRepository.save(printQueueDetail);
                            log.info("LOGID:{} - BARCODEPRINTING - USERID:{} - SAVED PRINT QUEUE DETAIL FOR GRR WITH ID: {}", loginUser.getLogId(), loginUser.getUserId(), stockMovement.getCrrGrrBarcode());
                        }
                        break;
                    default:
                        log.warn("LOGID:{} - BARCODEPRINTING - USERID:{} - UNKNOWN TYPE: {}", loginUser.getLogId(), loginUser.getUserId(), type);
                        break;
                }
            }
            baseResponse.setMessage("SUCCESSFULLY BARCODE PRINT  PLEASE CHECK YOUR PRINTER");
            baseResponse.setStatus(200);
            baseResponse.setData(new ArrayList<>());
            baseResponse.setCode(1);
            baseResponse.setLogId(loginUser.getLogId());
        } catch (Exception e) {
            // Log the exception
            baseResponse.setMessage("FAILED TO PRINT BARCODE");
            baseResponse.setStatus(500);
            baseResponse.setData(new ArrayList<>());
            baseResponse.setCode(0);
            baseResponse.setLogId(loginUser.getLogId());
            log.error("LOGID:{} - BARCODEPRINTING - USERID:{} - ERROR OCCURRED IN BARCODEPRINTING METHOD", loginUser.getLogId(), loginUser.getUserId(), e);
        }

        // Log the end of the method execution
        long endTime = System.currentTimeMillis();
        log.info("LOGID:{} - BARCODEPRINTING - USERID:{} - METHOD END - DURATION: {} MS", loginUser.getLogId(), loginUser.getUserId(), (endTime - startTime));
        return baseResponse;
    }

    // Helper method to create a PrintQueueDetail object
    private PrintQueueDetail createPrintQueueDetail(BarcodeMaster barcodeMaster, DeviceMaster deviceMaster, String value, String printJobStatus, SubModule subModule, Date date) {
        PrintQueueDetail printQueueDetail = new PrintQueueDetail();
        printQueueDetail.setBarcodeMaster(barcodeMaster);
        printQueueDetail.setDeviceMaster(deviceMaster);
        printQueueDetail.setValue(value);
        printQueueDetail.setPrintJobStatus(printJobStatus);
        printQueueDetail.setSubModules(subModule);
        printQueueDetail.setTimeStamp(date);
        printQueueDetail.setIsDeleted(false);
        printQueueDetail.setCreatedBy(loginUser.getUserId());
        printQueueDetail.setCreatedOn(date);
        printQueueDetail.setModifiedBy(loginUser.getUserId());
        printQueueDetail.setModifiedOn(date);
        printQueueDetail.setSubOrganizationId(loginUser.getSubOrgId());
        printQueueDetail.setOrganizationId(loginUser.getOrgId());
        return printQueueDetail;
    }

    @Override
    public BaseResponse printCrrBarcode(String barcodeNumber,Integer deviceId){
    BaseResponse baseResponse=new BaseResponse<>();
    try{
        BarcodeMaster barcodeMaster = barcodeMasterRepository.findByIsDeletedAndLabelFor(false, "CRR_CONTAINER");
        DeviceMaster deviceMaster = deviceMasterRepository.findByIsDeletedAndId(false, deviceId);
        SubModule crrSubModule = subModuleRepository.findByIsDeletedAndSubModuleCode(false, "DOSU");
        PrintQueueDetail printQueueDetail  =createPrintQueueDetail(barcodeMaster, deviceMaster, barcodeNumber, ServiceConstants.QUEUED, crrSubModule, new Date());
        baseResponse.setMessage("SUCCESSFULLY BARCODE PRINT  PLEASE CHECK YOUR PRINTER");
        baseResponse.setStatus(200);
        baseResponse.setData(new ArrayList<>());
        baseResponse.setCode(1);
        baseResponse.setLogId(loginUser.getLogId());
    } catch (Exception e) {
        // Log the exception
        baseResponse.setMessage("FAILED TO PRINT BARCODE");
        baseResponse.setStatus(500);
        baseResponse.setData(new ArrayList<>());
        baseResponse.setCode(0);
        baseResponse.setLogId(loginUser.getLogId());
        log.error("LOGID:{} - BARCODEPRINTING - USERID:{} - ERROR OCCURRED IN BARCODEPRINTING METHOD", loginUser.getLogId(), loginUser.getUserId(), e);
    }
    // Log the end of the method execution
    long endTime = System.currentTimeMillis();
    log.info("LOGID:{} - BARCODEPRINTING - USERID:{} - METHOD END - DURATION: {} MS", loginUser.getLogId(), loginUser.getUserId());
    return baseResponse;
}

}
