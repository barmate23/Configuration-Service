package com.stockmanagementsystem.utils;

import java.util.concurrent.atomic.AtomicInteger;

public class ServiceConstants {

    public static final String SUPPLIER_DELIMITER = "S";
    public static final String PURCHASE_ORDER_CONTROLLER="/purchaseOrder";
    public static final String LOCATION="location";
    public static final String BASE_URL="/api/v1/UploadingDownloadingService";
    public static final String CONFIGURATION_MASTER_CONTROLLER="/configMasterController";
    public static final String ITEM_CONTROLLER="/item";
    public static final String DEVICE_MASTER_CONTROLLER="/deviceMaster";
    public static final String LOCATION_CONTROLLER="/location";

    public static final String GET_ALL_LOCATION_DATA_WITH_FILTERS="/getAllLocations";
    public static final String GET_LOCATION_BY_ID="/getLocationById/{id}";
    public static final String GET_ITEM_BY_ID="/getItemById/{id}";

    public static final String GET_DISTINCT_AREA_NAME="/getDistinctAreaName";

    public static final String GET_DISTINCT_CARRING_CAPACITY="/getDistinctCarringCapacity";

    public static final String SEARCH_BY_LOCATION_ID="/searchByLocationId";

    public static final String SEARCH_BY_ITEM="/searchByItem";

    public static final String GET_ITEM_DROPDOWN_DATA="/getItemDropdownData";
    public static final String GET_DISTINCT_STORE_NAME="/getDistinctStoreName";
    public static final String GET_SUPPLIER_BY_ID="/getSupplierById/{id}";

    public static final String GET_STORE_BY_ID="/getStoreById/{id}";

    public static final String GET_STORE_DROPDOWN="/getStoreDropdown";

    public static final String GET_REASON_DROPDOWN="/getReasonDropdown";

    public static final String GET_PURCHASE_ORDER_HEAD_DROPDOWN="/getPurchaseOrderHeadDropdown";

    public static final String GET_PURCHASE_ORDER_LINE_DROPDOWN="/getPurchaseOrderLineDropdown/{id}";

    public static final String DELETE_PURCHASE_ORDER_HEAD_BY_ID="/deletePurchaseOrderHeadById/{id}";

    public static final String DELETE_PURCHASE_ORDER_LINE_BY_ID="/deletePurchaseOrderLineById/{id}";

    public static final String DELETE_ITEM_BY_ID="/deleteItemById/{id}";

    public static final String DELETE_LOCATION_BY_ID="/deleteLocationById/{id}";

    public static final String DELETE_STORE_BY_ID="/deleteStoreById/{id}";

    public static final String DELETE_SUPPLIER_BY_ID="/deleteSupplierById/{id}";

    public static final String DELETE_REASON_BY_ID="/deleteReasonById/{id}";
    public static final String GET_SUPPLIER_DROPDOWN="/getSupplierDropdown";

    public static final String SEARCH_BY_STORE_NAME="/searchByStoreName";

    public static final String SEARCH_BY_REASON_NAME="/searchByReasonName";

    public static final String SEARCH_BY_PURCHASE_ORDER_NUMBER="/searchByPurchaseOrderNumber";

    public static final String SEARCH_BY_PURCHASE_ORDER_LINE_ITEM_NAME="/searchByPurchaseOrderLineItemName";
    public static final String SEARCH_BY_SUPPLIER_NAME="/searchBySupplierName";
    public static final String UPDATE_SUPPLIER_BY_ID="/updateSupplierById/{id}";

    public static final String UPDATE_STORE_BY_ID="/updateStoreById/{id}";

    public static final String UPDATE_REASON_BY_ID="/updateReasonById/{id}";

    public static final String UPDATE_PURCHASE_ORDER_HEAD_BY_ID="/updatePurchaseOrderHeadById/{id}";

    public static final String UPDATE_PURCHASE_ORDER_LINE_BY_ID="/updatePurchaseOrderLineById/{id}";

    public static final String GET_REASON_BY_ID="/getReasonById/{id}";

    public static final String GET_PURCHASE_ORDER_HEAD_BY_ID="/getPurchaseOrderHeadById/{id}";

    public static final String GET_PURCHASE_ORDER_LINE_BY_ID="/getPurchaseOrderLineById/{id}";
    public static final String UPDATE_LOCATION_BY_ID="/updateLocationById/{id}";

    public static final String UPDATE_ITEM_BY_ID="/updateItemById/{id}";
    public static final String GET_ALL_ITEM_DATA="/getAllItemData";
    public static final String SPACE="     ";

    public static final String GET_ALL_SUPPLIER_DATA="/getAllSuppliersWithFilters";

    public static final String GET_ALL_PURCHASE_ORDER_HEAD_DATA="/getAllPurchaseOrderHeadWithFilters";

    public static final String GET_ALL_PURCHASE_ORDER_LINE_DATA="/getAllPurchaseOrderLineWithFilters";
    public static final String GET_ALL_STORE_DATA="/getAllStoreWithFilters";

    public static final String GET_ALL_REASON_DATA="/getAllReasonWithFilters";
    public static final String UPLOAD_PURCHASE_ORDERS_METHOD_STARTED="uploadPurchaseOrderMethod started";

    public static final String GET_ALL_LOCATION_WITH_FILTER_METHOD_STARTED="getAllLocationsWithFilters Method started";
    public static final String GET_LOCATION_BY_ID_METHOD_STARTED="getLocationDataBYId Method Started";

    public static final String UPDATE_LOCATION_BY_ID_METHOD_STARTED="updateLocationDataBYId Method Started";
    public static final String LOCATION_DATA_UPLOAD_FAILED="Location data upload failed";

    public static final String PURCHASE_ORDER_DATA_UPLOAD_FAILED="Purchase Order data upload failed";
    public static final String PURCHASE_ORDER_DATA_UPLOAD_SUCCESSFULLY="Purchase Order data upload Successfully ";

    public static final String LOCATION_DATA_FALIED_TO_FETCHED="Location Data failed to fetched";

    public static final String LOCATION_DATA_FALIED_TO_UPDATE="Location Data failed to update";
    public static final String LOCATION_DATA_FETCHED_SUCCESSFULLY="Location Data fetched successfully";

    public static final String LOCATION_DATA_UPDATED_SUCCESSFULLY="Location Data updated successfully";
    public static final String SUPPLIER_DATA_UPLOAD_FAILED="Supplier data upload failed";
    public static final String ITEM_DATA_UPLOAD_FAILED="Item data upload failed";
    public static final String EXCEPTION_LOG="Exception Log";
    public static final Integer STATUS_CODE_500=500;
    public static final Integer STATUS_CODE_200=200;
    public static final String TOTAL_ROWS_SCANNED="Total rows scanned : ";
    public static final String ITEM="item";

    public static final String SUPPLIER="supplier";
    public static final String PURCHASEORDER="purchaseorder";

    public static final String REASON="reason";

    public static final String STORE="store";

    public static final String EQUIPMENT="equipment";

    public static final String LOCATION_FILE="Location.xlsx";

    public static final String ITEM_FILE="Item.xlsx";

    public static final String SUPPLIER_FILE="Supplier.xlsx";

    public static final String PURCHASE_FILE="PurchaseOrder.xlsx";

    public static final String REASON_FILE="Reason.xlsx";

    public static final String STORE_FILE="Store.xlsx";
    public static final String EQUIPMENT_FILE="Equipment.xlsx";
    public static final String DOCKS_FILE="Dock.xlsx";
    public static final String BOM_FILE="bom.xlsx";
    public static final String FILE_NOT_FOUND= "File not found";
    public static final String INVALID_FILE_FORMAT = "Invalid file format. Only excel files are allowed.";



    public static final Integer LOCATION_DROPDOWN_INDEX= 17;

    public static final Integer SUPPLIER_ID_DROPDOWN_INDEX= 18;
    public static final Integer SUPPLIER_NAME_DROPDOWN_INDEX= 19;
    public static final Integer ITEM_CODE_DROPDOWN_INDEX= 3;

    public static final Integer ITEM_NAME_DROPDOWN_INDEX= 4;
    public static final Integer SHEET_INDEX= 0;

    public static final Integer HEADER_INDEX= 1;

    public static final Integer SUCCESS_CODE= 1;

    public static final Integer ERROR_CODE= 0;
    public static final Integer LOCATION_COLUMN_HEADER_ROW_INDEX= 1;
    public static final Integer SUPPLIER_COLUMN_HEADER_ROW_INDEX= 1;
    public static final Integer PURCHASE_COLUMN_HEADER_ROW_INDEX= 1;
    public static final Integer ITEM_COLUMN_HEADER_ROW_INDEX= 1;
    public static final String UPLOAD_LOCATION_DETAIL_METHOD_EXECUTED="uploadLocationDetail Method Executed";

    public static final String UPLOAD_SUPPLIER_DETAIL_METHOD_EXECUTED="uploadSupplierDetail Method Executed";

    public static final String PURCHASE_ORDER_DETAIL_METHOD_EXECUTED="purchaseOrderDetail Method Executed";
    public static final String UPLOAD_ITEM_DETAIL_METHOD_EXECUTED="uploadItemDetail Method Executed";
    public static final String EXEC_TIME="Exceution time :";

