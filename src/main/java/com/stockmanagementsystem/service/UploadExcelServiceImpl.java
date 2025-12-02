package com.stockmanagementsystem.service;

import com.stockmanagementsystem.entity.*;
import com.stockmanagementsystem.exception.ExceptionLogger;
import com.stockmanagementsystem.exception.ValidationFailureException;
import com.stockmanagementsystem.repository.*;
import com.stockmanagementsystem.request.ItemSupplierMapperRequest;
import com.stockmanagementsystem.response.*;
import com.stockmanagementsystem.utils.ResponseKeyConstant;
import com.stockmanagementsystem.utils.ServiceConstants;
import com.stockmanagementsystem.validation.Validations;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.multipart.MultipartFile;

import javax.swing.text.html.Option;
import java.io.IOException;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@Slf4j
public class UploadExcelServiceImpl extends Validations implements UploadExcelService {


    @Autowired
    private LocationRepository locationRepository;
    @Autowired
    private AssemblyLineRepository assemblyLineRepository;

    @Autowired
    private StageRepository stageRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private ExceptionLogger exceptionLogger;

    @Autowired
    private ContainerRepository containerRepository;
    @Autowired
    StockBalanceRepository stockBalanceRepository;

    @Autowired
    SupplierService supplierService;

    @Autowired
    private StoreRepository storeRepository;
    @Autowired
    private LocationIdGeneratorRepository locationIdGeneratorRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    StoreDockMapperRepository dockMapperRepository;

    @Autowired
    private ReasonRepository reasonRepository;

    @Autowired
    private PurchaseOrderHeadRepository purchaseOrderHeadRepository;

    @Autowired
    private PurchaseOrderLineRepository purchaseOrderLineRepository;
    @Autowired
    private PlatformTransactionManager transactionManager;

    @Autowired
    private ItemLocationMapperRepository itemLocationMapperRepository;

    @Autowired
    AcceptedRejectedStagingAreaRepository acceptedRejectedStagingAreaRepository;

    @Autowired
    private LoginUser loginUser;
    @Autowired
    private ReasonService reasonService;
    @Autowired
    StagingAreaRepository stagingAreaRepository;
    @Autowired
    DocksRepository docksRepository;
    @Autowired
    DocksService docksService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    UserLicenseKeyRepository userLicenseKeyRepository;

    @Autowired
    AreaRepository areaRepository;

    @Autowired
    EquipmentService equipmentService;

    @Autowired
    ZoneRepository zoneRepository;

    @Autowired
    EquipmentRepository equipmentRepository;

    @Autowired
    Validations validations;

    @Autowired
    PPEHeadRepository ppeHeadRepository;

    @Autowired
    PPELineRepository ppeLineRepository;

    @Autowired
    BomLineRepository bomLineRepository;
    @Autowired
    BomHeadRepository bomHeadRepository;

    @Autowired
    StoreKeeperMapperRepository storeKeeperMapperRepository;

    @Autowired
    AreaServices areaServices;

    @Autowired
    StoreNameRepository storeNameRepository;

    @Autowired
    private PPEStatusRepository ppeStatusRepository;

    @Autowired
    ItemService itemService;
    @Autowired
    private DeviceMasterRepository deviceMasterRepository;

    @Autowired
    private SubModuleRepository subModuleRepository;

    @Autowired
    SupplierItemMapperRepository supplierItemMapperRepository;


    @Value("${baseFilePath}")
    private String baseFilePath;

    @Autowired
    private ReasonCategoryMasterRepository masterRepository;

    @Autowired
    private AcceptedRejectedContainerBarcodeRepository acceptedRejectedContainerBarcodeRepository;

    @Autowired
    private SerialBatchNumberRepository serialBatchNumberRepository;

    @Autowired
    private AsnLineRepository asnLineRepository;

    @Autowired
    private CommonMasterRepository commonMasterRepository;

    @Autowired
    private StockMovementRepository stockMovementRepository;

    @Override
    public ResponseEntity<BaseResponse> uploadItemDetail(MultipartFile file, String type) throws IOException {
        long startTime = System.currentTimeMillis();
        log.info("LogId:{} - UploadExcelServiceImpl - uploadItemDetail - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), ServiceConstants.SPACE + ServiceConstants.UPLOAD_ITEM_DETAIL_METHOD_STARTED);
        try {
            // Read the Excel file and perform validation
            Workbook workbook = WorkbookFactory.create(file.getInputStream());
            Sheet sheet = workbook.getSheetAt(ServiceConstants.SHEET_INDEX); // Assuming the data is in the first sheet

            List<Item> items = new ArrayList<>();
            List<Container> containerList = new ArrayList<>();
            List<StockBalance> stockBalances = new ArrayList<>();
            List<ValidationResultResponse> resultResponses = new ArrayList<>();
            List<String> headerNames = new ArrayList<>();
            Integer count = 0;
            boolean hasDataRows = false; // Flag to track if there are data rows

            Set<String> itemCodeSet = new HashSet<>();
            Set<String> erpItemIdSet = new HashSet<>();

            List<String> expectedColumns = new ArrayList<>(Arrays.asList(
                    ITEM_CODES, NAME, ITEM_DESCRIPTION,
                    ITEM_GROUP,
                    ITEM_CATEGORY,
                    ITEM_SUB_CATEGORY,
                    TYPE, TYPE_SERIAL, QC_REQUIRED,ISSUE_TYPE, CLASS, ATTRIBUTE, SOURCE, UOM, ITEM_UNIT_WEIGHT,PHYSICAL_FORM, CONTAINER_CAPACITY_UOM,CONTAINER_CAPACITY, CODE, TYPES,
                    DIMENSION_UOM, ITEM_WIDTH, ITEM_HEIGHT, ITEM_LENGTH, CIRCUMFERENCE, WEIGHT, MINIMUM_ORDER_QTY,
                    OPTIMUM_LEVEL, REORDER_LEVEL, SAFETY_LEVEL, CRITICAL_LEVEL, DOCK, DOCKS_NAME
            ));
            List<ExcellHeaderValidatorResponse> excellHeaderValidatorResponse = validateExcelHeader(sheet, expectedColumns);
            if (!excellHeaderValidatorResponse.get(0).getIsValid()) {
                return ResponseEntity.ok(new BaseResponse<>(ServiceConstants.INTERNAL_SERVER_ERROR, excellHeaderValidatorResponse.get(0).getErrorMessage(), excellHeaderValidatorResponse, ServiceConstants.ERROR_CODE, loginUser.getLogId()));
            }
            // Iterate through the first row to get the header names
            Row headerRow = sheet.getRow(1);
            for (int cellIndex = 0; cellIndex < headerRow.getLastCellNum(); cellIndex++) {
                Cell headerCell = headerRow.getCell(cellIndex, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                if (headerCell != null) {
                    String headerName = headerCell.getStringCellValue();
                    headerNames.add(headerName);
                }
            }
            int itemNO = 1;
            for (Row data : sheet) {
                int emptyCellCount = 0;
                int lastCellNum = data.getLastCellNum();
                if (lastCellNum != -1) {
                    for (int i = 0; i < data.getLastCellNum(); i++) {
                        Cell emptyCell = data.getCell(i, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                        if (emptyCell == null) {
                            emptyCellCount++;
                        }
                    }
                    if (emptyCellCount != data.getLastCellNum()) {

                        // Assuming the data starts from the second row (index 1)
                        if (data.getRowNum() <= ServiceConstants.ITEM_COLUMN_HEADER_ROW_INDEX) {
                            // Skip the header row
                            continue;
                        }
                        hasDataRows = true;
                        // Extract and validate cell values
                        String itemCode = getCellStringValue(data, ServiceConstants.CELL_INDEX_0, resultResponses, type, headerNames);
                        String itemName = getCellStringValue(data, ServiceConstants.CELL_INDEX_1, resultResponses, type, headerNames);
                        String itemDescription = getCellStringValue(data, ServiceConstants.CELL_INDEX_2, resultResponses, type, headerNames);
                        String itemGroup = getCellStringValue(data, ServiceConstants.CELL_INDEX_3, resultResponses, type, headerNames);
                        String itemCategory = getCellStringValue(data, ServiceConstants.CELL_INDEX_4, resultResponses, type, headerNames);
                        String itemSubCategory = getCellStringValue(data, ServiceConstants.CELL_INDEX_5, resultResponses, type, headerNames);
                        String typeDirectIndirect = getCellStringValue(data, ServiceConstants.CELL_INDEX_6, resultResponses, type, headerNames);
                        String typeSerialBatch = getCellStringValue(data, ServiceConstants.CELL_INDEX_7, resultResponses, type, headerNames);
                        String isQCRequired = getCellStringValue(data, ServiceConstants.CELL_INDEX_8, resultResponses, type, headerNames);
                        String issueType = getCellStringValue(data, ServiceConstants.CELL_INDEX_9, resultResponses, type, headerNames);
                        String classABC = getCellStringValue(data, ServiceConstants.CELL_INDEX_10, resultResponses, type, headerNames);
                        String attribute = getCellStringValue(data, ServiceConstants.CELL_INDEX_11, resultResponses, type, headerNames);
                        String source = getCellStringValue(data, ServiceConstants.CELL_INDEX_12, resultResponses, type, headerNames);
                        String uom = getCellStringValue(data, ServiceConstants.CELL_INDEX_13, resultResponses, type, headerNames);
                        Float itemUnitWeight = getCellFloatValue(data, ServiceConstants.CELL_INDEX_14, resultResponses, type, headerNames);

                        String physicalForm   = getCellStringValue(data, ServiceConstants.CELL_INDEX_15, resultResponses, type, headerNames);

                        String containerCapacityUom = getCellStringValue(
                                data, ServiceConstants.CELL_INDEX_16, resultResponses, type, headerNames);

                        Float containerCapacity = getCellFloatValue(
                                data, ServiceConstants.CELL_INDEX_17, resultResponses, type, headerNames);

                        String code = getCellStringValue(data, ServiceConstants.CELL_INDEX_18, resultResponses, type, headerNames);
                        String types = getCellStringValue(data, ServiceConstants.CELL_INDEX_19, resultResponses, type, headerNames);
                        String dimensionUOM = getCellStringValue(data, ServiceConstants.CELL_INDEX_20, resultResponses, type, headerNames);

                        Float width = getCellFloatValue(data, ServiceConstants.CELL_INDEX_21, resultResponses, type, headerNames);
                        Float height = getCellFloatValue(data, ServiceConstants.CELL_INDEX_22, resultResponses, type, headerNames);
                        Float length = getCellFloatValue(data, ServiceConstants.CELL_INDEX_23, resultResponses, type, headerNames);
                        Float circumference = getCellFloatValue(data, ServiceConstants.CELL_INDEX_24, resultResponses, type, headerNames);
                        Float weight = getCellFloatValue(data, ServiceConstants.CELL_INDEX_25, resultResponses, type, headerNames);

                        Integer minimumOrderQty = getCellIntegerValue(data, ServiceConstants.CELL_INDEX_26, resultResponses, type, headerNames);
                        Integer optimumLevel = getCellIntegerValue(data, ServiceConstants.CELL_INDEX_27, resultResponses, type, headerNames);
                        Integer reorderLevel = getCellIntegerValue(data, ServiceConstants.CELL_INDEX_28, resultResponses, type, headerNames);
                        Integer safetyLevel = getCellIntegerValue(data, ServiceConstants.CELL_INDEX_29, resultResponses, type, headerNames);
                        Integer criticalLevel = getCellIntegerValue(data, ServiceConstants.CELL_INDEX_30, resultResponses, type, headerNames);

                        String dockId = getCellStringValue(data, ServiceConstants.CELL_INDEX_31, resultResponses, type, headerNames);
                        String dockName = getCellStringValue(data, ServiceConstants.CELL_INDEX_32, resultResponses, type, headerNames);
                        // Create a new Item object and set its properties
                        Item item = new Item();
                        item.setSubOrganizationId(loginUser.getSubOrgId());
                        item.setOrganizationId(loginUser.getOrgId());
                        // Check if item code already exists
                        Optional<Item> itemOptional = this.itemRepository.findByIsDeletedAndSubOrganizationIdAndItemCode(false, loginUser.getSubOrgId(), itemCode);
                        if (itemOptional.isEmpty()) {
                            item.setItemId(itemService.generateItemId(itemNO));
                            itemNO++;
                        } else {
                            resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.ITEM_CODE, ServiceConstants.ITEM_CODE_IS_ALREADY_PRESENT));
                        }
                        if (itemName == null || itemName.isEmpty()) {
                            resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.ITEM_NAME, ServiceConstants.ITEM_NAME_MANDATORY));
                        }

                        item.setName(itemName);
                        if (itemCode == null || itemCode.isEmpty()) {
                            resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.ITEM_CODE, ServiceConstants.ITEM_CODE_MANDATORY));
                        } else if (!validateRegex(itemCode, ServiceConstants.ID_REGEX)) {
                            resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.ITEM_CODE, ServiceConstants.INVALID_ITEM_CODE_FORMAT));
                        }
                        // Check if the item code is unique
                        if (itemCodeSet.contains(itemCode)) {
                            resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.ITEM_CODE, ServiceConstants.DUPLICATE_ITEM_CODE));
                        } else {
                            itemCodeSet.add(itemCode);
                        }
                        item.setItemCode(itemCode);

                        item.setDescription(itemDescription);

                        if (itemGroup == null || itemGroup.isEmpty()) {
                            resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.ITEM_GROUP, ServiceConstants.ITEM_GROUP_MANDATORY));
                        } else if (!validateRegex(itemGroup, ServiceConstants.NOT_ALLOW_SPECIAL_CHAR_REGEX)) {
                            resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.ITEM_GROUP, ServiceConstants.INVALID_ITEM_GROUP_FORMAT));
                        }
                        item.setItemGroup(itemGroup);

                        if (attribute == null || attribute.isEmpty()) {
                            resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.ATTRIBUTE, ServiceConstants.ATTRIBUTE_MANDATORY));
                        }
//                        else if (!validateRegex(attribute, ServiceConstants.NAME_FIRST_LETTER_CAPITAL_REGEX)) {
//                            resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.ATTRIBUTE, ServiceConstants.INVALID_ATTRIBUTE_FORMAT));
//                        }
                        item.setAttribute(attribute);
                        if (uom == null || uom.isEmpty()) {
                            resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.UOM, ServiceConstants.UOM_MANDATORY));
                        } else if (!validateRegex(uom, ServiceConstants.NOT_ALLOW_SPECIAL_CHAR_REGEX)) {
                            resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.UOM, ServiceConstants.INVALID_UOM_FORMAT));
                        }
                        item.setUom(uom);
                        if (itemCategory == null || itemCategory.isEmpty()) {
                            resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.ITEM_CATEGORY, ServiceConstants.ITEM_CATEGORY_MANDATORY));
                        } else if (!validateRegex(itemCategory, ServiceConstants.NOT_ALLOW_SPECIAL_CHAR_REGEX)) {
                            resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.ITEM_CATEGORY, ServiceConstants.INVALID_ITEM_CATEGORY_FORMAT));
                        }
                        item.setItemCategory(itemCategory);

                        if (itemSubCategory == null || itemSubCategory.isEmpty()) {
                            resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.ITEM_SUB_CATEGORY, ServiceConstants.ITEM_SUB_CATEGORY_MANDATORY));
                        } else if (!validateRegex(itemSubCategory, ServiceConstants.NOT_ALLOW_SPECIAL_CHAR_REGEX)) {
                            resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.ITEM_SUB_CATEGORY, ServiceConstants.INVALID_ITEM_SUB_CATEGORY_FORMAT));
                        }
                        item.setItemSubcategory(itemSubCategory);
                        List<String> drectIndrectList = new ArrayList<>(Arrays.asList("Direct", "Indirect"));
                        if (!drectIndrectList.stream().anyMatch(li -> li.equalsIgnoreCase(typeDirectIndirect))) {
                            resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.TYPE_DIRECT_INDIRECT, ServiceConstants.DIRECT_INDIRECT_INVALID));
                        }
                        if (typeDirectIndirect == null || typeDirectIndirect.isEmpty()) {
                            resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.TYPE_DIRECT_INDIRECT, ServiceConstants.TYPE_DIRECT_INDIRECT_MANDATORY));
                        } else if (!validateRegex(typeDirectIndirect, ServiceConstants.NOT_ALLOW_SPECIAL_CHAR_REGEX)) {
                            resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.TYPE_DIRECT_INDIRECT, ServiceConstants.INVALID_TYPE_DIRECT_INDIRECT_FORMAT));
                        }
                        item.setTypeDirectIndirect(typeDirectIndirect);

                        if (isQCRequired != null) {
                            if (isQCRequired.equalsIgnoreCase("yes")) {
                                item.setQcRequired(true);
                                item.setInspection(false);

                            } else if (isQCRequired.equalsIgnoreCase("No")) {
                                item.setQcRequired(false);
                                item.setInspection(false);

                            } else if(isQCRequired.equalsIgnoreCase("Certificate")){
                                item.setQcRequired(true);
                                item.setInspection(true);
                            }
                        } else {
                            resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.QC_REQUIRED, ServiceConstants.QC_REQUIRE_MANDATORY));
                        }

                        if (uom == null || uom.isEmpty()) {
                            resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1),
                                    ServiceConstants.UOM, ServiceConstants.UOM_MANDATORY));
                        } else if (!validateRegex(uom, ServiceConstants.NOT_ALLOW_SPECIAL_CHAR_REGEX)) {
                            resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1),
                                    ServiceConstants.UOM, ServiceConstants.INVALID_UOM_FORMAT));
                        }
                        item.setUom(uom);


                        List<String> serialBatchList = new ArrayList<>(Arrays.asList("Serial", "Batch", "None"));
                        if (!serialBatchList.stream().anyMatch(li -> li.equalsIgnoreCase(typeSerialBatch))) {
                            resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.TYPE_SERIAL_BATCH, ServiceConstants.SERIAL_BATCH_INVALID));
                        }

                        if (typeSerialBatch == null || typeSerialBatch.isEmpty()) {
                            resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.TYPE_SERIAL_BATCH, ServiceConstants.TYPE_SERIAL_BATCH_MANDATORY));
                        } else if (!validateRegex(typeSerialBatch, ServiceConstants.NOT_ALLOW_SPECIAL_CHAR_REGEX)) {
                            resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.TYPE_SERIAL_BATCH, ServiceConstants.INVALID_TYPE_SERIAL_BATCH_FORMAT));
                        }
                        item.setTypeSerialBatchNone(typeSerialBatch);

                        if (issueType == null || issueType.isEmpty()) {
                            resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.ISSUE_TYPE, ServiceConstants.ISSUE_TYPE_MANDATORY));
                        } else if (!validateRegex(issueType, ServiceConstants.NOT_ALLOW_SPECIAL_CHAR_REGEX)) {
                            resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.ISSUE_TYPE, ServiceConstants.INVALID_ISSUE_TYPE_FORMAT));
                        }
                        item.setIssueType(issueType);


                        if (source == null || source.isEmpty()) {
                            resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.SOURCE, ServiceConstants.SOURCE_MANDATORY));
                        } else if (!validateRegex(source, ServiceConstants.NOT_ALLOW_SPECIAL_CHAR_REGEX)) {
                            resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.SOURCE, ServiceConstants.INVALID_SOURCE_FORMAT));
                        }
                        item.setSource(source);

                        // ====================================================
                        List<String> allowedPhysicalForms = Arrays.asList("SOLID", "LIQUID", "GAS");

                        if (physicalForm == null || physicalForm.isEmpty()) {
                            resultResponses.add(new ValidationResultResponse(
                                    type,
                                    (data.getRowNum() + 1),
                                    "Physical Form",
                                    "Physical Form is mandatory (SOLID / LIQUID / GAS)."
                            ));
                        } else if (allowedPhysicalForms.stream().noneMatch(f -> f.equalsIgnoreCase(physicalForm))) {
                            resultResponses.add(new ValidationResultResponse(
                                    type,
                                    (data.getRowNum() + 1),
                                    "Physical Form",
                                    "Physical Form must be SOLID, LIQUID or GAS."
                            ));
                        }
                        item.setPhysicalForm(physicalForm);


                        List<String> classList = new ArrayList<>(Arrays.asList("A", "B", "C"));
                        if (!classList.stream().anyMatch(li -> li.equalsIgnoreCase(classABC))) {
                            resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.CLASSABC, ServiceConstants.CLASS_ABC_INVALID));
                        }
                        if (classABC == null || classABC.isEmpty()) {
                            resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.CLASSABC, ServiceConstants.CLASS_ABC_MANDATORY));
                        } else if (!validateRegex(classABC, ServiceConstants.CLASS_REGEX)) {
                            resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.CLASSABC, ServiceConstants.INVALID_CLASS_ABC_FORMAT));
                        }
                        item.setClassABC(classABC);

                        if (itemUnitWeight == null) {
                            resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.ITEM_UNIT_WEIGHT, ServiceConstants.ITEM_UNIT_WEIGHT_MANDATORY));
                        }
                        item.setItemUnitWeight(itemUnitWeight);

                        if (optimumLevel == null) {
                            resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.OPTIMUM_LEVEL, ServiceConstants.OPTIMUM_LEVEL_MANDATORY));
                        }
                        item.setOptimumLevel(optimumLevel);

                        if (reorderLevel == null) {
                            resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.REORDER_LEVEL, ServiceConstants.REORDER_LEVEL_MANDATORY));
                        }
                        item.setReorderLevel(reorderLevel);

                        if (criticalLevel == null) {
                            resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.CRITICAL_LEVEL, ServiceConstants.CRITICAL_LEVEL_MANDATORY));
                        }
                        item.setCriticalLevel(criticalLevel);

                        if (safetyLevel == null) {
                            resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.SAFETY_LEVEL, ServiceConstants.SAFETY_LEVEL_MANDATORY));
                        }
                        item.setSafetyStockLevel(safetyLevel);
                        item.setModifiedOn(new Date());
                        item.setIsDeleted(false);
                        item.setCreatedBy(loginUser.getUserId());
                        item.setCreatedOn(new Date());
                        if (dockName == null || dockName.isEmpty()) {
                            resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.DOCK_NAME, ServiceConstants.DOCK_NAME_MANDATORY));
                        }
//                        else if (!validateRegex(dockName, ServiceConstants.ADDRESS_REGEX)) {
//                            resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.DOCK_NAME, ServiceConstants.INVALID_DOCK_NAME_FORMAT));
//                        }
                        if (dockId == null || dockId.isEmpty()) {
                            resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.DOCK_ID, ServiceConstants.DOCK_ID_MANDATORY));
                        }
