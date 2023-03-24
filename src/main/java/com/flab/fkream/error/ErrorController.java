package com.flab.fkream.error;


import com.flab.fkream.error.exception.DuplicateEmailException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ErrorController {

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(DuplicateEmailException.class)
    public ErrorMsg handleDuplicateEmailException(DuplicateEmailException e){
        return new ErrorMsg(e.getMessage(), e.getClass().getSimpleName());
    }

}
