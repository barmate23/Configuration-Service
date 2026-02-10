package com.stockmanagementsystem.service;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface PackingTemplateService {

    ResponseEntity<byte[]> downloadTemplate(Integer areaId, Integer zoneId);

    Map<String, Object> uploadPackingProfileTemplate(MultipartFile file, Integer areaId, Integer zoneId);
}
