package com.chisimdi.finance.service.exceptions;

public class FallBackException extends RuntimeException{
    public FallBackException(String message){
        super(message);
    }

}