//                        else if (!validateRegex(dockId, ServiceConstants.ID_REGEX)) {
//                            resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.DOCK_ID, ServiceConstants.INVALID_DOCK_ID_FORMAT));
//                        }

                        if (dockName != null && dockId != null) {
                            Optional<Dock> dockOptional = docksRepository.findByIsDeletedAndSubOrganizationIdAndDockId(false, loginUser.getSubOrgId(), dockId);
                            if (dockOptional.isPresent()) {
                                if (!dockOptional.get().getDockName().equals(dockName)) {
                                    resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.DOCK_NAME, ServiceConstants.DOCK_ID_AND_DOCK_NAME_SHOULD_BE_CORRECT));
                                }
                                item.setDockId(dockOptional.get());
                            } else {
                                resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.DOCKS_ID, "Dock Id not Present"));
                            }
                        }

                        // ADd stock balance
                        if (itemOptional.isEmpty() && resultResponses.size() == 0) {
                            items.add(item);
                            StockBalance stockBalance = new StockBalance();
                            stockBalance.setItemId(item);
                            stockBalance.setBalanceQuantity(0.0F);
                            stockBalance.setIsDeleted(false);
                            stockBalance.setCreatedBy(loginUser.getUserId());
                            stockBalance.setCreatedOn(Timestamp.valueOf(LocalDateTime.now()));
                            stockBalance.setModifiedBy(loginUser.getUserId());
                            stockBalance.setModifiedOn(Timestamp.valueOf(LocalDateTime.now()));
                            stockBalance.setOrganizationId(loginUser.getOrgId());
                            stockBalance.setSubOrganizationId(loginUser.getSubOrgId());
                            stockBalances.add(stockBalance);
                        }
                        if (itemOptional.isEmpty() && resultResponses.size() == 0) {
                            Container container = new Container();
                            container.setItem(item);

                            // Container Capacity UOM: e.g. LTR, KG, NOS
                            if (containerCapacityUom == null || containerCapacityUom.isEmpty()) {
                                resultResponses.add(new ValidationResultResponse(
                                        type,
                                        (data.getRowNum() + 1),
                                        "Container Capacity UOM",
                                        "Container Capacity UOM is mandatory (e.g. LTR, KG, NOS)."
                                ));
                            } else if (!validateRegex(containerCapacityUom, ServiceConstants.NOT_ALLOW_SPECIAL_CHAR_REGEX)) {
                                resultResponses.add(new ValidationResultResponse(
                                        type,
                                        (data.getRowNum() + 1),
                                        "Container Capacity UOM",
                                        "Invalid Container Capacity UOM format."
                                ));
                            }

                            container.setContainerCapacityUom(containerCapacityUom);
                            // Container Capacity: > 0
                            if (containerCapacity == null || containerCapacity <= 0) {
                                resultResponses.add(new ValidationResultResponse(
                                        type,
                                        (data.getRowNum() + 1),
                                        "Container Capacity",
                                        "Container Capacity is mandatory and must be greater than 0."
                                ));
                            }
                            container.setContainerCapacity(containerCapacity);

                            if (code == null || code.isEmpty()) {
                                resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.CODE, ServiceConstants.CODE_MANDATORY));
                            } else if (!validateRegex(code, ServiceConstants.ID_REGEX)) {
                                resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.CODE, ServiceConstants.INVALID_CODE_FORMAT));
                            }
                            container.setCode(code);

                            if (weight == null) {
                                resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.WEIGHT, ServiceConstants.WEIGHT_MANDATORY));
                            }
                            container.setWeight(weight);

                            if (height == null) {
                                resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.HEIGHT, ServiceConstants.HEIGHT_MANDATORY));
                            }
                            container.setHeight(height);

                            if (length == null) {
                                resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.LENGTH, ServiceConstants.LENGTH_MANDATORY));
                            }
                            container.setLength(length);


                            if (dimensionUOM == null || dimensionUOM.isEmpty()) {
                                resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.DIMENSION_UOM, ServiceConstants.DIMENSION_UOM_MANDATORY));
                            } else if (!validateRegex(dimensionUOM, ServiceConstants.NOT_ALLOW_SPECIAL_CHAR_REGEX)) {
                                resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.DIMENSION_UOM, ServiceConstants.INVALID_DIMENSION_UOM_FORMAT));
                            }
                            container.setDimensionUOM(dimensionUOM);

                            if (circumference == null) {
                                resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.CIRCUMFERENCE, ServiceConstants.CIRCUMFERENCE_MANDATORY));
                            }
                            container.setCircumference(circumference);

                            if (width == null) {
                                resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.WIDTH, ServiceConstants.WIDTH_MANDATORY));
                            }
                            container.setWidth(width);

                            if (types == null || types.isEmpty()) {
                                resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.TYPES, ServiceConstants.TYPES_MANDATORY));
                            } else if (!validateRegex(types, ServiceConstants.NOT_ALLOW_SPECIAL_CHAR_REGEX)) {
                                resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.TYPES, ServiceConstants.INVALID_TYPES_FORMAT));
                            }
                            container.setType(types);
                            if (minimumOrderQty == null) {
                                resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.MINIMUM_ORDER_QTY, ServiceConstants.MINIMUM_ORDER_QTY_MANDATORY));
                            }
                            container.setMinimumOrderQty(minimumOrderQty);
                            container.setIsActive(true);
                            container.setIsDeleted(false);
                            container.setOrganizationId(loginUser.getOrgId());
                            container.setSubOrganizationId(loginUser.getSubOrgId());
                            container.setCreatedBy(loginUser.getUserId()); // Assuming 1 is the default value
                            container.setCreatedOn(new Date()); // Assuming current date/time
                            container.setModifiedBy(loginUser.getUserId()); // Assuming no modification initially
                            container.setModifiedOn(new Date()); // Assuming no modification initially
                            containerList.add(container);
                        }

                    }
                }
            }
            // Close the workbook
            workbook.close();
            if (!hasDataRows) {
                return ResponseEntity.ok(new BaseResponse<>(ServiceConstants.STATUS_CODE_500, ServiceConstants.EXCEL_FILE_CONTAINS_HEADER_AND_NO_DATA, null, ServiceConstants.ERROR_CODE, loginUser.getLogId()));
            }

            long endTime = System.currentTimeMillis();
            log.info(String.valueOf(new StringBuilder().append(loginUser.getLogId()).append(ServiceConstants.TOTAL_ROWS_SCANNED).append(count)));
            if (resultResponses.size() == 0) {
                this.itemRepository.saveAllAndFlush(items);
                this.containerRepository.saveAllAndFlush(containerList);
                this.stockBalanceRepository.saveAllAndFlush(stockBalances);
                log.info("LogId:{} - UploadExcelServiceImpl - uploadItemDetail - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), ServiceConstants.SPACE + ServiceConstants.UPLOAD_ITEM_DETAIL_METHOD_EXECUTED + (endTime - startTime));
                return ResponseEntity.ok(new BaseResponse<>(HttpStatus.OK.value(), ServiceConstants.FILE_UPLOADED_SUCCESSFULLY, null, ServiceConstants.SUCCESS_CODE, loginUser.getLogId()));
            } else {
                log.error("LogId:{} - UploadExcelServiceImpl - uploadItemDetail - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), ResponseKeyConstant.SPACE + ServiceConstants.ITEM_DATA_UPLOAD_FAILED + (endTime - startTime));
                return ResponseEntity.ok(new BaseResponse<>(ServiceConstants.STATUS_CODE_500, ServiceConstants.ITEM_DATA_UPLOAD_FAILED, resultResponses, ServiceConstants.ERROR_CODE, loginUser.getLogId()));
            }
        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            log.error("LogId:{} - UploadExcelServiceImpl - uploadItemDetail - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), ResponseKeyConstant.SPACE + ServiceConstants.ITEM_DATA_UPLOAD_FAILED + (endTime - startTime), e);
            ExceptionLogger.logException(e, loginUser.getLogId());
            return ResponseEntity.ok(new BaseResponse<>(ServiceConstants.STATUS_CODE_500, ServiceConstants.ITEM_DATA_UPLOAD_FAILED, null, ServiceConstants.ERROR_CODE, loginUser.getLogId()));
        }
    }

    @Override
    public ResponseEntity<BaseResponse> uploadLocationDetail(MultipartFile file, String type) throws IOException {
        long startTime = System.currentTimeMillis();
        log.info("LogId:{} - UploadExcelServiceImpl - uploadLocationDetail - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), ServiceConstants.SPACE + ServiceConstants.UPLOAD_LOCATION_DETAIL_METHOD_STARTED);
        try {
            // Read the Excel file and perform validation
            Workbook workbook = WorkbookFactory.create(file.getInputStream());
            Sheet sheet = workbook.getSheetAt(ServiceConstants.SHEET_INDEX); // Assuming the data is in the first sheet

            List<Location> locations = new ArrayList<>();
            List<Area> areaList = new ArrayList<>();
            List<Zone> zoneList = new ArrayList<>();
            List<ValidationResultResponse> resultResponses = new ArrayList<>();
            List<String> headerNames = new ArrayList<>();
            Integer count = 0;

            List<String> expectedColumns = Arrays.asList(
                    ServiceConstants.STORE_ID,
                    ServiceConstants.STORE_NAME,
                    ServiceConstants.ERP_AREA_ID,
                    ServiceConstants.AREA_ID,
                    ServiceConstants.AREA_NAME,
                    ServiceConstants.ERP_ZONE_ID,
                    ServiceConstants.Zone_ID,
                    ServiceConstants.Zone_NAME,
                    ServiceConstants.ERP_LOCATION_ID,
                    ServiceConstants.LOCATION_ID,
                    ServiceConstants.ITEM_ID,
                    ServiceConstants.ITEM_NAME,
                    ServiceConstants.LOCATION_TYPE,
                    ServiceConstants.ROW,
                    ServiceConstants.RACK_FLOOR,
                    ServiceConstants.RACK_NO,
                    ServiceConstants.SHELF_NO,
                    ServiceConstants.LENGTH,
                    ServiceConstants.WIDTH,
                    ServiceConstants.HEIGHT,
                    ServiceConstants.AREA_SQ_CM,
                    ServiceConstants.VOLUME_CU_CM,
                    ServiceConstants.CARRYING_CAPACITY
            );

            List<ExcellHeaderValidatorResponse> excellHeaderValidatorResponse = validateExcelHeader(sheet, expectedColumns);

            if (!excellHeaderValidatorResponse.get(0).getIsValid()) {
                return ResponseEntity.ok(new BaseResponse<>(ServiceConstants.INTERNAL_SERVER_ERROR, excellHeaderValidatorResponse.get(0).getErrorMessage(), excellHeaderValidatorResponse, ServiceConstants.ERROR_CODE, loginUser.getLogId()));
            }
            Row headerRow = sheet.getRow(1);
            for (int cellIndex = 0; cellIndex < headerRow.getLastCellNum(); cellIndex++) {
                Cell headerCell = headerRow.getCell(cellIndex, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                if (headerCell != null) {
                    String headerName = headerCell.getStringCellValue();
                    headerNames.add(headerName);
                }
            }
            for (Row data : sheet) {
                int emptyCellCount = 0;
                for (int i = 0; i < data.getLastCellNum(); i++) {
                    Cell emptyCell = data.getCell(i, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                    if (emptyCell == null) {
                        emptyCellCount++;
                    }
                }
                if (data.getLastCellNum() != emptyCellCount) {
                    // Assuming the data starts from the third row
                    if (data.getRowNum() <= ServiceConstants.LOCATION_COLUMN_HEADER_ROW_INDEX) {
                        // Skip the header row
                        continue;
                    }
                    String storeId = getCellStringValue(data, 0, resultResponses, type, headerNames);
                    String storeName = getCellStringValue(data, 1, resultResponses, type, headerNames);
                    String erpAreaId = getCellStringValue(data, 2, resultResponses, type, headerNames);
                    String areaId = getCellStringValue(data, 3, resultResponses, type, headerNames);
                    String areaName = getCellStringValue(data, 4, resultResponses, type, headerNames);
                    String erpZoneId = getCellStringValue(data, 5, resultResponses, type, headerNames);
                    String zoneId = getCellStringValue(data, 6, resultResponses, type, headerNames);
                    String zoneName = getCellStringValue(data, 7, resultResponses, type, headerNames);
                    String erpLocationId = getCellStringValue(data, 8, resultResponses, type, headerNames);
                    String locationId = getCellStringValue(data, 9, resultResponses, type, headerNames);
                    String itemId = getCellStringValue(data, 10, resultResponses, type, headerNames);
                    String itemName = getCellStringValue(data, 11, resultResponses, type, headerNames);
                    String locationType = getCellStringValue(data, 12, resultResponses, type, headerNames);
                    String row = getCellStringValue(data, 13, resultResponses, type, headerNames);
                    String rackFloor = getCellStringValue(data, 14, resultResponses, type, headerNames);
                    String rackNo = getCellStringValue(data, 15, resultResponses, type, headerNames);
                    String shelfNo = getCellStringValue(data, 16, resultResponses, type, headerNames);
                    Float length = getCellFloatValue(data, 17, resultResponses, type, headerNames);
                    Float width = getCellFloatValue(data, 18, resultResponses, type, headerNames);
                    Float height = getCellFloatValue(data, 19, resultResponses, type, headerNames);
                    Float area = getCellFloatValue(data, 20, resultResponses, type, headerNames);
                    Float volume = getCellFloatValue(data, 21, resultResponses, type, headerNames);
                    Integer carryingCapacityKg = getCellIntegerValue(data, 22, resultResponses, type, headerNames);
                    // Create a new Location object and set its properties
                    Area areas = areaRepository.findByIsDeletedAndSubOrganizationIdAndAreaIdAndStoreStoreId(false, loginUser, areaId, storeId);

                    areas.setErpAreaId(erpAreaId);
                    areas.setAreaName(areaName);
                    Optional<Store> store = storeRepository.findByIsDeletedAndStoreId(false, storeId);
                    if (!store.isEmpty()) {
                        areas.setStore(store.get());
                    } else {
                        resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), headerNames.get(ServiceConstants.CELL_INDEX_0), "This Store Id not Present in Database"));
                    }
                    areas.setIsDeleted(false);
                    areas.setCreatedBy(loginUser.getUserId());
                    areas.setCreatedOn(new Date());
                    Optional<Area> areaOptional = areaRepository.findByIsDeletedAndAreaId(false, areaId);
                    if (areaOptional.isEmpty()) {
                        areaRepository.save(areas);
                    }
                    Zone zone = new Zone();
                    zone.setZoneId(zoneId);
                    if (areaOptional.isPresent()) {
                        zone.setArea(areaOptional.get());
                    } else if (areas.getId() != null) {
                        zone.setArea(areas);
                    } else {
                        resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), headerNames.get(ServiceConstants.CELL_INDEX_6), "This Store Id not Present in Database"));
                    }
                    zone.setErpZoneId(erpZoneId);
                    zone.setZoneName(zoneName);
                    zone.setOrganizationId(loginUser.getOrgId());
                    zone.setSubOrganizationId(loginUser.getOrgId());
                    zone.setIsDeleted(false);
                    zone.setCreatedBy(loginUser.getUserId());
                    zone.setCreatedOn(new Date());
                    Optional<Zone> zoneOptional = zoneRepository.findByIsDeletedAndZoneId(false, zoneId);
                    if (zoneOptional.isEmpty()) {
                        zoneRepository.save(zone);
                    }
                    // Create a new Location object and set its properties
                    Location location = new Location();
                    Optional<Location> optionalLocation = locationRepository.findByIsDeletedAndLocationId(false, locationId);
                    if (optionalLocation.isEmpty()) {
                        location.setLocationId(locationId);
                    } else {
                        resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), headerNames.get(ServiceConstants.CELL_INDEX_9), "This Location Id is already Present in Database"));
                    }
                    if (zoneOptional.isPresent()) {
                        location.setZone(zoneOptional.get());
                    } else if (zone.getId() != null) {
                        location.setZone(zone);
                    } else {
                        resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), headerNames.get(ServiceConstants.CELL_INDEX_0), "This Store Id not Present in Database"));
                    }
                    location.setErpLocationId(erpLocationId);
                    location.setRow(row);
                    location.setRackFloor(rackFloor);
                    location.setRackNo(rackNo);
                    location.setShelfNo(shelfNo);
                    Optional<Item> itemOption = itemRepository.findByIsDeletedAndItemId(false, itemId);
                    if (itemOption.isPresent()) {
                        location.setItem(itemOption.get());
                    } else {
                        resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), headerNames.get(ServiceConstants.CELL_INDEX_10), "This Item Id not Present in Database"));
                    }
                    location.setLocationType(locationType);
                    location.setLength(length);
                    location.setHeight(height);
                    location.setWidth(width);
                    location.setAreaSqCm(area);
                    location.setVolumeCuCm(volume);
                    location.setCarryingCapacity(carryingCapacityKg);
                    location.setIsDeleted(false);
                    location.setCreatedBy(loginUser.getUserId());
                    location.setCreatedOn(new Date());
                    locations.add(location);
                    log.info(String.valueOf(new StringBuilder().append(loginUser.getLogId()).append(ServiceConstants.TOTAL_ROWS_SCANNED).append(count)));
                }
            }
            long endTime = System.currentTimeMillis();
            // Close the workbook
            workbook.close();

            if (resultResponses.size() == 0) {
                this.locationRepository.saveAllAndFlush(locations);
                log.info("LogId:{} - UploadExcelServiceImpl - uploadLocationDetail - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), ResponseKeyConstant.SPACE + ServiceConstants.UPLOAD_LOCATION_DETAIL_METHOD_EXECUTED + (endTime - startTime));

                return ResponseEntity.ok(new BaseResponse<>(HttpStatus.OK.value(), ServiceConstants.FILE_UPLOADED_SUCCESSFULLY, null, ServiceConstants.SUCCESS_CODE, loginUser.getLogId()));
            } else {
                log.error("LogId:{} - UploadExcelServiceImpl - uploadLocationDetail - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), ResponseKeyConstant.SPACE + ServiceConstants.LOCATION_DATA_UPLOAD_FAILED + (endTime - startTime));
                return ResponseEntity.ok(new BaseResponse<>(500, ServiceConstants.LOCATION_DATA_UPLOAD_FAILED, resultResponses, ServiceConstants.ERROR_CODE, loginUser.getLogId()));
            }
        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            log.error("LogId:{} - UploadExcelServiceImpl - uploadLocationDetail - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), ResponseKeyConstant.SPACE + ServiceConstants.LOCATION_DATA_UPLOAD_FAILED + (endTime - startTime), e);
            ExceptionLogger.logException(e, loginUser.getLogId());
            return ResponseEntity.ok(new BaseResponse<>(ServiceConstants.STATUS_CODE_500, ServiceConstants.LOCATION_DATA_UPLOAD_FAILED, null, ServiceConstants.ERROR_CODE, loginUser.getLogId()));
        }
    }

    @Override
    public ResponseEntity<BaseResponse> uploadSupplierDetail(MultipartFile file, String type) throws IOException {
        long startTime = System.currentTimeMillis();
        log.info("LogId:{} - UploadExcelServiceImpl - uploadSupplierDetail - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), ServiceConstants.SPACE + ServiceConstants.UPLOAD_SUPPLIER_DETAIL_METHOD_STARTED);

        try {
            // Read the Excel file and perform validation
            Workbook workbook = WorkbookFactory.create(file.getInputStream());
            Sheet sheet = workbook.getSheetAt(ServiceConstants.SHEET_INDEX); // Assuming the data is in the first sheet

            List<Supplier> suppliers = new ArrayList<>();
            List<ValidationResultResponse> resultResponses = new ArrayList<>();
            List<String> headerNames = new ArrayList<>();
            Integer count = 0;
            boolean hasDataRows = false; // Flag to track if there are data rows

            Set<String> panSet = new HashSet<>();
            Set<String> tanSet = new HashSet<>();
            Set<String> erpSupplierIdSet = new HashSet<>();
            Set<String> supplierNameSet = new HashSet<>();
            Set<String> emailSet = new HashSet<>();
            Set<String> phoneSet = new HashSet<>();

            List<String> expectedColumns = Arrays.asList(
                    ServiceConstants.ERP_SUPPLIER_ID,
                    ServiceConstants.SUPPLIER_NAME,
                    ServiceConstants.DATE_OF_REGISTRATION,
                    ServiceConstants.SUPPLIER_CATEGORY,
                    ServiceConstants.SUPPLIER_GROUP,
                    ServiceConstants.SUPPLIER_GST_REGISTRATION_NUMBER,
                    ServiceConstants.SUPPLIER_PAN_CARD_NUMBER,
                    ServiceConstants.SUPPLIER_TAN_NUMBER,
                    ServiceConstants.SUPPLIER_PAYMENT_TERMS,
                    ServiceConstants.SUPPLIER_PAYMENT_METHOD,
                    ServiceConstants.SUPPLIER_CREDIT_LIMIT_RS,
                    ServiceConstants.SUPPLIER_CREDIT_LIMIT_DAYS,
                    ServiceConstants.SUPPLIER_PRIMARY_BANKER,
                    ServiceConstants.FULL_BRANCH_ADDRESS,
                    ServiceConstants.MICR_CODE,
                    ServiceConstants.IFSC_CODE,
                    ServiceConstants.COUNTRY,
                    ServiceConstants.COUNTRY_CODE,
                    ServiceConstants.POST_CODE,
                    ServiceConstants.STATE,
                    ServiceConstants.DISTRICT,
                    ServiceConstants.TALUKA,
                    ServiceConstants.CITY,
                    ServiceConstants.TOWN,
                    ServiceConstants.VILLAGE,
                    ServiceConstants.ADDRESS_1,
                    ServiceConstants.ADDRESS_2,
                    ServiceConstants.BUILDING,
                    ServiceConstants.STREET,
                    ServiceConstants.LANDMARK,
                    ServiceConstants.SUB_LOCALITY,
                    ServiceConstants.LOCALITY,
                    ServiceConstants.AREA_CODE,
                    ServiceConstants.LATITUDE,
                    ServiceConstants.LONGITUDE,
                    ServiceConstants.OFFICE_PRIMARY_PHONE,
                    ServiceConstants.OFFICE_ALTERNATE_PHONE,
                    ServiceConstants.CONTACT_PERSON_NAME,
                    ServiceConstants.DESIGNATION,
                    ServiceConstants.DEPARTMENT,
                    ServiceConstants.PRIMARY_PHONE,
                    ServiceConstants.ALTERNATE_PHONE,
                    ServiceConstants.PRIMARY_EMAIL,
                    ServiceConstants.ALTERNATE_EMAIL
            );
            System.out.println(expectedColumns.get(0));
            List<ExcellHeaderValidatorResponse> excellHeaderValidatorResponse = validateExcelHeader(sheet, expectedColumns);

            if (!excellHeaderValidatorResponse.get(0).getIsValid()) {
                return ResponseEntity.ok(new BaseResponse<>(ServiceConstants.INTERNAL_SERVER_ERROR, excellHeaderValidatorResponse.get(0).getErrorMessage(), excellHeaderValidatorResponse, ServiceConstants.ERROR_CODE, loginUser.getLogId()));
            }

            // Iterate through the first row to get the header names
            Row headerRow = sheet.getRow(ServiceConstants.HEADER_INDEX);
            for (int cellIndex = 0; cellIndex < headerRow.getLastCellNum(); cellIndex++) {
                Cell headerCell = headerRow.getCell(cellIndex, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                if (headerCell != null) {
                    String headerName = headerCell.getStringCellValue();
                    headerNames.add(headerName);
                }
            }
            int supplierNo = 1;
            for (Row data : sheet) {
                int emptyCellCount = 0;
                for (int i = 0; i < data.getLastCellNum(); i++) {
                    Cell emptyCell = data.getCell(i, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                    if (emptyCell == null) {
                        emptyCellCount++;
                    }
                }

                if (emptyCellCount != data.getLastCellNum()) {
                    // Assuming the data starts from the second row (index 1)
                    if (data.getRowNum() <= ServiceConstants.SUPPLIER_COLUMN_HEADER_ROW_INDEX) {
                        // Skip the header row
                        continue;
                    }
                    hasDataRows = true;

                    String erpSupplierId = getCellStringValue(data, ServiceConstants.CELL_INDEX_0, resultResponses, type, headerNames);

                    String supplierName = getCellStringValue(data, ServiceConstants.CELL_INDEX_1, resultResponses, type, headerNames);

                    Date dateOfRegistration = getCellDateValue(data, ServiceConstants.CELL_INDEX_2, resultResponses, type, headerNames);

                    String supplierCategory = getCellStringValue(data, ServiceConstants.CELL_INDEX_3, resultResponses, type, headerNames);

                    String supplierType = getCellStringValue(data, ServiceConstants.CELL_INDEX_4, resultResponses, type, headerNames);

                    String supplierGSTRegistrationNumber = getCellStringValue(data, ServiceConstants.CELL_INDEX_5, resultResponses, type, headerNames);

                    String supplierPanCardNumber = getCellStringValue(data, ServiceConstants.CELL_INDEX_6, resultResponses, type, headerNames);

                    String supplierTanNumber = getCellStringValue(data, ServiceConstants.CELL_INDEX_7, resultResponses, type, headerNames);

                    String paymentTerms = getCellStringValue(data, ServiceConstants.CELL_INDEX_8, resultResponses, type, headerNames);

                    String paymentMethod = getCellStringValue(data, ServiceConstants.CELL_INDEX_9, resultResponses, type, headerNames);

                    Integer creditLimitRs = getCellIntegerValue(data, ServiceConstants.CELL_INDEX_10, resultResponses, type, headerNames);

                    Integer creditLimitDays = getCellIntegerValue(data, ServiceConstants.CELL_INDEX_11, resultResponses, type, headerNames);

                    String supplierPrimaryBanker = getCellStringValue(data, ServiceConstants.CELL_INDEX_12, resultResponses, type, headerNames);

                    String fullBranchAddress = getCellStringValue(data, ServiceConstants.CELL_INDEX_13, resultResponses, type, headerNames);

                    String micrCode = getCellStringValue(data, ServiceConstants.CELL_INDEX_14, resultResponses, type, headerNames);

                    String ifscCode = getCellStringValue(data, ServiceConstants.CELL_INDEX_15, resultResponses, type, headerNames);

                    String country = getCellStringValue(data, ServiceConstants.CELL_INDEX_16, resultResponses, type, headerNames);

                    Integer countryCode = getCellIntegerValue(data, ServiceConstants.CELL_INDEX_17, resultResponses, type, headerNames);

                    Integer postCode = getCellIntegerValue(data, ServiceConstants.CELL_INDEX_18, resultResponses, type, headerNames);

                    String state = getCellStringValue(data, ServiceConstants.CELL_INDEX_19, resultResponses, type, headerNames);

                    String district = getCellStringValue(data, ServiceConstants.CELL_INDEX_20, resultResponses, type, headerNames);

                    String taluka = getCellStringValue(data, ServiceConstants.CELL_INDEX_21, resultResponses, type, headerNames);

                    String city = getCellStringValue(data, ServiceConstants.CELL_INDEX_22, resultResponses, type, headerNames);

                    String town = getCellStringValue(data, ServiceConstants.CELL_INDEX_23, resultResponses, type, headerNames);

                    String village = getCellStringValue(data, ServiceConstants.CELL_INDEX_24, resultResponses, type, headerNames);

                    String address1 = getCellStringValue(data, ServiceConstants.CELL_INDEX_25, resultResponses, type, headerNames);

                    String address2 = getCellStringValue(data, ServiceConstants.CELL_INDEX_26, resultResponses, type, headerNames);

                    String building = getCellStringValue(data, ServiceConstants.CELL_INDEX_27, resultResponses, type, headerNames);

                    String street = getCellStringValue(data, ServiceConstants.CELL_INDEX_28, resultResponses, type, headerNames);

                    String landMark = getCellStringValue(data, ServiceConstants.CELL_INDEX_29, resultResponses, type, headerNames);

                    String subLocality = getCellStringValue(data, ServiceConstants.CELL_INDEX_30, resultResponses, type, headerNames);

                    String locality = getCellStringValue(data, ServiceConstants.CELL_INDEX_31, resultResponses, type, headerNames);

                    Integer areaCode = getCellIntegerValue(data, ServiceConstants.CELL_INDEX_32, resultResponses, type, headerNames);

                    Float latitude = getCellFloatValue(data, ServiceConstants.CELL_INDEX_33, resultResponses, type, headerNames);

                    Float longitude = getCellFloatValue(data, ServiceConstants.CELL_INDEX_34, resultResponses, type, headerNames);

                    String officePrimaryPhone = getCellStringValue(data, ServiceConstants.CELL_INDEX_35, resultResponses, type, headerNames);

                    String officeAlternatePhone = getCellStringValue(data, ServiceConstants.CELL_INDEX_36, resultResponses, type, headerNames);

                    String contactPersonName = getCellStringValue(data, ServiceConstants.CELL_INDEX_37, resultResponses, type, headerNames);

                    String designation = getCellStringValue(data, ServiceConstants.CELL_INDEX_38, resultResponses, type, headerNames);

                    String department = getCellStringValue(data, ServiceConstants.CELL_INDEX_39, resultResponses, type, headerNames);

                    String primaryPhone = getCellStringValue(data, ServiceConstants.CELL_INDEX_40, resultResponses, type, headerNames);

                    String alternatePhone = getCellStringValue(data, ServiceConstants.CELL_INDEX_41, resultResponses, type, headerNames);

                    String primaryEmail = getCellStringValue(data, ServiceConstants.CELL_INDEX_42, resultResponses, type, headerNames);

                    String alternateEmail = getCellStringValue(data, ServiceConstants.CELL_INDEX_43, resultResponses, type, headerNames);

                    // Create a new Supplier object and set its properties
                    Supplier supplier = new Supplier();


                    supplier.setSupplierId(supplierService.generateSupplierId(supplierNo));
                    supplierNo++;
                    if (supplierName == null || supplierName.isEmpty()) {
                        resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.SUPPLIER_NAME, ServiceConstants.SUPPLIER_NAME_MANDATORY_ERROR_MESSAGE));
                    }
//                    else if (!validateRegex(supplierName, ServiceConstants.NAME_REGEX)) {
//                        resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.SUPPLIER_NAME, ServiceConstants.INVALID_SUPPLIER_NAME_FORMAT));
//                    }
                    Supplier supplierName1 = this.supplierRepository.findByIsDeletedAndSupplierNameAndSubOrganizationId
                            (false, supplierName, loginUser.getOrgId());
                    if (supplierName1 == null) {
                        if (supplierNameSet.contains(supplierName)) {
                            resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.SUPPLIER_NAME, ServiceConstants.DUPLICATE_SUPPLIER_NAME_FOUND));
                        } else {
                            supplierNameSet.add(supplierName);
                        }
                        supplier.setSupplierName(supplierName);
                    } else {
                        resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.SUPPLIER_NAME, ServiceConstants.SUPPLIER_NAME_IS_ALREADY_PRESENT_IN_MASTER_CONFIGURATION));
                    }

                    if (dateOfRegistration == null) {
                        resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.DATE_OF_REGISTRATION, ServiceConstants.DATE_OF_REGISTRATION_MANDATORY));
                    } else {
                        SimpleDateFormat dateFormat = new SimpleDateFormat(ServiceConstants.DATE_FORMAT_PATTERN);
                        String dateOfRegistrationStr = dateFormat.format(dateOfRegistration);

                        Pattern pattern = Pattern.compile(ServiceConstants.DATE_REGEX_PATTERN);
                        Matcher matcher = pattern.matcher(dateOfRegistrationStr);

                        if (!matcher.matches()) {
                            resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.DATE_OF_REGISTRATION, "Date of registration must be in the format 'dd MMM yyyy' (e.g., '08 Jun 2024')"));
                        } else {
                            // Check if the date is not in the future
                            Date today = new Date();
                            if (dateOfRegistration.after(today)) {
                                resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.DATE_OF_REGISTRATION, ServiceConstants.DATE_OF_REGISTRATION_CANNOT_BE_IN_THE_FUTURE));
                            }
                        }
                    }

                    supplier.setDateOfRegistration(dateOfRegistration);

                    if (supplierCategory == null || supplierCategory.isEmpty()) {
                        resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.SUPPLIER_CATEGORY, ServiceConstants.CATEGORY_MANDATORY));
                    } else if (!validateRegex(supplierCategory, ServiceConstants.NOT_ALLOW_SPECIAL_CHAR_REGEX)) {
                        resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.SUPPLIER_CATEGORY, ServiceConstants.INVALID_SUPPLIER_CATEGORY_FORMAT));
                    }
                    supplier.setSupplierCategory(supplierCategory);

                    if (supplierType == null || supplierType.isEmpty()) {
                        resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.SUPPLIER_TYPE, ServiceConstants.SUPPLIER_TYPE_MANDATORY));
                    } else if (!validateRegex(supplierType, ServiceConstants.NOT_ALLOW_SPECIAL_CHAR_REGEX)) {
                        resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.SUPPLIER_TYPE, ServiceConstants.INVALID_SUPPLIER_TYPE_FORMAT));
                    }
                    supplier.setSupplierGroup(supplierType);

                    if (state == null || state.isEmpty()) {
                        resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.STATE, ServiceConstants.STATE_MANDATORY));
                    } else if (!validateRegex(state, ServiceConstants.NAME_FIRST_LETTER_CAPITAL_REGEX)) {
                        resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.STATE, ServiceConstants.INVALID_STATE_FORMAT));
                    }
                    supplier.setState(state);

                    if (address1 == null || address1.isEmpty()) {
                        resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.ADDRESS_1, ServiceConstants.ADDRESS_1_MANDATORY));
                    }
