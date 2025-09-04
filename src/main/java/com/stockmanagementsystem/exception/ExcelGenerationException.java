package com.stockmanagementsystem.exception;

import java.io.IOException;

public class ExcelGenerationException extends RuntimeException {
    public ExcelGenerationException(String s, IOException e) {
        super(s);
    }
}
