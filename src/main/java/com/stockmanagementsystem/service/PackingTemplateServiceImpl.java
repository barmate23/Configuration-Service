package com.stockmanagementsystem.service;

import com.stockmanagementsystem.entity.Item;
import com.stockmanagementsystem.entity.Location;
import com.stockmanagementsystem.entity.LoginUser;
import com.stockmanagementsystem.entity.SupplierItemMapper;
import com.stockmanagementsystem.repository.LocationRepository;
import com.stockmanagementsystem.repository.SupplierItemMapperRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.*;
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
                    cell.setCellValue(item.getErpItemId());
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



}
