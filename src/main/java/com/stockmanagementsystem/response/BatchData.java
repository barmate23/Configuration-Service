package com.stockmanagementsystem.response;

import lombok.Data;

import java.util.Date;
@Data
public
class BatchData {
    String serialOrBatch;
    Date mfgDate;
    Date expDate;
}