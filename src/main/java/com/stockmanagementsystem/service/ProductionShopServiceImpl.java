package com.stockmanagementsystem.service;

import com.stockmanagementsystem.entity.*;
import com.stockmanagementsystem.repository.AssemblyLineRepository;
import com.stockmanagementsystem.repository.StageRepository;
import com.stockmanagementsystem.repository.ProductionShopRepository;
import com.stockmanagementsystem.response.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class ProductionShopServiceImpl implements ProductionShopService {

    @Autowired
    private ProductionShopRepository shopRepository;

    @Autowired
    private AssemblyLineRepository lineRepository;

    @Autowired
    private StageRepository stageRepository;

    @Autowired
    private LoginUser loginUser;

    private void setAuditFields(Object entity, boolean isUpdate) {
        Date now = new Date();
        try {
            if (!isUpdate) {
                entity.getClass().getMethod("setCreatedBy", Integer.class).invoke(entity, loginUser.getUserId());
                entity.getClass().getMethod("setCreatedOn", Date.class).invoke(entity, now);
                entity.getClass().getMethod("setOrganizationId", Integer.class).invoke(entity, loginUser.getOrgId());
                entity.getClass().getMethod("setSubOrganizationId", Integer.class).invoke(entity,
                        loginUser.getSubOrgId());
                entity.getClass().getMethod("setIsDeleted", Boolean.class).invoke(entity, false);
            }
            entity.getClass().getMethod("setModifiedBy", Integer.class).invoke(entity, loginUser.getUserId());
            entity.getClass().getMethod("setModifiedOn", Date.class).invoke(entity, now);
        } catch (Exception e) {
            log.error("Error setting audit fields", e);
        }
    }

    @Override
    public BaseResponse<ProductionShop> saveProductionShop(ProductionShop shop) {
        setAuditFields(shop, false);
        ProductionShop savedShop = shopRepository.save(shop);
        List<ProductionShop> data = new ArrayList<>();
        data.add(savedShop);
        return new BaseResponse<>(200, "Shop saved successfully", data, 200, loginUser.getLogId());
    }

    @Override
    public BaseResponse<ProductionShop> updateProductionShop(Integer id, ProductionShop shop) {
        Optional<ProductionShop> existing = shopRepository.findById(id);
        if (existing.isPresent()) {
            ProductionShop updated = existing.get();
            updated.setErpShopCode(shop.getErpShopCode());
            updated.setShopCode(shop.getShopCode());
            updated.setShopName(shop.getShopName());
            updated.setDescription(shop.getDescription());
            updated.setShopType(shop.getShopType());
            setAuditFields(updated, true);
            shopRepository.save(updated);
            List<ProductionShop> data = new ArrayList<>();
            data.add(updated);
            return new BaseResponse<>(200, "Shop updated successfully", data, 200, loginUser.getLogId());
        }
        return new BaseResponse<>(404, "Shop not found", null, 404, loginUser.getLogId());
    }

    @Override
    public BaseResponse<ProductionShop> deleteProductionShop(Integer id) {
        Optional<ProductionShop> existing = shopRepository.findById(id);
        if (existing.isPresent()) {
            ProductionShop shop = existing.get();
            shop.setIsDeleted(true);
            setAuditFields(shop, true);
            shopRepository.save(shop);
            return new BaseResponse<>(200, "Shop deleted successfully", null, 200, loginUser.getLogId());
        }
        return new BaseResponse<>(404, "Shop not found", null, 404, loginUser.getLogId());
    }

    @Override
    public BaseResponse<ProductionShop> getAllProductionShops() {
        List<ProductionShop> shops = shopRepository.findByIsDeletedFalse();
        return new BaseResponse<>(200, "Shops fetched successfully", shops, 200, loginUser.getLogId());
    }

    @Override
    public BaseResponse<ProductionShop> getProductionShopById(Integer id) {
        Optional<ProductionShop> shop = shopRepository.findById(id);
        if (shop.isPresent() && !shop.get().getIsDeleted()) {
            List<ProductionShop> data = new ArrayList<>();
            data.add(shop.get());
            return new BaseResponse<>(200, "Shop fetched successfully", data, 200, loginUser.getLogId());
        }
        return new BaseResponse<>(404, "Shop not found", null, 404, loginUser.getLogId());
    }

    @Override
    public BaseResponse<AssemblyLine> saveProductionLine(AssemblyLine line) {
        if (line.getShopId() != null) {
            Optional<ProductionShop> shop = shopRepository.findById(line.getShopId());
            if (shop.isPresent()) {
                line.setProductionShop(shop.get());
            }
        }
        setAuditFields(line, false);
        AssemblyLine savedLine = lineRepository.save(line);
        List<AssemblyLine> data = new ArrayList<>();
        data.add(savedLine);
        return new BaseResponse<>(200, "Line saved successfully", data, 200, loginUser.getLogId());
    }

    @Override
    public BaseResponse<AssemblyLine> updateProductionLine(Integer id, AssemblyLine line) {
        AssemblyLine updated = lineRepository.findByIsDeletedAndId(false, id);
        if (updated != null) {
            updated.setErpLineCode(line.getErpLineCode());
            updated.setLineCode(line.getLineCode());
            updated.setLineName(line.getLineName());
            updated.setDescription(line.getDescription());
            updated.setSequenceNumber(line.getSequenceNumber());
            if (line.getShopId() != null) {
                Optional<ProductionShop> shop = shopRepository.findById(line.getShopId());
                shop.ifPresent(updated::setProductionShop);
            }
            setAuditFields(updated, true);
            lineRepository.save(updated);
            List<AssemblyLine> data = new ArrayList<>();
            data.add(updated);
            return new BaseResponse<>(200, "Line updated successfully", data, 200, loginUser.getLogId());
        }
        return new BaseResponse<>(404, "Line not found", null, 404, loginUser.getLogId());
    }

    @Override
    public BaseResponse<AssemblyLine> deleteProductionLine(Integer id) {
        AssemblyLine line = lineRepository.findByIsDeletedAndId(false, id);
        if (line != null) {
            line.setIsDeleted(true);
            setAuditFields(line, true);
            lineRepository.save(line);
            return new BaseResponse<>(200, "Line deleted successfully", null, 200, loginUser.getLogId());
        }
        return new BaseResponse<>(404, "Line not found", null, 404, loginUser.getLogId());
    }

    @Override
    public BaseResponse<AssemblyLine> getAllProductionLines() {
        List<AssemblyLine> lines = lineRepository.findByIsDeleted(false);
        return new BaseResponse<>(200, "Lines fetched successfully", lines, 200, loginUser.getLogId());
    }

    @Override
    public BaseResponse<AssemblyLine> getProductionLinesByShopId(Integer shopId) {
        Optional<ProductionShop> shop = shopRepository.findById(shopId);
        if (shop.isPresent()) {
            List<AssemblyLine> lines = lineRepository
                    .findByProductionShopAndIsDeletedFalseOrderBySequenceNumberAsc(shop.get());
            return new BaseResponse<>(200, "Lines fetched successfully", lines, 200, loginUser.getLogId());
        }
        return new BaseResponse<>(404, "Shop not found", null, 404, loginUser.getLogId());
    }

    @Override
    public BaseResponse<Stage> saveProductionLineStage(Stage stage) {
        if (stage.getLineId() != null) {
            Optional<AssemblyLine> line = lineRepository.findById(stage.getLineId());
            if (line.isPresent()) {
                stage.setAssemblyLine(line.get());
            }
        }
        setAuditFields(stage, false);
        Stage savedStage = stageRepository.save(stage);
        List<Stage> data = new ArrayList<>();
        data.add(savedStage);
        return new BaseResponse<>(200, "Stage saved successfully", data, 200, loginUser.getLogId());
    }

    @Override
    public BaseResponse<Stage> updateProductionLineStage(Integer id, Stage stage) {
        Stage updated = stageRepository.findByIsDeletedAndId(false, id);
        if (updated != null) {
            updated.setErpStageCode(stage.getErpStageCode());
            updated.setStageCode(stage.getStageCode());
            updated.setStageName(stage.getStageName());
            updated.setSequenceNumber(stage.getSequenceNumber());
            if (stage.getLineId() != null) {
                Optional<AssemblyLine> line = lineRepository.findById(stage.getLineId());
                line.ifPresent(updated::setAssemblyLine);
            }
            setAuditFields(updated, true);
            stageRepository.save(updated);
            List<Stage> data = new ArrayList<>();
            data.add(updated);
            return new BaseResponse<>(200, "Stage updated successfully", data, 200, loginUser.getLogId());
        }
        return new BaseResponse<>(404, "Stage not found", null, 404, loginUser.getLogId());
    }

    @Override
    public BaseResponse<Stage> deleteProductionLineStage(Integer id) {
        Stage stage = stageRepository.findByIsDeletedAndId(false, id);
        if (stage != null) {
            stage.setIsDeleted(true);
            setAuditFields(stage, true);
            stageRepository.save(stage);
            return new BaseResponse<>(200, "Stage deleted successfully", null, 200, loginUser.getLogId());
        }
        return new BaseResponse<>(404, "Stage not found", null, 404, loginUser.getLogId());
    }

    @Override
    public BaseResponse<Stage> getAllProductionLineStages() {
        List<Stage> stages = stageRepository.findByIsDeletedAndSubOrganizationId(false, loginUser.getSubOrgId());
        return new BaseResponse<>(200, "Stages fetched successfully", stages, 200, loginUser.getLogId());
    }

    @Override
    public BaseResponse<Stage> getStagesByLineId(Integer lineId) {
        Optional<AssemblyLine> line = lineRepository.findById(lineId);
        if (line.isPresent()) {
            List<Stage> stages = stageRepository
                    .findByAssemblyLineAndIsDeletedFalseOrderBySequenceNumberAsc(line.get());
            return new BaseResponse<>(200, "Stages fetched successfully", stages, 200, loginUser.getLogId());
        }
        return new BaseResponse<>(404, "Line not found", null, 404, loginUser.getLogId());
    }
}