//                    else if (!validateRegex(address1, ServiceConstants.NOT_ALLOW_SPECIAL_CHAR_REGEX)) {
//                        resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.ADDRESS_1, ServiceConstants.INVALID_ADDRESS_1_FORMAT));
//                    }
                    supplier.setAddress1(address1);
//                    if (address2 != null && !address2.isEmpty()) {
////                        if (!validateRegex(address2, ServiceConstants.NOT_ALLOW_SPECIAL_CHAR_REGEX)) {
//                        resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.ADDRESS_2, ServiceConstants.INVALID_ADDRESS_2_FORMAT));
////                        }
//
//                    }
                    supplier.setAddress2(address2);
//
//                    if (locality != null && !locality.isEmpty()) {
//                        if (!validateRegex(locality, ServiceConstants.ADDRESS_REGEX)) {
//                            resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.LOCALITY, ServiceConstants.INVALID_LOCALITY_FORMAT));
//                        }
//                    }
                    supplier.setLocality(locality);

                    if (district == null || district.isEmpty()) {
                        resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.DISTRICT, ServiceConstants.DISTRICT_MANDATORY));
                    } else if (!validateRegex(district, ServiceConstants.NAME_FIRST_LETTER_CAPITAL_REGEX)) {
                        resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.DISTRICT, ServiceConstants.INVALID_DISTRICT_FORMAT));
                    }
                    supplier.setDistrict(district);

                    if (taluka == null || taluka.isEmpty()) {
                        resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.TALUKA, ServiceConstants.TALUKA_MANDATORY));
                    } else if (!validateRegex(taluka, ServiceConstants.NAME_FIRST_LETTER_CAPITAL_REGEX)) {
                        resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.TALUKA, ServiceConstants.INVALID_TALUKA_FORMAT));
                    }
                    supplier.setTaluka(taluka);

                    if (city == null || city.isEmpty()) {
//                        resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.CITY, ServiceConstants.CITY_MANDATORY));
                    } else if (!validateRegex(city, ServiceConstants.NAME_FIRST_LETTER_CAPITAL_REGEX)) {
                        resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.CITY, ServiceConstants.INVALID_CITY_FORMAT));
                    }
                    supplier.setCity(city);
                    if (postCode == null) {
                        resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.POST_CODE, ServiceConstants.POST_CODE_MANDATORY));
                    } else if (!validateRegex(String.valueOf(postCode), ServiceConstants.POST_CODE_REGEX)) {
                        resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.POST_CODE, ServiceConstants.INVALID_POST_CODE_FORMAT));
                    }
                    supplier.setPostCode(postCode);

                    if (supplierGSTRegistrationNumber == null || supplierGSTRegistrationNumber.isEmpty()) {
                        resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.SUPPLIER_GST_REGISTRATION_NUMBER, ServiceConstants.GST_MANDATORY_ERROR_MESSAGE));
                    } else if (!validateRegex(supplierGSTRegistrationNumber, ServiceConstants.SUPPLIER_GST_REGEX)) {
                        resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.SUPPLIER_GST_REGISTRATION_NUMBER, ServiceConstants.INVALID_SUPPLIER_GST_FORMAT));
                    }
                    supplier.setSupplierGSTRegistrationNumber(supplierGSTRegistrationNumber);

                    if (supplierPanCardNumber == null || supplierPanCardNumber.isEmpty()) {
                        resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.SUPPLIER_PAN_CARD_NUMBER, ServiceConstants.PAN_MANDATORY_ERROR_MESSAGE));
                    } else if (!validateRegex(supplierPanCardNumber, ServiceConstants.SUPPLIER_PAN_REGEX)) {
                        resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.SUPPLIER_PAN_CARD_NUMBER, ServiceConstants.INVALID_SUPPLIER_PAN_FORMAT));
                    }
                    if (panSet.contains(supplierPanCardNumber)) {
                        resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.SUPPLIER_PAN_CARD_NUMBER, ServiceConstants.DUPLICATE_PAN_ERROR_MESSAGE));
                    } else {
                        panSet.add(supplierPanCardNumber);
                    }
                    // Validate that supplierPanCardNumber matches the PAN extracted from supplierGSTRegistrationNumber
                    if (StringUtils.isNotEmpty(supplierGSTRegistrationNumber) && supplierGSTRegistrationNumber.length() >= 15) {
                        String extractedPan = supplierGSTRegistrationNumber.substring(2, 12);
                        if (!extractedPan.equalsIgnoreCase(supplierPanCardNumber)) {
                            resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.SUPPLIER_PAN_CARD_NUMBER, ServiceConstants.SUPPLIER_PAN_CARD_NUMBER_DOES_NOT_MATCH_GST_NUMBER));
                        }
                    }

                    supplier.setSupplierPANNumber(supplierPanCardNumber);
                    if (contactPersonName != null && !contactPersonName.isEmpty()) {
                        // Add any specific validation for contactPersonName if needed
//                        if (!validateRegex(contactPersonName, ServiceConstants.NAME_FIRST_LETTER_CAPITAL_REGEX)) { // Assuming you have a regex for names
//                            resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.CONTACT_PERSON_NAME, ServiceConstants.INVALID_NAME_FORMAT_ERROR_MESSAGE));
//                        }
                    }
                    supplier.setContactPersonName(contactPersonName);
                    if (designation != null && !designation.isEmpty()) {
                        // Add any specific validation for designation if needed
                        if (!validateRegex(designation, ServiceConstants.NAME_FIRST_LETTER_CAPITAL_REGEX)) { // Assuming you have a regex for designations
                            resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.DESIGNATION, ServiceConstants.INVALID_DESIGNATION_FORMAT_ERROR_MESSAGE));
                        }
                    }
                    supplier.setDesignation(designation);

                    if (department != null && !department.isEmpty()) {
                        // Add any specific validation for department if needed
                        if (!validateRegex(department, ServiceConstants.NAME_FIRST_LETTER_CAPITAL_REGEX)) { // Assuming you have a regex for departments
                            resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.DEPARTMENT, ServiceConstants.INVALID_DEPARTMENT_FORMAT_ERROR_MESSAGE));
                        }
                    }
                    supplier.setDepartment(department);

                    if (primaryPhone == null || primaryPhone.isEmpty()) {
                        resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.PRIMARY_PHONE, ServiceConstants.PRIMARY_PHONE_MANDATORY));
                    }
//                    else if (!validateRegex(primaryPhone, ServiceConstants.PHONE_REGEX)) {
//                        resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.PRIMARY_PHONE, ServiceConstants.INVALID__PHONE_FORMAT_ERROR_MESSAGE));
//                    }
                    if (phoneSet.contains(primaryPhone)) {
                        resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.PRIMARY_PHONE, ServiceConstants.DUPLICATE_PHONE_ERROR_MESSAGE));
                    } else {
                        phoneSet.add(primaryPhone);
                    }
                    supplier.setPrimaryPhone(primaryPhone);

                    if (areaCode == null) {
                        resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.AREA_CODE, ServiceConstants.AREA_CODE_MANDATORY));
                    }
                    supplier.setAreaCode(areaCode);
                    if (landMark != null && !landMark.isEmpty()) {
//                        if (!validateRegex(landMark, ServiceConstants.ADDRESS_REGEX)) {
//                            resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.LANDMARK, ServiceConstants.INVALID_LANDMARK_ERROR));
//                        }
                    }
                    supplier.setLandmark(landMark);
                    supplier.setLatitude(String.valueOf(latitude));
                    supplier.setLongitude(String.valueOf(longitude));

                    if (micrCode == null || micrCode.isEmpty()) {
                        resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.MICR_CODE, ServiceConstants.MICR_CODE_MANDATORY));
                    } else if (!validateRegex(micrCode, ServiceConstants.MICR_CODE_REGEX)) {
                        resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.MICR_CODE, ServiceConstants.INVALID_MICR_CODE_FORMAT));

                    }
                    supplier.setMicrCode(micrCode);

                    if (ifscCode == null || ifscCode.isEmpty()) {
                        resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.IFSC_CODE, ServiceConstants.IFSC_CODE_MANDATORY));
                    } else if (!validateRegex(ifscCode, ServiceConstants.IFSC_CODE_REGEX)) {
                        resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.IFSC_CODE, ServiceConstants.INVALID_IFSC_CODE_FORMAT));

                    }
                    supplier.setIfscCode(ifscCode);

                    if (officeAlternatePhone != null && !officeAlternatePhone.isEmpty()) {
//                        if (!validateRegex(officeAlternatePhone, ServiceConstants.PHONE_REGEX)) {
//                            resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.OFFICE_ALTERNATE_PHONE, ServiceConstants.INVALID__PHONE_FORMAT_ERROR_MESSAGE));
//                        }
                    }
                    supplier.setOfficeAlternatePhone(officeAlternatePhone);

                    if (officePrimaryPhone == null || officePrimaryPhone.isEmpty()) {
                        resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.OFFICE_PRIMARY_PHONE, ServiceConstants.OFFICE_PRIMARY_PHONE_MANDATORY));
                    }
//                    else if (!validateRegex(officePrimaryPhone, ServiceConstants.PHONE_REGEX)) {
//                        resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.OFFICE_PRIMARY_PHONE, ServiceConstants.INVALID__PHONE_FORMAT_ERROR_MESSAGE));
//                    }
                    supplier.setOfficePrimaryPhone(officePrimaryPhone);

                    if (paymentMethod == null || paymentMethod.isEmpty()) {
                        resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.PAYMENT_METHOD, ServiceConstants.PAYMENT_METHOD_MANDATORY));
                    } else if (!validateRegex(paymentMethod, ServiceConstants.ADDRESS_REGEX)) {
                        resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.PAYMENT_METHOD, ServiceConstants.INVALID_PAYMENT_METHOD_FORMAT));
                    }
                    supplier.setPaymentMethod(paymentMethod);

                    if (paymentTerms == null || paymentTerms.isEmpty()) {
                        resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.PAYMENT_TERMS, ServiceConstants.PAYMENT_TERMS_MANDATORY));
                    } else if (!validateRegex(paymentTerms, ServiceConstants.NOT_ALLOW_SPECIAL_CHAR_REGEX)) {
                        resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.PAYMENT_TERMS, ServiceConstants.INVALID_PAYMENT_TERMS_FORMAT));
                    }
                    supplier.setPaymentTerms(paymentTerms);


                    if (erpSupplierId == null || erpSupplierId.isEmpty()) {
                        resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.ERP_SUPPLIER_ID, ServiceConstants.ERP_ID_MANDATORY_ERROR_MESSAGE));
                    }
//                    else if (!validateRegex(erpSupplierId, ServiceConstants.ID_REGEX)) {
////                        resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.ERP_SUPPLIER_ID, ServiceConstants.INVALID_ERP_SUPPLIER_ID_FORMAT));
////                    }
                    if (erpSupplierIdSet.contains(erpSupplierId)) {
                        resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.ERP_SUPPLIER_ID, ServiceConstants.DUPLICATE_ERP_ID_ERROR_MESSAGE));
                    } else {
                        Optional<Supplier> validateErpSupplierId = supplierRepository.findByIsDeletedAndSubOrganizationIdIsAndErpSupplierId(false, loginUser.getSubOrgId(), erpSupplierId);
                        if (validateErpSupplierId.isEmpty()) {
                            erpSupplierIdSet.add(erpSupplierId);
                            supplier.setErpSupplierId(erpSupplierId);
                        } else {
                            resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.ERP_SUPPLIER_ID, ServiceConstants.ERP_ID_DUPLICATE_ERROR_MESSAGE));
                        }

                    }
                    if (supplierTanNumber == null || supplierTanNumber.isEmpty()) {
//                        resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.SUPPLIER_TAN_NUMBER, ServiceConstants.TAN_MANDATORY_ERROR_MESSAGE));
                    } else if (!validateRegex(supplierTanNumber, ServiceConstants.SUPPLIER_TAN_REGEX)) {
                        resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.SUPPLIER_TAN_NUMBER, ServiceConstants.INVALID_SUPPLIER_TAN_FORMAT));
                    }
                    if (tanSet.contains(supplierTanNumber)) {
                        resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.SUPPLIER_TAN_NUMBER, ServiceConstants.DUPLICATE_TAN_ERROR_MESSAGE));
                    } else {
                        if (supplierTanNumber != null)
                            tanSet.add(supplierTanNumber);
                    }
                    supplier.setSupplierTANNumber(supplierTanNumber);

                    if (creditLimitDays == null) {
                        resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.CREDIT_LIMIT_DAYS, ServiceConstants.CREDIT_LIMIT_DAYS_MANDATORY));
                    } else if (creditLimitDays >= 365) {
                        resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.CREDIT_LIMIT_DAYS, ServiceConstants.CREDIT_LIMIT_DAYS_MUST_BE_LESS_THAN_365));
                    }
                    supplier.setCreditLimitDays(creditLimitDays);

                    if (creditLimitRs == null) {
                        resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.CREDIT_LIMIT_RS, ServiceConstants.CREDIT_LIMIT_RS_MANDATORY));
                    }
                    supplier.setCreditLimitRs(creditLimitRs);

                    if (supplierPrimaryBanker == null || supplierPrimaryBanker.isEmpty()) {
                        resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.PRIMARY_BANKER, ServiceConstants.PRIMARY_BANKER_MANDATORY));
                    } else if (!validateRegex(supplierPrimaryBanker, ServiceConstants.NOT_ALLOW_SPECIAL_CHAR_REGEX)) {
                        resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.PRIMARY_BANKER, ServiceConstants.INVALID_PRIMARY_BANKER_FORMAT));
                    }
                    supplier.setSupplierPrimaryBanker(supplierPrimaryBanker);

                    if (fullBranchAddress == null || fullBranchAddress.isEmpty()) {
                        resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.FULL_BRANCH_ADDRESS, ServiceConstants.FULL_BRANCH_ADDRESS_MANDATORY));
                    }
//                    else  {
//                        resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.FULL_BRANCH_ADDRESS, ServiceConstants.INVALID_FULL_BRANCH_ADDRESS_FORMAT));
//                    }
                    supplier.setFullBranchAddress(fullBranchAddress);

                    if (country == null || country.isEmpty()) {
                        resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.COUNTRY, ServiceConstants.COUNTRY_MANDATORY));
                    } else if (!validateRegex(country, ServiceConstants.NAME_FIRST_LETTER_CAPITAL_REGEX)) {
                        resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.COUNTRY, ServiceConstants.INVALID_COUNTRY_FORMAT));
                    }
                    supplier.setCountry(country);

                    if (countryCode == null) {
                        resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.COUNTRY_CODE, ServiceConstants.COUNTRY_CODE_MANDATORY));
                    }
                    supplier.setCountryCode(countryCode);

                    if (town == null || town.isEmpty()) {
//                        resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.TOWN, ServiceConstants.TOWN_MANDATORY));
                    } else if (!validateRegex(town, ServiceConstants.NAME_FIRST_LETTER_CAPITAL_REGEX)) {
                        resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.TOWN, ServiceConstants.INVALID_TOWN_NAME_FORMAT));
                    }
                    supplier.setTown(town);
                    if (village == null || village.isEmpty()) {
//                        resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.VILLAGE, ServiceConstants.VILLAGE_MANDATORY));
                    } else if (!validateRegex(village, ServiceConstants.NAME_FIRST_LETTER_CAPITAL_REGEX)) {
                        resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.VILLAGE, ServiceConstants.INVALID_VILLAGE_NAME_FORMAT));
                    }
                    supplier.setVillage(village);
//                    if (building != null && !building.isEmpty()) {
////                        if (!validateRegex(building, ServiceConstants.ADDRESS_REGEX)) {
//                            resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.BUILDING, ServiceConstants.INVALID_BUILDING_FORMAT));
////                        }
//                    }
                    supplier.setBuilding(building);
//                    if (street != null && !street.isEmpty()) {
////                        if (!validateRegex(street, ServiceConstants.ADDRESS_REGEX)) {
//                            resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.STREET, ServiceConstants.INVALID_STREET_FORMAT));
////                        }
//                    }
                    supplier.setStreet(street);