    public static final String UPLOAD_LOCATION_DETAIL_METHOD_STARTED="uploadLocationDetail Method Started";

    public static final String UPLOAD_SUPPLIER_DETAIL_METHOD_STARTED="uploadSupplierDetail Method Started";
    public static final String UPLOAD_ITEM_DETAIL_METHOD_STARTED="uploadItemDetail Method Execution Start";
        public static final Integer CELL_INDEX_0 = 0;
        public static final Integer CELL_INDEX_1 = 1;
        public static final Integer CELL_INDEX_2 = 2;
        public static final Integer CELL_INDEX_3 = 3;
        public static final Integer CELL_INDEX_4 = 4;
        public static final Integer CELL_INDEX_5 = 5;
        public static final Integer CELL_INDEX_6 = 6;
        public static final Integer CELL_INDEX_7 = 7;
        public static final Integer CELL_INDEX_8 = 8;
        public static final Integer CELL_INDEX_9 = 9;
        public static final Integer CELL_INDEX_10 = 10;
        public static final Integer CELL_INDEX_11 = 11;
        public static final Integer CELL_INDEX_12 = 12;
        public static final Integer CELL_INDEX_13 = 13;
        public static final Integer CELL_INDEX_14 = 14;
        public static final Integer CELL_INDEX_15 = 15;
        public static final Integer CELL_INDEX_16 = 16;
        public static final Integer CELL_INDEX_17 = 17;
        public static final Integer CELL_INDEX_18 = 18;
        public static final Integer CELL_INDEX_19 = 19;
        public static final Integer CELL_INDEX_20 = 20;
        public static final Integer CELL_INDEX_21 = 21;

        public static final Integer CELL_INDEX_22 = 22;
        public static final Integer CELL_INDEX_23 = 23;
        public static final Integer CELL_INDEX_24 = 24;
        public static final Integer CELL_INDEX_25 = 25;
        public static final String FILE_UPLOADED_SUCCESSFULLY="File uploaded and data inserted successfully";


    // Define constants for location column names
    public static final String ORGANIZATION_ID = "Organization Id";
    public static final String SUB_ORGANIZATION_ID ="Sub-organization Id";
    public static final String STORE_ID = "Store Id";
    public static final String EQUP_STORE_ID = "ERP Store Code";
    public static final String EQUP_TROLLEY_ID = "Equipment ID";
    public static final String EQUP_ASSET_ID = "Asset ID";
    public static final String EQUP_TROLLEY_TYPE = "Equipment Type";


    public static final String ERP_STORE_ID="ERP Store ID";

    public static final String ERP_AREA_ID="ERP Area Id";
    public static final String ERP_ZONE_ID="ERP Zone Id";
    public static final String Zone_ID="Zone Id";
    public static final String Zone_NAME="Zone Name";
    public static final String ERP_LOCATION_ID="ERP Location Id";
    public static final String LOCATION_ID="Location Id";

    public static final String ITEM_ID="Item Code";
    public static final String ITEM_NAME="Item Name";
    public static final String NAME="Name";

    public static final String ITEM_CODE="Item Code";
    public static final String STORE_NAME = "Store Name";
    public static final String AREA_ID = "Area Id";
    public static final String AREA_NAME = "Area Name";
    public static final String LEVEL = "Level";
    public static final String ROW = "Row";
    public static final String RACK_FLOOR = "Rack / Floor";
    public static final String RACK_NO = "Rack No.";
    public static final String SHELF_NO = "Shelf No.";
    public static final String LOCATION_TYPE = "Location Type (Attribute)";
    public static final String LENGTH = "Length (cm)";
    public static final String WIDTH = "Width (cm)";
    public static final String HEIGHT = "Height (cm)";
    public static final String AREA_SQ_CM = "Area (Sq. cm)";
    public static final String VOLUME_CU_CM = "Volume (cu cm)";
    public static final String CARRYING_CAPACITY = "Carrying Capacity (Kg)";


// Constants for Item column names
//public static final String ITEM_CODE = "ItemCode";

    public static final String ITEM_DESCRIPTION = "Description";
    public static final String TYPE = "Type (Direct / Indirect)";
    public static final String TYPES = "Type";
    public static final String TYPE_SERIAL= "Type (Serial / Batch / None)";


    public static final String QC_REQUIRED = "QC Required(Yes/No)";
    public static final String INSPECTION = "Inspection";
    public static final String ERP_ITEM_ID="ERP Item ID";
    public static final String ITEM_CLASS = "ItemClass";
    public static final String ITEM_GROUP = "Item Group";
    public static final String ISSUE_TYPE =  "Issue Type";
    public static final String CLASS ="Class (A / B / C)";
    public static final String ATTRIBUTE = "Attribute";
    public static final String SOURCE =  "Source";
    public static final String ITEM_CATEGORY ="Item Category";
    public static final String ITEM_SUB_CATEGORY ="Item Subcategory";
    public static final String ITEM_UNIT_WEIGHT = " Item Unit Weight";

    public static final String UOM = "UOM";
    public static final String SUPPLIER_ID = "Supplier ID";
    public static final String USR = "USR";
    public static final String USR_FILE = "usershift.xlsx";
    public static final String USR_LIST_FILE = "userlist.xlsx";

    public static final String PREFIX_ACCEPTED_REJECTED = "AR";
    public static final String DELIMITER_ACCEPTED_REJECTED_STAGING_AREA = "S";
    public static final int MAX_ACCEPTED_REJECTED__PER_AR = 9;
    public static final AtomicInteger accepteRejectedCounter = new AtomicInteger(1);
    public static final String ZONEC = "ZONEC";
    public static final String USRLST = "USRLST";
    public static final String USERLIST = "userList";

    protected static final String ERP_SUPPLIER_ID = "ERP Supplier Code";
    public static final String SUPPLIER_NAME = "Supplier Name";
    public static final String ITEM_UNIT_RATE = "Item Unit Rate";
    public static final String CURRENCY = "Currency";
    public static final String CODE = "Code";
    public static final String CONTAINER_TYPE = "ContainerType";
    public static final String DIMENSION_UOM = "Dimension UOM";

    public static final String ITEM_WIDTH = "Width";

    public static final String ITEM_HEIGHT = "Height";

    public static final String ITEM_LENGTH = "Length";

    public static final String CIRCUMFERENCE = "Circumference";
    public static final String WEIGHT = "Weight";
    public static final String ITEM_QTY = "Item Qty";
    public static final String MINIMUM_ORDER_QTY = "Minimum Order Qty(Container)";
    public static final String OPTIMUM_LEVEL = "Optimum";
    public static final String REORDER_LEVEL = "Reorder";
    public static final String SAFETY_LEVEL = "Safety";
    public static final String CRITICAL_LEVEL = "Critical";
    public static final String LEAD_DAYS = "Days";
    public static final String LEAD_HOURS = "Hours";
    public static final String DOCK = "Dock ID";
    public static final String DOCK_NAME = "Dock Name";
    public static final String SUPPLIER_TAN_NUMBER = "Supplier TAN Number";

    public static final String SUPPLIER_PAYMENT_TERMS = "Payment Terms";

    public static final String SUPPLIER_PAYMENT_METHOD = "Payment Method";
    public static final String SUPPLIER_CREDIT_LIMIT_RS= "Credit Limit (Rs.)";

    public static final String SUPPLIER_CREDIT_LIMIT_DAYS= "Credit Limit (Days)";

    public static final String SUPPLIER_PRIMARY_BANKER= "Supplier Primary Banker";

    public static final String FULL_BRANCH_ADDRESS= "Full Branch Address";

    public static final String MICR_CODE= "MICR Code";

    public static final String IFSC_CODE = "IFSC Code";
    public static final String COUNTRY = "Country";
    public static final String COUNTRY_CODE = "Country Code";
    public static final String STATE = "State";
    public static final String DISTRICT = "District";
    public static final String TALUKA = "Taluka";
    public static final String CITY = "City";
    public static final String TOWN = "Town";
    public static final String VILLAGE = "Village";
    public static final String ADDRESS_1 = "Address 1";
    public static final String ADDRESS_2 = "Address 2";
    public static final String BUILDING = "Building";
    public static final String STREET = "Street";
    public static final String LANDMARK = "Landmark";
    public static final String SUB_LOCALITY = "Sub-Locality";
    public static final String LOCALITY = "Locality";
    public static final String POST_CODE = "Post Code";
    public static final String AREA_CODE = "Area Code";
    public static final String LATITUDE = "Latitude";
    public static final String LONGITUDE = "Longitude";
    public static final String OFFICE_PRIMARY_PHONE = "Office Primary Phone";
    public static final String OFFICE_ALTERNATE_PHONE = "Office Alternate Phone";
    public static final String SUPPLIER_GST_REGISTRATION_NUMBER = "Supplier GST Registration Number";
    public static final String CONTACT_PERSON_NAME = "Contact Person Name";
    public static final String DESIGNATION = "Desigination";
    public static final String DEPARTMENT = "Department";
    public static final String PRIMARY_PHONE = "Primary Phone";
    public static final String ALTERNATE_PHONE = "Alternate Phone";
    public static final String PRIMARY_EMAIL = "Primary Email";
    public static final String ALTERNATE_EMAIL = "Alternate Email";
    public static final String MATERIAL_UNIT_RATE = "MaterialUnitRate";


