package com.stockmanagementsystem.response;

import java.util.List;

public class ResponseWithPagination<T> {

    private Integer pageCount;
    private Integer recordCount;
    private List<T> records;

}
