package com.lamdayne.todo.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum ErrorCode {

    REQUEST_BODY_MISSING_OR_INVALID("REQUEST_BODY_MISSING_OR_INVALID", "Body missing or invalid", HttpStatus.BAD_REQUEST),
    INVALID_ERROR_CODE("INVALID_ERROR_CODE", "Invalid error code", HttpStatus.BAD_REQUEST),
    VALIDATION_ERROR("VALIDATION_ERROR", "Request validation failed", HttpStatus.BAD_REQUEST),
    PAGE_NO_INVALID("PAGE_NO_INVALID", "Invalid page number", HttpStatus.BAD_REQUEST),
    PAGE_SIZE_INVALID("PAGE_SIZE_INVALID", "Invalid page size", HttpStatus.BAD_REQUEST),

    TODO_TITLE_REQUIRED("TODO_TITLE_REQUIRED", "Todo title can not blank", HttpStatus.BAD_REQUEST),
    TODO_NOT_FOUND("TODO_NOT_FOUND", "To do not found", HttpStatus.NOT_FOUND),

    ;

    private String code;
    private String defaultMessage;
    private HttpStatus status;

}
