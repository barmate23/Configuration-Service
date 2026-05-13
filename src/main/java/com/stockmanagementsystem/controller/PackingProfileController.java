package com.stockmanagementsystem.controller;

import com.stockmanagementsystem.service.PackingProfileService;
import com.stockmanagementsystem.utils.APIConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;

@RestController
@RequestMapping({ APIConstants.BASE_REQUEST + APIConstants.SERVICENAME })
@RequiredArgsConstructor
public class PackingProfileController {

    private final PackingProfileService packingProfileService;

    // ===== DOWNLOAD TEMPLATE =====
    @GetMapping("/downloadPackingProfileTemplate")
    public ResponseEntity<byte[]> downloadTemplate(@RequestParam("zoneId") Integer zoneId) {

        ByteArrayInputStream stream = packingProfileService.generateTemplate(zoneId);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=packing_profile.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(stream.readAllBytes());
    }

    // ===== UPLOAD EXCEL =====
    @PostMapping("/uploadPackingProfileTemplate")
    public ResponseEntity<String> upload(@RequestParam("file") MultipartFile file) {

        packingProfileService.upload(file);
        return ResponseEntity.ok("Upload successful");
    }
}