package com.stockmanagementsystem.service;

import com.stockmanagementsystem.entity.LoginUser;
import com.stockmanagementsystem.entity.AssemblyLine;
import com.stockmanagementsystem.entity.ProductionShop;
import com.stockmanagementsystem.entity.ResponseMessage;
import com.stockmanagementsystem.entity.Stage;
import com.stockmanagementsystem.repository.AssemblyLineRepository;
import com.stockmanagementsystem.repository.ProductionShopRepository;
import com.stockmanagementsystem.repository.StageRepository;
import com.stockmanagementsystem.request.ProductionLineRequest;
import com.stockmanagementsystem.request.ProductionLineStageRequest;
import com.stockmanagementsystem.request.ProductionShopRequest;
import com.stockmanagementsystem.response.*;
import com.stockmanagementsystem.utils.ResponseKeyConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.stockmanagementsystem.utils.GlobalMessages.getResponseMessages;

@Slf4j
@Service
public class ProductionShopServiceImpl implements ProductionShopService {

    @Autowired
    private ProductionShopRepository productionShopRepository;

    @Autowired
    private AssemblyLineRepository assemblyLineRepository;

    @Autowired
    private StageRepository stageRepository;

    @Autowired
    private LoginUser loginUser;

    @Override
    public BaseResponse<ProductionShopResponse> saveProductionShop(ProductionShopRequest request) {
        long startTime = System.currentTimeMillis();
        BaseResponse<ProductionShopResponse> response = new BaseResponse<>();
        try {
            ProductionShop shop = new ProductionShop();
            shop.setErpShopCode(request.getErpShopCode());
            shop.setShopCode(request.getShopCode());
            shop.setShopName(request.getShopName());
            shop.setDescription(request.getDescription());
            shop.setShopType(request.getShopType());
            shop.setIsDeleted(false);
            shop.setCreatedOn(new Date());
            shop.setCreatedBy(loginUser.getUserId());
            shop.setModifiedBy(loginUser.getUserId());
            shop.setModifiedOn(new Date());
            shop.setOrganizationId(loginUser.getOrgId());
            shop.setSubOrganizationId(loginUser.getSubOrgId());
            
            productionShopRepository.save(shop);

            if (request.getProductionLineRequests() != null) {
                for (ProductionLineRequest lineReq : request.getProductionLineRequests()) {
                    AssemblyLine line = new AssemblyLine();
                    line.setErpLineCode(lineReq.getErpLineCode());
                    line.setLineCode(lineReq.getLineCode());
                    line.setLineName(lineReq.getLineName());
                    line.setDescription(lineReq.getDescription());
                    line.setSequenceNumber(lineReq.getSequenceNumber());
                    line.setProductionShop(shop);
                    line.setIsDeleted(false);
                    line.setCreatedOn(new Date());
                    line.setCreatedBy(loginUser.getUserId());
                    line.setModifiedBy(loginUser.getUserId());
                    line.setModifiedOn(new Date());
                    line.setOrganizationId(loginUser.getOrgId());
                    line.setSubOrganizationId(loginUser.getSubOrgId());
                    assemblyLineRepository.save(line);

                    if (lineReq.getStageRequests() != null) {
                        for (ProductionLineStageRequest stageReq : lineReq.getStageRequests()) {
                            Stage stage = new Stage();
                            stage.setErpStageCode(stageReq.getErpStageCode());
                            stage.setStageCode(stageReq.getStageCode());
                            stage.setStageName(stageReq.getStageName());
                            stage.setSequenceNumber(stageReq.getSequenceNumber());
                            stage.setAssemblyLine(line);
                            stage.setIsDeleted(false);
                            stage.setCreatedOn(new Date());
                            stage.setCreatedBy(loginUser.getUserId());
                            stage.setModifiedBy(loginUser.getUserId());
                            stage.setModifiedOn(new Date());
                            stage.setOrganizationId(loginUser.getOrgId());
                            stage.setSubOrganizationId(loginUser.getSubOrgId());
                            stageRepository.save(stage);
                        }
                    }
                }
            }

            ResponseMessage responseMessage = getResponseMessages(ResponseKeyConstant.UPLD10024S);
            response.setCode(responseMessage.getCode());
            response.setStatus(responseMessage.getStatus());
            response.setMessage(responseMessage.getMessage());
            List<ProductionShopResponse> data = new ArrayList<>();
            data.add(mapToProductionShopResponse(shop));
            response.setData(data);
            response.setStatus(1);
        } catch (Exception e) {
            log.error("Error in saveProductionShop", e);
            ResponseMessage responseMessage = getResponseMessages(ResponseKeyConstant.UPLD10023F);
            response.setCode(responseMessage.getCode());
            response.setStatus(0);
            response.setMessage(responseMessage.getMessage());
        }
        return response;
    }

