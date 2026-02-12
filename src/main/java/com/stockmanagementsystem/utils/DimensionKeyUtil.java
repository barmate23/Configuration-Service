package com.stockmanagementsystem.utils;

import com.stockmanagementsystem.exception.UploadRowException;
import com.stockmanagementsystem.response.UploadErrorDetail;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;

@Slf4j
public final class DimensionKeyUtil {

    private DimensionKeyUtil() {}

    // =====================================================
    // TEXT NORMALIZATION (TYPE / SUBTYPE)
    // =====================================================
    public static String normalizeText(String value) {
        if (value == null) return "na";

        return value
                .toLowerCase()
                .replaceAll("\\s+", "")
                .replaceAll("[^a-z0-9]", "");
    }

    // =====================================================
    // PARSE "LxWxH"
    // =====================================================
    public static BigDecimal[] parseLwh(
            String value,
            int row,
            String column) {

        if (value == null || value.trim().isEmpty()) {
            throw new UploadRowException(
                    new UploadErrorDetail(row, column, "Dimension is mandatory")
            );
        }

        try {
            String[] parts = value.toLowerCase().split("x");
            if (parts.length != 3) {
                throw new IllegalArgumentException();
            }

            return new BigDecimal[]{
                    new BigDecimal(parts[0].trim()),
                    new BigDecimal(parts[1].trim()),
                    new BigDecimal(parts[2].trim())
            };

        } catch (Exception e) {
            throw new UploadRowException(
                    new UploadErrorDetail(
                            row,
                            column,
                            "Invalid format. Expected: LxWxH"
                    )
            );
        }
    }

    // =====================================================
    // UOM → MILLIMETER CONVERSION
    // =====================================================
    public static BigDecimal toMillimeter(BigDecimal value, String uom) {

        if (value == null) {
            return BigDecimal.ZERO;
        }

        if (uom == null || uom.trim().isEmpty()) {
            log.warn("UOM is missing, defaulting to mm");
        }else {

            switch (uom.trim().toLowerCase()) {
                case "mm":
                    return value;

                case "cm":
                    return value.multiply(BigDecimal.valueOf(10));

                case "m":
                case "meter":
                    return value.multiply(BigDecimal.valueOf(1000));

                default:
                    throw new IllegalArgumentException("Unsupported UOM: " + uom);
            }
        }
        return value;
    }

    // =====================================================
    // REQUIRED METHOD
    // =====================================================
    public static BigDecimal calculateVolume(
            BigDecimal lengthMm,
            BigDecimal widthMm,
            BigDecimal heightMm) {

        if (lengthMm == null || widthMm == null || heightMm == null) {
            return BigDecimal.ZERO;
        }

        return lengthMm
                .multiply(widthMm)
                .multiply(heightMm)
                .setScale(0, RoundingMode.DOWN);
    }

    // =====================================================
    // CANONICAL (ORDER-INDEPENDENT) VOLUME
    // =====================================================
    public static BigDecimal calculateCanonicalVolume(
            BigDecimal lMm,
            BigDecimal wMm,
            BigDecimal hMm) {

        if (lMm == null || wMm == null || hMm == null) {
            return BigDecimal.ZERO;
        }

        BigDecimal[] dims = new BigDecimal[]{lMm, wMm, hMm};
        Arrays.sort(dims);

        return dims[0]
                .multiply(dims[1])
                .multiply(dims[2])
                .setScale(0, RoundingMode.DOWN);
    }

    // =====================================================
    // KEY BUILDER (CACHE + UPLOAD)
    // =====================================================
    public static String buildKey(
            String type,
            String subtype,
            BigDecimal volumeMm3,
            BigDecimal diameterMm) {

        return normalizeText(type) + "|" +
                normalizeText(subtype) + "|" +
                normalizeNumber(volumeMm3) + "|" +
                normalizeNumber(diameterMm);
    }

    private static String normalizeNumber(BigDecimal value) {
        if (value == null || value.compareTo(BigDecimal.ZERO) == 0) {
            return "na";
        }
        return value.stripTrailingZeros().toPlainString();
    }
}
