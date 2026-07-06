package com.lamdayne.todo.exception;

import com.lamdayne.todo.dto.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AppException.class)
    public ResponseEntity<ApiResponse<Void>> handleAppException(AppException e) {
        return buildErrorResponse(e.getErrorCode());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleHttpMessageNotReadableException() {
        return buildErrorResponse(ErrorCode.REQUEST_BODY_MISSING_OR_INVALID);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException e
    ) {
        FieldError fieldError = e.getFieldError();
        if (fieldError == null) {
            return buildErrorResponse(ErrorCode.VALIDATION_ERROR);
        }

        ErrorCode errorCode = resolveErrorCode(
                fieldError.getDefaultMessage(),
                fieldError.getField(),
                fieldError.getObjectName()
        );

        return buildErrorResponse(errorCode);
    }

    private ErrorCode resolveErrorCode(String enumKey, String field, String objectName) {
        try {
            return ErrorCode.valueOf(enumKey);
        } catch (IllegalArgumentException ex) {
            log.error("Invalid ErrorCode with key = \"{}\", field = \"{}\", classError = \"{}\"",
                    enumKey,
                    field,
                    objectName
            );
        }
        return ErrorCode.INVALID_ERROR_CODE;
    }

    private ResponseEntity<ApiResponse<Void>> buildErrorResponse(ErrorCode errorCode) {
        return ResponseEntity
                .status(errorCode.getStatus())
                .body(ApiResponse.failure(errorCode));
    }

}
