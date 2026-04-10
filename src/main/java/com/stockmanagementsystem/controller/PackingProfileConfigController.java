//package com.stockmanagementsystem.controller;
//
//
//import com.stockmanagementsystem.entity.ItemSupplierPackingProfileMap;
//import com.stockmanagementsystem.entity.PackingProfileConfigMaster;
//import com.stockmanagementsystem.request.ItemSupplierPackingProfileUpdateRequest;
//import com.stockmanagementsystem.request.PackingProfileConfigRequest;
//import com.stockmanagementsystem.response.BaseResponse;
//import com.stockmanagementsystem.response.PackingProfileDetailDTO;
//import com.stockmanagementsystem.response.PackingProfileListDTO;
//import com.stockmanagementsystem.response.PackingProfileResponse;
//import com.stockmanagementsystem.service.PackingTemplateService;
//import com.stockmanagementsystem.utils.APIConstants;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.util.Map;
//
//@RestController
//@RequestMapping({APIConstants.BASE_REQUEST + APIConstants.SERVICENAME})
//public class PackingProfileConfigController {
//
//    @Autowired
//    private PackingTemplateService packingTemplateService;
//
//    @GetMapping(APIConstants.PACKING_TEMPLATE_DOWNLOAD)
//    public ResponseEntity<byte[]> downloadPackingTemplate(
//            @RequestParam Integer areaId,
//            @RequestParam Integer zoneId) {
//        return packingTemplateService.downloadTemplate(areaId, zoneId);
//    }
//
//    /**
//     * Upload Packing Configuration Template
//     *
//     * @param file   Excel template file
//     * @param areaId Area Id
//     * @param zoneId Zone Id
//     * @return upload summary with row-wise errors
//     */
//    @PostMapping("/upload")
//    public ResponseEntity<Map<String, Object>> uploadPackingProfileTemplate(
//            @RequestParam("file") MultipartFile file,
//            @RequestParam("areaId") Integer areaId,
//            @RequestParam("zoneId") Integer zoneId) {
//
//        Map<String, Object> response =
//                packingTemplateService
//                        .uploadPackingProfileTemplate(file, areaId, zoneId);
//
//        return ResponseEntity.ok(response);
//    }
//
//    @GetMapping("/getAllPackingConfigData")
//    public BaseResponse<PackingProfileListDTO> getAllPackingProfiles(
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "10") int size,
//            @RequestParam(defaultValue = "modifiedOn") String sortBy,
//            @RequestParam(defaultValue = "desc") String sortDir) {
//
//        BaseResponse<PackingProfileListDTO> response =
//                packingTemplateService
//                        .getAllPackingProfiles(page, size, sortBy, sortDir);
//
//        return response;
//    }
//
//
//    @GetMapping("/getPackingConfigById/{id}")
//    public BaseResponse<PackingProfileDetailDTO> getPackingConfigById(
//            @PathVariable("id") Long id) {
//
//        BaseResponse<PackingProfileDetailDTO> response =
//                packingTemplateService.getPackingProfileById(id);
//
//        return response;
//    }
//
//    @PutMapping("/updateItemSupplierPackingProfile/{mappingId}")
//    public ResponseEntity<BaseResponse<ItemSupplierPackingProfileMap>>
//    updateItemSupplierPackingProfile(
//            @PathVariable Long mappingId,
//            @RequestBody ItemSupplierPackingProfileUpdateRequest request) {
//
//        BaseResponse<ItemSupplierPackingProfileMap> response =
//                packingTemplateService
//                        .updateItemSupplierPackingProfile(mappingId, request);
//
//        return ResponseEntity.ok(response);
//    }
//
//    @PostMapping("/savePackingConfig")
//    public ResponseEntity<BaseResponse<PackingProfileConfigMaster>> savePackingProfileConfig(
//            @RequestBody PackingProfileConfigRequest request) {
//
//        BaseResponse<PackingProfileConfigMaster> response =
//                packingTemplateService.savePackingProfileConfig(request);
//
//        return ResponseEntity.ok(response);
//    }
//
//    @GetMapping("/getPackingConfigByIdProperly/{id}")
//    public ResponseEntity<BaseResponse<PackingProfileResponse>> getPackingConfigByIdProperly(
//            @PathVariable Long id) {
//
//        BaseResponse<PackingProfileResponse> response =
//                packingTemplateService.getPackingProfileByIdProperly(id);
//
//        return ResponseEntity.ok(response);
//    }
//}
