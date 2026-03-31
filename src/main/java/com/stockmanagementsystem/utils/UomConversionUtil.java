package com.stockmanagementsystem.utils;

import java.math.BigDecimal;


public final class UomConversionUtil {

    private UomConversionUtil() {}

    /**
     * Converts value from given UOM to millimeters
     */
    public static BigDecimal toMillimeter(BigDecimal value, String uom) {

        if (value == null) return null;

        switch (normalize(uom)) {
            case "mm":
                return value;

            case "cm":
                return value.multiply(BigDecimal.valueOf(10));

            case "m":
            case "meter":
            case "meters":
                return value.multiply(BigDecimal.valueOf(1000));

            default:
                throw new IllegalArgumentException("Unsupported UOM: " + uom);
        }
    }

    private static String normalize(String uom) {
        return uom == null ? "" :
                uom.trim().toLowerCase();
    }
}
