package com.stockmanagementsystem.service;

import com.stockmanagementsystem.entity.AcceptedRejectedStagingArea;
import com.stockmanagementsystem.entity.Dock;
import com.stockmanagementsystem.request.*;
import com.stockmanagementsystem.response.*;
import java.io.ByteArrayOutputStream;

import java.util.List;

public interface DocksService {

    BaseResponse saveDock(DockRequest dockRequestRequest);

    String generateDockId(Integer count);

    BaseResponse deleteByDockId(Integer dockId);

    BaseResponse updateDock(Integer id, DockRequest dockRequest);
    BaseResponse<List<DockNameResponse>> getDockNamesWithIds();

    BaseResponse<List<AttributeResponse>> getAttributesWithIds();

    BaseResponse<Dock> getAllDocks(int page, int pageSize);

    BaseResponse<Dock> getDocksById(Integer dockId);

    byte[] generateExcelContentById(Integer id);
    BaseResponse<Dock> searchDocks(
            Integer pageNumber, Integer pageSize, List<String> dockIds, List<String> attributes, List<Integer> createdYear
    );
    byte[] generateExcelForAllDocks();
    List<String> getAllDockIds();


    byte[] generateDockBarcodePDF();

    byte[] generateBarcode(String barcode);

    byte[] getStageBarcode(Boolean isAccepted);

    BaseResponse<List<UserResponse>> getUsersWithIds();

    BaseResponse<List<StoreWithIdResponse>> getStoresWithIds();

    BaseResponse<AcceptedRejectedStagingArea> getAllStagingArea();
}