    public static final String CONTAINER_CODE = "ContainerCode";




// CONSTATNTS FOR SUPPLIER MASTER LIST

    public static final String DATE_OF_REGISTRATION = "Date of Registration";
    public static final String SUPPLIER_CATEGORY = "Supplier Category";
    public static final String SUPPLIER_GROUP = "Supplier Group";
    public static final String SUPPLIER_TYPE = "SupplierType";

    public static final String ADDRESS = "Address";


    public static final String CITY_TOWN_VILLAGE = "City/Town/Village";

    public static final String OFFICE_PRIMARY_NUMBER = "OfficePrimaryNumber";
    public static final String OFFICE_ALTERNATE_NUMBER = "OfficeAlternateNumber";

    public static final String SUPPLIER_PAN_CARD_NUMBER = "Supplier PAN Number";
    public static final String PRIMARY_CONTACT_PERSON_NAME = "PrimaryContactPersonName";


    public static final String SUPPLIER_PAN_CARD_NUMBER_DOES_NOT_MATCH_GST_NUMBER = "Supplier PAN Card Number does not match the PAN extracted from GST Registration Number";

    public static final String PURCHASE_ORDER_NUMBER = "Purchase Order Number";
    public static final String PURCHASE_ORDER_DATE = "Purchase Order Date";
    public static final String LINE_NUMBER = "Line Number";
    public static final String P_ITEM_CODE = "Item Code";
    public static final String P_ITEM_NAME = "Item Name";
    public static final String P_UOM = "UOM";
    public static final String UNIT_PRICE = "Unit Price (Rs)";
    public static final String PURCHASE_ORDER_QUANTITY = "Purchase Order Quantity";
    public static final String SUB_TOTAL = "Sub-Total (Rs.)";
    public static final String STATE_GST_PERCENTAGE = "State GST %";
    public static final String STATE_GST = "State GST (Rs.)";
    public static final String CENTRAL_GST_PERCENTAGE = "Central GST %";
    public static final String CENTRAL_GST = "Central GST (Rs.)";
    public static final String INTERSTATE_GST_PERCENTAGE = "Inter State GST %";
    public static final String INTERSTATE_GST = "Inter State GST (Rs.)";
    public static final String TOTAL_AMOUNT = "Total Amount (Rs.)";
    public static final String DELIVER_BY_DATE = "Deliver by Date";
    public static final String DELIVER_TYPE = "Delivery Type";
    public static final String P_SUPPLIER_ID = "Supplier Code";
    public static final String P_SUPPLIER_NAME = "Supplier Name";


//    public static final String ERP_STORE_ID="ERP Store ID";
    public static final String STORE_MANAGER_NAME="Storekeeper (username)";
    public static final String CONTACT_NUMBER="ContactNumber";
    public static final String EMAIL_ID="EmailId";
    public static final String STORAGE_LOCATION_ID_NOT_PRESENT = "StorageLocationId is not present in Location master";

    public static final String ITEM_CODE_NOT_PRESENT = "Item code is not present in item master";


    public static final String SUPPLIER_ID_NOT_PRESENT = "SuppplierId is not present in supplier master";

    public static final String PURCHASE_ORDER_NUMBER_ALREADY_PRESENT = "Purchase Order Number is already present in master";
    public static final String SUPPLIER_NAME_NOT_PRESENT = "Suppplier Name is not valid against provided SuppplierId";
    public static final String ITEM_NAME_NOT_PRESENT = "Item Name is not valid against provided item code";


    public static final String LOCATION_NOT_FOUND ="Location id not found";
    public static final String GET_ALL_ITEMS_WITH_FILTER_METHOD_STARTED = "getAllItemsWithFilters Started";
    public static final String ITEM_DATA_FETCHED_SUCCESSFULLY = "Item data fetched succesfully";
    public static final String ITEM_DATA_FALIED_TO_FETCHED = "Item data failed to fetched";
    public static final String GET_ITEM_BY_ID_METHOD_STARTED ="getItemDataById Method Started" ;
    public static final String ITEM_NOT_FOUND = "Item Not Found";
    public static final String UPDATE_ITEM_BY_ID_METHOD_STARTED = "updateItemById Method started";
    public static final String ITEM_DATA_UPDATED_SUCCESSFULLY = "Item Data updated successfully";

    public static final String ITEM_DATA_FALIED_TO_UPDATE = "Item Data Failed to update";

    public static final String SUPPLIER_MASTER_DATA_NOT_FOUND = "Supplier master data not found";
    public static final String GET_ALL_SUPPLIERS_WITH_FILTER_METHOD_STARTED = "getAllSupplierWithFiler Method Started";
    public static final String SUPPLIER_DATA_FETCHED_SUCCESSFULLY = "Supplier Data fetched Successfully";
    public static final String SUPPLIER_DATA_FALIED_TO_FETCHED = "Supplier Data failed to Fetched";
    public static final String GET_SUPPLIER_BY_ID_METHOD_STARTED = "getSupplierById Method Started";
    public static final String SUPPLIER_NOT_FOUND = "Supplier Not Found";
    public static final String SUPPLIER_DATA_FAILED_TO_FETCHED = "Supplier Data Failed to Fetch";
    public static final String UPDATE_SUPPLIER_BY_ID_METHOD_STARTED = "updateSupplierById Method Started";
    public static final String SUPPLIER_DATA_UPDATED_SUCCESSFULLY = "Supplier Data updated successfully";
    public static final String SUPPLIER_DATA_FAILED_TO_UPDATE = "Supplier Data failed to fetched";
    public static final String GET_ALL_STORES_WITH_FILTER_METHOD_STARTED = "getAllStoreDataWithFilter Method Started";
    public static final String STORE_DATA_FETCHED_SUCCESSFULLY = "Store Data Fetched Successfully";

    public static final String REASON_DATA_FETCHED_SUCCESSFULLY = "Reason Data Fetched Successfully";

    public static final String PURCHASE_ORDER_HEAD_FETCHED_SUCCESSFULLY = "Purchase Order Head Data Fetched Successfully";

    public static final String PURCHASE_ORDER_LINE_FETCHED_SUCCESSFULLY = "Purchase Order Line Data Fetched Successfully";
    public static final String PURCHASE_ORDER_LINE_DATA_FETCHED_SUCCESSFULLY = "Purchase order line Data Fetched Successfully";

    public static final String PURCHASE_ORDER_HEAD_DATA_DELETED_SUCCESSFULLY = "Purchase order head Data deleted Successfully";

    public static final String PURCHASE_ORDER_LINE_DATA_DELETED_SUCCESSFULLY = "Purchase order Line Data deleted Successfully";

    public static final String LOCATION_MASTER_DATA_NOT_FOUND = "Location master data not found";

    public static final String ITEM_MASTER_DATA_NOT_FOUND = "Item master data not found";

    public static final String STORE_MASTER_DATA_NOT_FOUND = "Store master data not found";

    public static final String REASON_MASTER_DATA_NOT_FOUND = "Reason master data not found";

    public static final String LOCATION_MASTER_DATA_DELETED_SUCCESSFULLY = "Location master Data deleted Successfully";

    public static final String ITEM_MASTER_DATA_DELETED_SUCCESSFULLY = "Item master Data deleted Successfully";

    public static final String STORE_MASTER_DATA_DELETED_SUCCESSFULLY = "Store master Data deleted Successfully";

    public static final String SUPPLIER_MASTER_DATA_DELETED_SUCCESSFULLY = "Supplier master Data deleted Successfully";

    public static final String REASON_MASTER_DATA_DELETED_SUCCESSFULLY = "Reason master Data deleted Successfully";
    public static final String STORE_DATA_FALIED_TO_FETCHED = "Store Data Failed to Fetched";
    public static final String GET_STORE_BY_ID_METHOD_STARTED = "getStoreById Method Started";

    public static final String GET_REASON_BY_ID_METHOD_STARTED = "getReasonById Method Started";

    public static final String GET_PURCHASE_ORDER_HEAD_BY_ID_METHOD_STARTED = "getPurchaseOrderHeadById Method Started";

    public static final String GET_PURCHASE_ORDER_LINE_BY_ID_METHOD_STARTED = "getPurchaseOrderLineById Method Started";
    public static final String STORE_NOT_FOUND = "Store Not Found";
    public static final String UPDATE_STORE_BY_ID_METHOD_STARTED = "updateStoreById Method Started";
    public static final String STORE_DATA_UPDATED_SUCCESSFULLY = "Store Data Updated Successfully";
    public static final String DISTINCT_AREA_NAME_FETCHED_SUCCESSFULLY= "Distinct area name fetched successfully";

    public static final String ITEM_DATA_DROPDWON_FETCHED_SUCCESSFULLY= "Item data dropdown fetched successfully";

