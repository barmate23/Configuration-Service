package com.stockmanagementsystem.utils;

import java.math.BigDecimal;

public final class ExcelKeyUtil {

    private ExcelKeyUtil() {}

    public static String normalize(String value) {
        return value == null
                ? "NA"
                : value.toUpperCase()
                .replaceAll("[^A-Z0-9]", "_");
    }

    public static BigDecimal toMillimeter(BigDecimal v, String uom) {

        if (v == null) return BigDecimal.ZERO;
        if (uom == null) throw new IllegalArgumentException("Unsupported UOM: null");

        switch (uom.toUpperCase()) {
            case "MM":
                return v;
            case "CM":
                return v.multiply(BigDecimal.valueOf(10));
            case "METER":
                return v.multiply(BigDecimal.valueOf(1000));
            case "INCH":
                return v.multiply(BigDecimal.valueOf(25.4));
            default:
                throw new IllegalArgumentException("Unsupported UOM: " + uom);
        }
    }

    public static String dimension(BigDecimal l, BigDecimal w, BigDecimal h) {
        return l.stripTrailingZeros().toPlainString() + "x" +
                w.stripTrailingZeros().toPlainString() + "x" +
                h.stripTrailingZeros().toPlainString();
    }
}
