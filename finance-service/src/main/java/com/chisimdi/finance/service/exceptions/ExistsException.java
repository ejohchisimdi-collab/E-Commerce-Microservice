package com.chisimdi.finance.service.exceptions;

import org.springframework.stereotype.Component;


public class ExistsException extends RuntimeException {
    public ExistsException(String message){
        super(message);
    }
}