    public static final String REASON_DATA_DROPDWON_FETCHED_SUCCESSFULLY= "Reason data dropdown fetched successfully";
    public static final String STORE_DATA_DROPDWON_FETCHED_SUCCESSFULLY= "Store data dropdown fetched successfully";
    public static final String SUPPLIER_DATA_DROPDWON_FETCHED_SUCCESSFULLY= "Supplier data dropdown fetched successfully";
    public static final String LOCATION_ID_SEARCHED_SUCCESSFULLY= "Location Id searched successfully";

    public static final String ITEM_DATA_SEARCHED_SUCCESSFULLY= "Item data searched successfully";

    public static final String PURCHASE_ORDER_HEAD_DATA_SEARCHED_SUCCESSFULLY= "Purchase order head data searched successfully";

    public static final String PURCHASE_ORDER_LINE_DATA_SEARCHED_SUCCESSFULLY= "Purchase order Line data searched successfully";
    public static final String STORE_DATA_FAILED_TO_UPDATE ="Store Data Failed to fetched";
    public static final String STORE_ID_IS_NOT_PRESENT_MASTER_CONFIGURATION_TABLE = "StoreId is not found Master Configuration table";
    public static final String STORE_NAME_IS_NOT_MATCH_AGAINST_PROVIDED_STORE_ID= "StoreName is not matched against provided storeId";
    public static final String LOCATION_ID_ALREADY_ASSIGNED_TO_AREAID= "Location id is already assigned against areaId and storeId";

    public static final String SUPPLIER_ID_IS_ALREADY_PRESENT_IN_MASTER_CONFIGURATION= "Supplier id is already present in master configuration";
    public static final String SUPPLIER_NAME_IS_ALREADY_PRESENT_IN_MASTER_CONFIGURATION= "Supplier Name is already present in master configuration";
    public static final String DEVICE_NAME_IS_ALREADY_PRESENT_IN_DB= "Device Name is already present in DB";
    public static final String BOM_DATA_UPLOAD_FAILED = " BILL OF MATERIAL DATA UPLOAD FAILED ";
    public static final String EQUIPMENT_DATA_UPLOAD_FAILED = "EQUIPMENT DATA UPLOAD FAILED ";
    public static final String PPE_DATA_UPLOAD_FAILED = "PPE DATA UPLOAD FAILED ";
    public static final String STORE_DATA_UPLOAD_FAILED = "STORE DATA UPLOAD FAILED ";



    public static final Integer STORE_COLUMN_HEADER_ROW_INDEX = 1;
    public static final String UPLOAD_STORE_DETAIL_METHOD_EXECUTED = "upload store Detail Method Executed";
    public static final String UPLOAD_PPE_DETAIL_METHOD_EXECUTED = "upload ppe Detail Method Executed";
    public static final String UPLOAD_EQUIPMENT_DETAIL_METHOD_EXECUTED = "upload Equipment Detail Method Executed";

    public static final String UPLOAD_BOM_DETAIL_METHOD_EXECUTED = "upload bill of material Detail Method Executed";
    public static final String UPLOAD_STORE_DETAIL_METHOD_STARTED = "uploadStoreDetail Method Started";
    public static final String UPLOAD_EQUIPMENT_DETAIL_METHOD_STARTED = "uploadEquipmentDetail Method Started";
    public static final String UPLOAD_PPE_DETAIL_METHOD_STARTED = "uploadPpeDetail Method Started";
    public static final String UPLOAD_BOM_DETAIL_METHOD_STARTED = "uploadBomDetail Method Started";
    public static final Integer STORE_ID_DROPDOWN_INDEX_IN_LOCATION =1;
    public static final Integer STORE_NAME_DROPDOWN_INDEX_IN_LOCATION =2;
    public static final String LOCATION_ID_SEARCH_FAILED = "Location searched failed";

    public static final String ITEM_DATA_SEARCH_FAILED = "Item Data searched failed";

    public static final String PURCHASE_ORDER_HEAD_DATA_SEARCH_FAILED = "Purchase Order Head Data searched failed";
    public static final String PURCHASE_ORDER_LINE_DATA_SEARCH_FAILED = "Purchase Order Line Data searched failed";
    public static final String DISTINCT_AREA_NAME_FETCH_FAILED = "Distinct area name fetch failed";
    public static final String DISTINCT_STORE_NAME_FETCHED_SUCCESSFULLY = "Store name fetched successfully";
    public static final String DISTINCT_STORE_NAME_FETCH_FAILED = "Store name failed to fetch";
    public static final String DISTINCT_CARRING_CAPACITY_FETCHED_SUCCESSFULLY = "Carrying Capacity fetch successfully";
    public static final String DISTINCT_CARRING_CAPACITY_FETCH_FAILED = "Carrying Capacity failed to fetch";
    public static final String Batch="Batch";

    public static final String Serial="Serial";
    public static final String MODE_CELL_VALUE_NOT_MATCH ="It will take only Batch Or Serial";
    public static final String BATCH_NUMBER_FOUND_NULL = "Batch Number found null";
    public static final String SERIAL_NUMBER_NOT_NULL = "Serial Number found null";
    public static final String BATCH_QUANTITY_FOUND_NULL = "Batch Quantity found null";

    public static final String ITEM_CODE_IS_ALREADY_PRESENT = "THIS ITEM CODE IS ALREADY PRESENT";
    public static final String DOCK_ID_IS_ALREADY_PRESENT = "Dock Id is already present in Item master";
    public static final String SERIAL_QUANTITY_NOT_NULL = "Serial Quantity found null";
    public static final String BATCH_QUANTITY_SHOULD_NOT_BE_GREATER_THAN_LOCATION_CAPACITY ="Batch quantity should not be greater than Location Capacity" ;
    public static final String SERIAL_QUANTITY_SHOULD_NOT_BE_GREATER_THAN_LOCATION_CAPACITY = "Serial quantity should not be greater than Location Capacity";
    public static final String OPTIMUM_LEVEL_SHOULD_BE_EQUAL_TO_LOCATION_CAPACITY = "Optimum level should be equal to minimum order Qty";

    public static final String LOCATION_MASTER_NOT_FOUND = "Location master not found";
    public static final String REORDER_LEVEL_SHOULD_BE_LESS_THAN_LOCATION_CAPACITY ="Reorder Level should be less than minimum order Qty";
    public static final String SAFETY_LEVEL_SHOULD_BE_LESS_THAN_LOCATION_CAPACITY = "Safety Level should be less than minimum order Qty";

    public static final String CRITICAL_LEVEL_SHOULD_BE_LESS_THAN_LOCATION_CAPACITY = "Critical Level should be less than minimum order Qty";
    public static final String ITEM_DATA_FETCH_FAILED = "Item data failed to fetched";
    public static final String REASON_CODE ="ReasonCode";
    public static final String REASON_NAME ="ReasonName";
    public static final String UPLOAD_REASON_DETAIL_METHOD_STARTED = "uploadReasonDetail Method Started";
    public static final String UPLOAD_DEVICE_MASTER_METHOD_STARTED = "uploadDeviceMasterDetail Method Started";
    public static final String REASON_DATA_UPLOAD_FAILED = "Reason Data Upload Failed";
    public static final String DEVICE_MASTER_DATA_UPLOAD_FAILED = "Device Master Data Upload Failed";
    public static final Integer REASON_COLUMN_HEADER_ROW_INDEX = 1;
    public static final String UPLOAD_REASON_DETAIL_METHOD_EXECUTED = "uploadReasonMethod Executed";
    public static final String UPLOAD_DEVICE_MASTER_DETAIL_METHOD_EXECUTED = "uploadDeviceMasterDetails Executed";
    public static final String REASON_DATA_FAILED_TO_FETCH ="Reason Data failed to fetched";

    public static final String PURCHASE_ORDER_HEAD_DATA_FAILED_TO_FETCH ="Purchase Order Head Data failed to fetched";
    public static final String PURCHASE_ORDER_LINE_DATA_FAILED_TO_FETCH ="Purchase order line Data failed to fetched";
    public static final String GET_ALL_REASONS_WITH_FILTER_METHOD_STARTED = "getAllReasonWithFilter Method Started";

    public static final String GET_ALL_PURCHASE_ORDER_WITH_FILTER_METHOD_STARTED = "getAllPurchaseOrderWithFilter Method Started";
    public static final String UPDATE_REASON_BY_ID_METHOD_STARTED = "updateReasonById Method Started";
    public static final String REASON_NOT_FOUND = "Reason Not Found";

    public static final String PURCHASE_ORDER_HEAD_NOT_FOUND = "Purchase Order Head Not Found";

    public static final String PURCHASE_ORDER_LINE_NOT_FOUND = "Purchase Order Line Not Found";
    public static final String REASON_DATA_UPDATED_SUCCESSFULLY = "Reason Data updated successfully";

    public static final String PURCHASE_ORDER_HEAD_DATA_UPDATED_SUCCESSFULLY = "Purchase Order Head Data updated successfully";

    public static final String PURCHASE_ORDER_LINE_DATA_UPDATED_SUCCESSFULLY = "Purchase Order Line Data updated successfully";
    public static final String REASON_DATA_FAILED_TO_UPDATE ="Reason Data Failed to update";

    public static final String PURCHASE_ORDER_HEAD_DATA_FAILED_TO_UPDATE ="Purchase Order Head Data Failed to update";

