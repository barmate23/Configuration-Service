package com.stockmanagementsystem.utils;

import org.hibernate.procedure.spi.ParameterRegistrationImplementor;

public class APIConstants {

    public static final String BASE_REQUEST="/api";
    public static final String FILE_CONTROLLER="/file";
    public static final String AREA_CONTROLLER="/area";
    public static final String DEVICE_MASTER_CONTROLLER="/DeviceMaster";
    public static final String BARCODE_CONTROLLER="/barcode";
    public static final String EQUIPMENT_CONTROLLER="/equipment";
    public static final String ASSEMBLY_LINE_CONTROLLER="/AssemblyLine";
    public static final String ZONE_CONTROLLER="/zone";


    public static final String SERVICENAME= "/UploadingDownloadingService/v1";
    public static final String FILE_DOWNLOAD="/download/excel/{type}";
    public static final String FILE_UPLOAD="/uploadExcel";

    public static final String CALENDER_CONTROLLER="/calender";
    public static final String COMMON_MASTER_CONTROLLER="/commonMaster";

    public static final String GET_HOLIDAYTYPE="/getHolidayType";
    public static final String GET_HOLIDAY="/getHoliday";
    public static final String SAVE_HOLIDAY="/saveHoliday";
    public static final String UPDATE_HOLIDAY="/updateHoliday/{holidayId}";
    public static final String DELETE_HOLIDAY="/delete/{holidayId}";
    public static final String GET_DAY="/getDay";
    public static final String GET_HOLIDAYS="/getHolidays";
    public static final String SAVE_SHIFT="/saveShift";
    public static final String UPDATE_SHIFT="/updateShift/{shiftId}";
    public static final String GET_BY_SHIFT_ID="/getByShiftId/{shiftId}";
    public static final String DELETE_SHIFT="/deleteShift/{shiftId}";

    public static final String GET_SHIFT="/getShift";
    public static final String SAVE_USERS_SHIFT="/saveUsersShift";
    public static final String GET_USER="/getUser";
    public static final String GET_USERS_BY_SHIFT="/getUsersByShift/{shiftId}";
    public static final String GET_USERS="/getUsers/{shiftId}";
    public static final String UPLOAD_USERS="/uploadUsers";
    public static final String DELETE_USER_FROM_SHIFT="/delete/{shiftId}/{userId}";
    public static final String DOCKS_CONTROLLER="/docks";
    public static final String BOM_CONTROLLER="/bom";
    public static final String SAVE_DOCK="/saveDock";
    public static final String DELETE_DOCK="/delete/{id}";
    public static final String UPDATE_DOCK="/updateDock/{id}";
    public static final String GET_DOCK="/getDocks";
    public static final String GET_DOCK_SEARCH="/getDocksWithSearch";
    public static final String GET_DOCKNAME="/getDocknames";
    public static final String GET_ATTRIBUTE="/getAttributes";
    public static final String GET_DOCK_EXCEL_BY_ID="/getDocks/downloadExcel/{id}";
    public static final String GET_EXCEL_DOCKS="/generateExcelDocks";
    public static final String GET_BARCODE_DOCKS="/generateDockBarcodePDF";
    public static final String GENERATE_BARCODE_FOR_DOCK="/dockBarcodePDFById/{id}";
    public static final String SUPPLIER_CONTROLLER="/supplier";
    public static final String SAVE_SUPPLIER="/saveSupplier";
    public static final String DELETE_SUPPLIER="/delete/{id}";
    public static final String UPDATE_SUPPLIER="/updateSupplier/{id}";
    public static final String GET_SUPPLIER_SEARCH="/getSuppliersWithSearch";
    public static final String GET_SUPPLIERS="/getSuppliers";
    public static final String GET_BY_SUPPLIER_ID="/getSupplierById/{id}";

    public static final String GET_ITEM="/getItems";

    public static final String SAVE_REASON="/saveReason";
    public static final String DELETE_REASON="/delete/{id}";
    public static final String UPDATE_REASON="/updateReason";