//                    if (subLocality != null && !subLocality.isEmpty()) {
////                        if (!validateRegex(subLocality, ServiceConstants.ADDRESS_REGEX)) {
//                            resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.SUB_LOCALITY, ServiceConstants.INVALID_SUB_LOCALITY_FORMAT));
////                        }
//                    }
                    supplier.setSubLocality(subLocality);

                    if (alternatePhone != null && !alternatePhone.isEmpty()) {
//                        if (!validateRegex(alternatePhone, ServiceConstants.PHONE_REGEX)) {
//                            resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.ALTERNATE_PHONE, ServiceConstants.INVALID__PHONE_FORMAT_ERROR_MESSAGE));
//                        }
                        if (phoneSet.contains(alternatePhone)) {
                            resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.ALTERNATE_PHONE, ServiceConstants.DUPLICATE_PHONE_ERROR_MESSAGE));
                        } else {
                            phoneSet.add(alternatePhone);
                        }
                    }
                    supplier.setAlternatePhone(alternatePhone);

                    if (primaryEmail == null || primaryEmail.isEmpty()) {
                        resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.PRIMARY_EMAIL, ServiceConstants.EMAIL_MANDATORY));
                    } else if (!validateRegex(primaryEmail, ServiceConstants.EMAIL_REGEX)) {
                        resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.PRIMARY_EMAIL, ServiceConstants.INVALID_EMAIL_FORMAT_ERROR_MESSAGE));
                    }
                    if (emailSet.contains(primaryEmail)) {
                        resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.PRIMARY_EMAIL, ServiceConstants.DUPLICATE_EMAIL_ERROR_MESSAGE));
                    } else {
                        emailSet.add(primaryEmail);
                    }
                    supplier.setPrimaryEmail(primaryEmail);

                    if (alternateEmail != null && !alternateEmail.isEmpty()) {
                        if (!validateRegex(alternateEmail, ServiceConstants.EMAIL_REGEX)) {
                            resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.ALTERNATE_EMAIL, ServiceConstants.INVALID_EMAIL_FORMAT_ERROR_MESSAGE));
                        }
                        if (emailSet.contains(alternateEmail)) {
                            resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.ALTERNATE_EMAIL, ServiceConstants.DUPLICATE_EMAIL_ERROR_MESSAGE));
                        } else {
                            emailSet.add(alternateEmail);
                        }
                    }
                    supplier.setAlternateEmail(alternateEmail);
                    supplier.setIsDeleted(false);
                    supplier.setCreatedOn(new Date());
                    supplier.setCreatedBy(loginUser.getUserId());
                    supplier.setModifiedOn(new Date());
                    supplier.setModifiedBy(loginUser.getUserId());
                    supplier.setSubOrganizationId(loginUser.getSubOrgId());
                    supplier.setOrganizationId(loginUser.getOrgId());
                    suppliers.add(supplier);

                    count++;
                }
            }
            // Close the workbook
            workbook.close();
            if (!hasDataRows) {
                log.info(String.valueOf(loginUser.getLogId() + ServiceConstants.SPACE + ServiceConstants.EXCEL_FILE_CONTAINS_HEADER_AND_NO_DATA));
                return ResponseEntity.ok(new BaseResponse<>(ServiceConstants.STATUS_CODE_500, ServiceConstants.EXCEL_FILE_CONTAINS_HEADER_AND_NO_DATA, null, ServiceConstants.ERROR_CODE, loginUser.getLogId()));
            }
            log.info(String.valueOf(new StringBuilder().append(loginUser.getLogId()).append(ServiceConstants.TOTAL_ROWS_SCANNED).append(count)));

            long endTime = System.currentTimeMillis();

            if (resultResponses.size() == 0) {
//                this.supplierRepository.saveAllAndFlush(suppliers);
                this.supplierRepository.saveAll(suppliers);

                log.info("LogId:{} - UploadExcelServiceImpl - uploadSupplierDetail - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), ResponseKeyConstant.SPACE + ServiceConstants.UPLOAD_SUPPLIER_DETAIL_METHOD_EXECUTED + (endTime - startTime));

                return ResponseEntity.ok(new BaseResponse<>(HttpStatus.OK.value(), ServiceConstants.FILE_UPLOADED_SUCCESSFULLY, suppliers, ServiceConstants.SUCCESS_CODE, loginUser.getLogId()));
            } else {
                log.error("LogId:{} - UploadExcelServiceImpl - uploadSupplierDetail - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), ResponseKeyConstant.SPACE + ServiceConstants.SUPPLIER_DATA_UPLOAD_FAILED + (endTime - startTime));


                return ResponseEntity.ok(new BaseResponse<>(ServiceConstants.STATUS_CODE_500, ServiceConstants.SUPPLIER_DATA_UPLOAD_FAILED, resultResponses, ServiceConstants.ERROR_CODE, loginUser.getLogId()));
            }

        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            log.error("LogId:{} - UploadExcelServiceImpl - uploadSupplierDetail - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), ResponseKeyConstant.SPACE + ServiceConstants.SUPPLIER_DATA_UPLOAD_FAILED + (endTime - startTime), e);
            ExceptionLogger.logException(e, loginUser.getLogId());
            return ResponseEntity.ok(new BaseResponse<>(ServiceConstants.STATUS_CODE_500, ServiceConstants.SUPPLIER_DATA_UPLOAD_FAILED, null, ServiceConstants.ERROR_CODE, loginUser.getLogId()));
        }
    }

    private boolean validateRegex(String value, String regex) {
        if (value == null || regex == null) {
            return false;
        }
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(value);
        return matcher.matches();
    }

    @Override
    public ResponseEntity<BaseResponse> uploadStoreDetail(MultipartFile file, String type, String logId) throws IOException {
        long startTime = System.currentTimeMillis();
        log.info("LogId:{} - UploadExcelServiceImpl - uploadStoreDetail - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), ServiceConstants.SPACE + ServiceConstants.UPLOAD_STORE_DETAIL_METHOD_STARTED);

        try {
            // Read the Excel file and perform validation
            Workbook workbook = WorkbookFactory.create(file.getInputStream());
            Sheet sheet = workbook.getSheetAt(ServiceConstants.SHEET_INDEX); // Assuming the data is in the first sheet

            List<Store> stores = new ArrayList<>();
            List<ValidationResultResponse> resultResponses = new ArrayList<>();
            List<String> headerNames = new ArrayList<>();
            Integer count = 0;
            List<String> expectedColumns = Arrays.asList(
                    ServiceConstants.STORE_ID,
                    ServiceConstants.ERP_STORE_ID,
                    ServiceConstants.STORE_NAME,
                    ServiceConstants.STORE_MANAGER_NAME
            );
            List<ExcellHeaderValidatorResponse> excellHeaderValidatorResponse = validateExcelHeader(sheet, expectedColumns);
            if (!excellHeaderValidatorResponse.get(0).getIsValid()) {
                return ResponseEntity.ok(new BaseResponse<>(ServiceConstants.INTERNAL_SERVER_ERROR, excellHeaderValidatorResponse.get(0).getErrorMessage(), excellHeaderValidatorResponse, ServiceConstants.ERROR_CODE, logId));
            }

            Row headerRow = sheet.getRow(ServiceConstants.HEADER_INDEX);
            for (int cellIndex = 0; cellIndex < headerRow.getLastCellNum(); cellIndex++) {
                Cell headerCell = headerRow.getCell(cellIndex, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                if (headerCell != null) {
                    String headerName = headerCell.getStringCellValue();
                    headerNames.add(headerName);
                }
            }

            for (Row data : sheet) {
                int emptyCellCount = 0;
                for (int i = 0; i < data.getLastCellNum(); i++) {
                    Cell emptyCell = data.getCell(i, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                    if (emptyCell == null) {
                        emptyCellCount++;
                    }
                }
                if (data.getLastCellNum() != emptyCellCount) {
                    // Assuming the data starts from the third row
                    if (data.getRowNum() == 0 || data.getRowNum() == 1) {
                        // Skip the header row
                        continue;
                    }
                    String storeId = getCellStringValue(data, ServiceConstants.CELL_INDEX_0, resultResponses, type, headerNames);
                    String erpStoreId = getCellStringValue(data, ServiceConstants.CELL_INDEX_1, resultResponses, type, headerNames);
                    String storeName = getCellStringValue(data, ServiceConstants.CELL_INDEX_2, resultResponses, type, headerNames);
                    String storeManagerName = getCellStringValue(data, ServiceConstants.CELL_INDEX_3, resultResponses, type, headerNames);
                    // Create a new Store object and set its properties
                    Store store = new Store();
                    if (storeId == null) {
//                        store.setStoreId(validations.storeIdGeneration());
                    } else {
                        resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), headerNames.get(ServiceConstants.CELL_INDEX_0), "STORE ID FIELD IS NOT NULL"));
                    }
                    StoreName storeNames = storeNameRepository.findByIsDeletedAndIsUsedAndSubOrganizationIdAndStoreName(false, false, loginUser.getSubOrgId(), storeName);

                    if (storeName.equals(storeName)) {
                        store.setStoreName(storeName);
                    } else {
                        resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), headerNames.get(ServiceConstants.CELL_INDEX_2), "THIS STORE NAME IS NOT VALID"));
                    }
                    if (validations.isDuplicateStoreName(null, erpStoreId)) {
                        store.setErpStoreId(erpStoreId);
                    } else {
                        resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), headerNames.get(ServiceConstants.CELL_INDEX_1), " THIS ERP ID IS ALREADY EXISTS PLEASE USE ANOTHER ERP ID"));
                    }
                    store.setStoreManagerName(storeManagerName);
                    store.setIsActive(true);
                    store.setIsDeleted(false);
                    store.setOrganizationId(loginUser.getOrgId());
                    store.setSubOrganizationId(loginUser.getSubOrgId());
                    store.setCreatedOn(new Date());
                    store.setModifiedOn(new Date());
                    stores.add(store);
                    count++;
                }
            }
            log.info(String.valueOf(new StringBuilder().append(logId).append(ServiceConstants.TOTAL_ROWS_SCANNED).append(count)));
            long endTime = System.currentTimeMillis();
            // Close the workbook
            workbook.close();
            if (resultResponses.size() == 0) {
                List<ModuleUserLicenceKey> moduleUserLicenceKeyList = userLicenseKeyRepository.findByIsDeletedAndIsUsedAndLicenceLineSubModuleSubModuleCodeAndLicenceLinePartNumberSubModuleMapperLicenseCategoryAndSubOrganizationId(false, false, "STOR", 3, loginUser.getSubOrgId());
                List<Store> storeList = storeRepository.findByIsDeletedAndOrganizationId(false, loginUser.getSubOrgId());
                if (stores.size() <= (moduleUserLicenceKeyList.size() - storeList.size())) {
                    this.storeRepository.saveAllAndFlush(stores);
                    for (Store store : stores) {
                        Optional<Users> users = userRepository.findByIsDeletedAndUsernameAndSubOrganizationId(false, store.getStoreManagerName(), loginUser.getSubOrgId());
                        StoreKeeperMapper storeKeeperMapper = new StoreKeeperMapper();
                        storeKeeperMapper.setStore(store);
                        storeKeeperMapper.setStoreKeeper(users.get());
                        storeKeeperMapper.setOrganizationId(loginUser.getOrgId());
                        storeKeeperMapper.setSubOrganizationId(loginUser.getSubOrgId());
                        storeKeeperMapper.setIsDeleted(false);
                        storeKeeperMapper.setCreatedBy(loginUser.getUserId());
                        storeKeeperMapper.setCreatedOn(new Date());
                        storeKeeperMapper.setModifiedBy(loginUser.getUserId());
                        storeKeeperMapper.setModifiedOn(new Date());
                        storeKeeperMapperRepository.save(storeKeeperMapper);
                        log.info("LogId:{} - StoreServiceImpl - saveStore - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), " STORE KEEPER MAPPED SUCCESSFULLY :");
                        for (Integer i = 1; i <= 4; i++) {
                            areaServices.createArea(store, i);
                        }
                    }
                } else {
                    BaseResponse baseResponse = new BaseResponse<>();
                    baseResponse.setCode(0);
                    baseResponse.setStatus(500);
                    baseResponse.setData(new ArrayList<>());
                    baseResponse.setMessage("YOU HAVE PURCHASED " + moduleUserLicenceKeyList.size() + " STORE LICENSES. FROM WHICH YOU HAVA ALREADY CREATED " + storeList.size() + " STORE HENCE YOU CANNOT ADD MORE THEN " + (moduleUserLicenceKeyList.size() - storeList.size()) + " STORES");
                    baseResponse.setLogId(loginUser.getLogId());
                    return ResponseEntity.ok(baseResponse);
                }
                log.info("LogId:{} - UploadExcelServiceImpl - uploadStoreDetail - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), ResponseKeyConstant.SPACE + ServiceConstants.UPLOAD_STORE_DETAIL_METHOD_EXECUTED + (endTime - startTime));

                return ResponseEntity.ok(new BaseResponse<>(HttpStatus.OK.value(), ServiceConstants.FILE_UPLOADED_SUCCESSFULLY, null, ServiceConstants.SUCCESS_CODE, logId));
            } else {
                log.error("LogId:{} - UploadExcelServiceImpl - uploadStoreDetail - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), ResponseKeyConstant.SPACE + ServiceConstants.STORE_DATA_UPLOAD_FAILED + (endTime - startTime));

                return ResponseEntity.ok(new BaseResponse<>(HttpStatus.OK.value(), ServiceConstants.STORE_DATA_UPLOAD_FAILED, resultResponses, ServiceConstants.ERROR_CODE, logId));
            }

        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            log.error("LogId:{} - UploadExcelServiceImpl - uploadStoreDetail - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), ResponseKeyConstant.SPACE + ServiceConstants.STORE_DATA_UPLOAD_FAILED + (endTime - startTime), e);
            ExceptionLogger.logException(e, logId);
            return ResponseEntity.ok(new BaseResponse<>(ServiceConstants.STATUS_CODE_500, ServiceConstants.STORE_DATA_UPLOAD_FAILED, null, ServiceConstants.ERROR_CODE, logId));
        }
    }

    @Override
    public ResponseEntity<BaseResponse> uploadDocksDetails(MultipartFile file, String type) throws IOException {
        long startTime = System.currentTimeMillis();
        log.info("LogId:{} - UploadExcelServiceImpl - uploadDocksDetails - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), ServiceConstants.SPACE + ServiceConstants.UPLOAD_DOCK_UPLOAD_METHOD_STARTED);

        try {
            // Read the Excel file and perform validation
            Workbook workbook = WorkbookFactory.create(file.getInputStream());
            Sheet sheet = workbook.getSheetAt(ServiceConstants.SHEET_INDEX); // Assuming the data is in the first sheet

            //List<Docks> items = new ArrayList<>();
            List<Dock> docks = new ArrayList<>();
            List<ValidationResultResponse> resultResponses = new ArrayList<>();
            List<String> headerNames = new ArrayList<>();
            List<StoreDockMapper> storeDockMappers = new ArrayList<>();
            Set<String> validateDockName = docksRepository.findByIsDeletedAndSubOrganizationId(false, loginUser.getSubOrgId()).stream().map(k -> k.getDockName()).collect(Collectors.toSet());
            Integer count = 0;
            boolean hasDataRows = false; // Flag to track if there are data rows
            List<String> expectedColumns = Arrays.asList(
                    ServiceConstants.DOCKS_NAME,
                    ServiceConstants.ATTRIBUTES,
                    ServiceConstants.DOCKS_SUPERVISOR,
                    ServiceConstants.DOCKS_SUPERVISOR_NAME,
                    ServiceConstants.STORE_ERP_CODE,
                    ServiceConstants.STORES_NAME
            );
            List<ExcellHeaderValidatorResponse> excellHeaderValidatorResponse = validateExcelHeader(sheet, expectedColumns);

            if (!excellHeaderValidatorResponse.get(0).getIsValid()) {
                return ResponseEntity.ok(new BaseResponse<>(ServiceConstants.INTERNAL_SERVER_ERROR, excellHeaderValidatorResponse.get(0).getErrorMessage(), excellHeaderValidatorResponse, ServiceConstants.ERROR_CODE, loginUser.getLogId()));
            }

            // Iterate through the first row to get the header names
            Row headerRow = sheet.getRow(ServiceConstants.HEADER_INDEX);
            for (int cellIndex = 0; cellIndex < headerRow.getLastCellNum(); cellIndex++) {
                Cell headerCell = headerRow.getCell(cellIndex, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                if (headerCell != null) {
                    String headerName = headerCell.getStringCellValue();
                    headerNames.add(headerName);
                }
            }
            int dk = 1;
            for (Row data : sheet) {
                int emptyCellCount = 0;
                int lastCellNum = data.getLastCellNum();
                if (lastCellNum != -1) {
                    for (int i = 0; i < data.getLastCellNum(); i++) {
                        Cell emptyCell = data.getCell(i, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                        if (emptyCell == null) {
                            emptyCellCount++;
                        }
                    }
                    if (emptyCellCount != data.getLastCellNum()) {
                        // Assuming the data starts from the second row (index 1)
                        if (data.getRowNum() <= ServiceConstants.ITEM_COLUMN_HEADER_ROW_INDEX) {
                            // Skip the header row
                            continue;
                        }
                        hasDataRows = true;
                        String dockName = getCellStringValue(data, ServiceConstants.CELL_INDEX_0, resultResponses, type, headerNames);
                        String attribute = getCellStringValue(data, ServiceConstants.CELL_INDEX_1, resultResponses, type, headerNames);
                        String dockSupervisor = getCellStringValue(data, ServiceConstants.CELL_INDEX_2, resultResponses, type, headerNames);
                        String dockSupervisorName = getCellStringValue(data, ServiceConstants.CELL_INDEX_3, resultResponses, type, headerNames);
                        String storeErpCode = getCellStringValue(data, ServiceConstants.CELL_INDEX_4, resultResponses, type, headerNames);
                        String storeName = getCellStringValue(data, ServiceConstants.CELL_INDEX_5, resultResponses, type, headerNames);
                        Dock dock = new Dock();

                        dock.setAttribute(attribute);
                        if (attribute != null) {
                            dock.setAttribute(attribute);
                        } else {
                            resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.ATTRIBUTE, ServiceConstants.ATTRIBUTE_IS_MANDATORY_FIELD_SHOULD_NOT_BE_NULL));
                        }
                        if (dockName != null) {
                            if (validateDockName.stream().noneMatch(existingDockName -> existingDockName.equalsIgnoreCase(dockName))) {
                                dock.setDockName(dockName);
                                validateDockName.add(dockName);
                            } else {
                                resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.DOCK_NAME, ServiceConstants.DOCK_NAME_DUPLICATE));
                            }
                        } else {
                            resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.DOCK_NAME, ServiceConstants.DOCK_NAME_IS_MANDATORY_FIELD_SHOULD_NOT_BE_NULL));
                        }
                        dock.setDockId(docksService.generateDockId(dk));
                        dk++;
                        dock.setIsDeleted(false);
                        dock.setCreatedBy(loginUser.getUserId());
                        dock.setCreatedOn(new Date());
                        dock.setModifiedOn(new Date());
                        dock.setModifiedBy(loginUser.getUserId());
                        dock.setOrganizationId(loginUser.getOrgId());
                        dock.setSubOrganizationId(loginUser.getSubOrgId());
                        Store storeERPCode = storeRepository.findBySubOrganizationIdAndIsDeletedAndErpStoreId(loginUser.getSubOrgId(), false, storeErpCode);
                        if (storeERPCode != null) {
                            StoreDockMapper storeDockMapper = new StoreDockMapper();
                            storeDockMapper.setDock(dock);
                            storeDockMapper.setStore(storeERPCode);
                            storeDockMapper.setIsDeleted(false);
                            storeDockMapper.setCreatedBy(loginUser.getUserId());
                            storeDockMapper.setCreatedOn(new Date());
                            storeDockMapper.setModifiedBy(loginUser.getUserId());
                            storeDockMapper.setModifiedOn(new Date());
                            storeDockMapper.setOrganizationId(loginUser.getOrgId());
                            storeDockMapper.setSubOrganizationId(loginUser.getSubOrgId());
                            storeDockMappers.add(storeDockMapper);
                        } else {
                            resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.STORE_ERP_CODE, ServiceConstants.DOCK_SUPERVISOR_NOT_FOUND));
                        }

                        docks.add(dock);
                        count++;
                    }
                }
            }
            // Close the workbook
            workbook.close();
            if (!hasDataRows) {
                return ResponseEntity.ok(new BaseResponse<>(ServiceConstants.STATUS_CODE_500, ServiceConstants.EXCEL_FILE_CONTAINS_HEADER_AND_NO_DATA, null, ServiceConstants.ERROR_CODE, loginUser.getLogId()));
            }
            long endTime = System.currentTimeMillis();
            log.info(String.valueOf(new StringBuilder().append(loginUser.getLogId()).append(ServiceConstants.TOTAL_ROWS_SCANNED).append(count)));
            if (resultResponses.size() == 0) {
                List<Dock> docks1 = this.docksRepository.saveAllAndFlush(docks);
                dockMapperRepository.saveAllAndFlush(storeDockMappers);
                //Staging Area
                for (Dock dock : docks1) {
                    StagingArea stagingArea = new StagingArea();
                    if (docks != null && dock.getId() != null) {
                        stagingArea.setStagingArea(dock.getDockName());
                        stagingArea.setStagingAreaId(dock.getDockId() + "-STG01");
                        stagingArea.setDock(dock);
                        stagingArea.setOrganizationId(loginUser.getOrgId());
                        stagingArea.setSubOrganizationId(loginUser.getSubOrgId());
                        stagingArea.setIsDeleted(false);
                        stagingArea.setCreatedOn(new Date());
                        stagingArea.setCreatedBy(loginUser.getUserId());
                    }
                    stagingAreaRepository.save(stagingArea);
                    //Accepted Staging Area
                    AcceptedRejectedStagingArea acceptedRejectedStagingArea = new AcceptedRejectedStagingArea();
                    acceptedRejectedStagingArea.setAcceptedRejectedCode(stagingArea.getStagingAreaId() + "-ARST-A-01");
                    acceptedRejectedStagingArea.setIsDeleted(false);
                    acceptedRejectedStagingArea.setOrganizationId(loginUser.getOrgId());
                    acceptedRejectedStagingArea.setSubOrganizationId(loginUser.getSubOrgId());
                    acceptedRejectedStagingArea.setStagingArea(stagingArea);
                    acceptedRejectedStagingArea.setCreatedOn(new Date());
                    acceptedRejectedStagingArea.setCreatedBy(loginUser.getUserId());
                    acceptedRejectedStagingArea.setIsAccepted(true);
                    acceptedRejectedStagingAreaRepository.save(acceptedRejectedStagingArea);
                    //Rejected Staging Area
                    AcceptedRejectedStagingArea acceptedRejectedStagingAreaF = new AcceptedRejectedStagingArea();
                    acceptedRejectedStagingAreaF.setAcceptedRejectedCode(stagingArea.getStagingAreaId() + "-ARST-R-01");
                    acceptedRejectedStagingAreaF.setIsDeleted(false);
                    acceptedRejectedStagingAreaF.setOrganizationId(loginUser.getOrgId());
                    acceptedRejectedStagingAreaF.setSubOrganizationId(loginUser.getSubOrgId());
                    acceptedRejectedStagingAreaF.setStagingArea(stagingArea);
                    acceptedRejectedStagingAreaF.setCreatedOn(new Date());
                    acceptedRejectedStagingAreaF.setCreatedBy(loginUser.getUserId());
                    acceptedRejectedStagingAreaF.setIsAccepted(false);
                    acceptedRejectedStagingAreaRepository.save(acceptedRejectedStagingAreaF);
                }
                log.info("LogId:{} - UploadExcelServiceImpl - uploadDocksDetails - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), ResponseKeyConstant.SPACE + ServiceConstants.UPLOAD_DOCK_METHOD_EXECUTED + (endTime - startTime));

                return ResponseEntity.ok(new BaseResponse<>(HttpStatus.OK.value(), ServiceConstants.FILE_UPLOADED_SUCCESSFULLY, docks1, ServiceConstants.SUCCESS_CODE, loginUser.getLogId()));
            } else {

                log.error("LogId:{} - UploadExcelServiceImpl - uploadDocksDetails - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), ResponseKeyConstant.SPACE + ServiceConstants.DOCK_DATA_UPLOAD_FAILED + (endTime - startTime));

                return ResponseEntity.ok(new BaseResponse<>(ServiceConstants.STATUS_CODE_500, ServiceConstants.DOCK_DATA_UPLOAD_FAILED, resultResponses, ServiceConstants.ERROR_CODE, loginUser.getLogId()));
            }
        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            log.error("LogId:{} - UploadExcelServiceImpl - uploadDocksDetails - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), ResponseKeyConstant.SPACE + ServiceConstants.DOCK_DATA_UPLOAD_FAILED + (endTime - startTime), e);
            ExceptionLogger.logException(e, loginUser.getLogId());
            return ResponseEntity.ok(new BaseResponse<>(ServiceConstants.STATUS_CODE_500, ServiceConstants.DOCK_DATA_UPLOAD_FAILED, null, ServiceConstants.ERROR_CODE, loginUser.getLogId()));
        }
    }

    @Override
    public ResponseEntity<BaseResponse> uploadPurchaseOrders(MultipartFile file, String type) throws IOException {
        String logId = loginUser.getLogId();
//        TransactionStatus transactionStatus = transactionManager.getTransaction(new DefaultTransactionDefinition());
        long startTime = System.currentTimeMillis();
        log.info("LogId:{} - UploadExcelServiceImpl - uploadPurchaseOrders - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), ServiceConstants.SPACE + ServiceConstants.UPLOAD_PURCHASE_ORDERS_METHOD_STARTED);

        try {
            // Read the Excel file and perform validation
            Workbook workbook = WorkbookFactory.create(file.getInputStream());
            Sheet sheet = workbook.getSheetAt(ServiceConstants.SHEET_INDEX); // Assuming the data is in the first sheet
            List<PurchaseOrderLine> purchaseOrderLineList = new ArrayList<>();
            List<ValidationResultResponse> resultResponses = new ArrayList<>();
            List<ItemSupplierMapperRequest> itemSupplierMapperRequests = new ArrayList<>();
            List<String> headerNames = new ArrayList<>();
            Integer count = 0;
            boolean hasDataRows = false; // Flag to track if there are data rows

            List<String> expectedColumns = Arrays.asList(
                    ServiceConstants.PURCHASE_ORDER_NUMBER,
                    ServiceConstants.PURCHASE_ORDER_DATE,
                    ServiceConstants.LINE_NUMBER,
                    ServiceConstants.P_ITEM_CODE,
                    ServiceConstants.P_ITEM_NAME,
                    ServiceConstants.P_UOM,
                    ServiceConstants.UNIT_PRICE,
                    ServiceConstants.P_LEAD_TIME,
                    ServiceConstants.P_LEAD_TIME_HRS,
                    ServiceConstants.PURCHASE_ORDER_QUANTITY,
                    ServiceConstants.SUB_TOTAL,
                    ServiceConstants.STATE_GST_PERCENTAGE,
                    ServiceConstants.STATE_GST,
                    ServiceConstants.CENTRAL_GST_PERCENTAGE,
                    ServiceConstants.CENTRAL_GST,
                    ServiceConstants.INTERSTATE_GST_PERCENTAGE,
                    ServiceConstants.INTERSTATE_GST,
                    ServiceConstants.TOTAL_AMOUNT,
                    ServiceConstants.DELIVER_TYPE,
                    ServiceConstants.DELIVER_BY_DATE,
                    ServiceConstants.P_SUPPLIER_ID,
                    ServiceConstants.P_SUPPLIER_NAME
            );
            List<ExcellHeaderValidatorResponse> excellHeaderValidatorResponse = validateExcelHeader(sheet, expectedColumns);
            if (!excellHeaderValidatorResponse.get(0).getIsValid()) {
                return ResponseEntity.ok(new BaseResponse<>(ServiceConstants.INTERNAL_SERVER_ERROR, excellHeaderValidatorResponse.get(0).getErrorMessage(), excellHeaderValidatorResponse, ServiceConstants.ERROR_CODE, logId));
            }

            // Iterate through the first row to get the header names
            Row headerRow = sheet.getRow(1);
            for (int cellIndex = 0; cellIndex < headerRow.getLastCellNum(); cellIndex++) {
                Cell headerCell = headerRow.getCell(cellIndex, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                if (headerCell != null) {
                    String headerName = headerCell.getStringCellValue();
                    headerNames.add(headerName);
                }
            }
            for (Row data : sheet) {
                int emptyCellCount = 0;
                for (int i = 0; i < data.getLastCellNum(); i++) {
                    Cell emptyCell = data.getCell(i, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                    if (emptyCell == null) {
                        emptyCellCount++;
                    }
                }
                if (emptyCellCount != data.getLastCellNum()) {
                    // Assuming the data starts from the second row
                    if (data.getRowNum() <= ServiceConstants.PURCHASE_COLUMN_HEADER_ROW_INDEX) {
                        // Skip the header row
                        continue;
                    }
                    hasDataRows = true;
                    String purchaseOrderNumber = getCellStringValue(data, ServiceConstants.CELL_INDEX_0, resultResponses, type, headerNames);
                    Date purchaseOrderDate = getCellDateValue(data, ServiceConstants.CELL_INDEX_1, resultResponses, type, headerNames);
                    Integer lineNumber = getCellIntegerValue(data, ServiceConstants.CELL_INDEX_2, resultResponses, type, headerNames);
                    String itemCode = getCellStringValue(data, ServiceConstants.CELL_INDEX_3, resultResponses, type, headerNames);
                    String itemName = getCellStringValue(data, ServiceConstants.CELL_INDEX_4, resultResponses, type, headerNames);
                    String uom = getCellStringValue(data, ServiceConstants.CELL_INDEX_5, resultResponses, type, headerNames);
                    Float unitPrice = getCellFloatValue(data, ServiceConstants.CELL_INDEX_6, resultResponses, type, headerNames);
                    Integer leadTimeDays = getCellIntegerValue(data, ServiceConstants.CELL_INDEX_7, resultResponses, type, headerNames);
                    Integer leadTimeHrs = getCellIntegerValue(data, ServiceConstants.CELL_INDEX_8, resultResponses, type, headerNames);
                    Float purchaseOrderQuantity = getCellFloatValue(data, ServiceConstants.CELL_INDEX_9, resultResponses, type, headerNames);
                    Float subTotal = getCellFloatValue(data, ServiceConstants.CELL_INDEX_10, resultResponses, type, headerNames);
                    Float stateGstPercent = getCellFloatValue(data, ServiceConstants.CELL_INDEX_11, resultResponses, type, headerNames);
                    Float stateGstAmount = getCellFloatValue(data, ServiceConstants.CELL_INDEX_12, resultResponses, type, headerNames);
                    Float centralGstPercent = getCellFloatValue(data, ServiceConstants.CELL_INDEX_13, resultResponses, type, headerNames);
                    Float centralGstAmount = getCellFloatValue(data, ServiceConstants.CELL_INDEX_14, resultResponses, type, headerNames);
                    Float interStateGstPercent = getCellFloatValue(data, ServiceConstants.CELL_INDEX_15, resultResponses, type, headerNames);
                    Float interStateGstAmount = getCellFloatValue(data, ServiceConstants.CELL_INDEX_16, resultResponses, type, headerNames);
                    Float totalAmount = getCellFloatValue(data, ServiceConstants.CELL_INDEX_17, resultResponses, type, headerNames);
                    String deliveryType = getCellStringValue(data, ServiceConstants.CELL_INDEX_18, resultResponses, type, headerNames);
                    Date deliverByDate = getCellDateValue(data, ServiceConstants.CELL_INDEX_19, resultResponses, type, headerNames);
                    String supplierId = getCellStringValue(data, ServiceConstants.CELL_INDEX_20, resultResponses, type, headerNames);
                    String supplierName = getCellStringValue(data, ServiceConstants.CELL_INDEX_21, resultResponses, type, headerNames);
                    // Create a new instance of PurchaseOrder and set its properties
                    PurchaseOrderHead purchaseOrderHead = new PurchaseOrderHead();

                    if (supplierName == null) {
                        resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.SUPPLIER_NAME, ServiceConstants.SUPPLIER_NAME_MANDATORY));
                    }
//
                    if (supplierId == null) {
                        resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.SUPPLIER_ID, ServiceConstants.SUPPLIER_ID_MANDATORY));
                    }
                    Optional<Supplier> supplierOptional = supplierRepository.findByIsDeletedAndSubOrganizationIdAndErpSupplierId(false, loginUser.getSubOrgId(), supplierId);
                    if (supplierOptional.isPresent()) {
                        if (!supplierOptional.get().getSupplierName().equals(supplierName)) {
                            resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.SUPPLIER_NAME, ServiceConstants.SUPPLIER_ID_AND_SUPPLIER_NAME_SHOULD_BE_CORRECT));
                        } else {
                            purchaseOrderHead.setSupplier(supplierOptional.get());
                        }
                    } else {
                        resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.P_SUPPLIER_ID, "SUPPLIER ID NOT PRESENT IN DATABASE"));
                    }

                    purchaseOrderHead.setPurchaseOrderId(validations.poIdGeneration());

                    if (purchaseOrderNumber == null) {
                        resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.PURCHASE_ORDER_NUMBER, ServiceConstants.PURCHASE_ORDER_NUMBER_MANDATORY));
                    }
//                    else if (!validateRegex(purchaseOrderNumber, ServiceConstants.ID_REGEX)) {
//                        resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.PURCHASE_ORDER_NUMBER, ServiceConstants.INVALID_PURCHASE_ORDER_NUMBER_FORMAT));
//                    }
                    purchaseOrderHead.setPurchaseOrderNumber(purchaseOrderNumber);

                    if (deliverByDate == null) {
                        resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.DELIVER_BY_DATE, ServiceConstants.DELIVERY_DATE_MANDATORY));
                    } else {
                        // Perform the financial year validation
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(purchaseOrderDate);

                        int year = cal.get(Calendar.YEAR);
                        int month = cal.get(Calendar.MONTH);

                        if (month < Calendar.APRIL) {
                            year--;
                        }

                        cal.set(year + 1, Calendar.MARCH, 31);  // Set to 31st March of the next year
                        Date expectedDeliverByDate = cal.getTime();

//                        if (!deliverByDate.equals(expectedDeliverByDate)) {
//                            resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.DELIVER_BY_DATE, "DELIVER BY DATE MUST BE 31-MAR-" + (year + 1)));
//                        }
                    }

                    purchaseOrderHead.setDeliverByDate(deliverByDate);

                    if (purchaseOrderDate == null) {
                        resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.PURCHASE_ORDER_DATE, ServiceConstants.PURCHASE_ORDER_DATE_MANDATORY));
                    }
                    purchaseOrderHead.setPurchaseOrderDate(purchaseOrderDate);

                    if (deliveryType == null || deliveryType.isEmpty()) {
                        resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.DELIVER_TYPE, ServiceConstants.DELIVERY_TYPE_MANDATORY));
                    }