    public static final String PURCHASE_ORDER_LINE_DATA_FAILED_TO_UPDATE ="Purchase Order Line Data Failed to update";

    public static final String PURCHASE_ORDER_HEAD_DATA_DELETION_FAILED = "Purchase order head deleted successfully";

    public static final String PURCHASE_ORDER_LINE_DATA_DELETION_FAILED = "Purchase order line deletion failed";

    public static final String LOCATION_DATA_DELETION_FAILED = "Location master deletion failed";

    public static final String ITEM_DATA_DELETION_FAILED = "Item master deletion failed";

    public static final String STORE_DATA_DELETION_FAILED = "Store master deletion failed";

    public static final String SUPPLIER_DATA_DELETION_FAILED = "Supplier master deletion failed";

    public static final String REASON_DATA_DELETION_FAILED = "Reason master deletion failed";
    public static final Integer CELL_INDEX_26 = 26;
    public static final Integer CELL_INDEX_27 = 27;
    public static final Integer CELL_INDEX_28 = 28;
    public static final Integer CELL_INDEX_29 = 29;

    public static final Integer CELL_INDEX_30 = 30;

    public static final Integer CELL_INDEX_31 = 31;
    public static final Integer CELL_INDEX_32 = 32;
    public static final Integer CELL_INDEX_33 = 33;
    public static final Integer CELL_INDEX_34 = 34;
    public static final Integer CELL_INDEX_35 = 35;
    public static final Integer CELL_INDEX_36 = 36;
    public static final Integer CELL_INDEX_37 = 37;
    public static final Integer CELL_INDEX_38 = 38;
    public static final Integer CELL_INDEX_39 = 39;
    public static final Integer CELL_INDEX_40 = 40;
    public static final Integer CELL_INDEX_41 = 41;
    public static final Integer CELL_INDEX_42 = 42;
    public static final Integer CELL_INDEX_43 = 43;
    public static final Integer CELL_INDEX_44 = 44;
    public static final Integer CELL_INDEX_45 = 45;
    public static final Integer CELL_INDEX_46 = 46;
    public static final Integer CELL_INDEX_47 = 47;
    public static final Integer CELL_INDEX_48 = 48;
    public static final Integer CELL_INDEX_49 = 49;
    public static final Integer CELL_INDEX_50 = 50;
    public static final Integer CELL_INDEX_51 = 51;

    public static final Integer INTERNAL_SERVER_ERROR=500;


    public static final String ITEM_BASE_QUERY ="SELECT * FROM item ";

    public static final String WHERE ="WHERE ";
    public static final String CLOSING_BRACKET = ")";
    public static final String ITEM_CODE_IN_CLAUSE ="item.item_code IN (";

    public static final String ITEM_NAME_IN_CLAUSE ="item.item_name IN (";

    public static final String STORE_NAME_IN_CLAUSE ="item_location_mapper.store_name IN (";

    public static final String ITEM_STORAGE_LOCATION_IDS_IN_CLAUSE ="location.location_id IN (";

    public static final String AND =" AND ";
    public static final String OFFSET =" OFFSET ";

    public static final String ITEM_ORDER_BY_CLAUSE =" ORDER BY item.item_code LIMIT ";

    public static final String ITEM_BASE_QUERY_LEFT_JOIN_QUERY ="LEFT JOIN item_location_mapper ON item.id = item_location_mapper.item_id " +
            " LEFT JOIN location ON location.id=item_location_mapper.location_id ";
    public static final Integer STATUS_200=200;
    public static final Integer STATUS_500=500;
    public static final Integer STATUS_404=404;
    protected static final String DOCKS_ID = "Dock ID";
    protected static final String DOCKS_NAME = "Dock Name";
    protected static final String ATTRIBUTES = "Attribute";
    protected static final String STORES_NAME = "Store Name";
    public static final String STORE_ERP_CODE="Store ERP Code";
    protected static final String DOCKS_SUPERVISOR = "Dock Supervisor (username)";
    public static final String DOCKS = "dock";
    public static final String BOM = "bom";
    public static final String DEVICEMASTER = "deviceMaster";
    public static final String UPLOAD_DOCK_UPLOAD_METHOD_STARTED="upload dock Method Execution Start";
    public static final Integer DOCK_COLUMN_HEADER_ROW_INDEX= 0;
//    public static final String DOCK_ID_IS_ALREADY_PRESENT = "dockId is already present in dock";

    public static final String STORE_ID_IS_ALREADY_PRESENT = "Store Id is already present in Database";

    public static final String STORE_ID_IS_NOT_PRESENT = "Store Id is Not present ";
    public static final String DOCK_ID = "Dock";
    public static final String DOCK_SUPERVISOR_ID = "dockSupervisor";
    public static final String DOCK_SUPERVISOR_NOT_FOUND = "dock supervisor not found";
    public static final String DOCK_NAME_DUPLICATE = "THIS DOCK NAME IS ALREADY USED PLEASE ENTER ANOTHER NAME";
    public static final String ATTRIBUTE_IS_MANDATORY_FIELD_SHOULD_NOT_BE_NULL = " ATTRIBUTE IS MANDATORY FIELD SHOULD NOT BE NULL";
    public static final String DOCK_NAME_IS_MANDATORY_FIELD_SHOULD_NOT_BE_NULL = " DOCK NAME IS MANDATORY FIELD SHOULD NOT BE NULL";
    public static final String UPLOAD_DOCK_METHOD_EXECUTED="upload dock Method Executed";
    public static final String DOCK_DATA_UPLOAD_FAILED="dock data upload failed";
    public static final String ATTRIBUTE_NOT_VALID = "Attribute should only contain letters, spaces, and special characters";


    //reason
    public static final String REASON_CATEGORY ="Reason Category";
    public static final String DEVICE_NAME ="Device Name";
    public static final String DEVICE_IP ="Device Ip";
    public static final String DEVICE_BRAND ="Device Brand";
    public static final String DEVICE_PORT ="Device Port";
    public static final String DEVICE_ROLE ="Device Role";
    public static final String SUB_MODULE_CODE ="Sub Module Code";
    public static final String REJECTION_REASON ="Rejection Reason";
    public static final String REASON_ID ="Reason ID";
    public static final String ITEM_NAMES ="Item Name";
    public static final String ITEM_CODE_AND_REASON_ID = "item code and reason id";
    public static final String ITEM_CODES ="Item Code";
    public static final String REJECTION_REASON_NOT_VALID = "Invalid rejection reason,special char not allow";
    public static final String REJECTION_CATEGORY_NOT_VALID = "Invalid rejection category,special char not allow";
    public static final String REASONID_FOR_ITEMCODE_IS_ALREADY_PRESENT = "reasonid for itemcode is already present";

    public static final String ITEM_CODE_NOT_FOUND = "item code Not Found";
    public static final String ITEM_NAME_AND_ITEM_CODE_SHOULD_BE_CORRECT = "item name and item code mismatch,it should be correct";
    public static final String ITEM_NAME_AND_ITEM_ID_SHOULD_BE_CORRECT = " This item name and item id mismatch,it should be correct";
    public static final String DOCK_ID_AND_DOCK_NAME_SHOULD_BE_CORRECT = "dock id and dock name mismatch,it should be correct";

    public static final String ITEM_NAME_NOT_FOUND = "Item Name Not Found";
    public static final String SUB_MODULE_CODE_NOT_FOUND = "Sub Module Code Not Found";
    public static final AtomicInteger dockCounter = new AtomicInteger(1);
    public static final AtomicInteger itemCounter = new AtomicInteger(1);
    public static final String PREFIX = "RM";
    public static final String DELIMITER = "D";
    public static final String ITM = "ITM";
    public static final int MAX_DOCKS_PER_RM = 9;
    public static final int MAX_ITEMS_PER_RM = 9;
    public static final AtomicInteger supplierCounter = new AtomicInteger(1);
    public static final String ORG_PREFIX = "ORG";

    public static final int MAX_SUPPLIERS_PER_ORG = 99;


    public static final String DEVICE_NAME_NOT_VALID = "Invalid Device Name";
    public static final String INVALID_DEVICE_ROLE_FORMAT = "Invalid Device Role";
    public static final String DEVICE_IP_NOT_VALID = "Invalid Device Ip";
    public static final String DEVICE_PORT_NOT_VALID = "Invalid Device Port";
    public static final String DEVICE_BRAND_NOT_VALID = "Invalid Device Brand";
    //BOM
    public static final String STAGE="Stage";
    public static final String LEVELS="Level";
    public static final String LINE_NUMBERS="Line Number";
    public static final String BOM_ITEM_CODES="Item Code";
    public static final String BOM_ITEM_NAME="Item Name";
    public static final String QUANTITY="Quantity";
    public static final String CLASSABC="Class";
    public static final String UNIT_OF_MEASURE="Unit of Measure";
    public static final String BOM_ISSUE_TYPE="Issue Type";
    public static final String DEPENDENCY="Dependency";
    public static final String REFERENCE_DESIGNATORS="Reference Designators";
    public static final String BOM_NOTES="BOM Notes";
    public static final String PRODUCT="Product";
    public static final String MODEL="Model";
    public static final String VARIANT="Variant";
    public static final String COLOUR="Colour";
    public static final String BOM_IDS="BOM ERP Code";
    public static final String DATE="Date";
    public static final String VERSION  ="Version";
    public static final String ASSEMBLY_LINE="Assembly Line";
    public static final String LIFECYCLE_PHASE="Lifecycle Phase";


