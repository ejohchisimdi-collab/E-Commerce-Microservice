package com.chisimdi.product.service.exceptions;

import java.time.LocalDateTime;

public class ApiError {
    private int status;
    private String message;
    private LocalDateTime localDateTime;

    public ApiError(int status,String message){
        this.status=status;
        this.message=message;
        this.localDateTime=LocalDateTime.now();
    }



    public String getMessage() {
        return message;
    }

    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }

    public int getStatus() {
        return status;
    }
}
