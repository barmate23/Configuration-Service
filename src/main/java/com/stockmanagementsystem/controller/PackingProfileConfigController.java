package com.stockmanagementsystem.controller;

import com.stockmanagementsystem.service.PackingTemplateService;
import com.stockmanagementsystem.utils.APIConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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



}
