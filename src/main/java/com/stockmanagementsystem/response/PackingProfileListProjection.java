package com.stockmanagementsystem.response;

import java.util.Date;

public interface PackingProfileListProjection {

    Long getId();

    String getItemName();
    String getSupplierName();

    String getPackingLevel();
    Boolean getIsActive();

    Date getModifiedOn();
}
