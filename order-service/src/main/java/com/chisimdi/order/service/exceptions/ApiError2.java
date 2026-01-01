package com.chisimdi.order.service.exceptions;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ApiError2 {
    private int status;
    private String message;
    private LocalDateTime localDateTime;
    private List<String> reasons=new ArrayList<>();

    public ApiError2(int status,String message){
        this.status=status;
        this.message=message;
        this.localDateTime=LocalDateTime.now();
    }


    public void setReasons(List<String> reasons) {
        this.reasons = reasons;
    }

    public List<String> getReasons() {
        return reasons;
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
