package com.stockmanagementsystem.utils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.itextpdf.text.*;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.*;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.List;

@Component
public class BarcodeGenerator {


    public static String generateRandomBarcode() {
        String regex = "^[0-9A-Z]{7}[0-9]{3}$";
        Random random = new Random();
        StringBuilder barcode = new StringBuilder();
        for (int i = 0; i < 7; i++) {
            int randomCharIndex = random.nextInt(36); // Numbers (0-9) + Uppercase Letters (A-Z)
            char randomChar = (char) (randomCharIndex < 10 ? '0' + randomCharIndex : 'A' + randomCharIndex - 10);
            barcode.append(randomChar);
        }
        for (int i = 0; i < 3; i++) {
            int randomDigit = random.nextInt(10);
            barcode.append(randomDigit);
        }
        return barcode.toString();
    }

    public static byte[] generateBarcode(String content) {
        try {
            int width = 330;
            int height = 163;

            // Generate barcode image
            BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.PDF_417, width, height);
            BufferedImage barcodeImage = bitMatrixToImage(bitMatrix);

            // Create a new image with space for the barcode string below
            BufferedImage combined = new BufferedImage(width, height + 30, BufferedImage.TYPE_INT_ARGB);
            Graphics2D graphics = combined.createGraphics();

            // Draw the barcode image
            graphics.drawImage(barcodeImage, 0, 0, null);

            // Add the barcode string below the barcode with gaps between letters
            graphics.setColor(Color.BLACK);
            graphics.setFont(new Font("TimesRoman", Font.PLAIN, 10));
            FontMetrics fontMetrics = graphics.getFontMetrics();
            int totalStringWidth = fontMetrics.stringWidth(content);
            int gap = (width - totalStringWidth) / (content.length() + 4);

            int xPosition = 0;
            for (char letter : content.toCharArray()) {
                int charWidth = fontMetrics.charWidth(letter);
                graphics.drawString(String.valueOf(letter), xPosition + 30, height - 50);
                xPosition += charWidth + gap;
            }

            // Dispose of the graphics object
            graphics.dispose();

            // Write the combined image to ByteArrayOutputStream
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(combined, "png", outputStream);

            return outputStream.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    /*
    public static byte[] generateBarcode(String content) {
        try {
            // Calculate the optimal width and height based on the number of characters
            int charCount = content.length();
            int width = Math.max(300, charCount * 12); // Adjust the minimum width as needed
            int height = 150;

            // Set PDF417 barcode parameters including error correction level
            Map<EncodeHintType, Integer> hints = new HashMap<>();
            hints.put(EncodeHintType.ERROR_CORRECTION, 5);

            PDF417Writer writer = new PDF417Writer();
            BitMatrix bitMatrix = writer.encode(content, BarcodeFormat.PDF_417, width, height, hints);

            // Convert BitMatrix to BufferedImage
            BufferedImage barcodeImage = bitMatrixToImage(bitMatrix);

            // Create a new image with space for the barcode string below
            BufferedImage combined = new BufferedImage(width, height + 30, BufferedImage.TYPE_INT_ARGB);
            Graphics2D graphics = combined.createGraphics();

            // Draw the barcode image
            graphics.drawImage(barcodeImage, 0, 0, null);

            // Add the barcode string below the barcode with gaps between letters
            graphics.setColor(Color.BLACK);
            graphics.setFont(new Font("TimesRoman", Font.PLAIN, 10));
            FontMetrics fontMetrics = graphics.getFontMetrics();
            int totalStringWidth = fontMetrics.stringWidth(content);
            int gap = (width - totalStringWidth) / (content.length() + 1);

            int xPosition = 0;
            for (char letter : content.toCharArray()) {
                int charWidth = fontMetrics.charWidth(letter);
                graphics.drawString(String.valueOf(letter), xPosition + gap / 2, height - 50);
                xPosition += charWidth + gap;
            }

            // Dispose of the graphics object
            graphics.dispose();

            // Write the combined image to ByteArrayOutputStream
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(combined, "png", outputStream);

            return outputStream.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

     */


    private static BufferedImage bitMatrixToImage(BitMatrix matrix) {
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image.setRGB(x, y, matrix.get(x, y) ? 0x000000 : 0xFFFFFF);
            }
        }
        return image;
    }

    public static Integer getAsnNumber() {
        Random random = new Random();
        return random.nextInt(90000000) + 10000000;
    }

    public static Integer getRemainingDays(Date requiredDate) {
        // Convert java.sql.Date to java.util.Date
        Date utilDate = new Date(requiredDate.getTime());

        // Convert java.util.Date to java.time.LocalDate
        Instant instant = utilDate.toInstant();
        LocalDate requiredLocalDate = instant.atZone(ZoneId.systemDefault()).toLocalDate();

        // Get today's date
        LocalDate today = LocalDate.now();

        // Calculate the difference in days
        long daysDifference = ChronoUnit.DAYS.between(today, requiredLocalDate);

        // Ensure the result is positive (if requiredDate is in the future)
        return Math.toIntExact(Math.max(0, daysDifference));
    }


}
