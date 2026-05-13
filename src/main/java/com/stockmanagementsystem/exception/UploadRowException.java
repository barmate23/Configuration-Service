package com.stockmanagementsystem.exception;

import com.stockmanagementsystem.response.UploadErrorDetail;

public class UploadRowException extends RuntimeException {

    private final UploadErrorDetail errorDetail;

    public UploadRowException(UploadErrorDetail errorDetail) {
        this.errorDetail = errorDetail;
    }

    public UploadErrorDetail getErrorDetail() {
        return errorDetail;
    }
}
