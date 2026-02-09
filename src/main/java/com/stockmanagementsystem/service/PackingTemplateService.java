package com.stockmanagementsystem.service;

import org.springframework.http.ResponseEntity;

public interface PackingTemplateService {

    ResponseEntity<byte[]> downloadTemplate(Integer areaId, Integer zoneId);

}
