package com.stockmanagementsystem.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;

public interface PackingProfileService {

    ByteArrayInputStream generateTemplate(Integer zoneId);

    void upload(MultipartFile file);
}