    @Override
    public BaseResponse<ProductionShopResponse> updateProductionShop(Integer id, ProductionShopRequest request) {
        BaseResponse<ProductionShopResponse> response = new BaseResponse<>();
        try {
            ProductionShop shop = productionShopRepository.findByIsDeletedAndId(false, id);
            if (shop != null) {
                shop.setErpShopCode(request.getErpShopCode());
                shop.setShopCode(request.getShopCode());
                shop.setShopName(request.getShopName());
                shop.setDescription(request.getDescription());
                shop.setShopType(request.getShopType());
                shop.setModifiedBy(loginUser.getUserId());
                shop.setModifiedOn(new Date());
                productionShopRepository.save(shop);

                ResponseMessage responseMessage = getResponseMessages(ResponseKeyConstant.UPLD10023S);
                response.setCode(responseMessage.getCode());
                response.setStatus(responseMessage.getStatus());
                response.setMessage(responseMessage.getMessage());
                List<ProductionShopResponse> data = new ArrayList<>();
                data.add(mapToProductionShopResponse(shop));
                response.setData(data);
                response.setStatus(1);
            }
        } catch (Exception e) {
            log.error("Error in updateProductionShop", e);
            ResponseMessage responseMessage = getResponseMessages(ResponseKeyConstant.UPLD10022F);
            response.setCode(responseMessage.getCode());
            response.setStatus(0);
            response.setMessage(responseMessage.getMessage());
        }
        return response;
    }

    @Override
    public BaseResponse<ProductionShopResponse> getAllProductionShops() {
        BaseResponse<ProductionShopResponse> response = new BaseResponse<>();
        try {
            List<ProductionShop> shops = productionShopRepository.findByIsDeletedAndSubOrganizationId(false, loginUser.getSubOrgId());
            ResponseMessage responseMessage = getResponseMessages(ResponseKeyConstant.UPLD10021S);
            response.setCode(responseMessage.getCode());
            response.setStatus(responseMessage.getStatus());
            response.setMessage(responseMessage.getMessage());
            response.setData(mapToProductionShopResponseList(shops));
            response.setStatus(1);
        } catch (Exception e) {
            log.error("Error in getAllProductionShops", e);
            ResponseMessage responseMessage = getResponseMessages(ResponseKeyConstant.UPLD10020F);
            response.setCode(responseMessage.getCode());
            response.setStatus(0);
            response.setMessage(responseMessage.getMessage());
        }
        return response;
    }

    @Override
    public BaseResponse<ProductionShopResponse> getProductionShopById(Integer id) {
        BaseResponse<ProductionShopResponse> response = new BaseResponse<>();
        try {
            ProductionShop shop = productionShopRepository.findByIsDeletedAndId(false, id);
            List<ProductionShopResponse> data = new ArrayList<>();
            if (shop != null) data.add(mapToProductionShopResponse(shop));
            response.setData(data);
            response.setStatus(1);
        } catch (Exception e) {
            log.error("Error in getProductionShopById", e);
            response.setStatus(0);
        }
        return response;
    }

    @Override
    public BaseResponse<ProductionShopResponse> deleteProductionShopById(Integer id) {
        BaseResponse<ProductionShopResponse> response = new BaseResponse<>();
        try {
            ProductionShop shop = productionShopRepository.findByIsDeletedAndId(false, id);
            if (shop != null) {
                shop.setIsDeleted(true);
                productionShopRepository.save(shop);
                ResponseMessage responseMessage = getResponseMessages(ResponseKeyConstant.UPLD10019S);
                response.setCode(responseMessage.getCode());
                response.setStatus(responseMessage.getStatus());
                response.setMessage(responseMessage.getMessage());
                response.setStatus(1);
            }
        } catch (Exception e) {
            log.error("Error in deleteProductionShopById", e);
            ResponseMessage responseMessage = getResponseMessages(ResponseKeyConstant.UPLD10018F);
            response.setCode(responseMessage.getCode());
            response.setStatus(0);
            response.setMessage(responseMessage.getMessage());
        }
        return response;
    }

    @Override
    public BaseResponse<ProductionShopResponse> getAllProductionShopsWithPagination(Integer pageNo, Integer pageSize) {
        BaseResponse<ProductionShopResponse> response = new BaseResponse<>();
        try {
            Pageable pageable = PageRequest.of(pageNo, pageSize);
            Page<ProductionShop> page = productionShopRepository.findByIsDeletedAndSubOrganizationId(false, loginUser.getSubOrgId(), pageable);
            response.setData(mapToProductionShopResponseList(page.getContent()));
            response.setTotalRecordCount(page.getTotalElements());
            response.setTotalPageCount(page.getTotalPages());
            response.setStatus(1);
        } catch (Exception e) {
            log.error("Error in getAllProductionShopsWithPagination", e);
            response.setStatus(0);
        }
        return response;
    }

