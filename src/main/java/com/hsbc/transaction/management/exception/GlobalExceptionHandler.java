package com.hsbc.transaction.management.exception;

import com.hsbc.transaction.management.model.BaseResult;
import com.hsbc.transaction.management.enums.ResultCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({BusinessException.class})
    public BaseResult businessError(Exception e) {
        String msg = e.getMessage();
        log.error("business error = {}", msg);
        return BaseResult.fail(ResultCodeEnum.BUSINESS_ERROR.getCode(), msg);
    }

    @ExceptionHandler({Throwable.class})
    public BaseResult error(Throwable e) {
        log.error("general error = {}", e.getMessage(), e);
        String msg = StringUtils.isEmpty(e.getMessage()) ? ResultCodeEnum.SYSTEM_ERROR.getMsg() : e.getMessage();
        return BaseResult.fail(ResultCodeEnum.BUSINESS_ERROR.getCode(), msg);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public BaseResult handleValidationExceptions(MethodArgumentNotValidException e) {
        //store all param errors
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return BaseResult.fail(ResultCodeEnum.PARAM_ERROR.getCode(), errors.toString());
    }
}
