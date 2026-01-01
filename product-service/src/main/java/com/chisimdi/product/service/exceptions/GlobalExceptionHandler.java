package com.chisimdi.product.service.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> notFoundHandler(ResourceNotFoundException e){
        log.warn(e.getMessage());
        ApiError apiError=new ApiError(404, e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiError);
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError>generalHandler(Exception e){
        ApiError apiError=new ApiError(500,"Internal Server Error");
        log.error(e.getMessage(),e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiError);
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError2>validationHandler(MethodArgumentNotValidException e){
        ApiError2 apiError2=new ApiError2(400,"Validation Error");
        for(FieldError f: e.getFieldErrors()){
            apiError2.getReasons().add(f.getField()+" : "+f.getDefaultMessage());
        }
        log.warn(e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError2);
    }
    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ApiError>authorizationHandler(AuthorizationDeniedException e){
        ApiError apiError=new ApiError(401,"Unauthorized");
        log.warn(e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiError);
    }
}
