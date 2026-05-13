package com.stockmanagementsystem.response;

import java.util.Date;

public interface PackingProfileListProjection {

    Long getId();                    // ItemSupplierPackingProfileMap.id

    Long getPackingProfileId();      // PackingProfileConfigMaster.id

    String getItemName();

    String getItemCode();

    String getErpSupplierId();

    String getSupplierName();


    Boolean getIsActive();

    Date getModifiedOn();
}


