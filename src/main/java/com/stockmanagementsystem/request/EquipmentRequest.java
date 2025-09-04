package com.stockmanagementsystem.request;

import com.stockmanagementsystem.entity.Store;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Data
public class EquipmentRequest {
    private String trolleyId;
    private String assetId;
    private String equipmentName;

    private String trolleyType;
    private Integer storeId;

}
