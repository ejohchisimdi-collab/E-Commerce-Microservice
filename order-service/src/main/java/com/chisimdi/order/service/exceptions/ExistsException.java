package com.chisimdi.order.service.exceptions;

public class ExistsException extends RuntimeException {
    public ExistsException(String message){
        super(message);
    }
}
