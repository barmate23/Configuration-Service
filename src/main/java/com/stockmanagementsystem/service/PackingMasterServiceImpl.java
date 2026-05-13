package com.stockmanagementsystem.service;

import com.stockmanagementsystem.entity.PackagingMaster;
import com.stockmanagementsystem.entity.PackagingSubtype;
import com.stockmanagementsystem.entity.PackagingType;
import com.stockmanagementsystem.entity.PackingHierarchyLevel;
import com.stockmanagementsystem.entity.LoginUser;
import com.stockmanagementsystem.repository.PackagingMasterRepository;
import com.stockmanagementsystem.repository.PackagingSubtypeRepository;
import com.stockmanagementsystem.repository.PackagingTypeRepository;
import com.stockmanagementsystem.repository.PackingHierarchyLevelRepository;
import com.stockmanagementsystem.response.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PackingMasterServiceImpl implements PackingMasterService {

    @Autowired
    private PackagingTypeRepository packagingTypeRepository;

    @Autowired
    private PackagingSubtypeRepository packagingSubtypeRepository;

    @Autowired
    private PackagingMasterRepository packagingMasterRepository;

    @Autowired
    private PackingHierarchyLevelRepository packingHierarchyLevelRepository;

    @Autowired
    private LoginUser loginUser;

    // === Packaging Type ===

    @Override
    public BaseResponse<PackagingTypeResponse> savePackagingType(PackagingTypeResponse data) {
        log.info("Saving packaging type with subtypes hierarchical data");
        BaseResponse<PackagingTypeResponse> response = new BaseResponse<>();
        try {
            PackagingType type = new PackagingType();
            if (data.getId() != null) {
                type = packagingTypeRepository.findById(data.getId()).orElse(new PackagingType());
                type.setModifiedOn(new Date());
                type.setModifiedBy(loginUser.getUserId());
            } else {
                type.setCreatedOn(new Date());
                type.setCreatedBy(loginUser.getUserId());
            }
            type.setTypeName(data.getTypeName());
            
            PackagingType savedType = packagingTypeRepository.save(type);
            log.info("Saved Packaging Type with id: {}", savedType.getId());

            if (data.getSubtypes() != null) {
                List<PackagingSubtype> subtypesToSave = data.getSubtypes().stream().map(sDto -> {
                    PackagingSubtype s = new PackagingSubtype();
                    if (sDto.getId() != null) {
                        s = packagingSubtypeRepository.findById(sDto.getId()).orElse(new PackagingSubtype());
                        s.setModifiedOn(new Date());
                        s.setModifiedBy(loginUser.getUserId());
                    } else {
                        s.setCreatedOn(new Date());
                        s.setCreatedBy(loginUser.getUserId());
                    }
                    s.setSubtypeName(sDto.getSubtypeName());
                    s.setMaterialType(sDto.getMaterialType());
                    s.setPackagingType(savedType);
                    return s;
                }).collect(Collectors.toList());

                packagingSubtypeRepository.saveAll(subtypesToSave);
            }

            response.setData(Collections.singletonList(mapToPackagingTypeResponseWithSubtypes(savedType)));
            response.setStatus(1);
            response.setMessage("Packaging Type and Subtypes saved successfully");
        } catch (Exception e) {
            log.error("Error saving hierarchical packaging data", e);
            response.setStatus(0);
            response.setMessage("Error: " + e.getMessage());
        }
        return response;
    }

    @Override
    public BaseResponse<PackagingTypeResponse> getAllPackagingTypes() {
        log.info("Fetching all packaging types with subtypes");
        BaseResponse<PackagingTypeResponse> response = new BaseResponse<>();
        try {
            List<PackagingType> types = packagingTypeRepository.findAll();
            List<PackagingSubtype> allSubtypes = packagingSubtypeRepository.findAll();

            List<PackagingTypeResponse> hierarchicalData = types.stream().map(t -> {
                PackagingTypeResponse r = new PackagingTypeResponse();
                r.setId(t.getId());
                r.setTypeName(t.getTypeName());
                r.setSubtypes(allSubtypes.stream()
                        .filter(s -> s.getPackagingType() != null && s.getPackagingType().getId().equals(t.getId()))
                        .map(this::mapToPackagingSubtypeResponse)
                        .collect(Collectors.toList()));
                return r;
            }).collect(Collectors.toList());

            response.setData(hierarchicalData);
            response.setStatus(1);
            log.info("Found {} packaging types with their subtypes", hierarchicalData.size());
        } catch (Exception e) {
            log.error("Error fetching hierarchical packaging data", e);
            response.setStatus(0);
            response.setMessage("Error: " + e.getMessage());
        }
        return response;
    }

    @Override
    public BaseResponse<PackagingTypeResponse> getPackagingTypeById(Long id) {
        log.info("Fetching packaging type with subtypes for id: {}", id);
        BaseResponse<PackagingTypeResponse> response = new BaseResponse<>();
        packagingTypeRepository.findById(id).ifPresent(t -> 
            response.setData(Collections.singletonList(mapToPackagingTypeResponseWithSubtypes(t)))
        );
        response.setStatus(1);
        return response;
    }

    @Override
    public BaseResponse<PackagingTypeResponse> deletePackagingType(Long id) {
        log.info("Deleting packaging type and its subtypes for id: {}", id);
        BaseResponse<PackagingTypeResponse> response = new BaseResponse<>();
        // In a real scenario, you might want to handle subtypes first if they aren't CascadeType.REMOVE
        packagingTypeRepository.deleteById(id);
        log.info("Packaging type deleted successfully for id: {}", id);
        response.setStatus(1);
        response.setMessage("Deleted successfully");
        return response;
    }

    @Override
    public BaseResponse<PackagingSubtype> savePackagingSubtype(PackagingSubtype subtype) {
        BaseResponse<PackagingSubtype> response = new BaseResponse<>();
        try {
            if (subtype.getId() == null) {
                subtype.setCreatedOn(new Date());
                subtype.setCreatedBy(loginUser.getUserId());
            } else {
                subtype.setModifiedOn(new Date());
                subtype.setModifiedBy(loginUser.getUserId());
            }
            PackagingSubtype saved = packagingSubtypeRepository.save(subtype);
            log.info("Packaging Subtype saved successfully with id: {}", saved.getId());
            response.setStatus(1);
            response.setMessage("Packaging Subtype saved successfully");
            response.setData(Collections.singletonList(saved));
        } catch (Exception e) {
            log.error("Error saving packaging subtype", e);
            response.setStatus(0);
            response.setMessage("Error: " + e.getMessage());
        }
        return response;
    }

    @Override
    public BaseResponse<PackagingSubtypeResponse> getAllPackagingSubtypes() {
        log.info("Fetching all packaging subtypes");
        BaseResponse<PackagingSubtypeResponse> response = new BaseResponse<>();
        List<PackagingSubtype> list = packagingSubtypeRepository.findAll();
        List<PackagingSubtypeResponse> responseList = list.stream()
                .map(this::mapToPackagingSubtypeResponse)
                .collect(Collectors.toList());
        response.setData(responseList);
        response.setStatus(1);
        log.info("Found {} packaging subtypes", responseList.size());
        return response;
    }

    @Override
    public BaseResponse<PackagingSubtypeResponse> getPackagingSubtypeById(Long id) {
        log.info("Fetching packaging subtype by id: {}", id);
        BaseResponse<PackagingSubtypeResponse> response = new BaseResponse<>();
        packagingSubtypeRepository.findById(id).ifPresent(s -> 
            response.setData(Collections.singletonList(mapToPackagingSubtypeResponse(s)))
        );
        response.setStatus(1);
        if (response.getData() != null && !response.getData().isEmpty()) {
            log.info("Packaging subtype found for id: {}", id);
        } else {
            log.warn("Packaging subtype not found for id: {}", id);
        }
        return response;
    }

    @Override
    public BaseResponse<PackagingSubtype> deletePackagingSubtype(Long id) {
        log.info("Deleting packaging subtype with id: {}", id);
        BaseResponse<PackagingSubtype> response = new BaseResponse<>();
        packagingSubtypeRepository.deleteById(id);
        log.info("Packaging subtype deleted successfully for id: {}", id);
        response.setStatus(1);
        response.setMessage("Deleted successfully");
        return response;
    }

    // === Packaging Master ===

    @Override
    public BaseResponse<PackagingMaster> savePackagingMaster(PackagingMaster master) {
        BaseResponse<PackagingMaster> response = new BaseResponse<>();
        try {
            if (master.getId() == null) {
                master.setCreatedDate(new Date());
            }
            PackagingMaster saved = packagingMasterRepository.save(master);
            log.info("Packaging Master saved successfully with id: {}", saved.getId());
            response.setStatus(1);
            response.setMessage("Packaging Master saved successfully");
            response.setData(Collections.singletonList(saved));
        } catch (Exception e) {
            log.error("Error saving packaging master", e);
            response.setStatus(0);
            response.setMessage("Error: " + e.getMessage());
        }
        return response;
    }

    @Override
    public BaseResponse<PackagingMasterResponse> getAllPackagingMasters() {
        log.info("Fetching all packaging masters");
        BaseResponse<PackagingMasterResponse> response = new BaseResponse<>();
        List<PackagingMaster> list = packagingMasterRepository.findAll();
        List<PackagingMasterResponse> responseList = list.stream()
                .map(this::mapToPackagingMasterResponse)
                .collect(Collectors.toList());
        response.setData(responseList);
        response.setStatus(1);
        log.info("Found {} packaging masters", responseList.size());
        return response;
    }

    @Override
    public BaseResponse<PackagingMasterResponse> getPackagingMasterById(Long id) {
        log.info("Fetching packaging master by id: {}", id);
        BaseResponse<PackagingMasterResponse> response = new BaseResponse<>();
        packagingMasterRepository.findById(id).ifPresent(m -> 
            response.setData(Collections.singletonList(mapToPackagingMasterResponse(m)))
        );
        response.setStatus(1);
        if (response.getData() != null && !response.getData().isEmpty()) {
            log.info("Packaging master found for id: {}", id);
        } else {
            log.warn("Packaging master not found for id: {}", id);
        }
        return response;
    }

    @Override
    public BaseResponse<PackagingMaster> deletePackagingMaster(Long id) {
        log.info("Deleting packaging master with id: {}", id);
        BaseResponse<PackagingMaster> response = new BaseResponse<>();
        packagingMasterRepository.deleteById(id);
        log.info("Packaging master deleted successfully for id: {}", id);
        response.setStatus(1);
        response.setMessage("Deleted successfully");
        return response;
    }

    // === Packing Hierarchy Level ===

    @Override
    public BaseResponse<PackingHierarchyLevel> saveHierarchyLevel(PackingHierarchyLevel level) {
        BaseResponse<PackingHierarchyLevel> response = new BaseResponse<>();
        try {
            if (level.getId() == null) {
                level.setCreatedOn(new Date());
                level.setCreatedBy(loginUser.getUserId());
                level.setOrganizationId(loginUser.getOrgId());
                level.setSubOrganizationId(loginUser.getSubOrgId());
            } else {
                level.setModifiedOn(new Date());
                level.setModifiedBy(loginUser.getUserId());
            }
            PackingHierarchyLevel saved = packingHierarchyLevelRepository.save(level);
            log.info("Hierarchy Level saved successfully with id: {}", saved.getId());
            response.setStatus(1);
            response.setMessage("Hierarchy Level saved successfully");
            response.setData(Collections.singletonList(saved));
        } catch (Exception e) {
            log.error("Error saving hierarchy level", e);
            response.setStatus(0);
            response.setMessage("Error: " + e.getMessage());
        }
        return response;
    }

    @Override
    public BaseResponse<PackingHierarchyLevelResponse> getAllHierarchyLevels() {
        log.info("Fetching all hierarchy levels");
        BaseResponse<PackingHierarchyLevelResponse> response = new BaseResponse<>();
        List<PackingHierarchyLevel> list = packingHierarchyLevelRepository.findAll();
        List<PackingHierarchyLevelResponse> responseList = list.stream()
                .map(this::mapToPackingHierarchyLevelResponse)
                .collect(Collectors.toList());
        response.setData(responseList);
        response.setStatus(1);
        log.info("Found {} hierarchy levels", responseList.size());
        return response;
    }

    @Override
    public BaseResponse<PackingHierarchyLevelResponse> getHierarchyLevelById(Long id) {
        log.info("Fetching hierarchy level by id: {}", id);
        BaseResponse<PackingHierarchyLevelResponse> response = new BaseResponse<>();
        packingHierarchyLevelRepository.findById(id).ifPresent(l -> 
            response.setData(Collections.singletonList(mapToPackingHierarchyLevelResponse(l)))
        );
        response.setStatus(1);
        if (response.getData() != null && !response.getData().isEmpty()) {
            log.info("Hierarchy level found for id: {}", id);
        } else {
            log.warn("Hierarchy level not found for id: {}", id);
        }
        return response;
    }

    @Override
    public BaseResponse<PackingHierarchyLevel> deleteHierarchyLevel(Long id) {
        log.info("Deleting hierarchy level with id: {}", id);
        BaseResponse<PackingHierarchyLevel> response = new BaseResponse<>();
        packingHierarchyLevelRepository.deleteById(id);
        log.info("Hierarchy level deleted successfully for id: {}", id);
        response.setStatus(1);
        response.setMessage("Deleted successfully");
        return response;
    }
    // === Mapping Methods ===

    private PackagingTypeResponse mapToPackagingTypeResponse(PackagingType entity) {
        if (entity == null) return null;
        PackagingTypeResponse dto = new PackagingTypeResponse();
        dto.setId(entity.getId());
        dto.setTypeName(entity.getTypeName());
        return dto;
    }

    private PackagingTypeResponse mapToPackagingTypeResponseWithSubtypes(PackagingType entity) {
        if (entity == null) return null;
        PackagingTypeResponse dto = mapToPackagingTypeResponse(entity);
        List<PackagingSubtype> subtypes = packagingSubtypeRepository.findAll(); // Optimization: should filter in DB
        dto.setSubtypes(subtypes.stream()
                .filter(s -> s.getPackagingType() != null && s.getPackagingType().getId().equals(entity.getId()))
                .map(this::mapToPackagingSubtypeResponse)
                .collect(Collectors.toList()));
        return dto;
    }

    private PackagingSubtypeResponse mapToPackagingSubtypeResponse(PackagingSubtype entity) {
        if (entity == null) return null;
        PackagingSubtypeResponse dto = new PackagingSubtypeResponse();
        dto.setId(entity.getId());
        dto.setSubtypeName(entity.getSubtypeName());
        dto.setMaterialType(entity.getMaterialType());
        return dto;
    }

    private PackagingMasterResponse mapToPackagingMasterResponse(PackagingMaster entity) {
        if (entity == null) return null;
        PackagingMasterResponse dto = new PackagingMasterResponse();
        dto.setId(entity.getId());
        dto.setPackagingCode(entity.getPackagingCode());
        dto.setPackagingName(entity.getPackagingName());
        if (entity.getPackagingSubtype() != null) {
            dto.setPackagingSubtypeId(entity.getPackagingSubtype().getId());
            dto.setPackagingSubtypeName(entity.getPackagingSubtype().getSubtypeName());
        }
        dto.setUom(entity.getUom());
        dto.setLength(entity.getLength());
        dto.setWidth(entity.getWidth());
        dto.setHeight(entity.getHeight());
        dto.setDiameter(entity.getDiameter());
        dto.setWeight(entity.getWeight());
        dto.setVolume(entity.getVolume());
        dto.setIsStackable(entity.getIsStackable());
        dto.setNumberOfStackLevel(entity.getNumberOfStackLevel());
        dto.setMaxWeightCapacity(entity.getMaxWeightCapacity());
        return dto;
    }

    private PackingHierarchyLevelResponse mapToPackingHierarchyLevelResponse(PackingHierarchyLevel entity) {
        if (entity == null) return null;
        PackingHierarchyLevelResponse dto = new PackingHierarchyLevelResponse();
        dto.setId(entity.getId());
        dto.setOrganizationId(entity.getOrganizationId());
        dto.setSubOrganizationId(entity.getSubOrganizationId());
        dto.setLevelCode(entity.getLevelCode());
        dto.setLevelOrder(entity.getLevelOrder());
        return dto;
    }
}