    //Added Constants For PPE Plan Upload Excel
    public static final String PPE_PLAN_ID="Plan/Order ID";
    public static final String PPE_ID="PpeId";
    public static final String ERP_ID ="ERP ID";
    public static final String BOM_ID="BOM Code";
    public static final String PRODUCT_NAME="Product Name";
    public static final String BRAND="Brand";
//    public static final String MODEL="Model";
//    public static final String VARIANT="Variant";
    public static final String COLOR="Color";
    public static final String UOM1="UoM";
    public static final String PLAN_QUANTITY="Plan Quantity";
    public static final String PRODUCTION_SHOP="Production Shop";
    public static final String SHOP_ID="Shop Code";
    public static final String LINE="Line";
    public static final String LINE_ID="Line Code";
    public static final String START_DATE="Start Date";
    public static final String START_TIME="Start Time";
    public static final String END_DATE="End Date";
    public static final String END_TIME="End Time";
    public static final String ITEM_CODE_PPE="Item Code";
    public static final String ITEM_NAME_PPE="Item Name";
    public static final String ITEM_TYPE="Item Type";
    public static final String ITEM_CLASS_PPE="Item Class";
    public static final String ATTRIBUTE_PPE="Attribute";
    public static final String UOM2="UOM";
    public static final String REQUIRED_QUANTITY="Required Quantity";
    public static final String STORE_PPE="Store";
    public static final String PPE_HEAD="PPE";
    public static final String PPE_HEAD_FILE="PPE.xlsx";
      public static final String ASSET_ID_IS_ALREADY_PRESENT = " THIS ASSET ID IS ALREADY PRESENT";





    // Regex Constants
    public static final String ID_REGEX = "^(?=.*[a-zA-Z])(?=.*[0-9])[a-zA-Z0-9]+$"; //allows at least one digit and at least one letter but disallows any special characters
    public static final String DELIVERY_TYPE_REGEX = "^(ASN|PO)$"; //allowsn only ASN OR PO

//    public static final String ID_REGEX = "^[a-zA-Z0-9]+$"; //allows only characters (letters) and integers (digits) but disallows any special characters
    public static final String INTEGER_REGEX =  "^[0-9]+(\\.[0-9]+)?$"; //allows only integers (digits) OR allow floats
    public static final String NAME_REGEX = "^[A-Za-z.\\s]{1,50}$";// Example: Alphabets and spaces with fullstops up to 50 characters
    public static final String NAME_FIRST_LETTER_CAPITAL_REGEX = "^(?! )[A-Za-z]+(?:[ .'-][A-Za-z]+)*(?<! )$";// Example: Alphabets and spaces and first char of each String must capital
   // public static final String STRING_REGEX = "^[a -zA-Z\\s.]*$";//strings that consist only of letters (both uppercase and lowercase) and whitespace and fullstops characters.
    public static final String NOT_ALLOW_SPECIAL_CHAR_REGEX = "^[a-zA-Z\\d ]+$";// not allow special characters
    public static final String POST_CODE_REGEX = "^\\d{6}$";

    //  public static final String NAME_REGEX = "^[A-Za-z\\s]{1,50}$"; // Example: Alphabets and spaces without fullstops up to 50 characters
     public static final String ADDRESS_REGEX = "^[a-zA-Z\\s]*$";//strings that consist only of letters (both uppercase and lowercase) and whitespace characters.
     public static final String DATE_FORMAT_PATTERN = "dd MMM yyyy";
    public static final String DATE_REGEX_PATTERN = "^\\d{2} \\w{3} \\d{4}$"; // e.g., "08 Jun 2024"
    public static final String NAME_WITH_DIGIT_REGEX = "^[A-Za-z0-9.\\s]{1,50}$";//Alphabets and spaces with fullstops and digits up to 50 characters

    public static final String SUPPLIER_GST_REGEX = "^[0-9]{2}[A-Z]{5}[0-9]{4}[A-Z]{1}[A-Z0-9]{1}[Z]{1}[A-Z0-9]{1}$"; // Example: GST number format
    public static final String SUPPLIER_PAN_REGEX = "^[A-Z]{5}[0-9]{4}[A-Z]{1}$"; // Example: PAN number format
    public static final String PHONE_REGEX = "^\\(?\\d{3}\\)?[-.\\s]?\\d{3}[-.\\s]?\\d{4}$";// Example: phone no number format

    public static final String SUPPLIER_TAN_REGEX = "^[A-Z]{4}[0-9]{5}[A-Z]{1}$"; // Example: TAN number format
    public static final String IFSC_CODE_REGEX = "^[A-Za-z]{4}0[A-Za-z0-9]{6}$"; // Example: IFSC code format
    public static final String MICR_CODE_REGEX = "^\\d{9}$"; // ALLOW EXACT 9 DIGITS
    public static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";

    public static final String CLASS_REGEX = "^[ABC]$";




    // Error Messages
    public static final String INVALID_PURCHASE_ORDER_NUMBER_FORMAT = "Invalid purchase order number format";
    public static final String INVALID_DELIVER_TYPE_FORMAT = "Invalid delivery type format,it should be ASN or PO";
    public static final String INVALID_SUPPLIER_ID_FORMAT = "Invalid supplier Id format";
    public static final String INVALID_SUB_LOCALITY_FORMAT = "Invalid sub locality format,should be only of letters and whitespace ";
    public static final String INVALID_LANDMARK_ERROR = "Invalid landmark format,should be only of letters and whitespace ";
    public static final String INVALID_DOCK_NAME_FORMAT = "Invalid dock name format,should be only of letters and whitespace ";
    public static final String INVALID_STREET_FORMAT = "Invalid street format,should be only of letters and whitespace ";
    public static final String INVALID_BUILDING_FORMAT ="Invalid building name format,should be only of letters and whitespace ";
    public static final String INVALID_LOCALITY_FORMAT = "Invalid locality format,should be only of letters and whitespace ";
    public static final String INVALID_PAYMENT_METHOD_FORMAT = "Invalid payment method format,should be only of letters and whitespace ";
    public static final String INVALID_PAYMENT_TERMS_FORMAT = "Invalid payment term format,it should not be contain special character";
    public static final String INVALID_FULL_BRANCH_ADDRESS_FORMAT = "Invalid full branch address format,should be only of letters and whitespace ";
    public static final String INVALID_ITEM_NAME_FORMAT = "Invalid Item Name format,it should be Uppercase first letter of every word";
    public static final String INVALID_ATTRIBUTE_FORMAT = "Invalid attribute format,it should be Uppercase first letter of every word";
    public static final String INVALID_ITEM_ID_FORMAT = "Invalid Item Id format";
    public static final String INVALID_ITEM_CODE_FORMAT = "Invalid Item Code format";
    public static final String INVALID_DOCK_ID_FORMAT = "Invalid dock id format";
    public static final String INVALID_CODE_FORMAT = "Invalid Code format";
    public static final String INVALID_ERP_ITEM_ID_FORMAT = "Invalid ERP Item ID format";
    public static final String INVALID_ITEM_DESCRIPTION_FORMAT = "Invalid item description format,special char not allow";
    public static final String INVALID_DIMENSION_UOM_FORMAT = "Invalid dimension uom format,special char not allow";
    public static final String INVALID_TYPES_FORMAT = "Invalid types format,special char not allow";
    public static final String INVALID_ITEM_GROUP_FORMAT = "Invalid item group format,special char not allow";
    public static final String INVALID_ITEM_CATEGORY_FORMAT = "Invalid item category format,special char not allow";
    public static final String INVALID_ITEM_SUB_CATEGORY_FORMAT = "Invalid item sub category format,special char not allow";
    public static final String INVALID_TYPE_DIRECT_INDIRECT_FORMAT = "Invalid type format,special char not allow";
    public static final String YES_NO = " Either enter Yes/No ";
    public static final String INVALID_CURRENCY_FORMAT = "Invalid currency format,special char not allow";
    public static final String INVALID_TYPE_SERIAL_BATCH_FORMAT = "Invalid type serial batch format,special char not allow";
    public static final String INVALID_ISSUE_TYPE_FORMAT = "Invalid issue type format,special char not allow";
    public static final String INVALID_SOURCE_FORMAT = "Invalid source format,special char not allow";
    public static final String INVALID_UOM_FORMAT = "Invalid uom format,special char not allow";

