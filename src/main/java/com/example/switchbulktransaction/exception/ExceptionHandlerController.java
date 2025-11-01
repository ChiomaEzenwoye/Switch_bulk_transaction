package com.example.switchbulktransaction.exception;

import com.example.switchbulktransaction.model.dto.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ExceptionHandlerController {


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<String>> handleValidationException(MethodArgumentNotValidException ex) {
        ApiResponse<String> apiResponse = new ApiResponse<>();

        ex.getBindingResult().getFieldErrors().forEach(error ->{
            apiResponse.setData(error.getDefaultMessage());
            apiResponse.setMessage("Failed Validation");
        });

        return ResponseEntity.badRequest().body(apiResponse);
    }
}
