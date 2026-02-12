package com.stockmanagementsystem.utils;

import com.stockmanagementsystem.exception.UploadRowException;
import com.stockmanagementsystem.response.UploadErrorDetail;

import java.math.BigDecimal;

public final class DimensionUtil {

    private DimensionUtil() {}

    public static BigDecimal[] parseLwh(
            String value,
            int row,
            String column) {

        if (value == null || value.trim().isEmpty()) {
            throw uploadError(row, column, "Dimension is mandatory");
        }

        try {
            String cleaned = value
                    .toLowerCase()
                    .replace("×", "x")
                    .replace("*", "x")
                    .replaceAll("\\s+", "");

            String[] parts = cleaned.split("x");
            if (parts.length != 3) throw new IllegalArgumentException();

            return new BigDecimal[]{
                    normalize(parts[0]),
                    normalize(parts[1]),
                    normalize(parts[2])
            };

        } catch (Exception e) {
            throw uploadError(
                    row,
                    column,
                    "Invalid format. Expected: L x W x H"
            );
        }
    }

    private static BigDecimal normalize(String s) {
        return new BigDecimal(s).stripTrailingZeros();
    }

    private static UploadRowException uploadError(
            int row,
            String column,
            String message) {

        return new UploadRowException(
                new UploadErrorDetail(row, column, message)
        );
    }
}
