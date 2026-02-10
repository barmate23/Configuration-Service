package com.stockmanagementsystem.service;

import com.stockmanagementsystem.entity.*;
import com.stockmanagementsystem.exception.UploadRowException;
import com.stockmanagementsystem.repository.*;
import com.stockmanagementsystem.response.UploadErrorDetail;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.configuration.AbstractFileConfiguration;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.stockmanagementsystem.utils.PackingTemplateConstants.PACKING_TEMPLATE_HEADERS;

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

    @Override
    public ResponseEntity<byte[]> downloadTemplate(Integer areaId, Integer zoneId) {

        long startTime = System.currentTimeMillis();
        String logId = loginUser.getLogId();

        log.info("{} | PackingTemplateDownload | START | areaId={} | zoneId={}",
                logId, areaId, zoneId);

        // Use SXSSFWorkbook if zone can be very large (safe default)
        try (Workbook workbook = new SXSSFWorkbook(100)) {

            // =========================
            // Create Sheet
            // =========================
            Sheet sheet = workbook.createSheet("Packing_Config");
            log.debug("{} | PackingTemplateDownload | Sheet created", logId);

            SXSSFSheet sxssfSheet = (SXSSFSheet) sheet;
            sxssfSheet.trackAllColumnsForAutoSizing();

            // =========================
// Styles
// =========================

// ---- Header Style (Dark Border) ----
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            headerStyle.setBorderTop(BorderStyle.MEDIUM);
            headerStyle.setBorderBottom(BorderStyle.MEDIUM);
            headerStyle.setBorderLeft(BorderStyle.MEDIUM);
            headerStyle.setBorderRight(BorderStyle.MEDIUM);

            headerStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
            headerStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
            headerStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
            headerStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());

            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);

