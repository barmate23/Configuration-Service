package com.stockmanagementsystem.validation;

import com.stockmanagementsystem.entity.*;
import com.stockmanagementsystem.repository.*;
import com.stockmanagementsystem.response.ExcellHeaderValidatorResponse;
import com.stockmanagementsystem.response.ValidationResultResponse;
import com.stockmanagementsystem.utils.ServiceConstants;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

@Component
public class Validations extends ServiceConstants {

    @Autowired
    private LocationRepository locationRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private SupplierRepository supplierRepository;
    @Autowired
    StoreRepository storeRepository;

    @Autowired
    ZoneRepository zoneRepository;

    @Autowired
    AreaRepository areaRepository;

    @Autowired
    EquipmentRepository equipmentRepository;

    @Autowired
    LoginUser loginUser;

    @Autowired
    StoreNameRepository storeNameRepository;

    @Autowired
    PurchaseOrderHeadRepository purchaseOrderHeadRepository;
    @Autowired
    BomHeadRepository bomHeadRepository;

    @Autowired
    BomLineRepository bomLineRepository;

    @Autowired
    PPEHeadRepository ppeHeadRepository;
    @Autowired
    OrganizationRepository organizationRepository;

    public boolean isValidEmail(String email) {
        // Simple email validation using regex
        return email != null && email.matches("^[\\w.-]+@[a-zA-Z\\d.-]+\\.[a-zA-Z]{2,}$");
    }
    public List<ValidationResultResponse> resultResponseList=new ArrayList<>();

    public String getCellStringValue(Row row, int cellIndex, List<ValidationResultResponse> resultResponses, String type, List<String> headerNames) {
        Cell cell = row.getCell(cellIndex, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
        String cellValue=null;
        if (cell == null && !headerNames.get(cellIndex).equalsIgnoreCase(ServiceConstants.STORE_ID)) {
//            resultResponses.add(new ValidationResultResponse(type, (row.getRowNum() + 1), headerNames.get(cellIndex), "Data value found null"));
            return null;
        }
        if(cell!=null){
            cell.setCellType(CellType.STRING);
            cellValue= cell.getStringCellValue().trim();
        }

//        if (!cellValue.matches("^[a-zA-Z0-9]+( [a-zA-Z0-9]+)*$")){
//            resultResponses.add(new ValidationResultResponse(type, (row.getRowNum() + 1), headerNames.get(cellIndex), "Data value is invalid"));
//        }
        return cellValue;
    }

    public Integer getCellIntegerValue(Row row, int cellIndex, List<ValidationResultResponse> resultResponses, String type, List<String> headerNames) {

        Cell cell = row.getCell(cellIndex, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);

        if (cell == null) {
//            resultResponses.add(new ValidationResultResponse(type, (row.getRowNum() + 1), headerNames.get(cellIndex), "Data value found null"));
        }
        if(cell!=null && cell.getCellType()!=null) {
            CellType cellType = cell.getCellType();
            if (cellType == CellType.NUMERIC) {
                // Check if the numeric cell value is an integer
                if (cell.getNumericCellValue() % 1 == 0) {
                    return (int) cell.getNumericCellValue();
                } else {
                    // Return null for non-integer numeric values
                    resultResponses.add(new ValidationResultResponse(type, (row.getRowNum() + 1), headerNames.get(cellIndex), "Data must be numeric value"));
                }
            } else {
                resultResponses.add(new ValidationResultResponse(type, (row.getRowNum() + 1), headerNames.get(cellIndex), "Data must be numeric value"));
            }
        }
        return null;
    }
    public Date getCellDateValue(Row row, int cellIndex, List<ValidationResultResponse> resultResponses, String type, List<String> headerNames) {
        Cell cell = row.getCell(cellIndex, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);

        if (cell == null) {
            resultResponses.add(new ValidationResultResponse(type, (row.getRowNum() + 1), headerNames.get(cellIndex), "Data value found null"));
            return null;
        }
        if(cell!=null && cell.getCellType()!=null) {
            CellType cellType = cell.getCellType();
            if (cellType == CellType.NUMERIC) {
                return cell.getDateCellValue();
            } else if (cellType == CellType.STRING) {
                String cellValue = cell.getStringCellValue().trim();
                if (!cellValue.isEmpty()) {
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        return sdf.parse(cellValue);
                    } catch (Exception e) {
//                        ExceptionLogger.logException(e,logId);
                    }
                }
            }
        }
        return null;
    }