    public static final String GET_REASON_SEARCH="/getReasonsWithSearch";
    public static final String GET_REASON="/getReason";
    public static final String GET_All_REASONS="/getReasons";
    public static final String GET_EXCEL_REASONS="/generateExcelReasons";
    public static final String REASON_CONTROLLER="/reason";
    public static final String GET_STORE="/getStore";
    public static final String GET_ALL_USERS="/getUser";
    public static final String GET_ITEM_SEARCH="/getItemsWithSearch";

    public static final String GET_LOCATION_BARCODE = "/getLocationBarcode";
    public static final String GET_BARCODE_AREA="/generateAreaBarcodePDF";
    public static final String GET_BARCODE_STORES="/generateStoreBarcodePDF";
    public static final String GET_BARCODE_BY_CODE = "/getSingleBarcode";
    public static final String GET_STAGE_BARCODE = "/getAcceptedRejectedStageBarcode";

    public static final String GET_BARCODE_ZONE="/generateZoneBarcodePDF";
    public static final String GET_BARCODE_STAGE="/generateStageBarcodePDF";
    public static final String GET_BARCODE_EQUIPMENT = "/generateEquipmentBarcode";
    public static final String GET_ADDRESS_BY_PINCODE="/getAddressByPincode/{pincode}";
    public static final String GET_SHIFTS="/getShiftWithYearAndDays";
    public static final String GET_USER_NOT_IN_SHIFT="/getUserNotAddedInShift/{shiftId}";
    public static final String SAVE_DEVICE="/saveDevice";
    public static final String DELETE_DEVICE="/deleteDeviceById/{deviceId}";
    public static final String UPDATE_DEVICE="/updateDevice/{deviceId}";
    public static final String GET_DEVICE_BY_ID="/getDeviceById/{deviceId}";
    public static final String GET_ALL_DEVICE="/getAllDevice";
    public static final String GET_DEVICE_WITH_PAGINATION="/getAllDevicesWithPagination";
    public static final String GET_DEVICE_MASTER_WITH_SEARCH="/getDeviceMasterWithSearch";
    public static final String ACTIVE_DEVICE_BY_ID="/activeDeviceById";

    // area
    public static final String GET_ALL_AREA_WITH_PAGINATION="/getALlAreasWithPagination";
    public static final String GET_ALL_AREA_BY_STORE_ID="/getALlAreasByStoreId/{id}";
    public static final String SAVE_AREA="/saveArea";
    public static final String UPDATE_AREA="/updateArea/{id}";
    public static final String DELETED_BY_AREA_ID="/deleteAreaById/{id}";

    //ASSEMBLY LINE
    public static final String SAVE_ASSEMBLY_WITH_STAGE="/saveAssemblyWithStages";

    public static final String UPDATE_ASSEMBLY_LINE_BY_ID="/updateAssemblyLine/{id}";
    public static final String GET_ALL_ASSEMBLY_LINE_WITH_PAGINATION="/getAllAssemblyLineWithPagination";
    public static final String GET_ALL_ASSEMBLY_LINE="/getAllAssemblyLines";
    public static final String GET_ALL_STAGES="/getAllStages";
    public static final String GET_ALL_STAGES_WITH_PAGINATION="/getAllStageWithPagination";
    public static final String DELETE_ASSEMBLY_LINE_BY_ID="/deleteAssemblyLineById/{id}";
    public static final String DELETE_STAGE_BY_ID="deleteStageById/{id}";
    // BARCODE
    public static final String BARCODE_PRINT="/barcodePrint";
    public static final String GET_ALL_BOM_HEAD_WITH_PAGINATION = "/getAllBomHeadWithPagination";
    public static final String GET_ALL_BOM_LINES_WITH_PAGINATION = "/getAllBomLineWithPagination";
    public static final String GET_ALL_B0M_LINE_BY_BOM_ID = "/getAllBomLineByBomId/{id}";
    public static final String GET_REASON_BY_CATEGORY = "/getReasonByCategory";
    public static final String GET_ALL_SUB_MODULE = "/getAllSubModule";
    public static final String GET_STAGING_AREA_BY_DOCK = "/getAllStagingArea";
    public static final String GET_WEEKLY_OFF = "/getWeeklyOff";
    public static final String SAVE_WEEKLY_OFF = "/saveWeeklyOff";
}


