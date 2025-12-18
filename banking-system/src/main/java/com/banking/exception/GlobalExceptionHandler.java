package com.banking.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

//    handle only resource not found exception
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException exception){
        ErrorResponse errorResponse= new ErrorResponse("Resource Not found", exception.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }
// handle insufficient balance exception

    @ExceptionHandler(InsufficientBalanceException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientBalance(InsufficientBalanceException ex){
        ErrorResponse errorResponse = new ErrorResponse("Insufficient Balance", ex.getMessage());
        return new ResponseEntity<>(errorResponse,HttpStatus.BAD_REQUEST);
    }

 // handle invalid balance
    @ExceptionHandler(InvalidTransactionException.class)
 public ResponseEntity<ErrorResponse> handleInvalidTransaction(InvalidTransactionException ex){
        ErrorResponse errorResponse= new ErrorResponse("Invalid Transaction", ex.getMessage());
        return new ResponseEntity<>(errorResponse,HttpStatus.BAD_REQUEST);
 }


 //    handle all other exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex){
        ErrorResponse errorResponse= new ErrorResponse("Internal Server Error", ex.getMessage());
        return new ResponseEntity<>(errorResponse,HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // handle only validations exceptions
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String,String>> handleValidationExceptions(MethodArgumentNotValidException exception){
        Map<String,String> error= new HashMap<>();
        exception.getBindingResult()
                .getAllErrors()
                .forEach((err)->{
                    String filedName= ((FieldError)err).getField();
                    String errorMessage= err.getDefaultMessage();
                    error.put(filedName,errorMessage);
                });
        return new ResponseEntity<>(error,HttpStatus.BAD_REQUEST);
    }
}
