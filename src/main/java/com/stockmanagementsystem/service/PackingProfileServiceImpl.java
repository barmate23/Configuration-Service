package com.stockmanagementsystem.service;

import com.stockmanagementsystem.entity.*;
import com.stockmanagementsystem.repository.*;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PackingProfileServiceImpl implements PackingProfileService {

    private final PackagingMasterRepository packagingRepo;
    private final PackingHierarchyLevelRepository hierarchyRepo;
    private final PackingProfileLevelRepository profileRepo;
    private final LocationRepository locationRepo;
    private final SupplierItemMapperRepository mapperRepo;

    // ================= TEMPLATE =================
    @Override
    public ByteArrayInputStream generateTemplate(Integer zoneId) {

        try (Workbook workbook = new XSSFWorkbook()) {

            // 1. Fetch Hierarchy Levels
            List<PackingHierarchyLevel> targetLevels = hierarchyRepo.findByIsDeletedFalse()
                    .stream()
                    .sorted(Comparator.comparing(PackingHierarchyLevel::getLevelOrder))
                    .collect(Collectors.toList());

            if (targetLevels.isEmpty()) {
                throw new RuntimeException("No packing hierarchy levels found in database.");
            }

            // 2. Fetch Items from Zone Locations
            List<Location> locations = locationRepo.findByZoneIdAndIsDeleted(zoneId, false);
            List<Integer> itemIds = locations.stream()
                    .map(l -> l.getItem() != null ? l.getItem().getId() : null)
                    .filter(Objects::nonNull)
                    .distinct()
                    .collect(Collectors.toList());

            // 3. Fetch Supplier Item Mappings
            List<SupplierItemMapper> mappers = new ArrayList<>();
            if (!itemIds.isEmpty()) {
                mappers = mapperRepo.findByItemIdInAndIsDeleted(itemIds, false);
                // Sort by Item Name to keep the listing item-wise
                mappers.sort(Comparator.comparing(
                        m -> (m.getItem() != null && m.getItem().getName() != null) ? m.getItem().getName() : ""));
            }

            // 4. Build Header
            Sheet sheet = workbook.createSheet("Packing Profile");
            List<String> columnList = new ArrayList<>();
            columnList.add("Supplier Name");
            columnList.add("Item Name");
            columnList.add("Level Type");

            for (PackingHierarchyLevel level : targetLevels) {
                String name = level.getLevelCode();
                columnList.add(name + " Packaging");
                columnList.add(name + " Units");
                columnList.add(name + " Order");
            }

            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < columnList.size(); i++) {
                headerRow.createCell(i).setCellValue(columnList.get(i));
            }

            // 5. Populate Rows with Mappers
            int rowIndex = 1;
            for (SupplierItemMapper mapper : mappers) {
                Row row = sheet.createRow(rowIndex++);
                row.createCell(0)
                        .setCellValue(mapper.getSupplier() != null ? mapper.getSupplier().getSupplierName() : "N/A");
                row.createCell(1).setCellValue(mapper.getItem() != null ? mapper.getItem().getName() : "N/A");
            }

            // If no mappers found, provide a few empty rows for manual entry
            if (mappers.isEmpty()) {
                for (int i = 0; i < 10; i++) {
                    sheet.createRow(rowIndex++);
                }
            }

            // 6. Dropdowns & "Disabling" Logic
            DataValidationHelper helper = sheet.getDataValidationHelper();

            // Level Type Dropdown (Column C, Index 2)
            String[] levelOptions = targetLevels.stream().map(PackingHierarchyLevel::getLevelCode)
                    .toArray(String[]::new);
            DataValidationConstraint levelConstraint = helper.createExplicitListConstraint(levelOptions);
            DataValidation levelValidation = helper.createValidation(levelConstraint,
                    new CellRangeAddressList(1, 2000, 2, 2));
            sheet.addValidationData(levelValidation);

            // Packaging Dropdown (Global hidden sheet)
            Sheet hiddenSheet = workbook.createSheet("dropdown_data");
            List<String> packagingNames = packagingRepo.findByIsDeletedFalse().stream()
                    .map(PackagingMaster::getPackagingName).collect(Collectors.toList());
            for (int i = 0; i < packagingNames.size(); i++) {
                hiddenSheet.createRow(i).createCell(0).setCellValue(packagingNames.get(i));
            }
            Name namedRange = workbook.createName();
            namedRange.setNameName("PACKAGING_LIST");
            namedRange.setRefersToFormula("dropdown_data!$A$1:$A$" + packagingNames.size());
            DataValidationConstraint packagingConstraint = helper.createFormulaListConstraint("PACKAGING_LIST");

            SheetConditionalFormatting sheetCF = sheet.getSheetConditionalFormatting();

            for (int i = 0; i < targetLevels.size(); i++) {
                int startCol = 3 + (i * 3); // Shifted by 3 due to Supplier + Item + LevelType columns
                int endCol = startCol + 2;
                String levelCode = targetLevels.get(i).getLevelCode();

                // Apply Packaging Dropdown to each level's packaging column
                CellRangeAddressList packAddress = new CellRangeAddressList(1, 2000, startCol, startCol);
                DataValidation packVal = helper.createValidation(packagingConstraint, packAddress);
                sheet.addValidationData(packVal);

                // Disable logic for levels above Primary
                if (i > 0) {
                    // Formula to check if the selected level in Column C allows this column
                    StringBuilder formulaBuilder = new StringBuilder("OR(");
                    for (int j = 0; j < i; j++) {
                        formulaBuilder.append("$C2=\"").append(targetLevels.get(j).getLevelCode()).append("\",");
                    }
                    formulaBuilder.setLength(formulaBuilder.length() - 1);
                    formulaBuilder.append(")");
                    String disableFormula = formulaBuilder.toString();

                    // Visual Hiding (White on White)
                    ConditionalFormattingRule rule = sheetCF.createConditionalFormattingRule(disableFormula);
                    PatternFormatting fill = rule.createPatternFormatting();
                    fill.setFillBackgroundColor(IndexedColors.WHITE.index);
                    fill.setFillPattern(PatternFormatting.SOLID_FOREGROUND);
                    FontFormatting font = rule.createFontFormatting();
                    font.setFontColorIndex(IndexedColors.WHITE.index);

                    sheetCF.addConditionalFormatting(
                            new CellRangeAddress[] { new CellRangeAddress(1, 2000, startCol, endCol) }, rule);

                    // Block Input
                    DataValidationConstraint blockConstraint = helper
                            .createCustomConstraint("NOT(" + disableFormula + ")");
                    DataValidation blockValidation = helper.createValidation(blockConstraint,
                            new CellRangeAddressList(1, 2000, startCol, endCol));
                    blockValidation.setErrorStyle(DataValidation.ErrorStyle.STOP);
                    blockValidation.createErrorBox("Not Applicable", "Hidden for " + levelCode);
                    sheet.addValidationData(blockValidation);
                }
            }

            workbook.setSheetHidden(workbook.getSheetIndex(hiddenSheet), true);

            // Set generous column widths
            sheet.setColumnWidth(0, 10000); // Supplier Name
            sheet.setColumnWidth(1, 10000); // Item Name
            sheet.setColumnWidth(2, 5000); // Level Type

            // Set widths for all dynamic columns (Packaging, Units, Order)
            for (int i = 3; i < columnList.size(); i++) {
                sheet.setColumnWidth(i, 6000);
            }

            sheet.createFreezePane(3, 1);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());

        } catch (Exception e) {
            throw new RuntimeException("Template generation failed: " + e.getMessage(), e);
        }
    }

    // ================= UPLOAD =================
    @Override
    public void upload(MultipartFile file) {

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {

            Sheet sheet = workbook.getSheetAt(0);

            // ===== PRELOAD DATA (OPTIMIZATION) =====
            Map<String, PackagingMaster> packagingMap = packagingRepo.findByIsDeletedFalse()
                    .stream()
                    .collect(Collectors.toMap(
                            p -> p.getPackagingName().toLowerCase(),
                            Function.identity()));

            Map<String, PackingHierarchyLevel> hierarchyMap = hierarchyRepo.findByIsDeletedFalse()
                    .stream()
                    .collect(Collectors.toMap(
                            h -> h.getLevelCode().toUpperCase(),
                            Function.identity()));

            List<PackingProfileLevel> saveList = new ArrayList<>();

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {

                Row row = sheet.getRow(i);
                if (row == null)
                    continue;

                String supplierName = getString(row.getCell(0));
                String itemName = getString(row.getCell(1));
                String levelType = getString(row.getCell(2));

                if (levelType == null)
                    continue;

                // Fetch mapper individually as per user request
                SupplierItemMapper mapper = mapperRepo
                        .findByIsDeletedFalseAndSupplierSupplierNameAndItemName(supplierName, itemName);
                if (mapper == null) {
                    throw new RuntimeException("No mapping found for Supplier: " + supplierName + " and Item: "
                            + itemName + " at row " + (i + 1));
                }

                switch (levelType.toUpperCase()) {

                    case "PRIMARY":
                        validatePrimary(row);
                        saveList.add(build(row, "PRIMARY", 3, 4, 5, packagingMap, hierarchyMap, mapper, "PRIMARY"));
                        break;

                    case "SECONDARY":
                        validatePrimary(row);
                        validateSecondary(row);

                        saveList.add(build(row, "PRIMARY", 3, 4, 5, packagingMap, hierarchyMap, mapper, "SECONDARY"));
                        saveList.add(build(row, "SECONDARY", 6, 7, 8, packagingMap, hierarchyMap, mapper, "SECONDARY"));
                        break;

                    case "TERTIARY":
                        validatePrimary(row);
                        validateSecondary(row);
                        validateTertiary(row);

                        saveList.add(build(row, "PRIMARY", 3, 4, 5, packagingMap, hierarchyMap, mapper, "TERTIARY"));
                        saveList.add(build(row, "SECONDARY", 6, 7, 8, packagingMap, hierarchyMap, mapper, "TERTIARY"));
                        saveList.add(build(row, "TERTIARY", 9, 10, 11, packagingMap, hierarchyMap, mapper, "TERTIARY"));
                        break;

                    default:
                        throw new RuntimeException("Invalid level type at row " + (i + 1));
                }
            }

            profileRepo.saveAll(saveList);

        } catch (Exception e) {
            throw new RuntimeException("Upload failed: " + e.getMessage(), e);
        }
    }

    // ================= BUILD ENTITY =================
    private PackingProfileLevel build(Row row, String levelCode,
            int packCol, int unitCol, int orderCol,
            Map<String, PackagingMaster> packagingMap,
            Map<String, PackingHierarchyLevel> hierarchyMap, SupplierItemMapper mapper, String packingLevelCurrent) {

        String packagingName = getString(row.getCell(packCol));
        Integer units = getInteger(row.getCell(unitCol));
        Integer order = getInteger(row.getCell(orderCol));

        if (packingLevelCurrent.equalsIgnoreCase("PRIMARY")) {
            mapper.setMoq(units);
        } else if (packingLevelCurrent.equalsIgnoreCase("SECONDARY")) {
            mapper.setMoq(getInteger(row.getCell(4)) * getInteger(row.getCell(7)));
        } else if (packingLevelCurrent.equalsIgnoreCase("TERTIARY")) {
            mapper.setMoq(getInteger(row.getCell(4)) * getInteger(row.getCell(7)) * getInteger(row.getCell(10)));
        }
        mapperRepo.save(mapper);
        PackagingMaster packaging = packagingMap.get(packagingName.toLowerCase());
        if (packaging == null) {
            throw new RuntimeException("Invalid packaging: " + packagingName);
        }

        PackingHierarchyLevel hierarchy = hierarchyMap.get(levelCode);

        PackingProfileLevel entity = new PackingProfileLevel();
        entity.setSupplierItemMapper(mapper);
        entity.setPackagingMaster(packaging);
        entity.setHierarchyLevel(hierarchy);
        entity.setUnitsPerParent(units);
        entity.setLevelOrder(order);
        entity.setIsActive(true);
        entity.setIsDeleted(false);
        entity.setCreatedOn(new Date());

        return entity;
    }

    // ================= VALIDATIONS =================
    private void validatePrimary(Row row) {
        if (getString(row.getCell(3)) == null)
            throw new RuntimeException("Primary packaging missing at row " + (row.getRowNum() + 1));
    }

    private void validateSecondary(Row row) {
        if (getString(row.getCell(6)) == null)
            throw new RuntimeException("Secondary packaging missing at row " + (row.getRowNum() + 1));
    }

    private void validateTertiary(Row row) {
        if (getString(row.getCell(9)) == null)
            throw new RuntimeException("Tertiary packaging missing at row " + (row.getRowNum() + 1));
    }

    // ================= UTIL =================
    private String getString(Cell cell) {
        if (cell == null)
            return null;
        return cell.getCellType() == CellType.STRING
                ? cell.getStringCellValue().trim()
                : String.valueOf((int) cell.getNumericCellValue());
    }

    private Integer getInteger(Cell cell) {
        if (cell == null)
            return null;
        return (int) cell.getNumericCellValue();
    }
}