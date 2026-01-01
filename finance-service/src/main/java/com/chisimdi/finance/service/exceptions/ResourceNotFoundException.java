package com.chisimdi.finance.service.exceptions;

import org.springframework.web.bind.annotation.RestControllerAdvice;


public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message){
        super(message);
    }
}
