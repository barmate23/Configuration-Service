package com.stockmanagementsystem.controller;

import com.stockmanagementsystem.entity.AcceptedRejectedStagingArea;
import com.stockmanagementsystem.entity.Dock;
import com.stockmanagementsystem.request.DockRequest;
import com.stockmanagementsystem.response.*;
import com.stockmanagementsystem.service.DocksService;
import com.stockmanagementsystem.utils.APIConstants;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.util.List;


@RestController
@RequestMapping({APIConstants.BASE_REQUEST + APIConstants.SERVICENAME + APIConstants.DOCKS_CONTROLLER})
public class DocksController {
    @Autowired
    DocksService docksService;

    @PostMapping(APIConstants.SAVE_DOCK)
    public BaseResponse saveDock(@RequestBody DockRequest dockRequest){
        return docksService.saveDock(dockRequest);
    }

    @DeleteMapping(APIConstants.DELETE_DOCK)
    public BaseResponse<Dock> deleteDockById(@PathVariable Integer id){
        return docksService.deleteByDockId(id);
    }

    @PutMapping(APIConstants.UPDATE_DOCK)
    public ResponseEntity<BaseResponse> updateDock(@PathVariable Integer id, @RequestBody DockRequest dockRequest) {
        BaseResponse baseResponse = docksService.updateDock(id, dockRequest);
        return new ResponseEntity<>(baseResponse, HttpStatus.valueOf(baseResponse.getStatus()));
    }

    @GetMapping(APIConstants.GET_DOCKNAME)
    public ResponseEntity<BaseResponse<List<DockNameResponse>>> getAllDockNamesWithIds() {
        BaseResponse<List<DockNameResponse>> response = docksService.getDockNamesWithIds();
        HttpStatus status = HttpStatus.resolve(response.getCode());
        if (status == null) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping(APIConstants.GET_ATTRIBUTE)
    public ResponseEntity<BaseResponse<List<AttributeResponse>>> getAttributesWithIds() {
        BaseResponse<List<AttributeResponse>> response = docksService.getAttributesWithIds();
        HttpStatus status = HttpStatus.resolve(response.getCode());
        if (status == null) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping(APIConstants.GET_DOCK)
    public BaseResponse<Dock> getAllDocks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int pageSize) {

        return docksService.getAllDocks(page, pageSize);
    }

    @GetMapping({"/getDocksById/{dockId}"})
    public BaseResponse<Dock> getDocksById(@PathVariable Integer dockId){

        return docksService.getDocksById(dockId);
    }

    @GetMapping(APIConstants.GET_DOCK_EXCEL_BY_ID)
    public ResponseEntity<ByteArrayResource> downloadExcelById(@PathVariable Integer id) {
        byte[] excelContent = docksService.generateExcelContentById(id);
        String filename = "dock_" + id + ".xlsx";

        ByteArrayResource resource = new ByteArrayResource(excelContent);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(excelContent.length)
                .body(resource);
    }


    @GetMapping(APIConstants.GET_DOCK_SEARCH)
    public BaseResponse<Dock> searchDocks(
            @RequestParam(value = "pageNumber", defaultValue = "0", required = false) Integer pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) Integer pageSize,
            @RequestParam(required = false) List<String> dockIds,
            @RequestParam(required = false) List<String> attributes,
            @RequestParam(required = false) List<Integer> createdYear
    ) {
        return docksService.searchDocks(pageNumber, pageSize, dockIds, attributes, createdYear);
    }

    @GetMapping(APIConstants.GET_EXCEL_DOCKS)
    public ResponseEntity<byte[]> generateExcelDocks() {
        byte[] excelContent = docksService.generateExcelForAllDocks();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "docks.xlsx");

        return new ResponseEntity<>(excelContent, headers, HttpStatus.OK);
    }


    @GetMapping(APIConstants.GET_STORE)
    public ResponseEntity<BaseResponse<List<StoreWithIdResponse>>> getStoresWithIds() {
        BaseResponse<List<StoreWithIdResponse>> response = docksService.getStoresWithIds();
        HttpStatus status = HttpStatus.resolve(response.getCode());
        if (status == null) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return ResponseEntity.ok(response);
    }
    @GetMapping(APIConstants.GET_ALL_USERS)
    public ResponseEntity<BaseResponse<List<UserResponse>>> getUsersWithIds() {
        BaseResponse<List<UserResponse>> response = docksService.getUsersWithIds();
        HttpStatus status = HttpStatus.resolve(response.getCode());
        if (status == null) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping(APIConstants.GET_BARCODE_DOCKS)
    public ResponseEntity<byte[]> generateDockBarcodePDF() {
        byte[] pdfFile =  docksService.generateDockBarcodePDF();
        if(pdfFile != null) {
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF).body(pdfFile);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping(APIConstants.GET_STAGE_BARCODE)
    public ResponseEntity<byte[]> getStageBarcode(@RequestParam Boolean isAccepted) {
        byte[] pdfFile =  docksService.getStageBarcode(isAccepted);
        if(pdfFile != null){
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF).body(pdfFile);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping(APIConstants.GET_BARCODE_BY_CODE)
    public ResponseEntity<byte[]> generateDockBarcodePDF(@RequestParam String barcode) {
        byte[] pdfFile =  docksService.generateBarcode(barcode);
        if(pdfFile != null){
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF).body(pdfFile);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    @GetMapping(APIConstants.GET_STAGING_AREA_BY_DOCK)
    public BaseResponse<AcceptedRejectedStagingArea> getAllStagingArea(){
        return docksService.getAllStagingArea();
    }


}