    public static final String INVALID_ERP_SUPPLIER_ID_FORMAT = "Invalid ERP Supplier ID format";
    public static final String INVALID_SUPPLIER_NAME_FORMAT = "Invalid Supplier Name format";
    public static final String INVALID_SUPPLIER_GST_FORMAT = "Invalid Supplier GST Registration Number format";
    public static final String INVALID_SUPPLIER_PAN_FORMAT = "Invalid Supplier PAN Card Number format";
    public static final String INVALID_POST_CODE_FORMAT = "Invalid Post Code format";
    public static final String INVALID_ADDRESS_1_FORMAT = "Invalid address1 format,it should not be contain special character";
    public static final String INVALID_SUPPLIER_CATEGORY_FORMAT = "Invalid supplier category format,it should not be contain special character";
    public static final String INVALID_PRIMARY_BANKER_FORMAT = "Invalid Supplier Primary Banker format,it should not be contain special character";
    public static final String INVALID_SUPPLIER_TYPE_FORMAT = "Invalid supplier group format,it should not be contain special character";
    public static final String INVALID_ADDRESS_2_FORMAT = "Invalid address2 format,it should not be contain special character";
    public static final String INVALID_CITY_FORMAT = "Invalid City format,it should be Uppercase first letter of every word";
    public static final String INVALID_TOWN_NAME_FORMAT = "Invalid Town format,it should be Uppercase first letter of every word";
    public static final String INVALID_COUNTRY_FORMAT = "Invalid Country name format,it should be Uppercase first letter of every word";
    public static final String INVALID_VILLAGE_NAME_FORMAT = "Invalid Village format,it should be Uppercase first letter of every word";
    public static final String INVALID_STATE_FORMAT = "Invalid State format,it should be Uppercase first letter of every word";
    public static final String INVALID_NAME_FORMAT_ERROR_MESSAGE = "Invalid contact person name format,it should be Uppercase first letter of every word";
    public static final String INVALID_DESIGNATION_FORMAT_ERROR_MESSAGE = "Invalid designation format,it should be Uppercase first letter of every word";
    public static final String INVALID_DEPARTMENT_FORMAT_ERROR_MESSAGE = "Invalid department format,it should be Uppercase first letter of every word";
    public static final String INVALID_DISTRICT_FORMAT = "Invalid District format,it should be Uppercase first letter of every word";
    public static final String INVALID_TALUKA_FORMAT = "Invalid Taluka format,it should be Uppercase first letter of every word";
    public static final String INVALID_SUPPLIER_TAN_FORMAT = "Invalid Supplier TAN Number format";
    public static final String INVALID_IFSC_CODE_FORMAT = "Invalid IFSC Code format";
    public static final String INVALID_MICR_CODE_FORMAT = "Invalid MICR Code format,it should be 9 digits";
    public static final String INVALID_CLASS_ABC_FORMAT = "Invalid class(A/B/C) format";
    public static final String INVALID_STORE_ID_FORMAT = "Invalid store id format";
    public static final String INVALID_STORE_NAME_FORMAT = "Invalid store name format,not allow special char";
    public static final String INVALID_EQUP_TROLLEY_TYPE_FORMAT = "Invalid equp trolley type format,it should be Uppercase first letter of every word";
    public static final String INVALID_EQUP_NAME_FORMAT = "Invalid Equipment Name  format,it should be Uppercase first letter of every word";
    public static final String INVALID_EQUP_ASSET_ID_FORMAT = "Invalid equipment asset id format";
    public static final String STORE_ID_AND_STORE_NAME_SHOULD_BE_CORRECT = "store id and store name mismatch,it should be correct";
    public static final String SUPPLIER_ID_AND_SUPPLIER_NAME_SHOULD_BE_CORRECT = "supplier id and supplier name mismatch,it should be correct";
    public static final String INVALID_UNIT_PRICE_FORMAT = "Invalid unit price format";
    public static final String INVALID_SUB_TOTAL_FORMAT = "Invalid sub total format";
    public static final String SUBTOTAL_MUST_EQUAL_UNIT_PRICE_MULTIPLIED_BY_PURCHASE_ORDER_QUANTITY = "Subtotal must equal Unit Price multiplied by Purchase Order Quantity";
    public static final String STATE_GST_AMOUNT_MUST_EQUAL_STATE_GST_PERCENT_OF_SUB_TOTAL = "State GST Amount must equal State GST Percent of Sub Total";
    public static final String INTER_STATE_GST_AMOUNT_MUST_EQUAL_INTER_STATE_GST_PERCENT_OF_SUB_TOTAL = "Inter state gst amount must equal inter state gst percent of sub total";
    public static final String INCORRECT_TOTAL_AMOUNT = "Total Amount must equal sum of State GST Amount, Central GST Amount, and Sub Total, or sum of Sub Total and Inter State GST Amount";
    public static final String CENTRAL_GST_AMOUNT_MUST_EQUAL_CENTRAL_GST_PERCENT_OF_SUB_TOTAL = "Central Gst Amount must equal Central Gst Percent of Sub Total";
    public static final String INVALID_PURCHASE_ORDER_QUANTITY_FORMAT = "Invalid purchase order quantity format";
    public static final String INVALID_TOTAL_AMOUNT_FORMAT = "Invalid total amount format";
    public static final String INVALID_INTER_STATE_GST_PERCENT_FORMAT = "Invalid inter state gst percent format";
    public static final String INVALID_INTER_STATE_GST_AMOUNT_FORMAT = "Invalid inter state gst amount format";
    public static final String INVALID_STATE_GST_AMOUNT_FORMAT = "Invalid state gst amount format";
    public static final String INVALID_STATE_GST_PERCENT_FORMAT = "Invalid state gst percent format";

    public static final String INVALID__PHONE_FORMAT_ERROR_MESSAGE = "Invalid Phone Number format";
    public static final String PAN_MANDATORY_ERROR_MESSAGE = "PAN is a mandatory field.";
    public static final String GST_MANDATORY_ERROR_MESSAGE = "GST is a mandatory field.";
    public static final String ERP_ID_MANDATORY_ERROR_MESSAGE = "ERP Supplier ID is a mandatory field.";
    public static final String SUPPLIER_NAME_MANDATORY_ERROR_MESSAGE = "Supplier Name is a mandatory field.";
    public static final String TAN_MANDATORY_ERROR_MESSAGE = "TAN is a mandatory field.";
    public static final String DUPLICATE_SUPPLIER_NAME_FOUND = "Duplicate Supplier Name found in the file.";
    public static final String INVALID_EMAIL_FORMAT_ERROR_MESSAGE = "Invalid email format.";
    public static final String DUPLICATE_PAN_ERROR_MESSAGE = "Duplicate PAN found in the file.";
    public static final String DUPLICATE_EMAIL_ERROR_MESSAGE = "Duplicate Email found in the file.";
    public static final String DUPLICATE_TAN_ERROR_MESSAGE = "Duplicate TAN found in the file.";
    public static final String DUPLICATE_ERP_ID_ERROR_MESSAGE = "Duplicate ERP Supplier ID found in the file.";
    public static final String ERP_ID_DUPLICATE_ERROR_MESSAGE = " THIS SUPPLIER ERP ID ALREADY PRESENT ";
    public static final String DUPLICATE_PURCHASE_ORDER_NUMBER_FOUND = "Duplicate Purchase Order Number found";
    public static final String DUPLICATE_PHONE_ERROR_MESSAGE = "Duplicate phone no found in the file.";

    public static final String DUPLICATE_DEVICE_NAME = "Duplicate Device Name found in the file.";
    public static final String DUPLICATE_DEVICE_IP = "Duplicate Device IP found in the file.";
    public static final String DUPLICATE_ERP_ITEM_ID = "Duplicate ERP Item ID found.";
    public static final String DUPLICATE_ITEM_CODE = "Duplicate Item Code found.";
    public static final String DUPLICATE_EQUP_ASSET_ID = "Duplicate equipment asset id found.";
    public static final String DUPLICATE_ITEM_NAME = "Duplicate Item Name found.";
    public static final String DUPLICATE_ITEM_ID_FOR_SAME_PURCHASE_ORDER_NUMBER = "Duplicate item ID for the same purchase order number";


        public static final String TYPE_SERIAL_BATCH="type Serial Batch";

        // Error messages for mandatory fields
        public static final String DATE_OF_REGISTRATION_MANDATORY = "Date of Registration is a mandatory field and must contain a valid date.";
        public static final String CATEGORY_MANDATORY = "Category is a mandatory field.";
        public static final String PAYMENT_TERMS_MANDATORY = "Payment Terms is a mandatory field.";
        public static final String PAYMENT_METHOD_MANDATORY = "Payment Method is a mandatory field.";
        public static final String OFFICE_PRIMARY_PHONE_MANDATORY = "office Primary Phone is a mandatory field.";
        public static final String CREDIT_LIMIT_RS_MANDATORY = "Credit Limit (RS) is a mandatory field.";
        public static final String CREDIT_LIMIT_DAYS_MANDATORY = "Credit Limit (Days) is a mandatory field.";
        public static final String PRIMARY_BANKER_MANDATORY = "Supplier Primary Banker is a mandatory field.";
        public static final String FULL_BRANCH_ADDRESS_MANDATORY = "Full Branch Address is a mandatory field.";
        public static final String MICR_CODE_MANDATORY = "MICR Code is a mandatory field.";
        public static final String IFSC_CODE_MANDATORY = "IFSC Code is a mandatory field.";
        public static final String COUNTRY_MANDATORY = "Country is a mandatory field.";
        public static final String COUNTRY_CODE_MANDATORY = "Country Code is a mandatory field.";
        public static final String POST_CODE_MANDATORY = "Post Code is a mandatory field.";
        public static final String STATE_MANDATORY = "State is a mandatory field.";
        public static final String DISTRICT_MANDATORY = "District is a mandatory field.";
        public static final String TALUKA_MANDATORY = "Taluka is a mandatory field.";
        public static final String CITY_MANDATORY = "City is a mandatory field.";
        public static final String TOWN_MANDATORY = "Town is a mandatory field.";
        public static final String VILLAGE_MANDATORY = "Village is a mandatory field.";
        public static final String EMAIL_MANDATORY = "email is a mandatory field.";
        public static final String ADDRESS_1_MANDATORY = "address 1 is a mandatory field.";
     public static final String AREA_CODE_MANDATORY = "Area Code is a mandatory field.";
     public static final String ITEM_NAME_MANDATORY = "Item Name is a mandatory field.";
     public static final String ITEM_ID_MANDATORY = "Item Id is a mandatory field.";
     public static final String PRIMARY_PHONE_MANDATORY = "Primary Phone is a mandatory field.";
     public static final String SUPPLIER_TYPE_MANDATORY = "supplier Type is a mandatory field.";

