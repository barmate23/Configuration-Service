package com.stockmanagementsystem.service;

import com.stockmanagementsystem.entity.PackagingMaster;
import com.stockmanagementsystem.entity.PackagingSubtype;
import com.stockmanagementsystem.entity.PackagingType;
import com.stockmanagementsystem.entity.PackingHierarchyLevel;
import com.stockmanagementsystem.response.*;

public interface PackingMasterService {

    // Packaging Type & Subtype (Hierarchical)
    BaseResponse<PackagingTypeResponse> savePackagingType(PackagingTypeResponse data);
    BaseResponse<PackagingTypeResponse> getAllPackagingTypes();
    BaseResponse<PackagingTypeResponse> getPackagingTypeById(Long id);
    BaseResponse<PackagingTypeResponse> deletePackagingType(Long id);

    // Packaging Subtype
    BaseResponse<PackagingSubtype> savePackagingSubtype(PackagingSubtype subtype);
    BaseResponse<PackagingSubtypeResponse> getAllPackagingSubtypes();
    BaseResponse<PackagingSubtypeResponse> getPackagingSubtypeById(Long id);
    BaseResponse<PackagingSubtype> deletePackagingSubtype(Long id);

    // Packaging Master
    BaseResponse<PackagingMaster> savePackagingMaster(PackagingMaster master);
    BaseResponse<PackagingMasterResponse> getAllPackagingMasters();
    BaseResponse<PackagingMasterResponse> getPackagingMasterById(Long id);
    BaseResponse<PackagingMaster> deletePackagingMaster(Long id);

    // Hierarchy Level
    BaseResponse<PackingHierarchyLevel> saveHierarchyLevel(PackingHierarchyLevel level);
    BaseResponse<PackingHierarchyLevelResponse> getAllHierarchyLevels();
    BaseResponse<PackingHierarchyLevelResponse> getHierarchyLevelById(Long id);
    BaseResponse<PackingHierarchyLevel> deleteHierarchyLevel(Long id);
}