    public Float getCellFloatValue(Row row, int cellIndex, List<ValidationResultResponse> resultResponses, String type, List<String> headerNames) {
        Cell cell = row.getCell(cellIndex, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
        if (cell == null) {
            return null;
        } else {
            CellType cellType = cell.getCellType();

            if (cellType == CellType.NUMERIC) {
                return (float) cell.getNumericCellValue();

            } else if (cellType == CellType.FORMULA) {
                // Create evaluator directly from the workbook
                FormulaEvaluator evaluator = row.getSheet().getWorkbook()
                        .getCreationHelper()
                        .createFormulaEvaluator();
                CellValue evaluatedValue = evaluator.evaluate(cell);

                if (evaluatedValue != null && evaluatedValue.getCellType() == CellType.NUMERIC) {
                    return (float) evaluatedValue.getNumberValue();
                } else if (evaluatedValue != null && evaluatedValue.getCellType() == CellType.STRING) {
                    try {
                        return Float.parseFloat(evaluatedValue.getStringValue().trim());
                    } catch (NumberFormatException e) {
                        resultResponses.add(new ValidationResultResponse(
                                type,
                                (row.getRowNum() + 1),
                                headerNames.get(cellIndex),
                                "Formula does not evaluate to a numeric value"
                        ));
                    }
                }
                return null;

            } else if (cellType == CellType.STRING) {
                try {
                    return Float.parseFloat(cell.getStringCellValue().trim());
                } catch (NumberFormatException e) {
                    resultResponses.add(new ValidationResultResponse(
                            type,
                            (row.getRowNum() + 1),
                            headerNames.get(cellIndex),
                            "Data must be numeric value"
                    ));
                    return null;
                }

            } else {
                resultResponses.add(new ValidationResultResponse(
                        type,
                        (row.getRowNum() + 1),
                        headerNames.get(cellIndex),
                        "Data must be numeric value"
                ));
                return null;
            }
        }
    }

    public List<ExcellHeaderValidatorResponse> validateExcelHeader(Sheet sheet, List<String> expectedColumns) {
        List<ExcellHeaderValidatorResponse> validationResultList = new ArrayList<>();
        ExcellHeaderValidatorResponse validationResult=new ExcellHeaderValidatorResponse();
        List<String> actualColumns = new ArrayList<>();

        // Assuming the header row is the first row (index 1)
        Row headerRow = sheet.getRow(1);
        if (headerRow == null) {
            validationResult.setIsValid(false);
            validationResult.setErrorMessage("Header row is missing in the uploaded Excel file.");
            validationResultList.add(validationResult);
            return validationResultList;
        }

        // Iterate through the cells in the header row to extract actual column names
        for (int cellIndex = 0; cellIndex < headerRow.getLastCellNum(); cellIndex++) {
            Cell headerCell = headerRow.getCell(cellIndex, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
            if (headerCell != null) {
                String headerName = headerCell.getStringCellValue();
                actualColumns.add(headerName);
            }
        }

        // Check if actual columns have the same sequence order as the expected columns (ignoring case)
        boolean isColumnsMatching = actualColumns.size() == expectedColumns.size() &&
                IntStream.range(0, actualColumns.size())
                        .allMatch(i -> actualColumns.get(i).equalsIgnoreCase(expectedColumns.get(i)));

        // Set validation result based on missing and extra columns
        if (!isColumnsMatching) {
            validationResult.setIsValid(false);
            validationResult.setErrorMessage("Uploaded Excel file header does not match the template.");
            validationResultList.add(validationResult);
            return validationResultList;
        }

        validationResult.setIsValid(true);
        validationResultList.add(validationResult);
        return validationResultList;
    }

    public Workbook addDropdownListToColumn(String filePath, int columnIndex, String[] values, int hiddenSheetColumnIndex) throws IOException {
        Workbook workbook;
        try (FileInputStream fis = new FileInputStream(filePath)) {
            workbook = new XSSFWorkbook(fis);
        }

        Sheet sheet = workbook.getSheetAt(0); // Assuming the data is in the first sheet

        // Check if 'DropdownValues' sheet already exists
        int existingSheetIndex = workbook.getSheetIndex("DropdownValues");
        if (existingSheetIndex != -1) {
            // 'DropdownValues' sheet already exists, so remove it
            workbook.removeSheetAt(existingSheetIndex);
        }

        // Create a hidden sheet to store the dropdown values
        Sheet hiddenSheet = workbook.createSheet("DropdownValues");
        for (int i = 0; i < values.length; i++) {
            Row row = hiddenSheet.createRow(i);
            Cell cell = row.createCell(hiddenSheetColumnIndex == 0 ? 0 : 1); // Use 0 for hiddenSheetColumnIndex = 0, and 1 for hiddenSheetColumnIndex = 1
            cell.setCellValue(values[i]);
        }
        workbook.setSheetHidden(workbook.getSheetIndex(hiddenSheet), true);

        // Create the dropdown in the main sheet
        DataValidationHelper validationHelper = sheet.getDataValidationHelper();
        String hiddenColumnLetter = hiddenSheetColumnIndex == 0 ? "A" : "B"; // Use "A" for hiddenSheetColumnIndex = 0, and "B" for hiddenSheetColumnIndex = 1
        DataValidationConstraint validationConstraint = validationHelper.createFormulaListConstraint("DropdownValues!$" + hiddenColumnLetter + "$1:$" + hiddenColumnLetter + "$" + (values.length));
        CellRangeAddressList addressList = new CellRangeAddressList(2, sheet.getLastRowNum(), columnIndex, columnIndex);
        DataValidation validation = validationHelper.createValidation(validationConstraint, addressList);
        sheet.addValidationData(validation);

        // Save the modified workbook back to the file path
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            workbook.write(fos);
        }

        return workbook;
    }


//    CHECK VALIDATION FOR ITEM MASTER

    public void checkDatabaseValidationForItemMaster(List<ValidationResultResponse> resultResponses,String logId){

    }


    public List<ExcellHeaderValidatorResponse> validateBomExcelHeader(Sheet sheet, List<String> expectedColumns) {
        List<ExcellHeaderValidatorResponse> validationResultList = new ArrayList<>();
        ExcellHeaderValidatorResponse validationResult=new ExcellHeaderValidatorResponse();
        List<String> actualColumns = new ArrayList<>();

        // Assuming the header row is the first row (index 1)
        Row headerRow = sheet.getRow(9);
        if (headerRow == null) {
            validationResult.setIsValid(false);
            validationResult.setErrorMessage("Header row is missing in the uploaded Excel file.");
            validationResultList.add(validationResult);
            return validationResultList;
        }

        for (int cellIndex = 0; cellIndex < headerRow.getLastCellNum(); cellIndex++) {
            Cell headerCell = headerRow.getCell(cellIndex, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
            if (headerCell != null) {
                String headerName = getCellValueAsString(headerCell);
                actualColumns.add(headerName);
            }
        }


        // Check if actual columns have the same sequence order as the expected columns (ignoring case)
        boolean isColumnsMatching = actualColumns.size() == expectedColumns.size() &&
                IntStream.range(0, actualColumns.size())
                        .allMatch(i -> actualColumns.get(i).equalsIgnoreCase(expectedColumns.get(i)));

        // Set validation result based on missing and extra columns
        if (!isColumnsMatching) {
            validationResult.setIsValid(false);
            validationResult.setErrorMessage("Uploaded Excel file header does not match the template.");
            validationResultList.add(validationResult);
            return validationResultList;
        }

        validationResult.setIsValid(true);
        validationResultList.add(validationResult);
        return validationResultList;
    }

    public String storeIdGeneration(String storeName){
        StoreName storeNames=storeNameRepository.findByIsDeletedAndSubOrganizationIdAndStoreName(false,loginUser.getSubOrgId(),storeName);
        return loginUser.getSubOrganizationCode()+"-"+storeNames.getStoreId();


    }
    public String itemIdGeneration(){
        List<Item>items=itemRepository.findBySubOrganizationId(loginUser.getSubOrgId());
        if(items!=null && items.size()!=0){
            return "ITM0"+items.get(items.size()-1).getId();
        }else {
            return "ITM0"+1;
        }

    }
    public String poIdGeneration(){
        List<PurchaseOrderHead>purchaseOrderHeads=purchaseOrderHeadRepository.findByIsDeletedAndSubOrganizationId(false,loginUser.getSubOrgId());
        if(purchaseOrderHeads!=null && purchaseOrderHeads.size()!=0){
            return "PO0"+purchaseOrderHeads.get(purchaseOrderHeads.size()-1).getId();
        }else {
            return "PO0"+1;
        }

    }
    public String areaIdGeneration(Integer storeId){
        List<Area>areas=areaRepository.findByIsDeletedAndSubOrganizationIdAndStoreId(false,loginUser.getSubOrgId(),storeId);
        if(areas!=null && areas.size()!=0 ){
            return areas.get(areas.size()-1).getStore().getStoreId()+"A0"+areas.get(areas.size()-1).getId();
        }else {
            Optional<Store> store=storeRepository.findByIsDeletedAndIdAndSubOrganizationId(false,storeId,loginUser.getSubOrgId());
           return store.get().getStoreId()+"A0"+1;
        }

    }
    public String locationIdGeneration(Integer zoneId){
        List<Location>locations=locationRepository.findByIsDeletedAndSubOrganizationIdAndZoneIdOrderByIdAsc(false,loginUser.getSubOrgId(),zoneId);
       if(locations!=null && locations.size()!=0){
           return locations.get(locations.size()-1).getZone().getZoneId()+"LOC0"+locations.get(locations.size()-1).getId();
       }else {
           Optional<Zone> zone=zoneRepository.findByIsDeletedAndIdAndSubOrganizationId(false,zoneId,loginUser.getSubOrgId());

           return zone.get().getZoneId()+"LOC0"+1;
       }

    }
   /* public String bomIdGenerator(){
        List<BoMHead> boMHeads=bomHeadRepository.findBySubOrganizationId(loginUser.getSubOrgId());
        if(boMHeads!=null && boMHeads.size()!=0){
            return "BOM0"+(boMHeads.get(boMHeads.size()-1).getId()+1);
        }else{
            return "BOM0"+1;
        }
    }*/
    public String bomIdGenerator() {
        List<BoMHead> boMHeads = bomHeadRepository.findBySubOrganizationId(loginUser.getSubOrgId());
        String supplierId = null;
        if (boMHeads != null && boMHeads.size()!=0 ) {
            // Extract the last SupplierId and parse the numerical part
            int itmNumber = boMHeads.size();
            // Use String.format to ensure the number is always padded to 3 digits
            supplierId = String.format("%s-BOM%03d", loginUser.getSubOrganizationCode(), itmNumber++);
        }else{
            supplierId=loginUser.getSubOrganizationCode()+"-BOM001";
        }
        return supplierId;
    }

    public String ZoneIdGeneration(Integer areaId){

        List<Zone>zones=zoneRepository.findByIsDeletedAndSubOrganizationIdAndAreaId(false,loginUser.getSubOrgId(),areaId);
        if(zones!=null && zones.size()!=0){
            return zones.get(zones.size()-1).getArea().getAreaId()+"Z0"+zones.get(zones.size()-1).getId();
        }else {
            Area area=areaRepository.findByIsDeletedAndIdAndSubOrganizationId(false,areaId,loginUser.getSubOrgId());
            return area.getAreaId()+"Z0"+1;
        }

    }

    public boolean isDuplicateStoreName(String storeName,String erpId){
        List<Store> store=storeRepository.findByIsDeletedAndSubOrganizationId(false,loginUser.getSubOrgId());
        if(store!=null && store.size()!=0 && storeName!=null ){
            return !store.stream().anyMatch(k->k.getStoreName().equalsIgnoreCase(storeName));
        }else if(store!=null && store.size()!=0 && erpId!=null){
            return !store.stream().anyMatch(k->k.getErpStoreId().equalsIgnoreCase(erpId));
        }else{
            return true;
        }
    }


    public boolean isValidPassword(String input) {
        String regex = ".*[^a-zA-Z0-9 ].*";

        // Compile the pattern
        Pattern pattern = Pattern.compile(regex);

        // Create a Matcher object
        Matcher matcher = pattern.matcher(input);

        // Check if the string contains any special characters
        if (matcher.matches()) {
            return false;
        } else {
            return true;
        }
    }

    //Added by Kamlesh
    public String generateNextPpeId(String lastPpeId) {
        if (lastPpeId == null || lastPpeId.isEmpty()) {
            return "PPE001";
        }
        int lastIdNumber = Integer.parseInt(lastPpeId.replace("PPE", ""));
        int nextIdNumber = lastIdNumber + 1;
        return "PPE" + String.format("%03d", nextIdNumber);
    }
	
	public Date getCellDateValueForPPE(Row row, int cellIndex, List<ValidationResultResponse> resultResponses, String type, List<String> headerNames) {
        Cell cell = row.getCell(cellIndex, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);

        if (cell == null) {
            return null;
        }
        if(cell!=null && cell.getCellType()!=null) {
            CellType cellType = cell.getCellType();
            if (cellType == CellType.NUMERIC) {
                return cell.getDateCellValue();
            } else if (cellType == CellType.STRING) {
                String cellValue = cell.getStringCellValue().trim();
                if (!cellValue.isEmpty()) {
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        return sdf.parse(cellValue);
                    } catch (Exception e) {
//                        ExceptionLogger.logException(e,logId);
                    }
                }
            }
        }
        return null;
    }


    public LocalTime getCellTimeValueForPPE(Row row, int cellIndex,
                                            List<ValidationResultResponse> resultResponses,
                                            String type, List<String> headerNames) {
        Cell cell = row.getCell(cellIndex, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);

        if (cell == null) {
            return null;
        }

        if (cell.getCellType() != null) {
            CellType cellType = cell.getCellType();

            if (cellType == CellType.NUMERIC) {
                // Excel stores time as a fraction of a day in numeric form
                if (DateUtil.isCellDateFormatted(cell)) {
                    Date date = cell.getDateCellValue();
                    return date.toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalTime();
                }
            } else if (cellType == CellType.STRING) {
                String cellValue = cell.getStringCellValue().trim();
                if (!cellValue.isEmpty()) {
                    // Strictly enforce 24-hour format (HH:mm or HH:mm:ss)
                    try {
                        DateTimeFormatter formatter;
                        if (cellValue.matches("^([01]\\d|2[0-3]):[0-5]\\d$")) {
                            formatter = DateTimeFormatter.ofPattern("HH:mm");
                        } else if (cellValue.matches("^([01]\\d|2[0-3]):[0-5]\\d:[0-5]\\d$")) {
                            formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
                        } else {
                            // Invalid time format -> log or record validation error
                            ValidationResultResponse response = new ValidationResultResponse();
                            response.setErrorMessage("Invalid time format at column: " + headerNames.get(cellIndex)
                                    + ". Expected 24-hour format (HH:mm or HH:mm:ss).");
                            resultResponses.add(response);
                            return null;
                        }
                        return LocalTime.parse(cellValue, formatter);
                    } catch (Exception e) {
                        ValidationResultResponse response = new ValidationResultResponse();
                        response.setErrorMessage("Error parsing time value at column: " + headerNames.get(cellIndex));
                        resultResponses.add(response);
                    }
                }
            }
        }

        return null;
    }



    public Integer getNumberOfItem(){
        // Define storage location dimensions and weight capacity
        double storageLength = 10.0; // in meters
        double storageBreadth = 5.0; // in meters
        double storageHeight = 3.0; // in meters
        double storageWeightCapacity = 5000.0; // in kg

        // Define container dimensions and weight
        double containerLengthCm = 10.0; // in cm
        double containerBreadthCm = 10.0; // in cm
        double containerHeightCm = 10.0; // in cm
        double containerWeightGrams = 10000.0; // in grams

        // Convert container dimensions from cm to meters
        double containerLength = containerLengthCm / 100.0;
        double containerBreadth = containerBreadthCm / 100.0;
        double containerHeight = containerHeightCm / 100.0;

        // Convert container weight from grams to kg
        double containerWeight = containerWeightGrams / 1000.0;

        // Calculate the number of containers based on volume
        int numberOfContainersByVolume = calculateContainersByVolume(storageLength, storageBreadth, storageHeight,
                containerLength, containerBreadth, containerHeight);

        // Calculate the number of containers based on weight
        int numberOfContainersByWeight = calculateContainersByWeight(storageWeightCapacity, containerWeight);

        // Determine the final number of containers that can be stored
        int finalNumberOfContainers = Math.min(numberOfContainersByVolume, numberOfContainersByWeight);

        // Output the result
        System.out.println("The number of containers that can be stored: " + finalNumberOfContainers);
        return finalNumberOfContainers;
    }

    // Method to calculate the number of containers based on volume
    public static int calculateContainersByVolume(double storageLength, double storageBreadth, double storageHeight,
                                                  double containerLength, double containerBreadth, double containerHeight) {
        double storageVolume = storageLength * storageBreadth * storageHeight;
        double containerVolume = containerLength * containerBreadth * containerHeight;
        return (int) Math.floor(storageVolume / containerVolume);
    }

    // Method to calculate the number of containers based on weight
    public static int calculateContainersByWeight(double storageWeightCapacity, double containerWeight) {
        return (int) Math.floor(storageWeightCapacity / containerWeight);
    }


    public String getCellValueAsString(Cell cell) {
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();

            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    // Format date if needed
                    Date date = cell.getDateCellValue();
                    return new SimpleDateFormat("yyyy-MM-dd").format(date);
                } else {
                    return String.valueOf(cell.getNumericCellValue());
                }

            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());

            case FORMULA:
                // Evaluate formula result
                FormulaEvaluator evaluator = cell.getSheet().getWorkbook().getCreationHelper().createFormulaEvaluator();
                return getCellValueAsString(evaluator.evaluateInCell(cell));

            case BLANK:
                return "";

            case ERROR:
                return "ERROR";

            default:
                return "UNKNOWN";
        }
    }

}