    @Override
    public BaseResponse<ProductionShopResponse> getProductionShopHierarchy() {
        BaseResponse<ProductionShopResponse> response = new BaseResponse<>();
        try {
            List<ProductionShop> shops = productionShopRepository.findByIsDeletedAndSubOrganizationId(false, loginUser.getSubOrgId());
            response.setData(mapToProductionShopResponseList(shops));
            response.setStatus(1);
            response.setMessage("Hierarchy fetched successfully");
        } catch (Exception e) {
            log.error("Error in getProductionShopHierarchy", e);
            response.setStatus(0);
            response.setMessage("Failed to fetch hierarchy");
        }
        return response;
    }

    @Override
    public BaseResponse<AssemblyLineResponse> getProductionLinesByShopId(Integer shopId) {
        BaseResponse<AssemblyLineResponse> response = new BaseResponse<>();
        try {
            List<AssemblyLine> lines = assemblyLineRepository.findByIsDeletedAndProductionShopId(false, shopId);
            response.setData(mapToAssemblyLineResponseList(lines));
            response.setStatus(1);
            response.setMessage("Production lines fetched successfully");
        } catch (Exception e) {
            log.error("Error in getProductionLinesByShopId", e);
            response.setStatus(0);
            response.setMessage("Failed to fetch production lines");
        }
        return response;
    }

    @Override
    public BaseResponse<AssemblyLineResponse> getProductionLineById(Integer id) {
        BaseResponse<AssemblyLineResponse> response = new BaseResponse<>();
        try {
            AssemblyLine line = assemblyLineRepository.findByIsDeletedAndId(false, id);
            List<AssemblyLineResponse> data = new ArrayList<>();
            if (line != null) data.add(mapToAssemblyLineResponse(line));
            response.setData(data);
            response.setStatus(1);
            response.setMessage("Production line fetched successfully");
        } catch (Exception e) {
            log.error("Error in getProductionLineById", e);
            response.setStatus(0);
            response.setMessage("Failed to fetch production line");
        }
        return response;
    }

    @Override
    public BaseResponse<StageResponse> getStagesByLineId(Integer lineId) {
        BaseResponse<StageResponse> response = new BaseResponse<>();
        try {
            List<Stage> stages = stageRepository.findByIsDeletedAndAssemblyLineId(false, lineId);
            response.setData(mapToStageResponseList(stages));
            response.setStatus(1);
            response.setMessage("Stages fetched successfully");
        } catch (Exception e) {
            log.error("Error in getStagesByLineId", e);
            response.setStatus(0);
            response.setMessage("Failed to fetch stages");
        }
        return response;
    }

    private List<ProductionShopResponse> mapToProductionShopResponseList(List<ProductionShop> shops) {
        List<ProductionShopResponse> responseList = new ArrayList<>();
        if (shops != null) {
            for (ProductionShop shop : shops) {
                responseList.add(mapToProductionShopResponse(shop));
            }
        }
        return responseList;
    }

    private ProductionShopResponse mapToProductionShopResponse(ProductionShop shop) {
        ProductionShopResponse response = new ProductionShopResponse();
        response.setId(shop.getId());
        response.setErpShopCode(shop.getErpShopCode());
        response.setShopCode(shop.getShopCode());
        response.setShopName(shop.getShopName());
        response.setDescription(shop.getDescription());
        response.setShopType(shop.getShopType());
        if (shop.getAssemblyLines() != null) {
            response.setAssemblyLines(mapToAssemblyLineResponseList(shop.getAssemblyLines()));
        }
        return response;
    }

    private List<AssemblyLineResponse> mapToAssemblyLineResponseList(List<AssemblyLine> lines) {
        List<AssemblyLineResponse> responseList = new ArrayList<>();
        if (lines != null) {
            for (AssemblyLine line : lines) {
                responseList.add(mapToAssemblyLineResponse(line));
            }
        }
        return responseList;
    }

    private AssemblyLineResponse mapToAssemblyLineResponse(AssemblyLine line) {
        AssemblyLineResponse response = new AssemblyLineResponse();
        response.setId(line.getId());
        response.setErpLineCode(line.getErpLineCode());
        response.setLineCode(line.getLineCode());
        response.setLineName(line.getLineName());
        response.setDescription(line.getDescription());
        response.setSequenceNumber(line.getSequenceNumber());
        if (line.getStages() != null) {
            response.setStages(mapToStageResponseList(line.getStages()));
        }
        return response;
    }

    private List<StageResponse> mapToStageResponseList(List<Stage> stages) {
        List<StageResponse> responseList = new ArrayList<>();
        if (stages != null) {
            for (Stage stage : stages) {
                responseList.add(mapToStageResponse(stage));
            }
        }
        return responseList;
    }

    private StageResponse mapToStageResponse(Stage stage) {
        StageResponse response = new StageResponse();
        response.setId(stage.getId());
        response.setErpStageCode(stage.getErpStageCode());
        response.setStageCode(stage.getStageCode());
        response.setStageName(stage.getStageName());
        response.setSequenceNumber(stage.getSequenceNumber());
        return response;
    }
}
