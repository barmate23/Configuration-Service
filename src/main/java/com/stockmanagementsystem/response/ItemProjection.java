package com.stockmanagementsystem.response;

import java.util.List;

public interface ItemProjection {

    Integer getId();
    String getItemCode();
    String getItemName();
    String getItemDescription();

    String getType();
    String getItemClass();
    Float getItemUnitWeight();
    String getAttribute();
    String getUom();
    SupplierProjection getSupplier();
    Float getMaterialUnitRate();
    String getCurrency();
    String getContainerCode();
    String getContainerType();
    String getDimensionUOM();
    Float getWidth();
    Float getHeight();
    Float getLength();
    Float getCircumference();
    Float getContainerWeight();
    Integer getItemQty();
    Integer getMinimumOrderQty();
    Integer getOptimumLevel();
    Integer getReorderLevel();
    Integer getSafetyLevel();
    Integer getCriticalLevel();
    Integer getLeadDays();
    Integer getLeadHours();
    String getDock();

    List<ItemResponseAsPerLocation> getResponseAsPerLocationList();

    void setResponseAsPerLocationList(List<ItemResponseAsPerLocation> responseAsPerLocationList);
}
