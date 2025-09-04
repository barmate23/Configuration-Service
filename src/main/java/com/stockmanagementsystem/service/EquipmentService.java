package com.stockmanagementsystem.service;

import com.stockmanagementsystem.entity.Equipment;
import com.stockmanagementsystem.request.EquipmentRequest;
import com.stockmanagementsystem.response.BaseResponse;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
public interface EquipmentService {
    BaseResponse<Equipment> saveEquipment(EquipmentRequest equipmentRequest);

    BaseResponse<Equipment> updateEquipment(Integer id, EquipmentRequest equipmentRequest);

    BaseResponse<Equipment> getAllEquipmentWithPagination(Integer pageNo, Integer pageSize, List<Integer> storeId, List<String> trolleyType);

    BaseResponse<Equipment> deleteEquipmentById(Integer id);



    BaseResponse<Equipment> getAllEquipment();

    ByteArrayOutputStream generateBarcodePDF();

    String equipmentGenerator(Integer count);
}