    public static final String PAYMENT_TERMS = "payment terms";
    public static final String PAYMENT_METHOD = "payment method";
    public static final String CREDIT_LIMIT_RS = "credit limit rs";
    public static final String PRIMARY_BANKER = "primary banker";
    public static final String TYPE_DIRECT_INDIRECT = "Type Direct Indirect";
    public static final String CREDIT_LIMIT_DAYS = "credit limit days";






    public static final String ITEM_CODE_MANDATORY = "Item Code is a mandatory field.";
    public static final String ERP_ITEM_ID_MANDATORY = "ERP Item ID is a mandatory field.";
    public static final String ITEM_DESCRIPTION_MANDATORY = "Item Description is a mandatory field.";
    public static final String ITEM_GROUP_MANDATORY = "Item Group is a mandatory field.";
    public static final String ITEM_CATEGORY_MANDATORY = "Item Category is a mandatory field.";
    public static final String ITEM_SUB_CATEGORY_MANDATORY = "Item Sub-Category is a mandatory field.";
    public static final String TYPE_DIRECT_INDIRECT_MANDATORY = "Type (Direct/Indirect) is a mandatory field.";
    public static final String ISSUE_TYPE_MANDATORY = "Issue Type is a mandatory field.";
    public static final String CLASS_ABC_MANDATORY = "Class (A/B/C) is a mandatory field.";
    public static final String ATTRIBUTE_MANDATORY = "Attribute is a mandatory field.";
    public static final String SOURCE_MANDATORY = "Source is a mandatory field.";
    public static final String UOM_MANDATORY = "UOM is a mandatory field.";
    public static final String CURRENCY_MANDATORY = "Currency is a mandatory field.";
    public static final String CODE_MANDATORY = "Code is a mandatory field.";
    public static final String TYPES_MANDATORY = "Types is a mandatory field.";
    public static final String DIMENSION_UOM_MANDATORY = "Dimension UOM is a mandatory field.";
    public static final String DOCK_ID_MANDATORY = "Dock ID is a mandatory field.";
    public static final String DOCK_NAME_MANDATORY = "Dock Name is a mandatory field.";
    public static final String SAFETY_LEVEL_MANDATORY = "Safety Level is a mandatory field.";
    public static final String CRITICAL_LEVEL_MANDATORY = "Critical Level is a mandatory field.";
    public static final String REORDER_LEVEL_MANDATORY = "Reorder Level is a mandatory field.";
    public static final String OPTIMUM_LEVEL_MANDATORY = "Optimum Level is a mandatory field.";
    public static final String ITEM_UNIT_WEIGHT_MANDATORY = "Item Unit Weight is a mandatory field.";
    public static final String ITEM_UNIT_RATE_MANDATORY = "Item Unit Rate is a mandatory field.";
    public static final String TYPE_SERIAL_BATCH_MANDATORY = "Type (Serial/Batch) is a mandatory field.";
    public static final String WEIGHT_MANDATORY = "Weight is a mandatory field.";
    public static final String HEIGHT_MANDATORY = "Height is a mandatory field.";
    public static final String LENGTH_MANDATORY = "Length is a mandatory field.";
    public static final String ITEM_QTY_MANDATORY = "Item Quantity is a mandatory field.";
    public static final String CIRCUMFERENCE_MANDATORY = "Circumference is a mandatory field.";
    public static final String WIDTH_MANDATORY = "Width is a mandatory field.";
    public static final String MINIMUM_ORDER_QTY_MANDATORY = "Minimum Order Qty is a mandatory field.";
    public static final String REASON_CATEGORY_MANDATORY = "Reason category is a mandatory field.";
    public static final String REJECTION_REASON_MANDATORY = "Rejection reason is a mandatory field.";
    public static final String LINE_NUMBER_MANDATORY = "Line number is a mandatory field.";
    public static final String EQUP_ASSET_ID_IS_MANDATORY = "equipment asset id is mandatory";
    public static final String REASON_CATEGORY_NOT_FOUND = "Reason Category Not Found";




    public static final String UNIT_PRICE_MANDATORY = "Unit Price is a mandatory field.";
    public static final String PURCHASE_ORDER_QUANTITY_MANDATORY = "Purchase Order Quantity is a mandatory field.";
    public static final String SUB_TOTAL_MANDATORY = "Sub Total is a mandatory field.";
    public static final String TOTAL_AMOUNT_MANDATORY = "Total Amount is a mandatory field.";
    public static final String PURCHASE_ORDER_NUMBER_MANDATORY = "Purchase Order Number is a mandatory field.";
    public static final String PURCHASE_ORDER_DATE_MANDATORY = "Purchase Order Date is a mandatory field.";
    public static final String PURCHASE_ORDER_DATE_CANNOT_BE_IN_THE_PAST = "Purchase Order Date cannot be in the past";
    public static final String SUPPLIER_ID_MANDATORY = "Supplier Id is a mandatory field.";
    public static final String SUPPLIER_NAME_MANDATORY = "Supplier Name is a mandatory field.";
    public static final String DELIVERY_TYPE_MANDATORY = "Delivery Type is a mandatory field.";
    public static final String DELIVERY_DATE_MANDATORY = "Delivery Date is a mandatory field.";
    public static final String DEVICE_ROLE_MANDATORY = "Device Role is a mandatory field.";
    public static final String DEVICE_IP_MANDATORY = "Device Ip is a mandatory field.";
    public static final String DEVICE_NAME_MANDATORY = "Device Name is a mandatory field.";
    public static final String DEVICE_PORT_MANDATORY = "Device Port is a mandatory field.";
    public static final String DEVICE_BRAND_MANDATORY = "Device Brand is a mandatory field.";

    public static final String EXCEL_FILE_CONTAINS_HEADER_AND_NO_DATA ="The Excel file is empty, Please add data.";

    public static final String ASSET_ID_IS_MANDATORY = "Asset ID is mandatory";
    public static final String EQUIPMENT_TYPE_IS_MANDATORY = "Equipment Type is mandatory";
    public static final String EQUIPMENT_NAME_IS_MANDATORY = "Equipment Name is mandatory";
    public static final String STORE_ID_IS_MANDATORY = "Store ID is mandatory";
    public static final String STORE_NAME_IS_MANDATORY = "Store Name is mandatory";
    public static final String CREDIT_LIMIT_DAYS_MUST_BE_LESS_THAN_365 = "Credit limit days must be less than 365";
    public static final String DATE_OF_REGISTRATION_CANNOT_BE_IN_THE_FUTURE = "Date of registration cannot be in the future";


    public final static String QUEUED="QUEUED";
    public final static String  IN_PROGRESS="IN_PROGRESS";
    public final static String  SUCCESS="SUCCESS";
    protected static final String DEVICE_ROLE_AND_SUB_MODULE_CODE_SHOULD_BE_CORRECT = "DEVICE ROLE AND SUB MODULE CODE SHOULD BE CORRECT";
    protected static final String DOCKS_SUPERVISOR_NAME = "Dock Supervisor Name";
    public static final String P_LEAD_TIME = "Lead Time (Days)";
    public static final String P_LEAD_TIME_HRS = "Lead Time (Hrs)";
    public static final String EITHER_ENTER_DAYS_AND_HRS = "Either enter days and hrs";
    public static final String PLEASE_ENTER_DAYS_AND_HRS_AT_TIME = "Please enter days and hrs";
    public static final String EQUIPMENT_NAME = "Equipment Name";
    protected static final String CLASS_ABC_INVALID = "Please enter a value between A, B or C";
    protected static final String DIRECT_INDIRECT_INVALID = "Please enter a value between Direct or Indirect";
    protected static final String SERIAL_BATCH_INVALID = "Please enter a value between Serial, Batch or None";
    protected static final String QC_REQUIRE_MANDATORY = "Qc Required is Mandatory ";
    protected static final String INSPECTION_REQUIRE_MANDATORY = "Inspection Required is Mandatory ";
    protected static final String USER_LIST_DATA_UPLOAD_FAILED = "Failed to upload user list data";
}
