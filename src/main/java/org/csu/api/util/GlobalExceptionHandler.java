package org.csu.api.util;

import lombok.extern.slf4j.Slf4j;
import org.csu.api.common.CommonResponse;
import org.csu.api.common.ResponseCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {


    //HTTP请求方法错误时的异常处理
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(code = HttpStatus.METHOD_NOT_ALLOWED)
    @ResponseBody
    public CommonResponse<String> httpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e){
        log.error(e.getMessage());
        return CommonResponse.createForError("HTTP请求方法错误");
    }

    //GET方法请求时，参数异常
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    @ResponseBody
    public CommonResponse<String> missingServletRequestParameterExceptionHandler(MissingServletRequestParameterException e){
        log.error(e.getMessage());
        return CommonResponse.createForError(
                ResponseCode.ARGUMENT_ILLEGAL.getCode(),ResponseCode.ARGUMENT_ILLEGAL.getDescription());
    }

    //参数异常，类型不匹配
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    @ResponseBody
    public CommonResponse<String> methodArgumentTypeMismatchExceptionHandler(MethodArgumentTypeMismatchException e){
        log.error(e.getMessage());
        return CommonResponse.createForError(
                ResponseCode.ARGUMENT_ILLEGAL.getCode(),ResponseCode.ARGUMENT_ILLEGAL.getDescription());
    }

    //POST方法请求时，参数异常
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    @ResponseBody
    public CommonResponse<String> methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e){
        log.error(e.getMessage());
        return CommonResponse.createForError(
                ResponseCode.ARGUMENT_ILLEGAL.getCode(),formatValidErrorsMessage(e.getAllErrors()));
    }


//    @ExceptionHandler(Exception.class)
//    @ResponseBody
//    public CommonResponse<String> exceptionHandler(Exception e){
//        e.printStackTrace();
//        log.error(e.getMessage());
//        return CommonResponse.createForError("服务器异常了...");
//    }

    private String formatValidErrorsMessage(List<ObjectError> errors){
        StringBuffer errorMessage = new StringBuffer();
        errors.forEach(error -> errorMessage.append(error.getDefaultMessage()).append(","));
        errorMessage.deleteCharAt(errorMessage.length()-1);
        return errorMessage.toString();
    }
}
