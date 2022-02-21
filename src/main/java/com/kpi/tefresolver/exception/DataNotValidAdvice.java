package com.kpi.tefresolver.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class DataNotValidAdvice {
    @ExceptionHandler(DataNotValidException.class)
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    public void handleDataNotValidException(DataNotValidException e){
        e.printStackTrace();
    }
}
