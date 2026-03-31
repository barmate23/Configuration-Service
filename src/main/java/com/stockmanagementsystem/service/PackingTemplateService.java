package com.stockmanagementsystem.service;

import com.stockmanagementsystem.entity.ItemSupplierPackingProfileMap;
import com.stockmanagementsystem.entity.PackingProfileConfigMaster;
import com.stockmanagementsystem.request.ItemSupplierPackingProfileUpdateRequest;
import com.stockmanagementsystem.request.PackingProfileUpdateRequest;
import com.stockmanagementsystem.response.BaseResponse;
import com.stockmanagementsystem.response.PackingProfileDetailDTO;
import com.stockmanagementsystem.response.PackingProfileListDTO;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface PackingTemplateService {

    ResponseEntity<byte[]> downloadTemplate(Integer areaId, Integer zoneId);

    Map<String, Object> uploadPackingProfileTemplate(MultipartFile file, Integer areaId, Integer zoneId);

    BaseResponse<PackingProfileListDTO> getAllPackingProfiles(int page, int size, String sortBy,
                                                              String sortDir);

    BaseResponse<PackingProfileDetailDTO> getPackingProfileById(Long id);

    BaseResponse<ItemSupplierPackingProfileMap> updateItemSupplierPackingProfile(Long mappingId, ItemSupplierPackingProfileUpdateRequest request);
}
