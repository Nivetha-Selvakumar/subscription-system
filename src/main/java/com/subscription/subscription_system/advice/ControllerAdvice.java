package com.subscription.subscription_system.advice;

import com.subscription.subscription_system.constants.AppConstant;
import com.subscription.subscription_system.exception.CommonException;
import com.subscription.subscription_system.exception.ValidationException;
import com.subscription.subscription_system.validator.ValidationError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ControllerAdvice {
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String,String> handleInvalidArgument(MethodArgumentNotValidException exception){
        Map<String, String> errorMsg = new HashMap<>();
        exception.getBindingResult().getFieldErrors()
                .forEach(error->errorMsg.put(error.getField(), error.getDefaultMessage()));
        return errorMsg;
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(CommonException.class)
    public Map<String,String> commonException(CommonException error){
        Map<String,String> errObj = new HashMap<>();
        errObj.put(AppConstant.ERROR,error.getMessage());
        return errObj;
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ValidationException.class)
    public Map<String,String> validationError(ValidationException error){
        Map<String,String> errObj = new HashMap<>();
        errObj.put(AppConstant.ERROR,error.getMessage());
        return errObj;
    }

    // Handle any other uncaught exception gracefully
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        errorResponse.put("error", "Internal Server Error");
        errorResponse.put("message", ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

//
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    @ExceptionHandler(MisMatchException.class)
//    public Map<String,String> misMatchException(MisMatchException error){
//        Map<String,String> errObj=new HashMap<>();
//        errObj.put(AppConstant.ERROR,error.getMessage());
//        return errObj;
//    }
//
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    @ExceptionHandler(IllegalArgumentException.class)
//    public Map<String,String> illegalArgumentException(IllegalArgumentException error){
//        Map<String,String> errObj=new HashMap<>();
//        errObj.put(AppConstant.ERROR,error.getMessage());
//        return errObj;
//    }
//
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    @ExceptionHandler(CommonException.class)
//    public Map<String,String> commonException(CommonException error){
//        Map<String,String> errObj = new HashMap<>();
//        errObj.put(AppConstant.ERROR,error.getMessage());
//        return errObj;
//    }
//
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    @ExceptionHandler(AggregateException.class)
//    public Map<String,String> aggregateException(AggregateException error){
//        Map<String,String> errObj = new HashMap<>();
//        errObj.put(AppConstant.ERROR,error.getMessage());
//        return errObj;
//    }
}
