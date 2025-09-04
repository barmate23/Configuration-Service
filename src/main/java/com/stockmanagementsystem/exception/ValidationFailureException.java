package com.stockmanagementsystem.exception;

public class ValidationFailureException extends RuntimeException{

    public ValidationFailureException(String s){
        super(s);
    }

}
