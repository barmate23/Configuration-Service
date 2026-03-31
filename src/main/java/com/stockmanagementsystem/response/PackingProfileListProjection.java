package com.stockmanagementsystem.response;

import java.util.Date;

public interface PackingProfileListProjection {

    Long getId();                    // ItemSupplierPackingProfileMap.id

    Long getPackingProfileId();      // PackingProfileConfigMaster.id

    String getItemName();

    String getItemCode();

    String getErpSupplierId();

    String getSupplierName();

    String getPackingHierarchyLevelCode(); // PRIMARY / SECONDARY / TERTIARY

    Boolean getIsActive();

    Date getModifiedOn();
}


