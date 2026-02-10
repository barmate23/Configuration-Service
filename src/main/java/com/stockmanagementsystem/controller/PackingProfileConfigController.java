package com.stockmanagementsystem.controller;

import com.stockmanagementsystem.service.PackingTemplateService;
import com.stockmanagementsystem.utils.APIConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping({APIConstants.BASE_REQUEST + APIConstants.SERVICENAME})
public class PackingProfileConfigController {

    @Autowired
    private PackingTemplateService packingTemplateService;

    @GetMapping(APIConstants.PACKING_TEMPLATE_DOWNLOAD)
    public ResponseEntity<byte[]> downloadPackingTemplate(
            @RequestParam Integer areaId,
            @RequestParam Integer zoneId) {
        return packingTemplateService.downloadTemplate(areaId, zoneId);
    }

    /**
     * Upload Packing Configuration Template
     *
     * @param file   Excel template file
     * @param areaId Area Id
     * @param zoneId Zone Id
     * @return upload summary with row-wise errors
     */
    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> uploadPackingProfileTemplate(
            @RequestParam("file") MultipartFile file,
            @RequestParam("areaId") Integer areaId,
            @RequestParam("zoneId") Integer zoneId) {

        Map<String, Object> response =
                packingTemplateService
                        .uploadPackingProfileTemplate(file, areaId, zoneId);

        return ResponseEntity.ok(response);
    }



}
