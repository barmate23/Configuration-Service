package com.stockmanagementsystem.controller;

import com.stockmanagementsystem.entity.PackagingMaster;
import com.stockmanagementsystem.entity.PackagingSubtype;
import com.stockmanagementsystem.entity.PackagingType;
import com.stockmanagementsystem.entity.PackingHierarchyLevel;
import com.stockmanagementsystem.response.*;
import com.stockmanagementsystem.service.PackingMasterService;
import com.stockmanagementsystem.utils.APIConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping({ APIConstants.BASE_REQUEST + APIConstants.SERVICENAME })
public class PackingMasterController {

    @Autowired
    private PackingMasterService packingMasterService;

    // === Packaging Type & Subtype (Hierarchical) ===

    @PostMapping("/savePackagingType")
    public BaseResponse<PackagingTypeResponse> savePackagingType(@RequestBody PackagingTypeResponse type) {
        return packingMasterService.savePackagingType(type);
    }

    @GetMapping("/getPackagingType")
    public BaseResponse<PackagingTypeResponse> getAllPackagingTypes() {
        return packingMasterService.getAllPackagingTypes();
    }

    @GetMapping("/getPackagingTypeById/{id}")
    public BaseResponse<PackagingTypeResponse> getPackagingTypeById(@PathVariable Long id) {
        return packingMasterService.getPackagingTypeById(id);
    }

    @DeleteMapping("/deletePackagingType/{id}")
    public BaseResponse<PackagingTypeResponse> deletePackagingType(@PathVariable Long id) {
        return packingMasterService.deletePackagingType(id);
    }

    // === Packaging Subtype ===

    @PostMapping("/packaging-subtype")
    public BaseResponse<PackagingSubtype> savePackagingSubtype(@RequestBody PackagingSubtype subtype) {
        return packingMasterService.savePackagingSubtype(subtype);
    }

    @GetMapping("/packaging-subtype")
    public BaseResponse<PackagingSubtypeResponse> getAllPackagingSubtypes() {
        return packingMasterService.getAllPackagingSubtypes();
    }

    @GetMapping("/packaging-subtype/{id}")
    public BaseResponse<PackagingSubtypeResponse> getPackagingSubtypeById(@PathVariable Long id) {
        return packingMasterService.getPackagingSubtypeById(id);
    }

    @DeleteMapping("/packaging-subtype/{id}")
    public BaseResponse<PackagingSubtype> deletePackagingSubtype(@PathVariable Long id) {
        return packingMasterService.deletePackagingSubtype(id);
    }

    // === Packaging Master ===

    @PostMapping("/savePackagingMaster")
    public BaseResponse<PackagingMaster> savePackagingMaster(@RequestBody PackagingMaster master) {
        return packingMasterService.savePackagingMaster(master);
    }

    @GetMapping("/getPackagingMaster")
    public BaseResponse<PackagingMasterResponse> getAllPackagingMasters() {
        return packingMasterService.getAllPackagingMasters();
    }

    @GetMapping("/getPackagingMasterById/{id}")
    public BaseResponse<PackagingMasterResponse> getPackagingMasterById(@PathVariable Long id) {
        return packingMasterService.getPackagingMasterById(id);
    }

    @DeleteMapping("/deletePackagingMaster/{id}")
    public BaseResponse<PackagingMaster> deletePackagingMaster(@PathVariable Long id) {
        return packingMasterService.deletePackagingMaster(id);
    }

    // === Packing Hierarchy Level ===

    @PostMapping("/saveHierarchyLevel")
    public BaseResponse<PackingHierarchyLevel> saveHierarchyLevel(@RequestBody PackingHierarchyLevel level) {
        return packingMasterService.saveHierarchyLevel(level);
    }

    @GetMapping("/getHierarchyLevel")
    public BaseResponse<PackingHierarchyLevelResponse> getAllHierarchyLevels() {
        return packingMasterService.getAllHierarchyLevels();
    }

    @GetMapping("/getHierarchyLevelById/{id}")
    public BaseResponse<PackingHierarchyLevelResponse> getHierarchyLevelById(@PathVariable Long id) {
        return packingMasterService.getHierarchyLevelById(id);
    }

    @DeleteMapping("/deleteHierarchyLevel/{id}")
    public BaseResponse<PackingHierarchyLevel> deleteHierarchyLevel(@PathVariable Long id) {
        return packingMasterService.deleteHierarchyLevel(id);
    }
}