//                    else if (!validateRegex(deliveryType, ServiceConstants.DELIVERY_TYPE_REGEX)) {
//                        resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.DELIVER_TYPE, ServiceConstants.INVALID_DELIVER_TYPE_FORMAT));
//                    }
                    purchaseOrderHead.setDeliveryType(deliveryType);

                    purchaseOrderHead.setIsActive(true);
                    purchaseOrderHead.setIsDeleted(false);
                    purchaseOrderHead.setSubOrganizationId(loginUser.getSubOrgId());
                    purchaseOrderHead.setOrganizationId(loginUser.getOrgId());
                    purchaseOrderHead.setCreatedBy(loginUser.getUserId());
                    purchaseOrderHead.setCreatedOn(new Date());
                    purchaseOrderHead.setModifiedBy(loginUser.getUserId());
                    purchaseOrderHead.setModifiedOn(new Date());
                    Optional<PurchaseOrderHead> orderHeadOptional = purchaseOrderHeadRepository.findByIsDeletedAndSubOrganizationIdAndPurchaseOrderNumber(false, loginUser.getSubOrgId(), purchaseOrderNumber);
                    if (orderHeadOptional.isEmpty() && resultResponses.size() == 0) {
                        purchaseOrderHeadRepository.save(purchaseOrderHead);
                    }
                    PurchaseOrderLine purchaseOrderLine = new PurchaseOrderLine();

                    if (purchaseOrderHead.getId() != null) {
                        purchaseOrderLine.setPurchaseOrderHead(purchaseOrderHead);
                    } else if (orderHeadOptional.isPresent() && resultResponses.size() == 0) {
                        purchaseOrderLine.setPurchaseOrderHead(orderHeadOptional.get());
                    } else {
                        resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.PURCHASE_ORDER_NUMBER, "PURCHASE ORDER NUMBER NOT PRESENT IN DATABASE"));
                    }

                    if (itemName == null || itemName.isEmpty()) {
                        resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.ITEM_NAME, ServiceConstants.ITEM_NAME_MANDATORY));
                    }
//                    else if (!validateRegex(itemName, ServiceConstants.NAME_FIRST_LETTER_CAPITAL_REGEX)) {
//                        resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.ITEM_NAME, ServiceConstants.INVALID_ITEM_NAME_FORMAT));
//                    }
                    Optional<Item> optionalItem = itemRepository.findByIsDeletedAndSubOrganizationIdAndItemCode(false, loginUser.getSubOrgId(), itemCode);
                    if (itemCode == null || itemCode.isEmpty()) {
                        resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.ITEM_ID, ServiceConstants.ITEM_ID_MANDATORY));
                    } else {
                        purchaseOrderLine.setItem(optionalItem.get());
                    }

                    // item And suplliar add in purchase
                    ItemSupplierMapperRequest itemSupplierMapperRequest = new ItemSupplierMapperRequest();
                    if (supplierOptional.isPresent()) {
                        itemSupplierMapperRequest.setSupplierId(supplierOptional.get().getId());
                    }
                    if (optionalItem.isPresent()) {
                        itemSupplierMapperRequest.setItemId(optionalItem.get().getId());
                    }
                    if (leadTimeHrs != null && leadTimeDays != null) {
                        resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.LEAD_DAYS, ServiceConstants.EITHER_ENTER_DAYS_AND_HRS));
                    } else if (leadTimeHrs == null && leadTimeDays == null) {
                        resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.LEAD_DAYS, ServiceConstants.PLEASE_ENTER_DAYS_AND_HRS_AT_TIME));
                    } else if (leadTimeHrs != null) {
                        itemSupplierMapperRequest.setIsDay(false);
                        itemSupplierMapperRequest.setLeadTime(leadTimeHrs);
                    } else {
                        itemSupplierMapperRequest.setIsDay(true);
                        itemSupplierMapperRequest.setLeadTime(leadTimeDays);
                    }
                    itemSupplierMapperRequests.add(itemSupplierMapperRequest);

//                  check null value
                    if (lineNumber == null) {
                        resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.LINE_NUMBER, ServiceConstants.LINE_NUMBER_MANDATORY));
                    }
                    purchaseOrderLine.setLineNumber(lineNumber);
//                  check null value
                    if (uom == null || uom.isEmpty()) {
                        resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.UOM, ServiceConstants.UOM_MANDATORY));
                    } else if (!validateRegex(uom, ServiceConstants.NOT_ALLOW_SPECIAL_CHAR_REGEX)) {
                        resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.UOM, ServiceConstants.INVALID_UOM_FORMAT));
                    }
                    purchaseOrderLine.setUom(uom);
