package com.stockmanagementsystem.controller;

import com.stockmanagementsystem.service.PackingTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

public class PackingProfileConfigController {

    @Autowired
    private PackingTemplateService packingTemplateService;

    @GetMapping("/packing/template/download")
    public ResponseEntity<byte[]> downloadPackingTemplate(
            @RequestParam Integer areaId,
            @RequestParam Integer zoneId) {
        return packingTemplateService.downloadTemplate(areaId, zoneId);
    }



}
