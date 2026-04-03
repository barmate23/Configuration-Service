package com.stockmanagementsystem.service;

import com.stockmanagementsystem.entity.*;
import com.stockmanagementsystem.exception.UploadRowException;
import com.stockmanagementsystem.repository.*;
import com.stockmanagementsystem.request.ItemSupplierPackingProfileUpdateRequest;
import com.stockmanagementsystem.response.*;
import com.stockmanagementsystem.utils.DimensionKeyUtil;
import com.stockmanagementsystem.utils.PackagingMasterCache;
import com.stockmanagementsystem.utils.UomConversionUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.stockmanagementsystem.utils.PackingTemplateConstants.*;

@Service
@Slf4j
public class PackingTemplateServiceImpl implements PackingTemplateService{

    @Autowired
    private LoginUser loginUser;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private SupplierItemMapperRepository supplierItemMapperRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private PackingProfileRepository packingProfileRepo;

    @Autowired
    private ItemSupplierPackingProfileMapRepository itemSupplierPackingProfileMapRepository;

    @Autowired
    private PackingHierarchyLevelRepository packingHierarchyLevelRepository;

    @Autowired
    private PackagingMasterCache packagingMasterCache;

    @Autowired
    private PackagingMasterRepository packagingMasterRepository;

    @Override
    public ResponseEntity<byte[]> downloadTemplate(Integer areaId, Integer zoneId) {

        String logId = loginUser.getLogId();
        long startTime = System.currentTimeMillis();

        log.info("{} | PackingTemplateDownload | START | areaId={} | zoneId={}",
                logId, areaId, zoneId);

        try (SXSSFWorkbook workbook = new SXSSFWorkbook(100)) {

            // =====================================================
            // MAIN SHEET
            // =====================================================
            SXSSFSheet sheet = workbook.createSheet("Packing_Config");
            sheet.trackAllColumnsForAutoSizing();

            // =====================================================
            // STYLES
            // =====================================================
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setBorderTop(BorderStyle.MEDIUM);
            headerStyle.setBorderBottom(BorderStyle.MEDIUM);
            headerStyle.setBorderLeft(BorderStyle.MEDIUM);
            headerStyle.setBorderRight(BorderStyle.MEDIUM);

            CellStyle dataStyle = workbook.createCellStyle();
            dataStyle.setBorderTop(BorderStyle.THIN);
            dataStyle.setBorderBottom(BorderStyle.THIN);
            dataStyle.setBorderLeft(BorderStyle.THIN);
            dataStyle.setBorderRight(BorderStyle.THIN);

            // =====================================================
            // HEADER
            // =====================================================
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < PACKING_TEMPLATE_HEADERS.size(); i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(PACKING_TEMPLATE_HEADERS.get(i));
                cell.setCellStyle(headerStyle);
            }

            // =====================================================
            // DATA ROWS
            // =====================================================
            List<Location> locations =
                    locationRepository.findByZoneIdAndIsDeleted(zoneId, false);

            Map<String, Location> uniqueMap = new LinkedHashMap<>();
            for (Location loc : locations) {
                if (loc.getItem() == null) continue;
                String key = loc.getItem().getId() + "|" + loc.getZone().getId();
                uniqueMap.putIfAbsent(key, loc);
            }

            Set<Integer> itemIds = uniqueMap.values().stream()
                    .map(l -> l.getItem().getId())
                    .collect(Collectors.toSet());

            Map<Integer, List<SupplierItemMapper>> supplierMap =
                    supplierItemMapperRepository
                            .findByItemIdInAndIsDeleted(new ArrayList<>(itemIds), false)
                            .stream()
                            .collect(Collectors.groupingBy(sim -> sim.getItem().getId()));

            int rowIndex = 1;

            for (Location location : uniqueMap.values()) {

                Item item = location.getItem();
                List<SupplierItemMapper> suppliers = supplierMap.get(item.getId());
                if (suppliers == null) continue;

                for (SupplierItemMapper sim : suppliers) {

                    Row row = sheet.createRow(rowIndex++);

                    for (int c = 0; c < PACKING_TEMPLATE_HEADERS.size(); c++) {
                        Cell cell = row.createCell(c);
                        cell.setCellValue("");
                        cell.setCellStyle(dataStyle);
                    }

                    row.getCell(0).setCellValue(item.getItemCode());
                    row.getCell(1).setCellValue(item.getName());
                    row.getCell(2).setCellValue(sim.getSupplier().getErpSupplierId());
                    row.getCell(3).setCellValue(sim.getSupplier().getSupplierName());
                    row.getCell(4).setCellValue(location.getZone().getArea().getAreaName());
                    row.getCell(5).setCellValue(location.getZone().getZoneName());
                }
            }

            int lastDataRow = rowIndex - 1;

            // =====================================================
            // HIDDEN SHEET – PACKAGING MASTER
            // =====================================================
            Sheet hidden = workbook.createSheet("__PACKAGING__");

            List<PackagingMaster> masters = packagingMasterRepository.findAll();

            Set<String> packTypes = new LinkedHashSet<>();
            Map<String, Set<String>> subtypeMap = new LinkedHashMap<>();
            Map<String, Set<String>> dimensionMap = new LinkedHashMap<>();

            for (PackagingMaster pm : masters) {

                if (!Boolean.TRUE.equals(pm.getIsActive())
                        || Boolean.TRUE.equals(pm.getIsDeleted())) {
                    continue;
                }

                PackagingSubtype ps = pm.getPackagingSubtype();
                PackagingType pt = ps.getPackagingType();

                String type = normalize(pt.getTypeName());
                String subtype = normalize(ps.getSubtypeName());

                BigDecimal factor = toMmFactor(pm.getUom());

                BigDecimal l = pm.getLength() != null
                        ? pm.getLength().multiply(factor)
                        : BigDecimal.ZERO;

                BigDecimal w = pm.getWidth() != null
                        ? pm.getWidth().multiply(factor)
                        : BigDecimal.ZERO;

                BigDecimal h = pm.getHeight() != null
                        ? pm.getHeight().multiply(factor)
                        : BigDecimal.ZERO;

                String dimension =
                        l.stripTrailingZeros().toPlainString() + "x" +
                                w.stripTrailingZeros().toPlainString() + "x" +
                                h.stripTrailingZeros().toPlainString();

                packTypes.add(type);

                subtypeMap.computeIfAbsent(type, k -> new LinkedHashSet<>())
                        .add(subtype);

                dimensionMap.computeIfAbsent(type + "_" + subtype,
                        k -> new LinkedHashSet<>()).add(dimension);
            }

            int col = 0;
            writeNamedRange(workbook, hidden, "PACK_TYPES", packTypes, col++);

            for (String type : subtypeMap.keySet()) {
                writeNamedRange(workbook, hidden, type, subtypeMap.get(type), col++);
            }

            for (String key : dimensionMap.keySet()) {
                writeNamedRange(workbook, hidden, key, dimensionMap.get(key), col++);
            }

            workbook.setSheetHidden(workbook.getSheetIndex(hidden), true);

            // =====================================================
            // DROPDOWNS – PACKING LEVEL
            // =====================================================
            List<String> packingLevels =
                    packingHierarchyLevelRepository
                            .findByOrganizationIdAndSubOrganizationIdAndIsDeletedAndIsActive(
                                    loginUser.getOrgId(),
                                    loginUser.getSubOrgId(),
                                    false,
                                    true
                            )
                            .stream()
                            .map(PackingHierarchyLevel::getLevelCode)
                            .collect(Collectors.toList());

            addDropdown(sheet, 1, lastDataRow,
                    PACKING_TEMPLATE_HEADERS.indexOf(PACKING_LEVEL),
                    packingLevels);

            // =====================================================
            // DROPDOWNS – DIMENSION UOM (ALL LEVELS)
            // =====================================================
            List<String> uoms = Arrays.asList(DIMENSION_UOMS);

            addDropdown(sheet, 1, lastDataRow,
                    PACKING_TEMPLATE_HEADERS.indexOf(PRIMARY_DIMENSION_UOM), uoms);
            addDropdown(sheet, 1, lastDataRow,
                    PACKING_TEMPLATE_HEADERS.indexOf(SECONDARY_DIMENSION_UOM), uoms);
            addDropdown(sheet, 1, lastDataRow,
                    PACKING_TEMPLATE_HEADERS.indexOf(TERTIARY_DIMENSION_UOM), uoms);

            // =====================================================
            // CASCADING DROPDOWNS – PRIMARY / SECONDARY / TERTIARY
            // =====================================================
            applyCascade(sheet, lastDataRow,
                    "Primary Pack Type",
                    "Primary Pack Sub Type",
                    "Primary Dimension (L x W x H)");

            applyCascade(sheet, lastDataRow,
                    "Secondary Pack Type",
                    "Secondary Pack Sub Type",
                    "Secondary Dimension (L x W x H)");

            applyCascade(sheet, lastDataRow,
                    "Tertiary Pack Type",
                    "Tertiary Pack Sub Type",
                    "Tertiary Dimension (L x W x H)");

            // =====================================================
            // AUTO SIZE
            // =====================================================
            for (int i = 0; i < PACKING_TEMPLATE_HEADERS.size(); i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);

            log.info("{} | PackingTemplateDownload | SUCCESS | rows={} | durationMs={}",
                    logId, lastDataRow, System.currentTimeMillis() - startTime);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=Packing_Config_Template.xlsx")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(out.toByteArray());

        } catch (Exception ex) {
            log.error("{} | PackingTemplateDownload | FAILED", logId, ex);
            throw new RuntimeException("Failed to generate Packing Configuration template", ex);
        }
    }

    private void addDropdown(
            Sheet sheet,
            int firstRow,
            int lastRow,
            int columnIndex,
            List<String> values) {

        if (values == null || values.isEmpty()) {
            return; // nothing to apply
        }

        DataValidationHelper helper = sheet.getDataValidationHelper();

        DataValidationConstraint constraint =
                helper.createExplicitListConstraint(
                        values.toArray(new String[0])
                );

        CellRangeAddressList addressList =
                new CellRangeAddressList(
                        firstRow,
                        lastRow,
                        columnIndex,
                        columnIndex
                );

        DataValidation validation =
                helper.createValidation(constraint, addressList);

        // Excel behaviour tweaks
        validation.setSuppressDropDownArrow(true);
        validation.setShowErrorBox(true);
        validation.createErrorBox(
                "Invalid Value",
                "Please select a value from the dropdown list"
        );

        sheet.addValidationData(validation);
    }


    private void applyCascade(
            Sheet sheet,
            int lastRow,
            String typeHeader,
            String subTypeHeader,
            String dimHeader) {

        addFormulaDropdown(sheet, 1, lastRow,
                PACKING_TEMPLATE_HEADERS.indexOf(typeHeader),
                "=PACK_TYPES");

        addFormulaDropdown(sheet, 1, lastRow,
                PACKING_TEMPLATE_HEADERS.indexOf(subTypeHeader),
                "=INDIRECT(SUBSTITUTE(" +
                        getExcelColumn(typeHeader) + "2,\" \",\"_\"))");

        addFormulaDropdown(sheet, 1, lastRow,
                PACKING_TEMPLATE_HEADERS.indexOf(dimHeader),
                "=INDIRECT(SUBSTITUTE(" +
                        getExcelColumn(typeHeader) + "2,\" \",\"_\") & \"_\" & " +
                        "SUBSTITUTE(" +
                        getExcelColumn(subTypeHeader) + "2,\" \",\"_\"))");
    }

    private BigDecimal toMmFactor(String uom) {
        if (uom == null) return BigDecimal.ONE;
        uom = uom.toUpperCase();
        if ("MM".equals(uom)) return BigDecimal.ONE;
        if ("CM".equals(uom)) return BigDecimal.valueOf(10);
        if ("METER".equals(uom)) return BigDecimal.valueOf(1000);
        if ("INCH".equals(uom)) return BigDecimal.valueOf(25.4);
        throw new IllegalArgumentException("Unsupported UOM: " + uom);
    }

    private String normalize(String v) {
        return v == null ? "NA"
                : v.toUpperCase().replaceAll("[^A-Z0-9]", "_");
    }



    private static final String[] DIMENSION_UOMS = {
            "MM", "CM", "METER", "INCH"
    };



    private void writeNamedRange(
            Workbook workbook,
            Sheet sheet,
            String name,
            Collection<String> values,
            int col) {

        int r = 0;
        for (String v : values) {
            Row row = sheet.getRow(r);
            if (row == null) row = sheet.createRow(r);
            row.createCell(col).setCellValue(v);
            r++;
        }

        Name n = workbook.createName();
        n.setNameName(name);

        String colLetter = CellReference.convertNumToColString(col);
        n.setRefersToFormula(
                sheet.getSheetName() + "!$" + colLetter + "$1:$" + colLetter + "$" + r);
    }

    private void addFormulaDropdown(
            Sheet sheet,
            int firstRow,
            int lastRow,
            int columnIndex,
            String formula) {

        DataValidationHelper helper = sheet.getDataValidationHelper();
        DataValidationConstraint constraint =
                helper.createFormulaListConstraint(formula);

        CellRangeAddressList range =
                new CellRangeAddressList(firstRow, lastRow, columnIndex, columnIndex);

        DataValidation validation =
                helper.createValidation(constraint, range);

        validation.setSuppressDropDownArrow(true);
        validation.setShowErrorBox(true);
        sheet.addValidationData(validation);
    }

    private String getExcelColumn(String header) {
        int idx = PACKING_TEMPLATE_HEADERS.indexOf(header);
        return CellReference.convertNumToColString(idx);
    }

    @Transactional
    public Map<String, Object> uploadPackingProfileTemplate(
            MultipartFile file,
            Integer areaId,
            Integer zoneId) {

        String logId = loginUser.getLogId();
        long startTime = System.currentTimeMillis();

        List<UploadErrorDetail> errors = new ArrayList<>();
        AtomicInteger successCount = new AtomicInteger();

        // =====================================================
        // LOAD MASTER DATA ONCE
        // =====================================================
        Map<String, Item> itemMap = loadItemsByZone(zoneId);
        Map<String, SupplierItemMapper> supplierItemMap = loadSupplierItemMap(itemMap);
        Map<String, PackingHierarchyLevel> hierarchyMap = loadHierarchyLevels();

        // 🔥 MOST IMPORTANT
        packagingMasterCache.load();

        List<PackingProfileConfigMaster> profilesToSave = new ArrayList<>();
        List<ItemSupplierPackingProfileMap> mappingsToSave = new ArrayList<>();

        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {

            Sheet sheet = workbook.getSheetAt(0);
            Map<String, Integer> headerMap =
                    buildHeaderIndexMap(sheet.getRow(0));

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {

                Row row = sheet.getRow(i);
                if (row == null || isRowCompletelyEmpty(row, headerMap.size())) {
                    break;
                }

                int excelRow = i + 1;

                try {
                    // =================================================
                    // BASIC IDENTIFIERS
                    // =================================================
                    String itemCode = getString(row, headerMap, "Item Code", excelRow);
                    String supplierCode = getString(row, headerMap, "Supplier Code", excelRow);
                    String packingLevelCode = getString(row, headerMap, "Packing Level", excelRow);

                    Item item = itemMap.get(itemCode);
                    SupplierItemMapper sim =
                            supplierItemMap.get(itemCode + "|" + supplierCode);
                    PackingHierarchyLevel hierarchy =
                            hierarchyMap.get(packingLevelCode);

                    if (item == null || sim == null || hierarchy == null) {
                        throw error(excelRow, "Validation",
                                "Invalid Item / Supplier / Packing Level");
                    }

                    int levelOrder = hierarchy.getLevelOrder();

                    // =================================================
                    // PRIMARY PACKAGING (MANDATORY)
                    // =================================================
                    PackagingMaster primaryPackaging =
                            resolvePackaging(row, headerMap, excelRow, "Primary");

                    Integer primaryUnits =
                            getInteger(row, headerMap,
                                    "Units per Primary Pack", excelRow);

                    if (primaryUnits == null || primaryUnits <= 0) {
                        throw error(excelRow,
                                "Units per Primary Pack",
                                "Must be greater than zero");
                    }

                    // =================================================
                    // SECONDARY PACKAGING
                    // =================================================
                    PackagingMaster secondaryPackaging = null;
                    Integer secondaryUnits = null;

                    if (levelOrder >= 2) {
                        secondaryPackaging =
                                resolvePackaging(row, headerMap, excelRow, "Secondary");

                        secondaryUnits =
                                getInteger(row, headerMap,
                                        "Units per Secondary Pack", excelRow);

                        if (secondaryUnits == null || secondaryUnits <= 0) {
                            throw error(excelRow,
                                    "Units per Secondary Pack",
                                    "Required for secondary level");
                        }
                    }

                    // =================================================
                    // TERTIARY PACKAGING
                    // =================================================
                    PackagingMaster tertiaryPackaging = null;
                    Integer tertiaryUnits = null;

                    if (levelOrder == 3) {
                        tertiaryPackaging =
                                resolvePackaging(row, headerMap, excelRow, "Tertiary");

                        tertiaryUnits =
                                getInteger(row, headerMap,
                                        "Units per Tertiary Pack", excelRow);

                        if (tertiaryUnits == null || tertiaryUnits <= 0) {
                            throw error(excelRow,
                                    "Units per Tertiary Pack",
                                    "Required for tertiary level");
                        }
                    }

                    // =================================================
                    // CREATE / UPDATE PACKING PROFILE
                    // =================================================
                    PackingProfileConfigMaster profile =
                            new PackingProfileConfigMaster();

                    profile.setOrganizationId(loginUser.getOrgId());
                    profile.setSubOrganizationId(loginUser.getSubOrgId());
                    profile.setPackingHierarchyLevel(hierarchy);

                    profile.setPrimaryPackaging(primaryPackaging);
                    profile.setPrimaryUnits(primaryUnits);

                    profile.setSecondaryPackaging(secondaryPackaging);
                    profile.setSecondaryUnits(secondaryUnits);

                    profile.setTertiaryPackaging(tertiaryPackaging);
                    profile.setTertiaryUnits(tertiaryUnits);

                    profile.setIsActive(true);
                    profile.setIsDeleted(false);
                    profile.setCreatedBy(loginUser.getUserId());
                    profile.setCreatedOn(new Date());

                    profilesToSave.add(profile);

                    // =================================================
                    // ITEM–SUPPLIER MAPPING
                    // =================================================
                    ItemSupplierPackingProfileMap mapping =
                            buildItemSupplierPackingProfileMap(
                                    item,
                                    sim.getSupplier(),
                                    profile
                            );

                    mappingsToSave.add(mapping);

                    successCount.incrementAndGet();

                } catch (UploadRowException ex) {
                    errors.add(ex.getErrorDetail());
                }
            }

            // =====================================================
            // BULK SAVE
            // =====================================================
            packingProfileRepo.saveAll(profilesToSave);
            itemSupplierPackingProfileMapRepository.saveAll(mappingsToSave);

        } catch (Exception ex) {
            log.error("{} | Upload failed", logId, ex);
            throw new RuntimeException("Packing profile upload failed", ex);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("successCount", successCount.get());
        response.put("errorCount", errors.size());
        response.put("errors", errors);

        return response;
    }


    private Map<String, Item> loadItemsByZone(Integer zoneId) {

        String logId = loginUser.getLogId();

        log.info("{} | loadItemsByZone | START | zoneId={}", logId, zoneId);

        List<Location> locations =
                locationRepository.findByZoneIdAndIsDeleted(zoneId, false);

        if (locations.isEmpty()) {
            throw new IllegalStateException(
                    "No locations found for selected zone");
        }

        Map<String, Item> itemMap =
                locations.stream()
                        .map(Location::getItem)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toMap(
                                Item::getItemCode,
                                Function.identity(),
                                (a, b) -> a   // dedupe safeguard
                        ));

        if (itemMap.isEmpty()) {
            throw new IllegalStateException(
                    "No items mapped to locations for selected zone");
        }

        log.info("{} | loadItemsByZone | SUCCESS | itemCount={}",
                logId, itemMap.size());

        return itemMap;
    }


    private Map<String, SupplierItemMapper> loadSupplierItemMap(
            Map<String, Item> itemMap) {

        String logId = loginUser.getLogId();

        log.info("{} | loadSupplierItemMap | START", logId);

        Set<Integer> itemIds =
                itemMap.values()
                        .stream()
                        .map(Item::getId)
                        .collect(Collectors.toSet());

        if (itemIds.isEmpty()) {
            throw new IllegalStateException("Item list is empty");
        }

        List<SupplierItemMapper> mappings =
                supplierItemMapperRepository
                        .findByItemIdInAndIsDeleted(
                                new ArrayList<>(itemIds),
                                false
                        );

        Map<String, SupplierItemMapper> supplierItemMap =
                mappings.stream()
                        .filter(sim ->
                                sim.getItem() != null &&
                                        sim.getSupplier() != null)
                        .collect(Collectors.toMap(
                                sim -> sim.getItem().getItemCode()
                                        + "|" +
                                        sim.getSupplier().getSupplierId(),
                                Function.identity(),
                                (a, b) -> a   // dedupe safeguard
                        ));

        log.info("{} | loadSupplierItemMap | SUCCESS | mappingCount={}",
                logId, supplierItemMap.size());

        return supplierItemMap;
    }


    private Map<String, PackingHierarchyLevel> loadHierarchyLevels() {

        String logId = loginUser.getLogId();

        log.info("{} | loadHierarchyLevels | START", logId);

        List<PackingHierarchyLevel> levels =
                packingHierarchyLevelRepository
                        .findByOrganizationIdAndSubOrganizationIdAndIsDeletedAndIsActive(
                                loginUser.getOrgId(),
                                loginUser.getSubOrgId(),
                                false,
                                true
                        );

        if (levels.isEmpty()) {
            throw new IllegalStateException(
                    "Packing hierarchy levels are not configured");
        }

        Map<String, PackingHierarchyLevel> hierarchyMap =
                levels.stream()
                        .collect(Collectors.toMap(
                                PackingHierarchyLevel::getLevelCode,
                                Function.identity()
                        ));

        log.info("{} | loadHierarchyLevels | SUCCESS | levels={}",
                logId, hierarchyMap.keySet());

        return hierarchyMap;
    }


    private PackagingMaster resolvePackaging(
            Row row,
            Map<String, Integer> headerMap,
            int excelRow,
            String level) {

        String type =
                getString(row, headerMap, level + " Pack Type", excelRow);

        String subtype =
                getString(row, headerMap, level + " Pack Sub Type", excelRow);

        String dimension =
                getString(row, headerMap,
                        level + " Dimension (L x W x H)", excelRow);

        String excelUom =
                getString(row, headerMap,
                        level + " Dimension UOM", excelRow);

        Double diameter =
                getDouble(row, headerMap,
                        level + " Circumference / Diameter", excelRow);

        BigDecimal[] dims =
                DimensionKeyUtil.parseLwh(
                        dimension,
                        excelRow,
                        level + " Dimension (L x W x H)"
                );

        // 🔥 Convert EXCEL → MM
        BigDecimal lMm =
                UomConversionUtil.toMillimeter(dims[0], excelUom);
        BigDecimal wMm =
                UomConversionUtil.toMillimeter(dims[1], excelUom);
        BigDecimal hMm =
                UomConversionUtil.toMillimeter(dims[2], excelUom);
        BigDecimal dMm =
                diameter == null ? null :
                        UomConversionUtil.toMillimeter(
                                BigDecimal.valueOf(diameter), excelUom);

        BigDecimal volume =
                DimensionKeyUtil.calculateCanonicalVolume(lMm, wMm, hMm);

        String lookupKey =
                DimensionKeyUtil.buildKey(
                        type,
                        subtype,
                        volume,
                        dMm
                );


        PackagingMaster master =
                packagingMasterCache.findByKey(lookupKey);

        if (master == null) {
            log.warn("PackagingLookup | NOT FOUND | row={} | level={} | lookupKey={}",
                    excelRow, level, lookupKey);

            packagingMasterCache.logSimilar(type, subtype);

            throw error(
                    excelRow,
                    level + " Packaging",
                    "No matching packaging master found"
            );
        }

        return master;
    }



    private Double getDouble(
            Row row,
            Map<String, Integer> headerIndexMap,
            String columnName,
            int excelRowNum) {

        Integer idx = headerIndexMap.get(columnName);

        if (idx == null) {
            throw error(excelRowNum, columnName,
                    "Column not found in template");
        }

        Cell cell = row.getCell(idx, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);

        if (cell == null) {
            return null;
        }

        try {
            switch (cell.getCellType()) {

                case NUMERIC:
                    return cell.getNumericCellValue();

                case STRING:
                    String val = cell.getStringCellValue().trim();
                    if (val.isEmpty()) return null;
                    return Double.parseDouble(val);

                case FORMULA:
                    return cell.getNumericCellValue();

                case BLANK:
                    return null;

                default:
                    throw error(excelRowNum, columnName,
                            "Invalid numeric value");
            }

        } catch (NumberFormatException ex) {
            throw error(excelRowNum, columnName,
                    "Invalid number format");
        }
    }


    private ItemSupplierPackingProfileMap buildItemSupplierPackingProfileMap(
            Item item,
            Supplier supplier,
            PackingProfileConfigMaster profile) {

        ItemSupplierPackingProfileMap mapping =
                itemSupplierPackingProfileMapRepository
                        .findByOrganizationIdAndSubOrganizationIdAndItemAndSupplierAndIsDeleted(
                                loginUser.getOrgId(),
                                loginUser.getSubOrgId(),
                                item,
                                supplier,
                                false
                        )
                        .orElseGet(ItemSupplierPackingProfileMap::new);

        // =========================
        // SET COMMON FIELDS
        // =========================
        mapping.setOrganizationId(loginUser.getOrgId());
        mapping.setSubOrganizationId(loginUser.getSubOrgId());

        mapping.setItem(item);
        mapping.setSupplier(supplier);
        mapping.setPackingProfile(profile);

        mapping.setIsActive(true);
        mapping.setIsDeleted(false);

        Date now = new Date();

        if (mapping.getId() == null) {
            mapping.setCreatedBy(loginUser.getUserId());
            mapping.setCreatedOn(now);
        }

        mapping.setModifiedBy(loginUser.getUserId());
        mapping.setModifiedOn(now);

        return mapping;
    }


    private boolean isRowCompletelyEmpty(Row row, int totalColumns) {
        if (row == null) return true;

        for (int i = 0; i < totalColumns; i++) {
            Cell cell = row.getCell(i, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
            if (cell != null && cell.getCellType() != CellType.BLANK) {
                return false;
            }
        }
        return true;
    }



    private void validateByHierarchy(
            int excelRowNum,
            PackingHierarchyLevel hierarchyLevel,
            String primaryUom, Integer primaryUnits,
            String secondaryUom, Integer secondaryUnits,
            String tertiaryUom, Integer tertiaryUnits) {

        int levelOrder = hierarchyLevel.getLevelOrder();

        if (primaryUom == null || primaryUnits == null || primaryUnits <= 0) {
            throw error(excelRowNum, "Primary Pack",
                    "Primary pack is mandatory");
        }

        if (levelOrder >= 2) {
            if (secondaryUom == null || secondaryUnits == null || secondaryUnits <= 0) {
                throw error(excelRowNum, "Secondary Pack",
                        "Secondary pack mandatory for level SECONDARY / TERTIARY");
            }
        }

        if (levelOrder == 3) {
            if (tertiaryUom == null || tertiaryUnits == null || tertiaryUnits <= 0) {
                throw error(excelRowNum, "Tertiary Pack",
                        "Tertiary pack mandatory for level TERTIARY");
            }
        }
    }


    private UploadRowException error(int row, String column, String message) {
        return new UploadRowException(
                new UploadErrorDetail(row, column, message)
        );
    }

    private String getString(
            Row row,
            Map<String, Integer> headerIndexMap,
            String columnName,
            int excelRowNum) {

        Integer idx = headerIndexMap.get(columnName);

        if (idx == null) {
            throw error(
                    excelRowNum,
                    columnName,
                    "Column not found in template"
            );
        }

        Cell cell = row.getCell(idx, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
        if (cell == null) {
            return null;
        }

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();

            case NUMERIC:
                return String.valueOf((long) cell.getNumericCellValue());

            case FORMULA:
                return cell.getCellFormula();

            default:
                return null;
        }
    }


    private Integer getInteger(
            Row row,
            Map<String, Integer> headerIndexMap,
            String columnName,
            int excelRowNum) {

        Integer idx = headerIndexMap.get(columnName);

        if (idx == null) {
            throw error(
                    excelRowNum,
                    columnName,
                    "Column not found in template"
            );
        }

        Cell cell = row.getCell(idx, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
        if (cell == null) {
            return null;
        }

        try {
            switch (cell.getCellType()) {
                case NUMERIC:
                    return (int) cell.getNumericCellValue();

                case STRING:
                    String val = cell.getStringCellValue().trim();
                    return val.isEmpty() ? null : Integer.parseInt(val);

                case FORMULA:
                    return (int) cell.getNumericCellValue();

                default:
                    return null;
            }
        } catch (Exception ex) {
            throw error(
                    excelRowNum,
                    columnName,
                    "Invalid numeric value"
            );
        }
    }


    private void validateHeader(Row headerRow) {

        if (headerRow == null) {
            throw new IllegalArgumentException("Uploaded file does not contain header row");
        }

        List<String> expectedHeaders = PACKING_TEMPLATE_HEADERS;
        List<String> actualHeaders = new ArrayList<>();

        int lastCellNum = headerRow.getLastCellNum();

        for (int i = 0; i < lastCellNum; i++) {
            Cell cell = headerRow.getCell(i, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
            String headerName = cell == null ? "" : cell.getStringCellValue().trim();
            actualHeaders.add(headerName);
        }

        // -------- Size check --------
        if (actualHeaders.size() != expectedHeaders.size()) {
            throw new IllegalArgumentException(
                    String.format(
                            "Invalid template. Expected %d columns but found %d",
                            expectedHeaders.size(),
                            actualHeaders.size()
                    )
            );
        }

        // -------- Order + name check --------
        for (int i = 0; i < expectedHeaders.size(); i++) {
            String expected = expectedHeaders.get(i);
            String actual = actualHeaders.get(i);

            if (!expected.equalsIgnoreCase(actual)) {
                throw new IllegalArgumentException(
                        String.format(
                                "Invalid template header at column %d. Expected '%s' but found '%s'",
                                i + 1,
                                expected,
                                actual
                        )
                );
            }
        }
    }

    private Map<String, Integer> buildHeaderIndexMap(Row headerRow) {

        Map<String, Integer> headerMap = new HashMap<>();

        for (Cell cell : headerRow) {
            String header = cell.getStringCellValue().trim();
            headerMap.put(header, cell.getColumnIndex());
        }

        return headerMap;
    }




    private void upsertItemSupplierPackingProfile(
            Item item,
            Supplier supplier,
            PackingProfileConfigMaster profile) {

        String logId = loginUser.getLogId();

        Optional<ItemSupplierPackingProfileMap> existingOpt =
                this.itemSupplierPackingProfileMapRepository
                        .findByOrganizationIdAndSubOrganizationIdAndItemAndSupplierAndIsDeleted(
                                loginUser.getOrgId(),
                                loginUser.getSubOrgId(),
                                item,
                                supplier,
                                false
                        );

        ItemSupplierPackingProfileMap mapping;

        if (existingOpt.isPresent()) {
            // -------------------------
            // UPDATE EXISTING MAPPING
            // -------------------------
            mapping = existingOpt.get();

            mapping.setPackingProfile(profile);
            mapping.setIsActive(true);
            mapping.setIsDeleted(false);
            mapping.setModifiedBy(loginUser.getUserId());
            mapping.setModifiedOn(new Date());

            log.debug("{} | ItemSupplierPackingProfile | UPDATE | itemCode={} | supplierId={} | profileId={}",
                    logId,
                    item.getItemCode(),
                    supplier.getId(),
                    profile.getId());

        } else {
            // -------------------------
            // CREATE NEW MAPPING
            // -------------------------
            mapping = new ItemSupplierPackingProfileMap();

            mapping.setOrganizationId(loginUser.getOrgId());
            mapping.setSubOrganizationId(loginUser.getSubOrgId());
            mapping.setItem(item);
            mapping.setSupplier(supplier);
            mapping.setPackingProfile(profile);

            mapping.setIsActive(true);
            mapping.setIsDeleted(false);
            mapping.setCreatedBy(loginUser.getUserId());
            mapping.setCreatedOn(new Date());

            log.debug("{} | ItemSupplierPackingProfile | CREATE | itemCode={} | supplierId={} | profileId={}",
                    logId,
                    item.getItemCode(),
                    supplier.getId(),
                    profile.getId());
        }

        itemSupplierPackingProfileMapRepository.save(mapping);
    }


    public BaseResponse<PackingProfileListDTO> getAllPackingProfiles(
            int page,
            int size,
            String sortBy,
            String sortDir) {

        String logId = loginUser.getLogId();
        long startTime = System.currentTimeMillis();

        log.info("{} | GetAllPackingProfiles | START | page={} | size={} | sortBy={} | sortDir={}",
                logId, page, size, sortBy, sortDir);

        BaseResponse<PackingProfileListDTO> response = new BaseResponse<>();

        try {
            Sort sort = sortDir.equalsIgnoreCase("asc")
                    ? Sort.by(sortBy).ascending()
                    : Sort.by(sortBy).descending();

            Pageable pageable = PageRequest.of(page, size, sort);

            Page<PackingProfileListProjection> pageResult =
                    itemSupplierPackingProfileMapRepository
                            .findAllPackingProfiles(
                                    loginUser.getOrgId(),
                                    loginUser.getSubOrgId(),
                                    pageable
                            );

            List<PackingProfileListDTO> dtoList =
                    pageResult.getContent().stream()
                            .map(p -> new PackingProfileListDTO(
                                    p.getId(),
                                    "CONF-" + p.getId(),
                                    p.getItemName(),
                                    p.getItemCode(),
                                    p.getSupplierName(),
                                    p.getErpSupplierId(),
                                    p.getPackingHierarchyLevelCode(),
                                    p.getIsActive()
                            ))
                            .collect(Collectors.toList());

            response.setData(dtoList);
            response.setTotalPageCount(pageResult.getTotalPages());
            response.setTotalRecordCount(pageResult.getTotalElements());

            response.setStatus(1);
            response.setCode(200);
            response.setMessage("Packing configuration data fetched successfully");
            response.setLogId(logId);

            log.info("{} | GetAllPackingProfiles | SUCCESS | records={} | totalPages={} | durationMs={}",
                    logId,
                    pageResult.getTotalElements(),
                    pageResult.getTotalPages(),
                    System.currentTimeMillis() - startTime);

        } catch (Exception ex) {

            log.error("{} | GetAllPackingProfiles | FAILED", logId, ex);

            response.setStatus(0);
            response.setCode(500);
            response.setMessage("Failed to fetch packing configuration data");
            response.setLogId(logId);
            response.setData(Collections.emptyList());
            response.setTotalPageCount(0);
            response.setTotalRecordCount(0L);
        }

        return response;
    }



    @Override
    public BaseResponse<PackingProfileDetailDTO> getPackingProfileById(Long configId) {

        String logId = loginUser.getLogId();
        long startTime = System.currentTimeMillis();

        log.info("{} | GetPackingProfileById | START | configId={}",
                logId, configId);

        BaseResponse<PackingProfileDetailDTO> response = new BaseResponse<>();

        try {
            ItemSupplierPackingProfileMap mapping =
                    itemSupplierPackingProfileMapRepository
                            .findPackingProfileById(
                                    configId,
                                    loginUser.getOrgId(),
                                    loginUser.getSubOrgId()
                            )
                            .orElseThrow(() ->
                                    new IllegalArgumentException("Packing configuration not found"));

            PackingProfileConfigMaster p = mapping.getPackingProfile();
            Item item = mapping.getItem();
            Supplier supplier = mapping.getSupplier();

            PackingProfileDetailDTO dto = new PackingProfileDetailDTO(
                    p.getId(),
                    "CONF-" + p.getId(),
                    item.getItemCode(),
                    item.getName(),
                    supplier.getSupplierId(),
                    supplier.getSupplierName(),
                    p.getPackingHierarchyLevel() !=null ? p.getPackingHierarchyLevel().getLevelCode():null,
                    p.getPrimaryUnits(),
                    p.getSecondaryUnits(),
                    p.getTertiaryUnits(),
                    p.getIsActive()
            );

            response.setData(Collections.singletonList(dto));
            response.setTotalPageCount(1);
            response.setTotalRecordCount(1L);
            response.setStatus(1);
            response.setCode(200);
            response.setMessage("Packing configuration fetched successfully");
            response.setLogId(logId);

            log.info("{} | GetPackingProfileById | SUCCESS | durationMs={}",
                    logId, System.currentTimeMillis() - startTime);

        } catch (IllegalArgumentException ex) {

            log.warn("{} | GetPackingProfileById | NOT FOUND | configId={}",
                    logId, configId);

            response.setStatus(0);
            response.setCode(404);
            response.setMessage(ex.getMessage());
            response.setLogId(logId);
            response.setData(Collections.emptyList());
            response.setTotalPageCount(0);
            response.setTotalRecordCount(0L);

        } catch (Exception ex) {

            log.error("{} | GetPackingProfileById | FAILED", logId, ex);

            response.setStatus(0);
            response.setCode(500);
            response.setMessage("Failed to fetch packing configuration");
            response.setLogId(logId);
            response.setData(Collections.emptyList());
            response.setTotalPageCount(0);
            response.setTotalRecordCount(0L);
        }

        return response;
    }

    @Transactional
    @Override
    public BaseResponse<ItemSupplierPackingProfileMap> updateItemSupplierPackingProfile(
            Long mappingId,
            ItemSupplierPackingProfileUpdateRequest request) {

        String logId = loginUser.getLogId();
        long startTime = System.currentTimeMillis();

        log.info(
                "{} | UpdateItemSupplierPackingProfile | START | mappingId={} | profileId={} | hierarchyLevelId={}",
                logId, mappingId, request.getPackingProfileId(), request.getPackingHierarchyLevelId()
        );

        BaseResponse<ItemSupplierPackingProfileMap> response = new BaseResponse<>();

        try {
            // =====================================================
            // 1️⃣ FETCH ITEM–SUPPLIER MAPPING
            // =====================================================
            ItemSupplierPackingProfileMap mapping =
                    itemSupplierPackingProfileMapRepository
                            .findById(Math.toIntExact(mappingId))
                            .filter(m ->
                                    !m.getIsDeleted()
                                            && m.getOrganizationId().equals(loginUser.getOrgId())
                                            && m.getSubOrganizationId().equals(loginUser.getSubOrgId()))
                            .orElseThrow(() ->
                                    new IllegalArgumentException(
                                            "Item-Supplier packing mapping not found"));

            // =====================================================
            // 2️⃣ FETCH PACKING PROFILE MASTER
            // =====================================================
            PackingProfileConfigMaster profile =
                    packingProfileRepo
                            .findById(Math.toIntExact(request.getPackingProfileId()))
                            .filter(p ->
                                    !p.getIsDeleted()
                                            && p.getOrganizationId().equals(loginUser.getOrgId())
                                            && p.getSubOrganizationId().equals(loginUser.getSubOrgId())
                                            && Boolean.TRUE.equals(p.getIsActive()))
                            .orElseThrow(() ->
                                    new IllegalArgumentException(
                                            "Packing profile master not found or inactive"));

            // =====================================================
            // 3️⃣ FETCH PACKING HIERARCHY LEVEL
            // =====================================================
            PackingHierarchyLevel hierarchyLevel =
                    this.packingHierarchyLevelRepository
                            .findById(request.getPackingHierarchyLevelId())
                            .filter(h ->
                                    !h.getIsDeleted()
                                            && h.getOrganizationId().equals(loginUser.getOrgId())
                                            && h.getSubOrganizationId().equals(loginUser.getSubOrgId())
                                            && Boolean.TRUE.equals(h.getIsActive()))
                            .orElseThrow(() ->
                                    new IllegalArgumentException(
                                            "Packing hierarchy level not found or inactive"));

            // =====================================================
            // 4️⃣ VALIDATE PACKING DATA AGAINST HIERARCHY
            // =====================================================
            int levelOrder = hierarchyLevel.getLevelOrder(); // 1,2,3

            if (request.getPrimaryUom() == null || request.getPrimaryUnits() == null
                    || request.getPrimaryUnits() <= 0) {
                throw new IllegalArgumentException("Primary pack UOM and units are mandatory");
            }

            if (levelOrder >= 2) {
                if (request.getSecondaryUom() == null || request.getSecondaryUnits() == null
                        || request.getSecondaryUnits() <= 0) {
                    throw new IllegalArgumentException(
                            "Secondary pack details required for hierarchy level >= 2");
                }
            }

            if (levelOrder == 3) {
                if (request.getTertiaryUom() == null || request.getTertiaryUnits() == null
                        || request.getTertiaryUnits() <= 0) {
                    throw new IllegalArgumentException(
                            "Tertiary pack details required for hierarchy level = 3");
                }
            }

            // =====================================================
            // 6️⃣ UPDATE PACKING PROFILE MASTER
            // =====================================================
            profile.setPackingHierarchyLevel(hierarchyLevel);

            profile.setPrimaryUnits(request.getPrimaryUnits());
            profile.setSecondaryUnits(levelOrder >= 2 ? request.getSecondaryUnits() : null);

            profile.setTertiaryUnits(levelOrder == 3 ? request.getTertiaryUnits() : null);

            profile.setMoqLevel(request.getMoqLevel());
            profile.setMoqQty(request.getMoqQty());

            profile.setModifiedBy(loginUser.getUserId());
            profile.setModifiedOn(new Date());

            packingProfileRepo.save(profile);

            // =====================================================
            // 7️⃣ UPDATE MAPPING
            // =====================================================
            mapping.setPackingProfile(profile);

            mapping.setModifiedBy(loginUser.getUserId());
            mapping.setModifiedOn(new Date());

            itemSupplierPackingProfileMapRepository.save(mapping);

            // =====================================================
            // 8️⃣ RESPONSE
            // =====================================================
            response.setStatus(1);
            response.setCode(200);
            response.setMessage("Item-Supplier packing profile updated successfully");
            response.setLogId(logId);
            response.setData(Collections.singletonList(mapping));
            response.setTotalPageCount(1);
            response.setTotalRecordCount(1L);

            log.info("{} | UpdateItemSupplierPackingProfile | SUCCESS | durationMs={}",
                    logId, System.currentTimeMillis() - startTime);

        } catch (IllegalArgumentException ex) {

            log.warn("{} | UpdateItemSupplierPackingProfile | VALIDATION FAILED | {}",
                    logId, ex.getMessage());

            response.setStatus(0);
            response.setCode(400);
            response.setMessage(ex.getMessage());
            response.setLogId(logId);
            response.setData(Collections.emptyList());
            response.setTotalPageCount(0);
            response.setTotalRecordCount(0L);

        } catch (Exception ex) {

            log.error("{} | UpdateItemSupplierPackingProfile | FAILED", logId, ex);

            response.setStatus(0);
            response.setCode(500);
            response.setMessage("Failed to update item-supplier packing profile");
            response.setLogId(logId);
            response.setData(Collections.emptyList());
            response.setTotalPageCount(0);
            response.setTotalRecordCount(0L);
        }

        return response;
    }





}