//                  check null value
                    if (unitPrice == null || unitPrice <= 0) {
                        resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.UNIT_PRICE, ServiceConstants.UNIT_PRICE_MANDATORY));
                    } else if (!validateRegex(String.valueOf(unitPrice), ServiceConstants.INTEGER_REGEX)) {
                        resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.UNIT_PRICE, ServiceConstants.INVALID_UNIT_PRICE_FORMAT));
                    }
                    purchaseOrderLine.setUnitPrice(unitPrice);

                    if (purchaseOrderQuantity == null || purchaseOrderQuantity <= 0) {
                        resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.PURCHASE_ORDER_QUANTITY, ServiceConstants.PURCHASE_ORDER_QUANTITY_MANDATORY));
                    }

                    purchaseOrderLine.setPurchaseOrderQuantity(purchaseOrderQuantity);
                    purchaseOrderLine.setRemainingQuantity(purchaseOrderQuantity);

                    if (subTotal == null || subTotal <= 0) {
                        resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.SUB_TOTAL, ServiceConstants.SUB_TOTAL_MANDATORY));
                    } else if (unitPrice != null && purchaseOrderQuantity != null && subTotal != unitPrice * purchaseOrderQuantity) {
                        resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.SUB_TOTAL, ServiceConstants.SUBTOTAL_MUST_EQUAL_UNIT_PRICE_MULTIPLIED_BY_PURCHASE_ORDER_QUANTITY));
                    }
                    purchaseOrderLine.setSubTotalRs(subTotal);
                    purchaseOrderLine.setRemainingAmount(subTotal);
                    if (stateGstPercent != null && stateGstPercent != 0) {
                        if (interStateGstPercent != null && interStateGstPercent != 0) {
                            resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.INTERSTATE_GST_PERCENTAGE, "IF YOU ARE SELECT STATE GST SO CAN NOT SELECT INTER STATE GST"));
                        }
                        if (stateGstAmount != null && stateGstAmount != 0) {
                            purchaseOrderLine.setStateGSTPercentage(stateGstPercent);
                            float expectedStateGstAmount = subTotal * stateGstPercent / 100;
                            if (!stateGstAmount.equals(expectedStateGstAmount)) {
                                resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.STATE_GST, ServiceConstants.STATE_GST_AMOUNT_MUST_EQUAL_STATE_GST_PERCENT_OF_SUB_TOTAL));
                            }
                            purchaseOrderLine.setStateGSTAmount(stateGstAmount);
                        } else {
                            resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.STATE_GST, "STATE GST AMOUNT NOT BE NULL"));
                        }

                        purchaseOrderLine.setCentralGSTPercentage(centralGstPercent);

                        float expectedCentralGstAmount = subTotal * centralGstPercent / 100;
                        if (!centralGstAmount.equals(expectedCentralGstAmount)) {
                            resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.CENTRAL_GST, ServiceConstants.CENTRAL_GST_AMOUNT_MUST_EQUAL_CENTRAL_GST_PERCENT_OF_SUB_TOTAL));
                        }
                        purchaseOrderLine.setCentralGSTAmount(centralGstAmount);
                    } else {
                        if (interStateGstPercent != null && interStateGstPercent != 0) {
                            if (interStateGstAmount != null && interStateGstAmount != 0) {
                                purchaseOrderLine.setInterStateGSTPercentage(interStateGstPercent);
                                float expectedInterStateGstAmount = subTotal * interStateGstPercent / 100;
                                if (!interStateGstAmount.equals(expectedInterStateGstAmount)) {
                                    resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.INTERSTATE_GST, ServiceConstants.INTER_STATE_GST_AMOUNT_MUST_EQUAL_INTER_STATE_GST_PERCENT_OF_SUB_TOTAL));
                                }
                                purchaseOrderLine.setInterStateGSTAmount(interStateGstAmount);
                            } else {
                                resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.INTERSTATE_GST, " INTER STATE GST AMOUNT NOT BE NULL"));
                            }
                        } else {
                            resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.INTERSTATE_GST_PERCENTAGE, " INTER STATE GST NOT BE NULL"));
                        }
                    }
                    if (totalAmount == null || totalAmount <= 0) {
                        resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.TOTAL_AMOUNT, ServiceConstants.TOTAL_AMOUNT_MANDATORY));
                    }
                    if (totalAmount != null) {
                        float expectedTotalAmountWithStateGst = (stateGstAmount != null ? stateGstAmount : 0) + (centralGstAmount != null ? centralGstAmount : 0) + subTotal;
                        float expectedTotalAmountWithInterStateGst = subTotal + (interStateGstAmount != null ? interStateGstAmount : 0);
                        if (!totalAmount.equals(expectedTotalAmountWithStateGst) && !totalAmount.equals(expectedTotalAmountWithInterStateGst)) {
                            resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.TOTAL_AMOUNT, ServiceConstants.INCORRECT_TOTAL_AMOUNT));
                        }
                    }
                    purchaseOrderLine.setTotalAmountRs(totalAmount);
                    purchaseOrderLine.setIsDeleted(false);
                    purchaseOrderLine.setSubOrganizationId(loginUser.getSubOrgId());
                    purchaseOrderLine.setOrganizationId(loginUser.getOrgId());
                    purchaseOrderLine.setCreatedBy(loginUser.getUserId());
                    purchaseOrderLine.setCreatedOn(new Date());
                    purchaseOrderLine.setModifiedBy(loginUser.getUserId());
                    purchaseOrderLine.setModifiedOn(new Date());
                    purchaseOrderLineList.add(purchaseOrderLine);
                    // Add the PurchaseOrder object to the list
                }
            }
            long endTime = System.currentTimeMillis();
            // Close the workbook
            workbook.close();
            if (!hasDataRows) {
                log.info("LogId:{} - UploadExcelServiceImpl - uploadPurchaseOrders - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), ResponseKeyConstant.SPACE + ServiceConstants.EXCEL_FILE_CONTAINS_HEADER_AND_NO_DATA);
                return ResponseEntity.ok(new BaseResponse<>(ServiceConstants.STATUS_CODE_500, ServiceConstants.EXCEL_FILE_CONTAINS_HEADER_AND_NO_DATA, null, ServiceConstants.ERROR_CODE, loginUser.getLogId()));
            }
            if (resultResponses == null || resultResponses.size() == 0) {
                purchaseOrderLineRepository.saveAllAndFlush(purchaseOrderLineList);
                List<PurchaseOrderHead> purchaseOrderHeads = purchaseOrderHeadRepository.findByIsDeletedAndSubOrganizationId(false, loginUser.getSubOrgId());
                for (PurchaseOrderHead purchaseOrderHead : purchaseOrderHeads) {
                    List<PurchaseOrderLine> purchaseOrderLineList1 = purchaseOrderLineRepository.findByIsDeletedAndSubOrganizationIdAndPurchaseOrderHeadId(false, loginUser.getSubOrgId(), purchaseOrderHead.getId());
                    purchaseOrderHead.setTotalAmount(purchaseOrderLineList1.stream().mapToDouble(PurchaseOrderLine::getTotalAmountRs).sum());
                    purchaseOrderHeadRepository.save(purchaseOrderHead);
                }
                supplierService.mapItemBySupplier(itemSupplierMapperRequests);
                return ResponseEntity.ok(new BaseResponse<>(ServiceConstants.STATUS_CODE_200, ServiceConstants.PURCHASE_ORDER_DATA_UPLOAD_SUCCESSFULLY, resultResponses, ServiceConstants.ERROR_CODE, logId));
            } else {
                return ResponseEntity.ok(new BaseResponse<>(ServiceConstants.STATUS_CODE_500, ServiceConstants.PURCHASE_ORDER_DATA_UPLOAD_FAILED, resultResponses, ServiceConstants.ERROR_CODE, logId));
            }

        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            log.error("LogId:{} - UploadExcelServiceImpl - uploadPurchaseOrders - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), ResponseKeyConstant.SPACE + ServiceConstants.PURCHASE_ORDER_DATA_UPLOAD_FAILED + (endTime - startTime), e);
            //            transactionManager.rollback(transactionStatus);
            ExceptionLogger.logException(e, logId);
            return ResponseEntity.ok(new BaseResponse<>(ServiceConstants.STATUS_CODE_500, ServiceConstants.PURCHASE_ORDER_DATA_UPLOAD_FAILED, null, ServiceConstants.ERROR_CODE, logId));
        }
    }

    @Override
    public ResponseEntity<BaseResponse> uploadReasonDetails(MultipartFile file, String type) throws IOException {
        long startTime = System.currentTimeMillis();
        log.info("LogId:{} - UploadExcelServiceImpl - uploadReasonDetails - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), ServiceConstants.SPACE + ServiceConstants.UPLOAD_REASON_DETAIL_METHOD_STARTED);

        String logId = loginUser.getLogId();
        try {
            // Read the Excel file and perform validation
            Workbook workbook = WorkbookFactory.create(file.getInputStream());
            Sheet sheet = workbook.getSheetAt(ServiceConstants.SHEET_INDEX);
            List<Reason> reasons = new ArrayList<>();
            List<ValidationResultResponse> resultResponses = new ArrayList<>();
            List<String> headerNames = new ArrayList<>();
            Integer count = 0;
            Set<String> itemCodeSet = new HashSet<>();
            boolean hasDataRows = false; // Flag to track if there are data rows
            List<String> expectedColumns = Arrays.asList(
                    ServiceConstants.REJECTION_REASON,
                    ServiceConstants.REASON_CATEGORY
            );
            List<ExcellHeaderValidatorResponse> excellHeaderValidatorResponse = validateExcelHeader(sheet, expectedColumns);
            if (!excellHeaderValidatorResponse.get(0).getIsValid()) {
                return ResponseEntity.ok(new BaseResponse<>(ServiceConstants.INTERNAL_SERVER_ERROR, excellHeaderValidatorResponse.get(0).getErrorMessage(), excellHeaderValidatorResponse, ServiceConstants.ERROR_CODE, logId));
            }
            Row headerRow = sheet.getRow(ServiceConstants.HEADER_INDEX);
            for (int cellIndex = 0; cellIndex < headerRow.getLastCellNum(); cellIndex++) {
                Cell headerCell = headerRow.getCell(cellIndex, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                if (headerCell != null) {
                    String headerName = headerCell.getStringCellValue();
                    headerNames.add(headerName);
                }
            }
            int rs = 1;
            for (Row data : sheet) {
                //  parse the cell values and create Reason objects

                int emptyCellCount = 0;
                for (int i = 0; i < data.getLastCellNum(); i++) {
                    Cell emptyCell = data.getCell(i, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                    if (emptyCell == null) {
                        emptyCellCount++;
                    }
                }
                if (emptyCellCount != data.getLastCellNum()) {
                    // Assuming the data starts from the third row
                    if (data.getRowNum() <= ServiceConstants.REASON_COLUMN_HEADER_ROW_INDEX) {
                        // Skip the header row
                        continue;
                    }
                    hasDataRows = true;
                    String rejectionReason = getCellStringValue(data, ServiceConstants.CELL_INDEX_0, resultResponses, type, headerNames);
                    String reasonCategory = getCellStringValue(data, ServiceConstants.CELL_INDEX_1, resultResponses, type, headerNames);

                    Reason reason = new Reason();

                    reason.setReasonId(reasonService.generateReasonId(rs));
                    rs++;

                    if (rejectionReason == null) {
                        resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.REJECTION_REASON, ServiceConstants.REJECTION_REASON_MANDATORY));
                    } else if (!validateRegex(rejectionReason, ServiceConstants.NOT_ALLOW_SPECIAL_CHAR_REGEX)) {
                        resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.REJECTION_REASON, ServiceConstants.REJECTION_REASON_NOT_VALID));
                    }
                    reason.setRejectedReason(rejectionReason);

                    ReasonCategoryMaster categoryMaster = this.masterRepository.findByIsDeletedAndReasonCategoryName(false, reasonCategory);

                    if (reasonCategory == null) {
                        resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.REASON_CATEGORY, ServiceConstants.REASON_CATEGORY_MANDATORY));
                    } else if (!validateRegex(reasonCategory, ServiceConstants.NOT_ALLOW_SPECIAL_CHAR_REGEX)) {
                        resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.REASON_CATEGORY, ServiceConstants.REJECTION_CATEGORY_NOT_VALID));
                    } else if (categoryMaster == null) {
                        resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.REASON_CATEGORY, ServiceConstants.REASON_CATEGORY_NOT_FOUND));
                    }
                    reason.setReasonCategoryMaster(categoryMaster);
                    reason.setIsDeleted(false);
                    reason.setOrganizationId(loginUser.getOrgId());
                    reason.setSubOrganizationId(loginUser.getSubOrgId());
                    reason.setCreatedBy(loginUser.getUserId());
                    reason.setCreatedOn(new Date());
                    reason.setModifiedOn(new Date());
                    reason.setModifiedBy(loginUser.getUserId());

                    // Add the Reason object to the list
                    reasons.add(reason);

                    count++;
                }

            }
            log.info(String.valueOf(new StringBuilder().append(logId).append(ServiceConstants.TOTAL_ROWS_SCANNED).append(count)));

            long endTime = System.currentTimeMillis();
            // Close the workbook
            workbook.close();

            if (!hasDataRows) {
                log.info("LogId:{} - UploadExcelServiceImpl - uploadReasonDetails - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), ServiceConstants.SPACE + ServiceConstants.EXCEL_FILE_CONTAINS_HEADER_AND_NO_DATA);
                return ResponseEntity.ok(new BaseResponse<>(ServiceConstants.STATUS_CODE_500, ServiceConstants.EXCEL_FILE_CONTAINS_HEADER_AND_NO_DATA, null, ServiceConstants.ERROR_CODE, loginUser.getLogId()));
            }

            if (resultResponses.size() == 0) {
                // Save valid data to the repository
                this.reasonRepository.saveAllAndFlush(reasons);
                log.info("LogId:{} - UploadExcelServiceImpl - uploadReasonDetails - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), ResponseKeyConstant.SPACE + ServiceConstants.UPLOAD_REASON_DETAIL_METHOD_EXECUTED + (endTime - startTime));

                // Return success response
                return ResponseEntity.ok(new BaseResponse<>(HttpStatus.OK.value(), ServiceConstants.FILE_UPLOADED_SUCCESSFULLY, null, ServiceConstants.SUCCESS_CODE, logId));
            } else {
                log.error("LogId:{} - UploadExcelServiceImpl - uploadReasonDetails - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), ResponseKeyConstant.SPACE + ServiceConstants.REASON_DATA_UPLOAD_FAILED + (endTime - startTime));

                // Return response with validation errors
                return ResponseEntity.ok(new BaseResponse<>(HttpStatus.OK.value(), ServiceConstants.REASON_DATA_UPLOAD_FAILED, resultResponses, ServiceConstants.ERROR_CODE, logId));
            }
        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            log.error("LogId:{} - UploadExcelServiceImpl - uploadReasonDetails - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), ResponseKeyConstant.SPACE + ServiceConstants.REASON_DATA_UPLOAD_FAILED + (endTime - startTime), e);
            ExceptionLogger.logException(e, logId);
            return ResponseEntity.ok(new BaseResponse<>(ServiceConstants.STATUS_CODE_500, ServiceConstants.REASON_DATA_UPLOAD_FAILED, null, ServiceConstants.ERROR_CODE, logId));
        }
        //return null;
    }

    @Override
    public ResponseEntity<BaseResponse> uploadBomDetail(MultipartFile file, String type, String logId) throws IOException {
        long startTime = System.currentTimeMillis();
        log.info("LogId:{} - UploadExcelServiceImpl - uploadBomDetail - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), ServiceConstants.SPACE + ServiceConstants.UPLOAD_BOM_DETAIL_METHOD_STARTED);

        BoMHead boMHead = new BoMHead();
        try {
            // Read the Excel file and perform validation
            Workbook workbook = WorkbookFactory.create(file.getInputStream());
            Sheet sheet = workbook.getSheetAt(ServiceConstants.SHEET_INDEX); // Assuming the data is in the first sheet
            List<BOMLine> bomLines = new ArrayList<>();
            List<ValidationResultResponse> resultResponses = new ArrayList<>();
            List<String> headerNames = new ArrayList<>();
            Integer count = 0;
            List<String> expectedColumns = Arrays.asList(
                    ServiceConstants.LEVELS,
                    ServiceConstants.BOM_ITEM_CODES,
                    ServiceConstants.BOM_ITEM_NAME,
                    ServiceConstants.QUANTITY,
                    ServiceConstants.UNIT_OF_MEASURE,
                    ServiceConstants.CLASSABC,
                    ServiceConstants.STAGE,
                    ServiceConstants.BOM_ISSUE_TYPE,
                    ServiceConstants.DEPENDENCY,
                    ServiceConstants.REFERENCE_DESIGNATORS,
                    ServiceConstants.BOM_NOTES
            );
            List<ExcellHeaderValidatorResponse> excellHeaderValidatorResponse = validateBomExcelHeader(sheet, expectedColumns);
            if (!excellHeaderValidatorResponse.get(0).getIsValid()) {
                return ResponseEntity.ok(new BaseResponse<>(ServiceConstants.INTERNAL_SERVER_ERROR, excellHeaderValidatorResponse.get(0).getErrorMessage(), excellHeaderValidatorResponse, ServiceConstants.ERROR_CODE, logId));
            }
            Row headerRow = sheet.getRow(9);
            for (int cellIndex = 0; cellIndex < headerRow.getLastCellNum(); cellIndex++) {
                Cell headerCell = headerRow.getCell(cellIndex, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                if (headerCell != null) {
                    String headerName = headerCell.getStringCellValue();
                    headerNames.add(headerName);
                }
            }
            List<String> bomHeaderExpected = Arrays.asList(
                    ServiceConstants.PRODUCT,
                    ServiceConstants.MODEL,
                    ServiceConstants.VARIANT,
                    ServiceConstants.COLOUR,
                    ServiceConstants.BOM_IDS,
                    ServiceConstants.DATE,
                    ServiceConstants.VERSION,
                    ServiceConstants.ASSEMBLY_LINE,
                    ServiceConstants.LIFECYCLE_PHASE
            );
            Cell productH = sheet.getRow(0).getCell(0, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
            Cell modelH = sheet.getRow(1).getCell(0, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
            Cell variantH = sheet.getRow(2).getCell(0, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
            Cell colourH = sheet.getRow(3).getCell(0, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
            Cell boMIdH = sheet.getRow(4).getCell(0, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
            Cell dateH = sheet.getRow(5).getCell(0, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
            Cell versionH = sheet.getRow(6).getCell(0, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
            Cell assemblyLine = sheet.getRow(7).getCell(0, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
            Cell lifecyclePhaseH = sheet.getRow(8).getCell(0, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
            List<String> bomHeaderActual = Arrays.asList(
                    productH.getStringCellValue(),
                    modelH.getStringCellValue(),
                    variantH.getStringCellValue(),
                    colourH.getStringCellValue(),
                    boMIdH.getStringCellValue(),
                    dateH.getStringCellValue(),
                    versionH.getStringCellValue(),
                    assemblyLine.getStringCellValue(),
                    lifecyclePhaseH.getStringCellValue()
            );
            boolean isColumnsMatching = bomHeaderActual.size() == bomHeaderExpected.size() &&
                    IntStream.range(0, bomHeaderActual.size())
                            .allMatch(i -> bomHeaderActual.get(i).equalsIgnoreCase(bomHeaderExpected.get(i)));
            // Set validation result based on missing and extra columns
            List<ExcellHeaderValidatorResponse> validationResultList = new ArrayList<>();
            ExcellHeaderValidatorResponse validationResult = new ExcellHeaderValidatorResponse();
            if (!isColumnsMatching) {
                validationResult.setIsValid(false);
                validationResult.setErrorMessage("Uploaded Excel file header does not match the template.");
                validationResultList.add(validationResult);
                return (ResponseEntity<BaseResponse>) validationResultList;
            }
            String product = getStringValue(sheet.getRow(0).getCell(1, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL), resultResponses, type, 0, ServiceConstants.PRODUCT);
            String model = getStringValue(sheet.getRow(1).getCell(1, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL), resultResponses, type, 1, ServiceConstants.MODEL);
            String varient = getStringValue(sheet.getRow(2).getCell(1, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL), resultResponses, type, 2, ServiceConstants.VARIANT);
            String colour = getStringValue(sheet.getRow(3).getCell(1, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL), resultResponses, type, 3, ServiceConstants.COLOUR);
            String bomErpCode = getStringValue(sheet.getRow(4).getCell(1, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL), resultResponses, type, 3, ServiceConstants.BOM_IDS);

            if (sheet.getRow(4).getCell(1, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL) != null) {
                Optional<BoMHead> boMHeads = bomHeadRepository.findByIsDeletedAndSubOrganizationIdAndBomERPCode(false, loginUser.getSubOrgId(), bomErpCode);
                if (boMHeads.isEmpty()) {
                    boMHead.setBomERPCode(bomErpCode);
                } else {
                    resultResponses.add(new ValidationResultResponse(type, (sheet.getRow(4).getRowNum() + 1), ServiceConstants.BOM_IDS, "This BOM ERP CODE IS ALREADY PRESENT"));
                }
            }
            Date date = getDateValue(sheet.getRow(5).getCell(1, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL), resultResponses, type, 5, ServiceConstants.DATE);
            Float version = getFloatValue(sheet.getRow(6).getCell(1, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL), resultResponses, type, 6, ServiceConstants.VERSION);
            String assemblyLineId = getStringValue(sheet.getRow(7).getCell(1, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL), resultResponses, type, 8, ServiceConstants.LIFECYCLE_PHASE);
            AssemblyLine assemblyLine1 = assemblyLineRepository.findByIsDeletedAndSubOrganizationIdAndAssemblyLineId(false, loginUser.getSubOrgId(), assemblyLineId);
            String lifecyclePhase = getStringValue(sheet.getRow(8).getCell(1, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL), resultResponses, type, 8, ServiceConstants.LIFECYCLE_PHASE);
            if (resultResponses.size() == 0) {
                boMHead.setProduct(product);
                boMHead.setModel(model);
                boMHead.setVariant(varient);
                boMHead.setColour(colour);
                boMHead.setBomId(validations.bomIdGenerator());
                boMHead.setDate(date);
                boMHead.setVersion(version);
                boMHead.setAssemblyLine(assemblyLine1.getId());
                boMHead.setLifecyclePhase(lifecyclePhase);
                boMHead.setOrganizationId(loginUser.getOrgId());
                boMHead.setSubOrganizationId(loginUser.getSubOrgId());
                boMHead.setIsActive(true);
                boMHead.setIsDeleted(false);
                boMHead.setCreatedBy(loginUser.getUserId());
                boMHead.setCreatedOn(new Date());
                boMHead.setModifiedOn(new Date());
                boMHead.setModifiedBy(loginUser.getUserId());
                bomHeadRepository.save(boMHead);
            }

            for (Row data : sheet) {

                int emptyCellCount = 0;
                int lastCellNum = data.getLastCellNum();
                if (lastCellNum != -1) {
                    for (int i = 0; i < data.getLastCellNum(); i++) {
                        Cell emptyCell = data.getCell(i, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                        if (emptyCell == null) {
                            emptyCellCount++;
                        }
                    }
                    if (emptyCellCount != data.getLastCellNum()) {

                        // Assuming the data starts from the second row (index 1)
                        if (data.getRowNum() <= 9) {
                            // Skip the header row
                            continue;
                        }

                        Integer level = getCellIntegerValue(data, ServiceConstants.CELL_INDEX_0, resultResponses, type, headerNames);
                        String itemCode = getCellStringValue(data, ServiceConstants.CELL_INDEX_1, resultResponses, type, headerNames);
                        String itemName = getCellStringValue(data, ServiceConstants.CELL_INDEX_2, resultResponses, type, headerNames);
                        Float quantity = getCellFloatValue(data, ServiceConstants.CELL_INDEX_3, resultResponses, type, headerNames);
                        String uom = getCellStringValue(data, ServiceConstants.CELL_INDEX_4, resultResponses, type, headerNames);
                        String classABC = getCellStringValue(data, ServiceConstants.CELL_INDEX_5, resultResponses, type, headerNames);
                        String stage = getCellStringValue(data, ServiceConstants.CELL_INDEX_6, resultResponses, type, headerNames);
                        String issueType = getCellStringValue(data, ServiceConstants.CELL_INDEX_7, resultResponses, type, headerNames);
                        String dependency = getCellStringValue(data, ServiceConstants.CELL_INDEX_8, resultResponses, type, headerNames);
                        String referenceDesignators = getCellStringValue(data, ServiceConstants.CELL_INDEX_9, resultResponses, type, headerNames);
                        String bomNotes = getCellStringValue(data, ServiceConstants.CELL_INDEX_10, resultResponses, type, headerNames);
                        // Create a new Store object and set its properties
                        BOMLine bomLine = new BOMLine();


                        Optional<Stage> stageOptional = stageRepository.findByStageCodeAndAssemblyLineId(stage, assemblyLine1.getId());
                        if (stageOptional.isEmpty()) {
                            resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), headerNames.get(ServiceConstants.CELL_INDEX_6), "The Stage does not exist or is not linked to the given Assembly Line."));
                        } else {
                            bomLine.setStage(stageOptional.get().getStageCode());
                        }
                        bomLine.setLevel(level);
                        Optional<Item> itemOption = itemRepository.findByIsDeletedAndSubOrganizationIdAndItemCode(false, loginUser.getSubOrgId(), itemCode);
                        if (itemOption.isPresent()) {
                            bomLine.setItem(itemOption.get());
                        } else {
                            resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), headerNames.get(ServiceConstants.CELL_INDEX_3), "This Item not Present in Database"));
                        }
                        bomLine.setBomHeadId(boMHead);
                        bomLine.setQuantity(quantity);
                        bomLine.setUnitOfMeasure(uom);
                        bomLine.setClassType(classABC);
                        bomLine.setStage(stage);
                        bomLine.setIssueType(issueType);
                        bomLine.setDependency(dependency);
                        bomLine.setReferenceDesignators(referenceDesignators);
                        bomLine.setBomNotes(bomNotes);
                        bomLine.setOrganizationId(loginUser.getOrgId());
                        bomLine.setSubOrganizationId(loginUser.getSubOrgId());
                        bomLine.setIsActive(true);
                        bomLine.setIsDeleted(false);
                        bomLine.setCreatedOn(Timestamp.valueOf(LocalDateTime.now()));
                        bomLine.setModifiedOn(Timestamp.valueOf(LocalDateTime.now()));
                        bomLines.add(bomLine);
                        count++;
                    }
                }
            }
            log.info(String.valueOf(new StringBuilder().append(logId).append(ServiceConstants.TOTAL_ROWS_SCANNED).append(count)));
            long endTime = System.currentTimeMillis();
            // Close the workbook
            workbook.close();
            if (resultResponses.size() == 0) {
                bomLineRepository.saveAllAndFlush(bomLines);
                log.info("LogId:{} - UploadExcelServiceImpl - uploadBomDetail - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), ResponseKeyConstant.SPACE + ServiceConstants.UPLOAD_BOM_DETAIL_METHOD_EXECUTED + (endTime - startTime));

                return ResponseEntity.ok(new BaseResponse<>(HttpStatus.OK.value(), ServiceConstants.FILE_UPLOADED_SUCCESSFULLY, null, ServiceConstants.SUCCESS_CODE, logId));
            } else {
                log.error("LogId:{} - UploadExcelServiceImpl - uploadBomDetail - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), ResponseKeyConstant.SPACE + ServiceConstants.BOM_DATA_UPLOAD_FAILED + (endTime - startTime));
                bomHeadRepository.deleteById(boMHead.getId());
                return ResponseEntity.ok(new BaseResponse<>(HttpStatus.OK.value(), ServiceConstants.STORE_DATA_UPLOAD_FAILED, resultResponses, ServiceConstants.ERROR_CODE, logId));
            }
        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            log.error("LogId:{} - UploadExcelServiceImpl - uploadBomDetail - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), ResponseKeyConstant.SPACE + ServiceConstants.BOM_DATA_UPLOAD_FAILED + (endTime - startTime), e);
            ExceptionLogger.logException(e, logId);
//            bomHeadRepository.deleteById(boMHead.getId());
            return ResponseEntity.ok(new BaseResponse<>(ServiceConstants.STATUS_CODE_500, ServiceConstants.STORE_DATA_UPLOAD_FAILED, null, ServiceConstants.ERROR_CODE, logId));
        }
    }

    @Override
    public ResponseEntity<BaseResponse> uploadEquipmentDetail(MultipartFile file, String type, String logId) throws IOException {
        long startTime = System.currentTimeMillis();
        log.info("LogId:{} - UploadExcelServiceImpl - uploadEquipmentDetail - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), ServiceConstants.SPACE + ServiceConstants.UPLOAD_EQUIPMENT_DETAIL_METHOD_STARTED);


        try {
            // Read the Excel file and perform validation
            Workbook workbook = WorkbookFactory.create(file.getInputStream());
            Sheet sheet = workbook.getSheetAt(ServiceConstants.SHEET_INDEX); // Assuming the data is in the first sheet

            List<Equipment> equipments = new ArrayList<>();
            List<ValidationResultResponse> resultResponses = new ArrayList<>();
            List<String> headerNames = new ArrayList<>();
            Integer count = 0;

            Set<String> assetIdSet = new HashSet<>();

            boolean hasDataRows = false; // Flag to track if there are data rows

            List<String> expectedColumns = Arrays.asList(
                    ServiceConstants.EQUP_ASSET_ID,
                    ServiceConstants.EQUP_TROLLEY_TYPE,
                    ServiceConstants.EQUIPMENT_NAME,
                    ServiceConstants.EQUP_STORE_ID,
                    ServiceConstants.STORE_NAME
            );
            List<ExcellHeaderValidatorResponse> excellHeaderValidatorResponse = validateExcelHeader(sheet, expectedColumns);
            if (!excellHeaderValidatorResponse.get(0).getIsValid()) {
                return ResponseEntity.ok(new BaseResponse<>(ServiceConstants.INTERNAL_SERVER_ERROR, excellHeaderValidatorResponse.get(0).getErrorMessage(), excellHeaderValidatorResponse, ServiceConstants.ERROR_CODE, logId));
            }

            Row headerRow = sheet.getRow(ServiceConstants.HEADER_INDEX);
            for (int cellIndex = 0; cellIndex < headerRow.getLastCellNum(); cellIndex++) {
                Cell headerCell = headerRow.getCell(cellIndex, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                if (headerCell != null) {
                    String headerName = headerCell.getStringCellValue();
                    headerNames.add(headerName);
                }
            }
            int eq = 1;
            for (Row data : sheet) {

                int emptyCellCount = 0;
                int lastCellNum = data.getLastCellNum();
                if (lastCellNum != -1) {
                    for (int i = 0; i < data.getLastCellNum(); i++) {
                        Cell emptyCell = data.getCell(i, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                        if (emptyCell == null) {
                            emptyCellCount++;
                        }
                    }
                    if (emptyCellCount != data.getLastCellNum()) {

                        // Assuming the data starts from the second row (index 1)
                        if (data.getRowNum() <= ServiceConstants.ITEM_COLUMN_HEADER_ROW_INDEX) {
                            // Skip the header row
                            continue;
                        }
                        hasDataRows = true;
                        String assetId = getCellStringValue(data, ServiceConstants.CELL_INDEX_0, resultResponses, type, headerNames);
                        String trolleyType = getCellStringValue(data, ServiceConstants.CELL_INDEX_1, resultResponses, type, headerNames);
                        String equipmentName = getCellStringValue(data, ServiceConstants.CELL_INDEX_2, resultResponses, type, headerNames);
                        String storeErpCode = getCellStringValue(data, ServiceConstants.CELL_INDEX_3, resultResponses, type, headerNames);
                        String storeName = getCellStringValue(data, ServiceConstants.CELL_INDEX_4, resultResponses, type, headerNames);
                        // Create a new Store object and set its properties
                        Equipment equipment = new Equipment();
                        if (storeName == null || storeName.isEmpty()) {
                            resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.STORE_NAME, ServiceConstants.STORE_NAME_IS_MANDATORY));
                        } else if (!validateRegex(storeName, ServiceConstants.NOT_ALLOW_SPECIAL_CHAR_REGEX)) {
                            resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.STORE_NAME, ServiceConstants.INVALID_STORE_NAME_FORMAT));
                        }
                        if (storeErpCode == null || storeErpCode.isEmpty()) {
                            resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.EQUP_STORE_ID, ServiceConstants.STORE_ID_IS_MANDATORY));
                        } else {
                            Optional<Store> storeOptional = storeRepository.findByIsDeletedAndSubOrganizationIdAndErpStoreId(false, loginUser.getSubOrgId(), storeErpCode);
                            if (storeOptional.isPresent()) {
                                if (!storeOptional.get().getStoreName().equals(storeName)) {
                                    resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.STORE_NAME, ServiceConstants.STORE_ID_AND_STORE_NAME_SHOULD_BE_CORRECT));
                                }
                                equipment.setStore(storeOptional.get());
                            } else {
                                resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), headerNames.get(ServiceConstants.CELL_INDEX_3), ServiceConstants.STORE_ID_IS_NOT_PRESENT));
                            }
                        }


                        equipment.setTrolleyId(equipmentService.equipmentGenerator(eq));
                        eq++;

                        if (assetId == null || assetId.isEmpty()) {
                            resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.EQUP_ASSET_ID, ServiceConstants.EQUP_ASSET_ID_IS_MANDATORY));
                        } else if (!validateRegex(assetId, ServiceConstants.ID_REGEX)) {
                            resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.EQUP_ASSET_ID, ServiceConstants.INVALID_EQUP_ASSET_ID_FORMAT));
                        }
                        if (assetIdSet.contains(assetId)) {
                            resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.EQUP_ASSET_ID, ServiceConstants.DUPLICATE_EQUP_ASSET_ID));
                        } else {
                            assetIdSet.add(assetId);
                        }

                        List<Equipment> equipmentList = equipmentRepository.findByIsDeletedAndSubOrganizationId(false, loginUser.getSubOrgId());
                        if (!equipmentList.stream().anyMatch(e -> e.getAssetId() != null && e.getAssetId().equals(assetId))) {
                            equipment.setAssetId(assetId);
                        } else {
                            resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), headerNames.get(ServiceConstants.CELL_INDEX_1), ServiceConstants.ASSET_ID_IS_ALREADY_PRESENT));
                        }

                        if (trolleyType == null || trolleyType.isEmpty()) {
                            resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.EQUP_TROLLEY_TYPE, ServiceConstants.EQUIPMENT_TYPE_IS_MANDATORY));
                        } else if (!validateRegex(trolleyType, ServiceConstants.NAME_FIRST_LETTER_CAPITAL_REGEX)) {
                            resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.EQUP_TROLLEY_TYPE, ServiceConstants.INVALID_EQUP_TROLLEY_TYPE_FORMAT));
                        }

                        if (equipmentName == null) {
                            resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.EQUIPMENT_NAME, ServiceConstants.EQUIPMENT_NAME_IS_MANDATORY));
                        } else if (!validateRegex(equipmentName, ServiceConstants.NAME_FIRST_LETTER_CAPITAL_REGEX)) {
                            resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.EQUIPMENT_NAME, ServiceConstants.INVALID_EQUP_NAME_FORMAT));
                        }
                        equipment.setTrolleyType(trolleyType);
                        equipment.setEquipmentName(equipmentName);
                        equipment.setSubOrganizationId(loginUser.getSubOrgId());
                        equipment.setOrganizationId(loginUser.getOrgId());
                        equipment.setIsDeleted(false);
                        equipment.setCreatedOn(new Date());
                        equipment.setModifiedOn(new Date());
                        equipments.add(equipment);
                        count++;
                    }
                }
            }
            log.info(String.valueOf(logId + ServiceConstants.TOTAL_ROWS_SCANNED + count));
            long endTime = System.currentTimeMillis();
            // Close the workbook
            workbook.close();

            if (!hasDataRows) {
                log.info("LogId:{} - UploadExcelServiceImpl - uploadEquipmentDetail - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), ServiceConstants.SPACE + ServiceConstants.EXCEL_FILE_CONTAINS_HEADER_AND_NO_DATA);
                return ResponseEntity.ok(new BaseResponse<>(ServiceConstants.STATUS_CODE_500, ServiceConstants.EXCEL_FILE_CONTAINS_HEADER_AND_NO_DATA, null, ServiceConstants.ERROR_CODE, loginUser.getLogId()));
            }
            if (resultResponses.size() == 0) {
                this.equipmentRepository.saveAllAndFlush(equipments);

                log.info("LogId:{} - UploadExcelServiceImpl - uploadEquipmentDetail - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), ResponseKeyConstant.SPACE + ServiceConstants.UPLOAD_EQUIPMENT_DETAIL_METHOD_EXECUTED + (endTime - startTime));

                return ResponseEntity.ok(new BaseResponse<>(HttpStatus.OK.value(), ServiceConstants.FILE_UPLOADED_SUCCESSFULLY.toUpperCase(), null, ServiceConstants.SUCCESS_CODE, logId));
            } else {
                log.error("LogId:{} - UploadExcelServiceImpl - uploadEquipmentDetail - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), ResponseKeyConstant.SPACE + ServiceConstants.EQUIPMENT_DATA_UPLOAD_FAILED + (endTime - startTime));

                return ResponseEntity.ok(new BaseResponse<>(HttpStatus.OK.value(), ServiceConstants.EQUIPMENT_DATA_UPLOAD_FAILED, resultResponses, ServiceConstants.ERROR_CODE, logId));
            }

        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            log.error("LogId:{} - UploadExcelServiceImpl - uploadEquipmentDetail - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), ResponseKeyConstant.SPACE + ServiceConstants.EQUIPMENT_DATA_UPLOAD_FAILED + (endTime - startTime), e);
            ExceptionLogger.logException(e, logId);
            return ResponseEntity.ok(new BaseResponse<>(ServiceConstants.STATUS_CODE_500, ServiceConstants.EQUIPMENT_DATA_UPLOAD_FAILED, null, ServiceConstants.ERROR_CODE, logId));
        }
    }

    //Added BY Kamlesh
    @Override
    public ResponseEntity<BaseResponse> uploadPpeDetails(MultipartFile file, String type) throws IOException, ValidationFailureException {
        long startTime = System.currentTimeMillis();
        log.info("LogId:{} - UploadExcelServiceImpl - uploadPpeDetails - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), ServiceConstants.SPACE + ServiceConstants.UPLOAD_PPE_DETAIL_METHOD_STARTED);

        try {
            Workbook workbook = WorkbookFactory.create(file.getInputStream());
            Sheet sheet = workbook.getSheetAt(ServiceConstants.SHEET_INDEX); // Assuming the data is in the first sheet

            List<PPEHead> ppePlans = new ArrayList<>();
            List<PPELine> ppeLineList = new ArrayList<>();
            List<ValidationResultResponse> resultResponses = new ArrayList<>();
            List<String> headerNames = new ArrayList<>();
            Integer count = 0;

            List<String> expectedColumns = Arrays.asList(PPE_PLAN_ID, ERP_ID, BOM_ID, PRODUCT_NAME,
                    BRAND, MODEL, VARIANT, COLOR, UOM1, PLAN_QUANTITY, PRODUCTION_SHOP, SHOP_ID, LINE,
                    LINE_ID, START_DATE, START_TIME, END_DATE, END_TIME, ITEM_CODE_PPE, ITEM_NAME_PPE,
                    ITEM_TYPE, ITEM_CLASS_PPE, ATTRIBUTE_PPE
            );
            List<ExcellHeaderValidatorResponse> excellHeaderValidatorResponse = validateExcelHeader(sheet, expectedColumns);

            if (!excellHeaderValidatorResponse.get(0).getIsValid()) {
                return ResponseEntity.ok(new BaseResponse<>(ServiceConstants.INTERNAL_SERVER_ERROR, excellHeaderValidatorResponse.get(0).getErrorMessage(), excellHeaderValidatorResponse, ServiceConstants.ERROR_CODE, loginUser.getLogId()));
            }

            String lastPpeId = null;
            List<PPEHead> existingPpeHeads = ppeHeadRepository.findByIsDeletedAndSubOrganizationIdOrderByIdAsc(false, loginUser.getSubOrgId());
            if (existingPpeHeads != null && !existingPpeHeads.isEmpty()) {
                lastPpeId = existingPpeHeads.get(existingPpeHeads.size() - 1).getPpeId();
            }

            // Iterate through the first row to get the header names
            Row headerRow = sheet.getRow(ServiceConstants.HEADER_INDEX);
            for (int cellIndex = 0; cellIndex < headerRow.getLastCellNum(); cellIndex++) {
                Cell headerCell = headerRow.getCell(cellIndex, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                if (headerCell != null) {
                    String headerName = headerCell.getStringCellValue();
                    headerNames.add(headerName);
                }
            }
            Map<String, List<String>> headLineMap = new HashMap<>();
            for (Row data : sheet) {

                int emptyCellCount = 0;
                int lastCellNum = data.getLastCellNum();
                if (lastCellNum != -1) {
                    for (int i = 0; i < data.getLastCellNum(); i++) {
                        Cell emptyCell = data.getCell(i, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                        if (emptyCell == null) {
                            emptyCellCount++;
                        }
                    }
                    if (emptyCellCount != data.getLastCellNum()) {

                        // Assuming the data starts from the second row (index 1)
                        if (data.getRowNum() <= ServiceConstants.ITEM_COLUMN_HEADER_ROW_INDEX) {
                            // Skip the header row
                            continue;
                        }

                        String planId = getCellStringValue(data, ServiceConstants.CELL_INDEX_0, resultResponses, type, headerNames);
                        String sapId = getCellStringValue(data, ServiceConstants.CELL_INDEX_1, resultResponses, type, headerNames);
                        String bomCode = getCellStringValue(data, ServiceConstants.CELL_INDEX_2, resultResponses, type, headerNames);
                        String productName = getCellStringValue(data, ServiceConstants.CELL_INDEX_3, resultResponses, type, headerNames);
                        String brand = getCellStringValue(data, ServiceConstants.CELL_INDEX_4, resultResponses, type, headerNames);
                        String model = getCellStringValue(data, ServiceConstants.CELL_INDEX_5, resultResponses, type, headerNames);
                        String variant = getCellStringValue(data, ServiceConstants.CELL_INDEX_6, resultResponses, type, headerNames);
                        String color = getCellStringValue(data, ServiceConstants.CELL_INDEX_7, resultResponses, type, headerNames);
                        String uom1 = getCellStringValue(data, ServiceConstants.CELL_INDEX_8, resultResponses, type, headerNames);
                        Integer planQunatity = getCellIntegerValue(data, ServiceConstants.CELL_INDEX_9, resultResponses, type, headerNames);
                        String productionShop = getCellStringValue(data, ServiceConstants.CELL_INDEX_10, resultResponses, type, headerNames);
                        String shopId = getCellStringValue(data, ServiceConstants.CELL_INDEX_11, resultResponses, type, headerNames);
                        String line = getCellStringValue(data, ServiceConstants.CELL_INDEX_12, resultResponses, type, headerNames);
                        String lineID = getCellStringValue(data, ServiceConstants.CELL_INDEX_13, resultResponses, type, headerNames);
                        Date startDate = getCellDateValue(data, ServiceConstants.CELL_INDEX_14, resultResponses, type, headerNames);
                        LocalTime starTime = getCellTimeValueForPPE(data, ServiceConstants.CELL_INDEX_15, resultResponses, type, headerNames);
                        Date endDate = getCellDateValueForPPE(data, ServiceConstants.CELL_INDEX_16, resultResponses, type, headerNames);
                        LocalTime endTime = getCellTimeValueForPPE(data, ServiceConstants.CELL_INDEX_17, resultResponses, type, headerNames);
                        String itemId = getCellStringValue(data, ServiceConstants.CELL_INDEX_18, resultResponses, type, headerNames);
                        String itemName = getCellStringValue(data, ServiceConstants.CELL_INDEX_19, resultResponses, type, headerNames);
                        String itemType = getCellStringValue(data, ServiceConstants.CELL_INDEX_20, resultResponses, type, headerNames);
                        String itemClass = getCellStringValue(data, ServiceConstants.CELL_INDEX_21, resultResponses, type, headerNames);
                        String attribute = getCellStringValue(data, ServiceConstants.CELL_INDEX_22, resultResponses, type, headerNames);

                        //setting the values to ppehead
                        PPEHead ppeHead = new PPEHead();
                        PPELine ppeLine = new PPELine();

                        // Generate and set the next PPE ID
                        lastPpeId = validations.generateNextPpeId(lastPpeId);
                        ppeHead.setPpeId(lastPpeId);
                        if (ppeHead.getPpeId().equals(null)) {
                            resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.PPE_ID, " PPEID CANNOT BE NULL "));
                        }

                        Optional<PPEHead> ppeHead1 = ppeHeadRepository.findByIsDeletedAndSubOrganizationIdAndPlanOrderId(false, loginUser.getSubOrgId(), planId);

                        if (!StringUtils.isEmpty(planId)) {
                            if(ppeHead1.isPresent()){
                                resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.PPE_PLAN_ID, " Plan Id Already Exist in Database "));
                            } else {
                                ppeHead.setPlanOrderId(planId);
                            }
                        } else {
                            resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.PPE_PLAN_ID, " PLANID CANNOT BE NULL "));
                        }

                        if (!StringUtils.isEmpty(sapId)) {
                            ppeHead.setSapId(sapId);
                        } else {
                            resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.ERP_ID, " SAPID CANNOT BE NULL "));
                        }

                        if (!StringUtils.isEmpty(bomCode)) {
                            Optional<BoMHead> boMHeadOptional = bomHeadRepository.findByIsDeletedAndSubOrganizationIdAndBomERPCode(false, loginUser.getSubOrgId(), bomCode);
                            if (boMHeadOptional.isPresent()) {
                                ppeHead.setBomHead(boMHeadOptional.get());
                            } else {
                                resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.BOM_ID, "THIS BOM ID IS NOT PRESENT IN DATABASE "));
                            }
                        } else {
                            resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.BOM_ID, " BOMID CANNOT BE NULL "));
                        }

                        if (!StringUtils.isEmpty(productName)) {
                            ppeHead.setProduct(productName);
                        } else {
                            resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.PRODUCT_NAME, " PRODUCT NAME CANNOT BE NULL "));
                        }

                        if (!StringUtils.isEmpty(brand)) {
                            ppeHead.setBrand(brand);
                        } else {
                            resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.BRAND, " BRAND NAME CANNOT BE NULL "));
                        }

                        if (!StringUtils.isEmpty(model)) {
                            ppeHead.setModel(model);
                        } else {
                            resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.MODEL, " MODEL NAME CANNOT BE NULL "));
                        }

                        if (!StringUtils.isEmpty(variant)) {
                            ppeHead.setVariant(variant);
                        } else {
                            resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.VARIANT, " VARIANT NAME CANNOT BE NULL "));
                        }

                        if (!StringUtils.isEmpty(color)) {
                            ppeHead.setColor(color);
                        } else {
                            resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.BOM_ID, " COLOR CANNOT BE NULL "));
                        }

                        if (!StringUtils.isEmpty(uom1)) {
                            ppeHead.setUom(uom1);
                        } else {
                            resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.UOM1, " UOM CANNOT BE NULL "));
                        }

                        if (planQunatity != null) {
                            ppeHead.setPlanQuantity(planQunatity);
                        } else {
                            resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.PLAN_QUANTITY, " PLAN QUANTITY CANNOT BE NULL "));
                        }

                        if (!StringUtils.isEmpty(productionShop)) {
                            ppeHead.setProductionShop(productionShop);
                        } else {
                            resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.PRODUCTION_SHOP, " PRODUCTION SHOP CANNOT BE NULL "));
                        }

                        if (!StringUtils.isEmpty(shopId)) {
                            ppeHead.setShopId(shopId);
                        } else {
                            resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.SHOP_ID, " SHOPID SHOP CANNOT BE NULL "));
                        }

                        if (!StringUtils.isEmpty(line)) {
                            ppeHead.setLine(line);
                        } else {
                            resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.LINE, " Line SHOP CANNOT BE NULL "));
                        }

                        if (!StringUtils.isEmpty(lineID)) {
                            AssemblyLine assemblyLine = assemblyLineRepository.findByIsDeletedAndSubOrganizationIdAndAssemblyLineId(false, loginUser.getSubOrgId(), lineID);
                            ppeHead.setAssemblyLine(assemblyLine);
                        } else {
                            resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.LINE_ID, " Line Id CANNOT BE NULL "));
                        }
