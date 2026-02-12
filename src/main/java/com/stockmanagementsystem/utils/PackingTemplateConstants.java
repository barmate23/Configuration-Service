package com.stockmanagementsystem.utils;


import java.util.Arrays;
import java.util.List;

public final class PackingTemplateConstants {

    private PackingTemplateConstants() {}

    // ===============================
    // IDENTIFICATION (READ-ONLY)
    // ===============================
    public static final String ITEM_CODE = "Item Code";
    public static final String ITEM_NAME = "Item Name";
    public static final String SUPPLIER_CODE = "Supplier Code";
    public static final String SUPPLIER_NAME = "Supplier Name";
    public static final String AREA_CODE = "Area Code";
    public static final String ZONE_CODE = "Zone Code";
    public static final String PACKING_LEVEL = "Packing Level";

    // ===============================
    // PRIMARY
    // ===============================
    public static final String PRIMARY_PACK_TYPE = "Primary Pack Type";
    public static final String PRIMARY_PACK_SUB_TYPE = "Primary Pack Sub Type";
    public static final String PRIMARY_DIMENSION_UOM = "Primary Dimension UOM";
    public static final String PRIMARY_DIMENSION_LWH = "Primary Dimension (L x W x H)";
    public static final String PRIMARY_CIRCUMFERENCE = "Primary Circumference / Diameter";
    public static final String PRIMARY_UNITS = "Units per Primary Pack";

    // ===============================
    // SECONDARY
    // ===============================
    public static final String SECONDARY_PACK_TYPE = "Secondary Pack Type";
    public static final String SECONDARY_PACK_SUB_TYPE = "Secondary Pack Sub Type";
    public static final String SECONDARY_DIMENSION_UOM = "Secondary Dimension UOM";
    public static final String SECONDARY_DIMENSION_LWH = "Secondary Dimension (L x W x H)";
    public static final String SECONDARY_CIRCUMFERENCE = "Secondary Circumference / Diameter";
    public static final String SECONDARY_UNITS = "Units per Secondary Pack";

    // ===============================
    // TERTIARY
    // ===============================
    public static final String TERTIARY_PACK_TYPE = "Tertiary Pack Type";
    public static final String TERTIARY_PACK_SUB_TYPE = "Tertiary Pack Sub Type";
    public static final String TERTIARY_DIMENSION_UOM = "Tertiary Dimension UOM";
    public static final String TERTIARY_DIMENSION_LWH = "Tertiary Dimension (L x W x H)";
    public static final String TERTIARY_CIRCUMFERENCE = "Tertiary Circumference / Diameter";
    public static final String TERTIARY_UNITS = "Units per Tertiary Pack";

    // ===============================
    // HEADER ORDER
    // ===============================
    public static final List<String> PACKING_TEMPLATE_HEADERS = Arrays.asList(
            ITEM_CODE, ITEM_NAME, SUPPLIER_CODE, SUPPLIER_NAME,
            AREA_CODE, ZONE_CODE, PACKING_LEVEL,

            PRIMARY_PACK_TYPE, PRIMARY_PACK_SUB_TYPE,
            PRIMARY_DIMENSION_UOM, PRIMARY_DIMENSION_LWH,
            PRIMARY_CIRCUMFERENCE, PRIMARY_UNITS,

            SECONDARY_PACK_TYPE, SECONDARY_PACK_SUB_TYPE,
            SECONDARY_DIMENSION_UOM, SECONDARY_DIMENSION_LWH,
            SECONDARY_CIRCUMFERENCE, SECONDARY_UNITS,

            TERTIARY_PACK_TYPE, TERTIARY_PACK_SUB_TYPE,
            TERTIARY_DIMENSION_UOM, TERTIARY_DIMENSION_LWH,
            TERTIARY_CIRCUMFERENCE, TERTIARY_UNITS
    );
}


