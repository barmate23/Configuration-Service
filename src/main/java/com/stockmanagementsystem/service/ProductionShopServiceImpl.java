package com.stockmanagementsystem.service;

import com.stockmanagementsystem.entity.*;
import com.stockmanagementsystem.repository.AssemblyLineRepository;
import com.stockmanagementsystem.repository.StageRepository;
import com.stockmanagementsystem.repository.ProductionShopRepository;
import com.stockmanagementsystem.request.AssemblyLineRequest;
import com.stockmanagementsystem.request.ProductionShopRequest;
import com.stockmanagementsystem.request.StageRequest;
import com.stockmanagementsystem.response.AssemblyLineResponse;
import com.stockmanagementsystem.response.BaseResponse;
import com.stockmanagementsystem.response.ProductionShopResponse;
import com.stockmanagementsystem.response.StageResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    public BaseResponse<ProductionShop> saveProductionShop(ProductionShopRequest shopRequest) {
        ProductionShop shop = new ProductionShop();
        shop.setErpShopCode(shopRequest.getErpShopCode());
        shop.setShopCode(shopRequest.getShopCode());
        shop.setShopName(shopRequest.getShopName());
        shop.setDescription(shopRequest.getDescription());
        shop.setShopType(shopRequest.getShopType());
        setAuditFields(shop, false);
        ProductionShop savedShop = shopRepository.save(shop);

        if (shopRequest.getAssemblyLineRequests() != null) {
            for (AssemblyLineRequest lineRequest : shopRequest.getAssemblyLineRequests()) {
                AssemblyLine line = new AssemblyLine();
                line.setErpLineCode(lineRequest.getErpLineCode());
                line.setLineCode(lineRequest.getLineCode());
                line.setLineName(lineRequest.getLineName());
                line.setLineNumber(lineRequest.getLineNumber());
                line.setAssemblyLineId(lineRequest.getAssemblyLineId());
                line.setDescription(lineRequest.getDescription());
                line.setSequenceNumber(lineRequest.getSequenceNumber());
                line.setProductionShop(savedShop);
                setAuditFields(line, false);
                AssemblyLine savedLine = lineRepository.save(line);

                if (lineRequest.getStageRequests() != null) {
                    for (StageRequest stageRequest : lineRequest.getStageRequests()) {
                        Stage stage = new Stage();
                        stage.setErpStageCode(stageRequest.getErpStageCode());
                        stage.setStageCode(stageRequest.getStageCode());
                        stage.setStageName(stageRequest.getStageName());
                        stage.setStageId(stageRequest.getStageId());
                        stage.setSequenceNumber(stageRequest.getSequenceNumber());
                        stage.setAssemblyLine(savedLine);
                        setAuditFields(stage, false);
                        stageRepository.save(stage);
                    }
                }
            }
        }

        List<ProductionShop> data = new ArrayList<>();
        data.add(savedShop);
        return new BaseResponse<>(200, "Shop hierarchy saved successfully", data, 200, loginUser.getLogId());
    }

    @Override
    public BaseResponse<ProductionShop> updateProductionShop(Integer id, ProductionShopRequest shopRequest) {
        Optional<ProductionShop> existing = shopRepository.findById(id);
        if (existing.isPresent()) {
            ProductionShop updated = existing.get();
            updated.setErpShopCode(shopRequest.getErpShopCode());
            updated.setShopCode(shopRequest.getShopCode());
            updated.setShopName(shopRequest.getShopName());
            updated.setDescription(shopRequest.getDescription());
            updated.setShopType(shopRequest.getShopType());
            setAuditFields(updated, true);
            shopRepository.save(updated);

            if (shopRequest.getAssemblyLineRequests() != null) {
                for (AssemblyLineRequest lineRequest : shopRequest.getAssemblyLineRequests()) {
                    AssemblyLine line;
                    if (lineRequest.getAssemblyLineId() != null && lineRepository.findByIsDeletedAndSubOrganizationIdAndAssemblyLineId(false, loginUser.getSubOrgId(), lineRequest.getAssemblyLineId()) != null) {
                        line = lineRepository.findByIsDeletedAndSubOrganizationIdAndAssemblyLineId(false, loginUser.getSubOrgId(), lineRequest.getAssemblyLineId());
                        setAuditFields(line, true);
                    } else {
                        line = new AssemblyLine();
                        setAuditFields(line, false);
                    }
                    line.setErpLineCode(lineRequest.getErpLineCode());
                    line.setLineCode(lineRequest.getLineCode());
                    line.setLineName(lineRequest.getLineName());
                    line.setLineNumber(lineRequest.getLineNumber());
                    line.setAssemblyLineId(lineRequest.getAssemblyLineId());
                    line.setDescription(lineRequest.getDescription());
                    line.setSequenceNumber(lineRequest.getSequenceNumber());
                    line.setProductionShop(updated);
                    AssemblyLine savedLine = lineRepository.save(line);

                    if (lineRequest.getStageRequests() != null) {
                        for (StageRequest stageRequest : lineRequest.getStageRequests()) {
                            Stage stage;
                            if (stageRequest.getId() != null) {
                                stage = stageRepository.findByIsDeletedAndId(false, stageRequest.getId());
                                setAuditFields(stage, true);
                            } else {
                                stage = new Stage();
                                setAuditFields(stage, false);
                            }
                            stage.setErpStageCode(stageRequest.getErpStageCode());
                            stage.setStageCode(stageRequest.getStageCode());
                            stage.setStageName(stageRequest.getStageName());
                            stage.setStageId(stageRequest.getStageId());
                            stage.setSequenceNumber(stageRequest.getSequenceNumber());
                            stage.setAssemblyLine(savedLine);
                            stageRepository.save(stage);
                        }
                    }
                }
            }

            List<ProductionShop> data = new ArrayList<>();
            data.add(updated);
            return new BaseResponse<>(200, "Shop hierarchy updated successfully", data, 200, loginUser.getLogId());
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
    public BaseResponse<ProductionShopResponse> getAllProductionShops() {
        List<ProductionShop> shops = shopRepository.findByIsDeletedFalse();
        List<ProductionShopResponse> data = shops.stream()
                .map(this::mapToProductionShopResponse)
                .collect(Collectors.toList());
        return new BaseResponse<>(200, "Shops fetched successfully", data, 200, loginUser.getLogId());
    }

    @Override
    public BaseResponse<ProductionShopResponse> getProductionShopById(Integer id) {
        Optional<ProductionShop> shopOpt = shopRepository.findById(id);
        if (shopOpt.isPresent() && !shopOpt.get().getIsDeleted()) {
            List<ProductionShopResponse> data = new ArrayList<>();
            data.add(mapToProductionShopResponse(shopOpt.get()));
            return new BaseResponse<>(200, "Shop fetched successfully", data, 200, loginUser.getLogId());
        }
        return new BaseResponse<>(404, "Shop not found", null, 404, loginUser.getLogId());
    }

    private ProductionShopResponse mapToProductionShopResponse(ProductionShop shop) {
        ProductionShopResponse response = new ProductionShopResponse();
        response.setId(shop.getId());
        response.setErpShopCode(shop.getErpShopCode());
        response.setShopCode(shop.getShopCode());
        response.setShopName(shop.getShopName());
        response.setDescription(shop.getDescription());
        response.setShopType(shop.getShopType());

        List<AssemblyLine> lines = lineRepository.findByProductionShopAndIsDeletedFalseOrderBySequenceNumberAsc(shop);
        if (lines != null) {
            List<AssemblyLineResponse> lineResponses = lines.stream().map(line -> {
                AssemblyLineResponse lr = new AssemblyLineResponse();
                lr.setId(line.getId());
                lr.setErpLineCode(line.getErpLineCode());
                lr.setLineCode(line.getLineCode());
                lr.setLineName(line.getLineName());
                lr.setLineNumber(line.getLineNumber());
                lr.setAssemblyLineId(line.getAssemblyLineId());
                lr.setDescription(line.getDescription());
                lr.setSequenceNumber(line.getSequenceNumber());

                List<Stage> stages = stageRepository.findByAssemblyLineAndIsDeletedFalseOrderBySequenceNumberAsc(line);
                if (stages != null) {
                    List<StageResponse> stageResponses = stages.stream().map(stage -> {
                        StageResponse sr = new StageResponse();
                        sr.setId(stage.getId());
                        sr.setErpStageCode(stage.getErpStageCode());
                        sr.setStageCode(stage.getStageCode());
                        sr.setStageName(stage.getStageName());
                        sr.setStageId(stage.getStageId());
                        sr.setSequenceNumber(stage.getSequenceNumber());
                        return sr;
                    }).collect(Collectors.toList());
                    lr.setStages(stageResponses);
                }
                return lr;
            }).collect(Collectors.toList());
            response.setAssemblyLines(lineResponses);
        }
        return response;
    }

    @Override
    public BaseResponse<AssemblyLine> saveProductionLine(AssemblyLineRequest lineRequest) {
        AssemblyLine line = new AssemblyLine();
        line.setErpLineCode(lineRequest.getErpLineCode());
        line.setLineCode(lineRequest.getLineCode());
        line.setLineName(lineRequest.getLineName());
        line.setLineNumber(lineRequest.getLineNumber());
        line.setAssemblyLineId(lineRequest.getAssemblyLineId());
        line.setDescription(lineRequest.getDescription());
        line.setSequenceNumber(lineRequest.getSequenceNumber());
        if (lineRequest.getShopId() != null) {
            Optional<ProductionShop> shop = shopRepository.findById(lineRequest.getShopId());
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
    public BaseResponse<AssemblyLine> updateProductionLine(Integer id, AssemblyLineRequest lineRequest) {
        AssemblyLine updated = lineRepository.findByIsDeletedAndId(false, id);
        if (updated != null) {
            updated.setErpLineCode(lineRequest.getErpLineCode());
            updated.setLineCode(lineRequest.getLineCode());
            updated.setLineName(lineRequest.getLineName());
            updated.setLineNumber(lineRequest.getLineNumber());
            updated.setAssemblyLineId(lineRequest.getAssemblyLineId());
            updated.setDescription(lineRequest.getDescription());
            updated.setSequenceNumber(lineRequest.getSequenceNumber());
            if (lineRequest.getShopId() != null) {
                Optional<ProductionShop> shop = shopRepository.findById(lineRequest.getShopId());
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
    public BaseResponse<Stage> saveProductionLineStage(StageRequest stageRequest) {
        Stage stage = new Stage();
        stage.setErpStageCode(stageRequest.getErpStageCode());
        stage.setStageCode(stageRequest.getStageCode());
        stage.setStageName(stageRequest.getStageName());
        stage.setStageId(stageRequest.getStageId());
        stage.setSequenceNumber(stageRequest.getSequenceNumber());
        if (stageRequest.getLineId() != null) {
            Optional<AssemblyLine> line = lineRepository.findById(stageRequest.getLineId());
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
    public BaseResponse<Stage> updateProductionLineStage(Integer id, StageRequest stageRequest) {
        Stage updated = stageRepository.findByIsDeletedAndId(false, id);
        if (updated != null) {
            updated.setErpStageCode(stageRequest.getErpStageCode());
            updated.setStageCode(stageRequest.getStageCode());
            updated.setStageName(stageRequest.getStageName());
            updated.setStageId(stageRequest.getStageId());
            updated.setSequenceNumber(stageRequest.getSequenceNumber());
            if (stageRequest.getLineId() != null) {
                Optional<AssemblyLine> line = lineRepository.findById(stageRequest.getLineId());
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