//                        if (startDate != null) {
//                            Date currentDate = new Date();
//                            if (startDate.after(currentDate)) {
//                                ppeHead.setStartDate(startDate);
//                            } else {
//                                resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.START_DATE, "PLAN START DATE MUST BE A FUTURE DATE"));
//                            }
//                        } else {
//                            resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.START_DATE, " START DATE SHOP CANNOT BE NULL "));
//                        }
//                        if (starTime != null) {
//                            ppeHead.setStartTime(starTime);
//                        }

                        if (startDate != null && starTime != null) {
                            try {
                                // Define UTC zone
                                ZoneId utcZone = ZoneId.of("UTC");

                                // Convert startDate (java.util.Date) to LocalDate
                                LocalDate startLocalDate = startDate.toInstant()
                                        .atZone(utcZone)
                                        .toLocalDate();

                                // starTime is already a LocalTime (from your getCellTimeValueForPPE)
                                LocalDateTime planStartDateTime = LocalDateTime.of(startLocalDate, starTime);

                                // Convert to ZonedDateTime in UTC
                                ZonedDateTime planStartUTC = planStartDateTime.atZone(utcZone);

                                // Current UTC time
                                ZonedDateTime currentUTC = ZonedDateTime.now(utcZone);

                                // Compare
                                if (planStartUTC.isAfter(currentUTC)) {
                                    // Valid — save values
                                    ppeHead.setStartDate(startDate);
                                    ppeHead.setStartTime(Time.valueOf(starTime)); // convert LocalTime → java.sql.Time if needed
                                } else {
                                    resultResponses.add(new ValidationResultResponse(
                                            type,
                                            (data.getRowNum() + 1),
                                            ServiceConstants.START_DATE,
                                            "PLAN START DATE & TIME MUST BE IN THE FUTURE"
                                    ));
                                }
                            } catch (Exception e) {
                                resultResponses.add(new ValidationResultResponse(
                                        type,
                                        (data.getRowNum() + 1),
                                        ServiceConstants.START_DATE,
                                        "ERROR PROCESSING START DATE/TIME"
                                ));
                            }
                        }

                        // Validate that start date/time is not after end date/time
                        if (startDate != null && starTime != null && endDate != null && endTime != null) {

                            ZoneOffset utcZone = ZoneOffset.UTC;

                            // Convert start date/time to UTC
                            LocalDate startLocalDate = startDate.toInstant().atZone(utcZone).toLocalDate();
                            LocalTime startLocalTime = starTime.atOffset(ZoneOffset.UTC).toLocalTime();
                            LocalDateTime startDateTime = LocalDateTime.of(startLocalDate, startLocalTime);

                            // Convert end date/time to UTC
                            LocalDate endLocalDate = endDate.toInstant().atZone(utcZone).toLocalDate();
                            LocalTime endLocalTime = endTime.atOffset(ZoneOffset.UTC).toLocalTime();
                            LocalDateTime endDateTime = LocalDateTime.of(endLocalDate, endLocalTime);

                            // Compare
                            if (startDateTime.isAfter(endDateTime)) {
                                resultResponses.add(new ValidationResultResponse(
                                        type,
                                        (data.getRowNum() + 1),
                                        ServiceConstants.END_DATE,
                                        "START DATE & TIME CANNOT BE AFTER END DATE & TIME"
                                ));
                            }
                        }

                        if (endDate != null) {
                            ppeHead.setEndDate(endDate);
                        }

                        if (endTime != null) {
                            ppeHead.setEndTime(Time.valueOf(endTime)); // endTime is also LocalTime
                        }


                        PpeStatus status = ppeStatusRepository.findByIsDeletedAndStatusName(false, "Uploaded");
                        ppeHead.setPpeStatus(status);

                        ppeHead.setOrganizationId(loginUser.getOrgId());
                        ppeHead.setSubOrganizationId(loginUser.getSubOrgId());
                        ppeHead.setIsDeleted(false);
                        ppeHead.setCreatedBy(loginUser.getUserId());
                        ppeHead.setCreatedOn(new Date());
                        ppeHead.setModifiedBy(loginUser.getUserId());
                        ppeHead.setModifiedOn(new Date());

                        BOMLine bomLine = bomLineRepository.findByIsDeletedAndSubOrganizationIdAndItemItemCodeAndBomHeadIdBomERPCode(false, loginUser.getSubOrgId(), itemId, bomCode);
                        if (bomLine != null) {
                            ppeLine.setBomLine(bomLine);
                            ppeLine.setRequiredQuantity(bomLine.getQuantity() * planQunatity);
                        } else {
                            resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.ITEM_ID, " BOM ItemId and PPE ItemId is not matched "));
                        }

                        Date planstartTime = null;
                        Date startTimeDate = null;
                        if (ppeHead.getStartTime() != null) {
                            planstartTime = ppeHead.getStartTime();
                            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                            String formattedstartTime = sdf.format(planstartTime);
                            try {

                                startTimeDate = sdf.parse(formattedstartTime);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        } else {
                            startTimeDate = null;
                        }

                        Optional<PPEHead> headOptional = ppeHeadRepository.findByIsDeletedAndSubOrganizationIdAndPlanOrderId(false, loginUser.getSubOrgId(), planId);

                        Optional<PPEHead> existingHeads = ppeHeadRepository.findByIsDeletedAndStartDateAndStartTimeAndAssemblyLineId(false, ppeHead.getStartDate(), startTimeDate, ppeHead.getAssemblyLine().getId());

                        Optional<PPEHead> duplicateInListByPlanId = ppePlans.stream().filter(existingPpeHead ->
                                existingPpeHead.getPlanOrderId().equals(planId)
                        ).findFirst();

                        Boolean duplicateInList = false;

                        for (PPEHead existingPpeHead : ppePlans) {
                            if (!existingPpeHead.getPlanOrderId().equals(ppeHead.getPlanOrderId()) && Objects.equals(existingPpeHead.getStartDate(), ppeHead.getStartDate()) &&
                                    existingPpeHead.getStartTime().equals(ppeHead.getStartTime()) &&
                                    existingPpeHead.getAssemblyLine().equals(ppeHead.getAssemblyLine())) {
                                duplicateInList = true;
                            }
                        }


                        if (headOptional.isPresent()) {
                            continue;
                        } else if (existingHeads.isPresent() || duplicateInList) {
                            resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.PPE_PLAN_ID, " This Plan is already mapped with existing plan's Production Shop, Line, Start Date and Start Time "));
                        }
                        List<PPELine> ppeLines = ppeLineRepository.findByIsDeletedAndSubOrganizationIdAndItemItemIdAndPPEHeadPlanOrderId(false, loginUser.getSubOrgId(), itemId, planId);
                        if (ppeLines.size() > 0) {
                            resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.BOM_ID, " This Item Is Already Exist In This Plan "));
                        } else {
                            Optional<Item> itemOption = itemRepository.findByIsDeletedAndSubOrganizationIdAndItemCode(false, loginUser.getSubOrgId(), itemId);
                            if (itemOption.isPresent()) {
                                ppeLine.setItem(itemOption.get());
                                List<Location> locationList =  locationRepository.findByIsDeletedAndSubOrganizationIdAndItemId(false, loginUser.getSubOrgId(), itemOption.get().getId());
                                ppeLine.setStore(locationList.get(0).getZone().getArea().getStore().getStoreName());
//                                ppeLine.setEta(itemOption.get().getLeadTime());

                            } else {
                                resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.ITEM_ID, "THIS ITEM ID IS NOT PRESENT IN DATABASE"));
                            }

                        }
                        ppeLine.setOrganizationId(loginUser.getOrgId());
                        ppeLine.setSubOrganizationId(loginUser.getSubOrgId());
                        ppeLine.setRequiredBy(new Date());
                        ppeLine.setIsDeleted(false);
                        ppeLine.setCreatedBy(loginUser.getUserId());
                        ppeLine.setCreatedOn(new Date());
                        ppeLine.setModifiedBy(loginUser.getUserId());
                        ppeLine.setModifiedOn(new Date());
                        if (duplicateInListByPlanId.isEmpty()) {
                            ppeLine.setPPEHead(ppeHead);
                            ppePlans.add(ppeHead);
                        } else {
                            ppeLine.setPPEHead(duplicateInListByPlanId.get());
                        }

                        ppeLineList.add(ppeLine);


                        count++;
                    }
                }

            }
            log.info(String.valueOf(new StringBuilder().append(loginUser.getLogId()).append(ServiceConstants.TOTAL_ROWS_SCANNED).append(count)));

            long endTime = System.currentTimeMillis();
            // Close the workbook
            workbook.close();
            for (PPEHead ppeHead : ppePlans) {
                List<String> ppeItemIdList = ppeLineList.stream()
                        .filter(e -> e.getPPEHead().getPlanOrderId().equals(ppeHead.getPlanOrderId()))
                        .map(e -> e.getItem().getItemCode())
                        .collect(Collectors.toList());
                List<BOMLine> extraBomLine = bomLineRepository.findByIsDeletedAndSubOrganizationIdAndItemItemCodeNotInAndBomHeadIdBomERPCode(false, loginUser.getSubOrgId(), ppeItemIdList, ppeHead.getBomHead().getBomERPCode());
                if (extraBomLine.size() != 0) {
                    List<String> itemids = extraBomLine.stream().map(e -> e.getItem().getItemId()).collect(Collectors.toList());
                    resultResponses.add(new ValidationResultResponse(type, null, ServiceConstants.ITEM_ID, "THIS BOM ITEM IDS :" + itemids.toString() + "IS NOT PRESENT IN PLAN/ORDER: " + ppeHead.getPlanOrderId()));
                }
            }
            if (resultResponses.size() == 0) {
                this.ppeHeadRepository.saveAll(ppePlans);
                this.ppeLineRepository.saveAll(ppeLineList);

                log.error("LogId:{} - UploadExcelServiceImpl - uploadPpeDetails - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), ResponseKeyConstant.SPACE + ServiceConstants.UPLOAD_PPE_DETAIL_METHOD_EXECUTED + (endTime - startTime));

                return ResponseEntity.ok(new BaseResponse<>(HttpStatus.OK.value(), ServiceConstants.FILE_UPLOADED_SUCCESSFULLY, null, ServiceConstants.SUCCESS_CODE, loginUser.getLogId()));
            } else {
                log.error("LogId:{} - UploadExcelServiceImpl - uploadPpeDetails - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), ResponseKeyConstant.SPACE + ServiceConstants.PPE_DATA_UPLOAD_FAILED + (endTime - startTime));

                return ResponseEntity.ok(new BaseResponse<>(HttpStatus.OK.value(), ServiceConstants.STORE_DATA_UPLOAD_FAILED, resultResponses, ServiceConstants.ERROR_CODE, loginUser.getLogId()));
            }

        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            log.error("LogId:{} - UploadExcelServiceImpl - uploadPpeDetails - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), ResponseKeyConstant.SPACE + ServiceConstants.PPE_DATA_UPLOAD_FAILED + (endTime - startTime), e);

            ExceptionLogger.logException(e, loginUser.getLogId());
            e.printStackTrace();
            return ResponseEntity.ok(new BaseResponse<>(ServiceConstants.STATUS_CODE_500, ServiceConstants.STORE_DATA_UPLOAD_FAILED, null, ServiceConstants.ERROR_CODE, loginUser.getLogId()));
        }
    }


    public String getStringValue(Cell cell, List<ValidationResultResponse> resultResponses, String type, Integer rowIndex, String headerName) {
        if (cell != null) {
            return validations.getCellValueAsString(cell);
        } else {
            resultResponses.add(new ValidationResultResponse(type, rowIndex, headerName, "Data value found null"));
        }
        return null;
    }

    public Integer getIntegerValue(Cell cell, List<ValidationResultResponse> resultResponses, String type, Integer rowIndex, String headerName) {
        if (cell == null) {
            resultResponses.add(new ValidationResultResponse(type, rowIndex, headerName, "Data value found null"));
        } else if (cell.getNumericCellValue() % 1 == 0) {
            return (int) cell.getNumericCellValue();
        } else {
            // Return null for non-integer numeric values
            resultResponses.add(new ValidationResultResponse(type, rowIndex, headerName, "Data must be numeric value"));
        }
        return null;
    }

    public Float getFloatValue(Cell cell, List<ValidationResultResponse> resultResponses, String type, Integer rowIndex, String headerName) {
        if (cell == null) {
            resultResponses.add(new ValidationResultResponse(type, rowIndex, headerName, "Data value found null"));
        }
        if (cell.getCellType() == CellType.NUMERIC) {
            // Return numeric value as float
            double numericValue = cell.getNumericCellValue();
            return (float) numericValue;
        } else {
            resultResponses.add(new ValidationResultResponse(type, rowIndex, headerName, "Data must be numeric value"));
            return null;
        }

    }

    public Date getDateValue(Cell cell, List<ValidationResultResponse> resultResponses, String type, Integer rowIndex, String headerName) {
        if (cell.getDateCellValue() != null) {
            return cell.getDateCellValue();
        } else {
            // Return null for non-integer numeric values
            resultResponses.add(new ValidationResultResponse(type, rowIndex, headerName, "Data value found null"));
        }
        return null;
    }

    @Override
    public ResponseEntity<BaseResponse> uploadDeviceMasterDetails(MultipartFile file, String type) throws IOException {
        long startTime = System.currentTimeMillis();
        log.info("LogId:{} - UploadExcelServiceImpl - uploadDeviceMasterDetails - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), ServiceConstants.SPACE + ServiceConstants.UPLOAD_DEVICE_MASTER_METHOD_STARTED);

        String logId = loginUser.getLogId();
        try {
            // Read the Excel file and perform validation
            Workbook workbook = WorkbookFactory.create(file.getInputStream());
            Sheet sheet = workbook.getSheetAt(ServiceConstants.SHEET_INDEX);

            List<DeviceMaster> deviceMasters = new ArrayList<>();
            List<ValidationResultResponse> resultResponses = new ArrayList<>();
            List<String> headerNames = new ArrayList<>();
            Integer count = 0;

            Set<String> deviceNameSet = new HashSet<>();
            Set<String> deviceIPSet = new HashSet<>();

            boolean hasDataRows = false; // Flag to track if there are data rows


            List<String> expectedColumns = Arrays.asList(
                    ServiceConstants.DEVICE_NAME,
                    ServiceConstants.DEVICE_BRAND,
                    ServiceConstants.DEVICE_IP,
                    ServiceConstants.DEVICE_PORT,
                    ServiceConstants.SUB_MODULE_CODE,
                    ServiceConstants.DEVICE_ROLE

            );

            List<ExcellHeaderValidatorResponse> excellHeaderValidatorResponse = validateExcelHeader(sheet, expectedColumns);

            if (!excellHeaderValidatorResponse.get(0).getIsValid()) {
                return ResponseEntity.ok(new BaseResponse<>(ServiceConstants.INTERNAL_SERVER_ERROR, excellHeaderValidatorResponse.get(0).getErrorMessage(), excellHeaderValidatorResponse, ServiceConstants.ERROR_CODE, logId));
            }


            Row headerRow = sheet.getRow(ServiceConstants.HEADER_INDEX);
            for (int cellIndex = 0; cellIndex < headerRow.getLastCellNum(); cellIndex++) {
                Cell headerCell = headerRow.getCell(cellIndex, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                if (headerCell != null) {
                    String headerName = headerCell.getStringCellValue();
                    headerNames.add(headerName);
                }
            }

            for (Row data : sheet) {

                int emptyCellCount = 0;
                for (int i = 0; i < data.getLastCellNum(); i++) {
                    Cell emptyCell = data.getCell(i, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                    if (emptyCell == null) {
                        emptyCellCount++;
                    }
                }
                if (emptyCellCount != data.getLastCellNum()) {

                    // Assuming the data starts from the third row
                    if (data.getRowNum() <= ServiceConstants.REASON_COLUMN_HEADER_ROW_INDEX) {
                        // Skip the header row
                        continue;
                    }


                    hasDataRows = true;

                    String deviceName = getCellStringValue(data, ServiceConstants.CELL_INDEX_0, resultResponses, type, headerNames);
                    String deviceBrand = getCellStringValue(data, ServiceConstants.CELL_INDEX_1, resultResponses, type, headerNames);
                    String deviceIp = getCellStringValue(data, ServiceConstants.CELL_INDEX_2, resultResponses, type, headerNames);
                    Integer devicePort = getCellIntegerValue(data, ServiceConstants.CELL_INDEX_3, resultResponses, type, headerNames);
                    String subModuleCode = getCellStringValue(data, ServiceConstants.CELL_INDEX_4, resultResponses, type, headerNames);
                    String deviceRole = getCellStringValue(data, ServiceConstants.CELL_INDEX_5, resultResponses, type, headerNames);

                    DeviceMaster deviceMaster = new DeviceMaster();

                    if (deviceRole == null || deviceRole.isEmpty()) {
                        resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.DEVICE_ROLE, ServiceConstants.DEVICE_ROLE_MANDATORY));
                    } else if (!validateRegex(deviceRole, ServiceConstants.NOT_ALLOW_SPECIAL_CHAR_REGEX)) {
                        resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.DEVICE_ROLE, ServiceConstants.INVALID_DEVICE_ROLE_FORMAT));
                    }


                    if (deviceIp == null || deviceIp.isEmpty()) {
                        resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.DEVICE_IP, ServiceConstants.DEVICE_IP_MANDATORY));
                    } else if (!validateRegex(deviceIp, ServiceConstants.NAME_WITH_DIGIT_REGEX)) {
                        resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.DEVICE_IP, ServiceConstants.DEVICE_IP_NOT_VALID));
                    }

                    DeviceMaster deviceMasterIP = this.deviceMasterRepository.findBySubOrganizationIdAndIsDeletedAndDeviceIp(loginUser.getSubOrgId(), false, deviceIp);
                    if (deviceMasterIP == null) {
                        if (deviceIPSet.contains(deviceIp)) {
                            resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.DEVICE_IP, ServiceConstants.DUPLICATE_DEVICE_IP));
                        } else {
                            deviceIPSet.add(deviceIp);
                        }
                        deviceMaster.setDeviceIp(deviceIp);
                    } else {
                        resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.DEVICE_NAME, ServiceConstants.DEVICE_NAME_IS_ALREADY_PRESENT_IN_DB));
                    }


                    if (deviceName == null || deviceName.isEmpty()) {
                        resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.DEVICE_NAME, ServiceConstants.DEVICE_NAME_MANDATORY));
                    } else if (!validateRegex(deviceName, ServiceConstants.NOT_ALLOW_SPECIAL_CHAR_REGEX)) {
                        resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.DEVICE_NAME, ServiceConstants.DEVICE_NAME_NOT_VALID));
                    }

                    DeviceMaster deviceMaster1 = this.deviceMasterRepository.findBySubOrganizationIdAndIsDeletedAndDeviceName(loginUser.getSubOrgId(), false, deviceName);
                    if (deviceMaster1 == null) {
                        if (deviceNameSet.contains(deviceName)) {
                            resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.DEVICE_NAME, ServiceConstants.DUPLICATE_DEVICE_NAME));
                        } else {
                            deviceNameSet.add(deviceName);
                        }
                        deviceMaster.setDeviceName(deviceName);
                    } else {
                        resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.DEVICE_NAME, ServiceConstants.DEVICE_NAME_IS_ALREADY_PRESENT_IN_DB));
                    }

                    if (devicePort == null) {
                        resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.DEVICE_PORT, ServiceConstants.DEVICE_PORT_MANDATORY));
                    }

                    deviceMaster.setDevicePort(devicePort);

                    if (deviceBrand == null || deviceBrand.isEmpty()) {
                        resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.DEVICE_BRAND, ServiceConstants.DEVICE_BRAND_MANDATORY));
                    } else if (!validateRegex(deviceBrand, ServiceConstants.NOT_ALLOW_SPECIAL_CHAR_REGEX)) {
                        resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.DEVICE_BRAND, ServiceConstants.DEVICE_BRAND_NOT_VALID));
                    }
                    deviceMaster.setDeviceBrandName(deviceBrand);
                    SubModule subModule = subModuleRepository.findByIsDeletedAndSubModuleCode(false, subModuleCode);

                    if (subModule == null) {
                        resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.SUB_MODULE_CODE, ServiceConstants.SUB_MODULE_CODE_NOT_FOUND));
                    } else {
                        if (!subModule.getSubModuleName().equals(deviceRole)) {
                            resultResponses.add(new ValidationResultResponse(type, (data.getRowNum() + 1), ServiceConstants.DEVICE_ROLE, ServiceConstants.DEVICE_ROLE_AND_SUB_MODULE_CODE_SHOULD_BE_CORRECT));
                        }
                    }
                    deviceMaster.setRole(subModule);
                    deviceMaster.setIsDeleted(false);
                    deviceMaster.setOrganizationId(loginUser.getOrgId());
                    deviceMaster.setSubOrganizationId(loginUser.getSubOrgId());
                    deviceMaster.setCreatedBy(loginUser.getUserId());
                    deviceMaster.setCreatedOn(new Date());
                    deviceMaster.setModifiedOn(new Date());
                    deviceMaster.setModifiedBy(loginUser.getUserId());
                    deviceMaster.setIsActive(true);

                    deviceMasters.add(deviceMaster);
                    count++;
                }

            }
            log.info(String.valueOf(new StringBuilder().append(logId).append(ServiceConstants.TOTAL_ROWS_SCANNED).append(count)));

            long endTime = System.currentTimeMillis();
            // Close the workbook
            workbook.close();

            if (!hasDataRows) {
                log.info("LogId:{} - UploadExcelServiceImpl - uploadDeviceMasterDetails - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), ServiceConstants.SPACE + ServiceConstants.EXCEL_FILE_CONTAINS_HEADER_AND_NO_DATA);
                return ResponseEntity.ok(new BaseResponse<>(ServiceConstants.STATUS_CODE_500, ServiceConstants.EXCEL_FILE_CONTAINS_HEADER_AND_NO_DATA, null, ServiceConstants.ERROR_CODE, loginUser.getLogId()));
            }

            if (resultResponses.size() == 0) {
                // Save valid data to the repository
                this.deviceMasterRepository.saveAllAndFlush(deviceMasters);
                log.info("LogId:{} - UploadExcelServiceImpl - uploadDeviceMasterDetails - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), ResponseKeyConstant.SPACE + ServiceConstants.UPLOAD_DEVICE_MASTER_DETAIL_METHOD_EXECUTED + (endTime - startTime));

                // Return success response
                return ResponseEntity.ok(new BaseResponse<>(HttpStatus.OK.value(), ServiceConstants.FILE_UPLOADED_SUCCESSFULLY, null, ServiceConstants.SUCCESS_CODE, logId));
            } else {
                log.error("LogId:{} - UploadExcelServiceImpl - uploadDeviceMasterDetails - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), ResponseKeyConstant.SPACE + ServiceConstants.DEVICE_MASTER_DATA_UPLOAD_FAILED + (endTime - startTime));

                // Return response with validation errors
                return ResponseEntity.ok(new BaseResponse<>(HttpStatus.OK.value(), ServiceConstants.DEVICE_MASTER_DATA_UPLOAD_FAILED, resultResponses, ServiceConstants.ERROR_CODE, logId));
            }
        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            log.error("LogId:{} - UploadExcelServiceImpl - uploadDeviceMasterDetails - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), ResponseKeyConstant.SPACE + ServiceConstants.DEVICE_MASTER_DATA_UPLOAD_FAILED + (endTime - startTime), e);
            ExceptionLogger.logException(e, logId);
            return ResponseEntity.ok(new BaseResponse<>(ServiceConstants.STATUS_CODE_500, ServiceConstants.DEVICE_MASTER_DATA_UPLOAD_FAILED, null, ServiceConstants.ERROR_CODE, logId));
        }
    }

    @Override
    public ResponseEntity<BaseResponse> uploadUserListDetails(MultipartFile file, String type) {
        long startTime = System.currentTimeMillis();
        log.info("LogId:{} - UploadExcelServiceImpl - uploadUserListDetails - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), ServiceConstants.SPACE + "UPLOAD_USER_LIST_METHOD_STARTED");

        String logId = loginUser.getLogId();
        try {
            Workbook workbook = WorkbookFactory.create(file.getInputStream());
            Sheet sheet = workbook.getSheetAt(ServiceConstants.SHEET_INDEX);

            List<Users> userLists = new ArrayList<>();
            List<ValidationResultResponse> resultResponses = new ArrayList<>();
            List<String> headerNames = new ArrayList<>();
            Integer count = 0;
            boolean hasDataRows = false;

            Row headerRow = sheet.getRow(ServiceConstants.HEADER_INDEX);
            for (int cellIndex = 0; cellIndex < headerRow.getLastCellNum(); cellIndex++) {
                Cell headerCell = headerRow.getCell(cellIndex, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                if (headerCell != null) {
                    String headerName = headerCell.getStringCellValue();
                    headerNames.add(headerName);
                }
            }

            for (Row data : sheet) {
                int emptyCellCount = 0;
                for (int i = 0; i < data.getLastCellNum(); i++) {
                    Cell emptyCell = data.getCell(i, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                    if (emptyCell == null) {
                        emptyCellCount++;
                    }
                }
                if (emptyCellCount != data.getLastCellNum()) {
                    if (data.getRowNum() <= ServiceConstants.REASON_COLUMN_HEADER_ROW_INDEX) {
                        continue;
                    }

                    hasDataRows = true;

                    String firstName = getCellStringValue(data, 0, resultResponses, type, headerNames);
                    String lastName = getCellStringValue(data, 1, resultResponses, type, headerNames);
                    String email = getCellStringValue(data, 2, resultResponses, type, headerNames);
                    String mobile = getCellStringValue(data, 3, resultResponses, type, headerNames);
                    Date dateOfBirth = getCellDateValue(data, 4, resultResponses, type, headerNames);
                    String designation = getCellStringValue(data, 5, resultResponses, type, headerNames);
                    String department = getCellStringValue(data, 6, resultResponses, type, headerNames);
                    String userInternalExternal = getCellStringValue(data, 7, resultResponses, type, headerNames);
                    String userType = getCellStringValue(data, 8, resultResponses, type, headerNames);
                    String supplairErpCode = getCellStringValue(data, 9, resultResponses, type, headerNames);
                    String supplairName = getCellStringValue(data, 10, resultResponses, type, headerNames);
                    Date startDate = getCellDateValue(data, 11, resultResponses, type, headerNames);
                    Date endDate = getCellDateValue(data, 12, resultResponses, type, headerNames);

                    Users userList = new Users();
                    userList.setFirstName(firstName);
                    userList.setLastName(lastName);
                    userList.setEmailId(email);
                    userList.setUserId(email);
                    userList.setMobileNo(mobile);
                    userList.setDateOfBirth(dateOfBirth);
                    userList.setDesignation(designation);
                    userList.setDepartment(department);
                    userList.setUsers(userInternalExternal);
                    userList.setUserType(userType);

                    userList.setSupplier(supplierRepository.findByIsDeletedAndSupplierNameAndSubOrganizationId(false, supplairName, loginUser.getSubOrgId()));
                    userList.setStartDate(startDate);
                    userList.setEndDate(endDate);
                    userList.setIsDeleted(false);
                    userList.setOrganization(organizationRepository.findByIsDeletedAndId(false, loginUser.getOrgId()));
                    userList.setSubOrganization(organizationRepository.findByIsDeletedAndId(false, loginUser.getSubOrgId()));
                    userList.setCreatedBy(loginUser.getUserId());
                    userList.setCreatedOn(new Date());
                    userList.setModifiedOn(new Date());
                    userList.setModifiedBy(loginUser.getUserId());
                    userList.setIsActive(true);

                    userLists.add(userList);
                    count++;
                }
            }

            log.info(String.valueOf(new StringBuilder().append(logId).append(ServiceConstants.TOTAL_ROWS_SCANNED).append(count)));
            long endTime = System.currentTimeMillis();
            workbook.close();

            if (!hasDataRows) {
                log.info("LogId:{} - UploadExcelServiceImpl - uploadUserListDetails - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), ServiceConstants.SPACE + "EXCEL_FILE_CONTAINS_HEADER_AND_NO_DATA");
                return ResponseEntity.ok(new BaseResponse<>(ServiceConstants.STATUS_CODE_500, ServiceConstants.EXCEL_FILE_CONTAINS_HEADER_AND_NO_DATA, null, ServiceConstants.ERROR_CODE, logId));
            }

            if (resultResponses.size() == 0) {
                this.userRepository.saveAllAndFlush(userLists);
                log.info("LogId:{} - UploadExcelServiceImpl - uploadUserListDetails - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), ServiceConstants.SPACE + "UPLOAD_USER_LIST_METHOD_EXECUTED" + (endTime - startTime));

                return ResponseEntity.ok(new BaseResponse<>(HttpStatus.OK.value(), ServiceConstants.FILE_UPLOADED_SUCCESSFULLY, null, ServiceConstants.SUCCESS_CODE, logId));
            } else {
                log.error("LogId:{} - UploadExcelServiceImpl - uploadUserListDetails - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), ServiceConstants.SPACE + "USER_LIST_DATA_UPLOAD_FAILED" + (endTime - startTime));
                return ResponseEntity.ok(new BaseResponse<>(HttpStatus.OK.value(), ServiceConstants.USER_LIST_DATA_UPLOAD_FAILED, resultResponses, ServiceConstants.ERROR_CODE, logId));
            }
        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            log.error("LogId:{} - UploadExcelServiceImpl - uploadUserListDetails - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), ServiceConstants.SPACE + "USER_LIST_DATA_UPLOAD_FAILED" + (endTime - startTime), e);
            ExceptionLogger.logException(e, logId);
            return ResponseEntity.ok(new BaseResponse<>(ServiceConstants.STATUS_CODE_500, ServiceConstants.USER_LIST_DATA_UPLOAD_FAILED, null, ServiceConstants.ERROR_CODE, logId));
        }
    }

//    @Override
//    public ResponseEntity<BaseResponse> uploadPackingList(MultipartFile file, String type, Integer requestId, String requestType,Boolean isFinalUpload) {
//        long startTime = System.currentTimeMillis();
//        log.info("LogId:{} - UploadExcelServiceImpl - uploadPackingList - UserId:{} - {}",
//                loginUser.getLogId(), loginUser.getUserId(), "UPLOAD_PACKING_LIST_METHOD_STARTED");
//
//        String logId = loginUser.getLogId();
//        try {
//            Workbook workbook = WorkbookFactory.create(file.getInputStream());
//            Sheet sheet = workbook.getSheetAt(ServiceConstants.SHEET_INDEX);
//
//            List<AcceptedRejectedContainerBarcode> containerBarcodeList = new ArrayList<>();
//            List<SerialBatchNumber> serialBatchNumberList = new ArrayList<>();
//            List<ValidationResultResponse> resultResponses = new ArrayList<>();
//
//            List<String> headerNames = new ArrayList<>();
//            Integer count = 0;
//            boolean hasDataRows = false;
//
//            Row headerRow = sheet.getRow(ServiceConstants.PACKING_LIST_HEADER_ROW_INDEX);
//            if (headerRow == null) {
//                return ResponseEntity.ok(new BaseResponse<>(ServiceConstants.STATUS_CODE_500,
//                        "Excel file missing header row", null, ServiceConstants.ERROR_CODE, logId));
//            }
//
//            for (int cellIndex = 0; cellIndex < headerRow.getLastCellNum(); cellIndex++) {
//                Cell headerCell = headerRow.getCell(cellIndex, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
//                if (headerCell != null) {
//                    headerNames.add(headerCell.getStringCellValue());
//                }
//            }
//
//            // ---- Read rows ----
//            List<Map<String, String>> packingRows = new ArrayList<>();
//            for (Row row : sheet) {
//                if (row.getRowNum() <= ServiceConstants.PACKING_LIST_COLUMN_HEADER_ROW_INDEX) continue;
//
//                int emptyCellCount = 0;
//                for (int i = 0; i < row.getLastCellNum() - 1; i++) {
//                    if (row.getCell(i, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL) == null)
//                        emptyCellCount++;
//                }
//
//                if (emptyCellCount == row.getLastCellNum()) continue; // skip empty rows
//                hasDataRows = true;
//
//                String itemCode = getCellStringValue(row, 1, resultResponses, type, headerNames);
//                String itemName = getCellStringValue(row, 2, resultResponses, type, headerNames);
//                String uom = getCellStringValue(row, 3, resultResponses, type, headerNames);
//                String serialBatchNumber = getCellStringValue(row, 4, resultResponses, type, headerNames);
//                String containerCode = getCellStringValue(row, 5, resultResponses, type, headerNames);
//                String containerType = getCellStringValue(row, 6, resultResponses, type, headerNames);
//
//                Map<String, String> dataMap = new HashMap<>();
//                dataMap.put("itemCode", itemCode);
//                dataMap.put("itemName", itemName);
//                dataMap.put("uom", uom);
//                dataMap.put("serialBatchNumber", serialBatchNumber);
//                dataMap.put("containerCode", containerCode);
//                dataMap.put("containerType", containerType);
//                packingRows.add(dataMap);
//                count++;
//            }
//
//            if (!hasDataRows) {
//                workbook.close();
//                return ResponseEntity.ok(new BaseResponse<>(ServiceConstants.STATUS_CODE_500,
//                        "Excel file contains header only, no data rows found", null,
//                        ServiceConstants.ERROR_CODE, logId));
//            }
//
//            // ---- Validate duplicate serial numbers in uploaded file ----
//            List<String> serialNumbers = packingRows.stream()
//                    .map(m -> m.get("serialBatchNumber"))
//                    .filter(Objects::nonNull)
//                    .map(String::trim)
//                    .filter(s -> !s.isEmpty())
//                    .collect(Collectors.toList());
//
//            Set<String> duplicateSerials = serialNumbers.stream()
//                    .filter(s -> Collections.frequency(serialNumbers, s) > 1)
//                    .collect(Collectors.toSet());
//
//            if (!duplicateSerials.isEmpty()) {
//                workbook.close();
//                return ResponseEntity.ok(new BaseResponse<>(ServiceConstants.STATUS_CODE_500,
//                        "Duplicate Serial Numbers found in Excel: " + String.join(", ", duplicateSerials),
//                        null, ServiceConstants.ERROR_CODE, logId));
//            }
//
//            // ---- Validate duplicate serial numbers already in DB ----
//            ASNLine asnLine = null;
//            if (requestType != null && requestType.equalsIgnoreCase("ASN")) {
//                asnLine = asnLineRepository.findByIsDeletedFalseAndId(requestId);
//            }
//
//            if (asnLine == null) {
//                workbook.close();
//                return ResponseEntity.ok(new BaseResponse<>(ServiceConstants.STATUS_CODE_500,
//                        "ASN Line not found for provided requestId/requestType", null,
//                        ServiceConstants.ERROR_CODE, logId));
//            }
//
//            List<String> existingSerials = serialBatchNumberRepository
//                    .findByIsDeletedFalseAndAsnLineId(asnLine.getId())
//                    .stream()
//                    .map(SerialBatchNumber::getSerialBatchNumber)
//                    .collect(Collectors.toList());
//
//            List<String> overlap = serialNumbers.stream()
//                    .filter(existingSerials::contains)
//                    .collect(Collectors.toList());
//
//            if (!overlap.isEmpty()) {
//                workbook.close();
//                return ResponseEntity.ok(new BaseResponse<>(ServiceConstants.STATUS_CODE_500,
//                        "Serial Numbers already exist in system: " + String.join(", ", overlap),
//                        null, ServiceConstants.ERROR_CODE, logId));
//            }
//
//            // ---- Group by container code ----
//            Map<String, List<Map<String, String>>> groupedByContainer =
//                    packingRows.stream()
//                            .filter(m -> m.get("containerCode") != null && !m.get("containerCode").trim().isEmpty())
//                            .collect(Collectors.groupingBy(m -> m.get("containerCode")));
//
//            Date now = new Date();
//            Integer orgId = loginUser.getOrgId();
//            Integer subOrgId = loginUser.getSubOrgId();
//            Integer userId = loginUser.getUserId();
//
//            // ---- Iterate each container group ----
//            for (Map.Entry<String, List<Map<String, String>>> entry : groupedByContainer.entrySet()) {
//                String containerCode = entry.getKey();
//                List<Map<String, String>> serialList = entry.getValue();
//
//                AcceptedRejectedContainerBarcode barcode = new AcceptedRejectedContainerBarcode();
//                barcode.setOrganizationId(orgId);
//                barcode.setSubOrganizationId(subOrgId);
//                barcode.setContainerCode(containerCode);
//                barcode.setContainerType(serialList.get(0).get("containerType"));
//                barcode.setIsDeleted(false);
//                barcode.setCreatedBy(userId);
//                barcode.setCreatedOn(now);
//                containerBarcodeList.add(barcode);
//                this.acceptedRejectedContainerBarcodeRepository.save(barcode);
//
//                for (Map<String, String> serial : serialList) {
//                    SerialBatchNumber batch = new SerialBatchNumber();
//                    batch.setOrganizationId(orgId);
//                    batch.setSubOrganizationId(subOrgId);
//                    batch.setSerialBatchNumber(serial.get("serialBatchNumber"));
//                    batch.setAcceptedRejectedContainerBarcode(barcode);
//                    batch.setAsnLine(asnLine);
//                    batch.setIsDeleted(false);
//                    batch.setCreatedBy(userId);
//                    batch.setCreatedOn(now);
//                    serialBatchNumberList.add(batch);
//                }
//            }
//
//            this.serialBatchNumberRepository.saveAllAndFlush(serialBatchNumberList);
//
//            long endTime = System.currentTimeMillis();
//            log.info("LogId:{} - UploadExcelServiceImpl - uploadPackingList - UserId:{} - {} Rows:{} Time:{}ms",
//                    logId, loginUser.getUserId(), "UPLOAD_PACKING_LIST_EXECUTED", count, (endTime - startTime));
//
//            workbook.close();
//            return ResponseEntity.ok(new BaseResponse<>(HttpStatus.OK.value(),
//                    "Packing List Uploaded Successfully", null, ServiceConstants.SUCCESS_CODE, logId));
//
//        } catch (Exception e) {
//            long endTime = System.currentTimeMillis();
//            log.error("LogId:{} - UploadExcelServiceImpl - uploadPackingList - UserId:{} - {}",
//                    loginUser.getLogId(), loginUser.getUserId(), "PACKING_LIST_UPLOAD_FAILED", e);
//            ExceptionLogger.logException(e, logId);
//            return ResponseEntity.ok(new BaseResponse<>(ServiceConstants.STATUS_CODE_500,
//                    ServiceConstants.FILE_UPLOAD_FAILED, null, ServiceConstants.ERROR_CODE, logId));
//        }
//    }


    @Override
    public ResponseEntity<BaseResponse> uploadPackingList(
            MultipartFile file, String type, Integer requestId,
            String requestType, Boolean isFinalUpload) {

        long startTime = System.currentTimeMillis();
        log.info("LogId:{} - UploadExcelServiceImpl - uploadPackingList - UserId:{} - {}",
                loginUser.getLogId(), loginUser.getUserId(), "UPLOAD_PACKING_LIST_METHOD_STARTED");

        String logId = loginUser.getLogId();

        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(ServiceConstants.SHEET_INDEX);

            List<ValidationResultResponse> resultResponses = new ArrayList<>();
            List<Map<String, String>> packingRows = new ArrayList<>();
            boolean hasDataRows = false;

            Row headerRow = sheet.getRow(ServiceConstants.PACKING_LIST_HEADER_ROW_INDEX);
            if (headerRow == null) {
                return ResponseEntity.ok(new BaseResponse<>(ServiceConstants.STATUS_CODE_500,
                        "Excel file missing header row", null, ServiceConstants.ERROR_CODE, logId));
            }

            List<String> headerNames = new ArrayList<>();
            for (int cellIndex = 0; cellIndex < headerRow.getLastCellNum(); cellIndex++) {
                Cell headerCell = headerRow.getCell(cellIndex, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                if (headerCell != null) {
                    headerNames.add(headerCell.getStringCellValue());
                }
            }

            // ==== Read Data Rows ====
            for (Row row : sheet) {
                if (row.getRowNum() <= ServiceConstants.PACKING_LIST_HEADER_ROW_INDEX) continue;

                int emptyCellCount = 0;
                for (int i = 0; i < row.getLastCellNum() - 1; i++) {
                    if (row.getCell(i, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL) == null)
                        emptyCellCount++;
                }
                if (emptyCellCount == row.getLastCellNum()) continue;

                hasDataRows = true;
                Map<String, String> dataMap = new HashMap<>();
                dataMap.put("itemCode", getCellStringValue(row, 1, resultResponses, type, headerNames));
                dataMap.put("itemName", getCellStringValue(row, 2, resultResponses, type, headerNames));
                dataMap.put("uom", getCellStringValue(row, 3, resultResponses, type, headerNames));
                dataMap.put("serialBatchNumber", getCellStringValue(row, 4, resultResponses, type, headerNames));
                dataMap.put("containerCode", getCellStringValue(row, 5, resultResponses, type, headerNames));
                dataMap.put("containerType", getCellStringValue(row, 6, resultResponses, type, headerNames));

                // 🔹 Skip blank rows early
                if (isBlank(dataMap.get("itemCode")) ||
                        isBlank(dataMap.get("containerCode")) ||
                        isBlank(dataMap.get("serialBatchNumber"))) {
                    log.warn("Skipping blank row at index {}", row.getRowNum());
                    continue;
                }

                packingRows.add(dataMap);
            }

            if (!hasDataRows || packingRows.isEmpty()) {
                return ResponseEntity.ok(new BaseResponse<>(ServiceConstants.STATUS_CODE_500,
                        "Excel file contains header only, no valid data rows found",
                        null, ServiceConstants.ERROR_CODE, logId));
            }

            // ==== Group by Item Code and Container ====
            Map<String, Map<String, List<String>>> itemToContainerSerials = new HashMap<>();
            for (Map<String, String> row : packingRows) {
                String itemCode = row.get("itemCode");
                String container = row.get("containerCode");
                String serial = row.get("serialBatchNumber");

                if (isBlank(itemCode) || isBlank(container) || isBlank(serial)) continue;

                itemToContainerSerials
                        .computeIfAbsent(itemCode, k -> new HashMap<>())
                        .computeIfAbsent(container, k -> new ArrayList<>())
                        .add(serial);
            }

            // 🔹 Remove invalid keys if somehow still present
            itemToContainerSerials.entrySet().removeIf(e -> isBlank(e.getKey()));

            // ==== Validate ASN ====
            ASNLine asnLine = null;
            if ("ASN".equalsIgnoreCase(requestType)) {
                asnLine = asnLineRepository.findByIsDeletedFalseAndId(requestId);
            }
            if (asnLine == null) {
                return ResponseEntity.ok(new BaseResponse<>(ServiceConstants.STATUS_CODE_500,
                        "ASN Line not found for provided requestId/requestType", null,
                        ServiceConstants.ERROR_CODE, logId));
            }

            // ==== Validate duplicate serial numbers ====
            List<String> serialNumbers = packingRows.stream()
                    .map(m -> m.get("serialBatchNumber"))
                    .filter(Objects::nonNull)
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toList());

            Set<String> duplicateSerials = serialNumbers.stream()
                    .filter(s -> Collections.frequency(serialNumbers, s) > 1)
                    .collect(Collectors.toSet());
            if (!duplicateSerials.isEmpty()) {
                return ResponseEntity.ok(new BaseResponse<>(ServiceConstants.STATUS_CODE_500,
                        "Duplicate Serial Numbers found in Excel: " + String.join(", ", duplicateSerials),
                        null, ServiceConstants.ERROR_CODE, logId));
            }

//            List<String> existingSerials = serialBatchNumberRepository
//                    .findByIsDeletedFalseAndAsnLineId(asnLine.getId())
//                    .stream()
//                    .map(SerialBatchNumber::getSerialBatchNumber)
//                    .collect(Collectors.toList());

//
//            List<String> overlap = serialNumbers.stream()
//                    .filter(existingSerials::contains)
//                    .collect(Collectors.toList());


            Integer itemId = asnLine.getItem().getId();

            Integer supplierId = null;

            if (asnLine != null
                    && asnLine.getAsnHeadId() != null
                    && asnLine.getAsnHeadId().getSupplier() != null
                    && asnLine.getAsnHeadId().getSupplier().getId() != null) {

                supplierId = asnLine.getAsnHeadId().getSupplier().getId();
            }

            List<String> overlap = serialBatchNumberRepository
                    .findExistingSerialsForItemSupplierAndSerials(itemId, supplierId, serialNumbers);

            if (!overlap.isEmpty()) {
                return ResponseEntity.ok(new BaseResponse<>(ServiceConstants.STATUS_CODE_500,
                        "Serial Numbers already exist in system: " + String.join(", ", overlap),
                        null, ServiceConstants.ERROR_CODE, logId));
            }

            // ==== If NOT final upload → build summary only ====
            if (!Boolean.TRUE.equals(isFinalUpload)) {
                List<PackingSummaryResponse> summaryList = new ArrayList<>();

                if (!itemToContainerSerials.isEmpty()) {
                    for (Map.Entry<String, Map<String, List<String>>> itemEntry : itemToContainerSerials.entrySet()) {

                        String itemCode = itemEntry.getKey();
                        if (isBlank(itemCode)) continue; // skip blanks
                        Map<String, List<String>> containerMap = itemEntry.getValue();
                        if (containerMap == null || containerMap.isEmpty()) continue;

                        // ---- Safely get item name ----
                        String itemName = packingRows.stream()
                                .filter(r -> itemCode.equals(r.get("itemCode")))
                                .map(r -> r.getOrDefault("itemName", ""))
                                .findFirst()
                                .orElse("");

                        // ---- Safely build container summaries ----
                        List<PackingSummaryResponse.ContainerSummary> containerSummaries = containerMap.entrySet().stream()
                                .filter(e -> !isBlank(e.getKey()))
                                .map(e -> {
                                    String containerCode = e.getKey();
                                    List<String> serials = e.getValue() == null
                                            ? Collections.emptyList()
                                            : e.getValue().stream().filter(Objects::nonNull).collect(Collectors.toList());
                                    return new PackingSummaryResponse.ContainerSummary(containerCode, serials);
                                })
                                .collect(Collectors.toList());

                        int totalContainers = containerSummaries.size();
                        int totalQty = containerSummaries.stream()
                                .mapToInt(c -> c.getSerialNumbers() != null ? c.getSerialNumbers().size() : 0)
                                .sum();

                        summaryList.add(new PackingSummaryResponse(
                                itemName,
                                itemCode,
                                totalContainers,
                                totalQty,
                                containerSummaries
                        ));
                    }
                }

                return ResponseEntity.ok(new BaseResponse<>(
                        HttpStatus.OK.value(),
                        "Packing summary generated successfully (preview mode)",
                        summaryList,
                        ServiceConstants.SUCCESS_CODE,
                        logId
                ));
            }

            // ==== FINAL upload: Save Data ====
            Date now = new Date();
            Integer orgId = loginUser.getOrgId();
            Integer subOrgId = loginUser.getSubOrgId();
            Integer userId = loginUser.getUserId();

            int totalContainers = itemToContainerSerials.values().stream()
                    .mapToInt(m -> m != null ? m.size() : 0)
                    .sum();

            int sequenceCounter = 1; // start from 1
            List<SerialBatchNumber> seq = this.serialBatchNumberRepository
                    .findByIsDeletedFalseAndAsnLineIdOrderByAcceptedRejectedContainerBarcodePackingSlipNumberDesc(requestId);

            String packingSlipNumber;
            if (seq != null && !seq.isEmpty()
                    && seq.get(0).getAcceptedRejectedContainerBarcode() != null
                    && seq.get(0).getAcceptedRejectedContainerBarcode().getPackingSlipNumber() != null) {
                packingSlipNumber = seq.get(0).getAcceptedRejectedContainerBarcode().getPackingSlipNumber();
            } else {
                packingSlipNumber = null; // Let generator handle initial creation
            }
            CommonMaster packingCompletedStatus = this.commonMasterRepository.findByTypeAndIsDeletedFalse("PCKSLP");

            for (Map.Entry<String, Map<String, List<String>>> itemEntry : itemToContainerSerials.entrySet()) {
                Map<String, List<String>> containerMap = itemEntry.getValue();
                if (containerMap == null || containerMap.isEmpty()) continue;

                for (Map.Entry<String, List<String>> entry : containerMap.entrySet()) {
                    String containerCode = entry.getKey();
                    List<String> serials = entry.getValue();

                    if (isBlank(containerCode) || serials == null || serials.isEmpty()) continue;

                    // ✅ Generate unique packing slip number
                     String nextPackingSlipNumber = generateNextPackingSlipNumber();

                    // ✅ Get container type from first matching Excel row
                    String containerType = packingRows.stream()
                            .filter(r -> containerCode.equals(r.get("containerCode")))
                            .map(r -> r.getOrDefault("containerType", ""))
                            .findFirst()
                            .orElse("");

                    // ✅ Create and save barcode entity
                    AcceptedRejectedContainerBarcode barcode = new AcceptedRejectedContainerBarcode();
                    barcode.setOrganizationId(orgId);
                    barcode.setSubOrganizationId(subOrgId);
                    barcode.setContainerCode(containerCode);
                    barcode.setContainerType(containerType);
                    barcode.setPackingSlipNumber(nextPackingSlipNumber); // new field
                    barcode.setStatus(packingCompletedStatus);
                    barcode.setPackingSequence(sequenceCounter + " of " + totalContainers); // new field
                    barcode.setIsDeleted(false);
                    barcode.setCreatedBy(userId);
                    barcode.setCreatedOn(now);
                    acceptedRejectedContainerBarcodeRepository.save(barcode);

                    ASNLine finalAsnLine = asnLine;
                    List<SerialBatchNumber> batchList = serials.stream()
                            .filter(Objects::nonNull)
                            .map(serial -> {
                                SerialBatchNumber s = new SerialBatchNumber();
                                s.setOrganizationId(orgId);
                                s.setSubOrganizationId(subOrgId);
                                s.setSerialBatchNumber(serial);
                                s.setAcceptedRejectedContainerBarcode(barcode);
                                s.setAsnLine(finalAsnLine);
                                s.setIsDeleted(false);
                                s.setCreatedBy(userId);
                                s.setCreatedOn(now);
                                return s;
                            })
                            .collect(Collectors.toList());
                    serialBatchNumberRepository.saveAll(batchList);


// Collect StockMovement objects for batch save
                    List<StockMovement> stockMovementList = batchList.stream()
                            .map(serialBatchNumber -> {
                                StockMovement sm = new StockMovement();
                                sm.setOrganizationId(orgId);
                                sm.setSubOrganizationId(subOrgId);
                                sm.setSerialBatchNumbers(serialBatchNumber);
                                sm.setItem(finalAsnLine.getItem());
                                sm.setIsDeleted(false);
                                sm.setCreatedBy(userId);
                                sm.setCreatedOn(now);
                                return sm;
                            })
                            .collect(Collectors.toList());

                    // Save all StockMovement objects in one batch
                    this.stockMovementRepository.saveAll(stockMovementList);
                    sequenceCounter++;
                }
            }
//          Updating Total Container in ASN Line
            ASNLine line = this.asnLineRepository.findByIsDeletedFalseAndId(requestId);
            if (line != null) {
                line.setNumberOfContainer(totalContainers);
                this.asnLineRepository.save(line);
            }

            long endTime = System.currentTimeMillis();
            log.info("LogId:{} - uploadPackingList - saved in {}ms", logId, (endTime - startTime));

            return ResponseEntity.ok(new BaseResponse<>(HttpStatus.OK.value(),
                    "Packing List Uploaded Successfully", null, ServiceConstants.SUCCESS_CODE, logId));

        } catch (Exception e) {
            log.error("LogId:{} - UploadExcelServiceImpl - uploadPackingList - UserId:{} - {}",
                    loginUser.getLogId(), loginUser.getUserId(), "PACKING_LIST_UPLOAD_FAILED", e);
            ExceptionLogger.logException(e, logId);
            return ResponseEntity.ok(new BaseResponse<>(ServiceConstants.STATUS_CODE_500,
                    ServiceConstants.FILE_UPLOAD_FAILED, null, ServiceConstants.ERROR_CODE, logId));
        }
    }

    // 🔹 Utility method
    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    private String generateNextPackingSlipNumber() {

        String[] monthLetters = {"A","B","C","D","E","F","G","H","I","J","K","L"};

        LocalDate today = LocalDate.now();
        String year = String.valueOf(today.getYear());
        String monthLetter = monthLetters[today.getMonthValue() - 1];
        String day = String.format("%02d", today.getDayOfMonth());

        // PKG-2025A01-
        String prefix = String.format("PKG-%s%s%s-", year, monthLetter, day);

        // Fetch max sequence from DB for today
        Integer maxSeq = acceptedRejectedContainerBarcodeRepository.findMaxSequenceForPrefix(prefix);
        int nextSequence = (maxSeq == null ? 1 : maxSeq + 1);

        // Final slip number -> PKG-2025A01-001
        return String.format("%s%d", prefix, nextSequence);
    }





}
