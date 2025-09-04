package com.stockmanagementsystem.service;

import com.stockmanagementsystem.entity.Area;
import com.stockmanagementsystem.entity.CommonMaster;
import com.stockmanagementsystem.entity.Zone;
import com.stockmanagementsystem.response.BaseResponse;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.List;

@Service
public interface ZoneService  {
    BaseResponse<Zone> updateZone(Integer id,Integer areaId, String erpZoneId, String zoneName,Integer statusId);

    BaseResponse<Zone> createZone(Area area, Integer sq);

    BaseResponse<Zone> getAllZones(List<Integer> areaId);



    BaseResponse<Zone> getAllZonesWithPagination(Integer pageNo, Integer pageSize, List<Integer> storeId, List<Integer> areaId, List<Integer> zoneId, Date startDate, Date endDate);

    BaseResponse<Zone> deleteZoneById(Integer zoneId);

    ByteArrayOutputStream generateBarcodePDF(String zoneId, Boolean getAll);
}
