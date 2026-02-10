package com.stockmanagementsystem.utils;


import java.util.Arrays;
import java.util.List;

public final class PackingTemplateConstants {

    private PackingTemplateConstants() {
        // Prevent instantiation
    }

    // -------------------------------
    // IDENTIFICATION COLUMNS
    // -------------------------------
    public static final String ITEM_CODE = "Item Code";
    public static final String ITEM_NAME = "Item Name";
    public static final String ERP_ITEM_ID = "ERP Item Id";
    public static final String SUPPLIER_CODE = "Supplier Code";
    public static final String SUPPLIER_NAME = "Supplier Name";
    public static final String AREA_CODE = "Area Code";
    public static final String ZONE_CODE = "Zone Code";

    // -------------------------------
    // PACKING PROFILE CONFIGURATION
    // -------------------------------
    public static final String PACKING_PROFILE_CODE = "Packing Profile Code";
    public static final String PACKING_LEVEL_COUNT = "Packing Level Count";

    // -------------------------------
    // PRIMARY PACKING
    // -------------------------------
    public static final String PRIMARY_PACK_UOM = "Primary Pack UOM";
    public static final String PRIMARY_PACK_QTY = "Units per Primary Pack";

    // -------------------------------
    // SECONDARY PACKING
    // -------------------------------
    public static final String SECONDARY_PACK_UOM = "Secondary Pack UOM";
    public static final String SECONDARY_PACK_QTY = "Units per Secondary Pack";

    // -------------------------------
    // TERTIARY PACKING
    // -------------------------------
    public static final String TERTIARY_PACK_UOM = "Tertiary Pack UOM";
    public static final String TERTIARY_PACK_QTY = "Units per Tertiary Pack";

    // -------------------------------
    // MOQ CONFIGURATION
    // -------------------------------
    public static final String MOQ_LEVEL = "MOQ Level";
    public static final String MOQ_QTY = "MOQ Qty";


    public static final List<String> PACKING_TEMPLATE_HEADERS =
            Arrays.asList(
                    ITEM_CODE,
                    ITEM_NAME,
                    SUPPLIER_CODE,
                    SUPPLIER_NAME,
                    AREA_CODE,
                    ZONE_CODE,
                    PACKING_PROFILE_CODE,
                    PRIMARY_PACK_UOM,
                    PRIMARY_PACK_QTY,
                    SECONDARY_PACK_UOM,
                    SECONDARY_PACK_QTY,
                    TERTIARY_PACK_UOM,
                    TERTIARY_PACK_QTY
            );


}