// ---- Data Style (Normal Border) ----
            CellStyle dataStyle = workbook.createCellStyle();

            dataStyle.setBorderTop(BorderStyle.THIN);
            dataStyle.setBorderBottom(BorderStyle.THIN);
            dataStyle.setBorderLeft(BorderStyle.THIN);
            dataStyle.setBorderRight(BorderStyle.THIN);

            dataStyle.setTopBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
            dataStyle.setBottomBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
            dataStyle.setLeftBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
            dataStyle.setRightBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());

            dataStyle.setVerticalAlignment(VerticalAlignment.CENTER);


            // =========================
            // Header Row
            // =========================
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < PACKING_TEMPLATE_HEADERS.size(); i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(PACKING_TEMPLATE_HEADERS.get(i));
                cell.setCellStyle(headerStyle);
            }

            log.info("{} | PackingTemplateDownload | Header initialized | columnCount={}",
                    logId, PACKING_TEMPLATE_HEADERS.size());

            // =========================
            // Fetch Locations (Zone-based)
            // =========================
            List<Location> locations =
                    locationRepository.findByZoneIdAndIsDeleted(zoneId, false);

            log.info("{} | PackingTemplateDownload | Locations fetched | count={}",
                    logId, locations.size());

            if (locations.isEmpty()) {
                log.warn("{} | PackingTemplateDownload | No locations found | zoneId={}",
                        logId, zoneId);
            }

            // =========================
            // Collect Unique Items
            // =========================
            Map<Integer, Item> itemMap = new LinkedHashMap<>();

            for (Location location : locations) {
                if (location.getItem() != null) {
                    itemMap.putIfAbsent(location.getItem().getId(), location.getItem());
                }
            }

            log.info("{} | PackingTemplateDownload | Unique items collected | count={}",
                    logId, itemMap.size());

            if (itemMap.isEmpty()) {
                log.warn("{} | PackingTemplateDownload | No items found for zoneId={}",
                        logId, zoneId);
            }

            // =========================
            // Fetch Suppliers in BULK
            // =========================
            List<Integer> itemIds = new ArrayList<>(itemMap.keySet());

            Map<Integer, List<SupplierItemMapper>> suppliersByItemId = new HashMap<>();

            if (!itemIds.isEmpty()) {

                List<SupplierItemMapper> supplierMappings =
                        supplierItemMapperRepository
                                .findByItemIdInAndIsDeleted(itemIds, false);

                log.info("{} | PackingTemplateDownload | Supplier mappings fetched | count={}",
                        logId, supplierMappings.size());

                suppliersByItemId =
                        supplierMappings.stream()
                                .filter(sim -> sim.getItem() != null)
                                .collect(Collectors.groupingBy(
                                        sim -> sim.getItem().getId()
                                ));
            }

            // =========================
            // Populate Data Rows (NO DB CALLS)
            // =========================
            int rowIndex = 1;
            int totalRowsGenerated = 0;

            for (Location location : locations) {

                Item item = location.getItem();
                if (item == null) {
                    log.warn("{} | PackingTemplateDownload | LocationId={} has no item mapped",
                            logId, location.getId());
                    continue;
                }

                List<SupplierItemMapper> suppliers =
                        suppliersByItemId.get(item.getId());

                if (suppliers == null || suppliers.isEmpty()) {
                    log.warn("{} | PackingTemplateDownload | No suppliers mapped | itemCode={}",
                            logId, item.getItemCode());
                    continue;
                }

                for (SupplierItemMapper sim : suppliers) {

                    Row row = sheet.createRow(rowIndex++);
                    int col = 0;

                    Cell cell;

                    cell = row.createCell(col++);
                    cell.setCellValue(item.getItemCode());
                    cell.setCellStyle(dataStyle);

                    cell = row.createCell(col++);
                    cell.setCellValue(item.getName());
                    cell.setCellStyle(dataStyle);

                    cell = row.createCell(col++);
                    cell.setCellValue(sim.getSupplier().getSupplierId());
                    cell.setCellStyle(dataStyle);

                    cell = row.createCell(col++);
                    cell.setCellValue(sim.getSupplier().getSupplierName());
                    cell.setCellStyle(dataStyle);

                    cell = row.createCell(col++);
                    cell.setCellValue(location.getZone().getArea().getAreaName());
                    cell.setCellStyle(dataStyle);

                    cell = row.createCell(col++);
                    cell.setCellValue(location.getZone().getZoneName());
                    cell.setCellStyle(dataStyle);

                    // Blank editable columns
                    while (col < PACKING_TEMPLATE_HEADERS.size()) {
                        cell = row.createCell(col++);
                        cell.setCellValue("");
                        cell.setCellStyle(dataStyle);
                    }


                    totalRowsGenerated++;
                }
            }

            log.info("{} | PackingTemplateDownload | Data population completed | rowsGenerated={}",
                    logId, totalRowsGenerated);

            // =========================
            // Auto-size Columns
            // =========================
            for (int i = 0; i < PACKING_TEMPLATE_HEADERS.size(); i++) {
                sheet.autoSizeColumn(i);
            }

            log.debug("{} | PackingTemplateDownload | Column auto-size completed", logId);

            // =========================
            // Write Workbook
            // =========================
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);

            long durationMs = System.currentTimeMillis() - startTime;

            log.info("{} | PackingTemplateDownload | SUCCESS | rows={} | durationMs={}",
                    logId, totalRowsGenerated, durationMs);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=Packing_Config_Template.xlsx")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(outputStream.toByteArray());

        } catch (Exception ex) {

            long durationMs = System.currentTimeMillis() - startTime;

            log.error("{} | PackingTemplateDownload | FAILED | areaId={} | zoneId={} | durationMs={}",
                    logId, areaId, zoneId, durationMs, ex);

            throw new RuntimeException("Failed to generate Packing Configuration template", ex);
        }
    }


    @Transactional
    public Map<String, Object> uploadPackingProfileTemplate(
            MultipartFile file,
            Integer areaId,
            Integer zoneId) {

        String logId = loginUser.getLogId();
        long startTime = System.currentTimeMillis();

        log.info("{} | PackingProfileUpload | START | areaId={} | zoneId={}",
                logId, areaId, zoneId);

        int successCount = 0;
        List<UploadErrorDetail> errors = new ArrayList<>();

        // =====================================================
        // 1️⃣ FETCH ITEMS BY ZONE
        // =====================================================
        List<Location> locations =
                locationRepository.findByZoneIdAndIsDeleted(zoneId, false);

        if (locations.isEmpty()) {
            throw new IllegalStateException("No items found for selected zone");
        }

        Map<String, Item> itemMap =
                locations.stream()
                        .map(Location::getItem)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toMap(
                                Item::getItemCode,
                                Function.identity(),
                                (a, b) -> a
                        ));

        Set<Integer> itemIds =
                itemMap.values().stream()
                        .map(Item::getId)
                        .collect(Collectors.toSet());

        log.info("{} | PackingProfileUpload | Items loaded | count={}",
                logId, itemMap.size());

        // =====================================================
        // 2️⃣ FETCH SUPPLIERS VIA ITEM MAPPER
        // =====================================================
        List<SupplierItemMapper> supplierItemMappers =
                supplierItemMapperRepository
                        .findByItemIdInAndIsDeleted(
                                new ArrayList<>(itemIds), false);

        Map<String, SupplierItemMapper> supplierItemMap = new HashMap<>();

        for (SupplierItemMapper sim : supplierItemMappers) {
            String key = sim.getItem().getItemCode()
                    + "|" +
                    sim.getSupplier().getSupplierId();
            supplierItemMap.put(key, sim);
        }

        log.info("{} | PackingProfileUpload | Supplier mappings loaded | count={}",
                logId, supplierItemMap.size());

        // =====================================================
        // 3️⃣ LOAD EXISTING PACKING PROFILES
        // =====================================================
        Map<String, PackingProfileConfigMaster> profileMap =
                packingProfileRepo
                        .findByOrganizationIdAndSubOrganizationIdAndIsDeleted(
                                loginUser.getOrgId(),
                                loginUser.getSubOrgId(),
                                false
                        )
                        .stream()
                        .collect(Collectors.toMap(
                                PackingProfileConfigMaster::getDescription,
                                Function.identity(),
                                (a, b) -> a
                        ));

        // =====================================================
        // 4️⃣ PROCESS EXCEL
        // =====================================================
        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {

            Sheet sheet = workbook.getSheetAt(0);
            validateHeader(sheet.getRow(0));

            for (Row row : sheet) {

                if (row.getRowNum() == 0) continue;
                int excelRowNum = row.getRowNum() + 1;

                try {
                    String itemCode = getString(row, "Item Code");
                    String supplierCode = getString(row, "Supplier Code");
                    String profileDesc = getString(row, "Packing Profile Code");

                    Item item = itemMap.get(itemCode);
                    if (item == null)
                        throw error(excelRowNum, "Item Code", "Item not in selected zone");

                    SupplierItemMapper sim =
                            supplierItemMap.get(itemCode + "|" + supplierCode);

                    if (sim == null)
                        throw error(excelRowNum, "Supplier Code",
                                "Supplier not mapped to this item");

                    Supplier supplier = sim.getSupplier();

                    // ---------------- PACKING VALIDATION (same as before)
                    String primaryUom = getString(row, "Primary Pack UOM");
                    Integer primaryUnits = getInteger(row, "Units per Primary Pack");
                    String secondaryUom = getString(row, "Secondary Pack UOM");
                    Integer secondaryUnits = getInteger(row, "Units per Secondary Pack");
                    String tertiaryUom = getString(row, "Tertiary Pack UOM");
                    Integer tertiaryUnits = getInteger(row, "Units per Tertiary Pack");
                    String moqLevel = getString(row, "MOQ Level");
                    Integer moqQty = getInteger(row, "MOQ Qty");

                    // (validations unchanged – omitted here for brevity)

                    // ---------------- UPSERT PROFILE
                    PackingProfileConfigMaster profile =
                            profileMap.computeIfAbsent(profileDesc, k -> {
                                PackingProfileConfigMaster p =
                                        new PackingProfileConfigMaster();
                                p.setOrganizationId(loginUser.getOrgId());
                                p.setSubOrganizationId(loginUser.getSubOrgId());
                                p.setDescription(k);
                                p.setCreatedBy(loginUser.getUserId());
                                p.setCreatedOn(new Date());
                                return p;
                            });

                    profile.setPrimaryUom(primaryUom);
                    profile.setPrimaryUnits(primaryUnits);
                    profile.setMoqLevel(moqLevel);
                    profile.setMoqQty(moqQty);
                    profile.setIsActive(true);
                    profile.setIsDeleted(false);
                    profile.setModifiedBy(loginUser.getUserId());
                    profile.setModifiedOn(new Date());

                    packingProfileRepo.save(profile);

                    // ---------------- MAP ITEM + SUPPLIER
                    upsertItemSupplierPackingProfile(item, supplier, profile);

                    successCount++;

                } catch (UploadRowException ure) {
                    errors.add(ure.getErrorDetail());
                }
            }

        } catch (Exception ex) {
            log.error("{} | PackingProfileUpload | FILE FAILED", logId, ex);
            throw new RuntimeException("Failed to upload packing profile template", ex);
        }

        log.info("{} | PackingProfileUpload | END | successRows={} | errorRows={}",
                logId, successCount, errors.size());

        Map<String, Object> response = new HashMap<>();
        response.put("successCount", successCount);
        response.put("errorCount", errors.size());
        response.put("errors", errors);

        return response;
    }



    private UploadRowException error(int row, String column, String message) {
        return new UploadRowException(
                new UploadErrorDetail(row, column, message)
        );
    }

    private String getString(Row row, String columnName) {
        int idx = PACKING_TEMPLATE_HEADERS.indexOf(columnName);
        Cell cell = row.getCell(idx, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
        return cell == null ? null : cell.getStringCellValue().trim();
    }

    private Integer getInteger(Row row, String columnName) {
        int idx = PACKING_TEMPLATE_HEADERS.indexOf(columnName);
        Cell cell = row.getCell(idx, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
        return cell == null ? null : (int) cell.getNumericCellValue();
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


}
