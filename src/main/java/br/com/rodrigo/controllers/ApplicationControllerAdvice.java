package br.com.rodrigo.controllers;

import br.com.rodrigo.exceptions.ApiErrors;
import br.com.rodrigo.exceptions.BusinessRuleException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class ApplicationControllerAdvice {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrors> handleValidationExceptions(MethodArgumentNotValidException ex) {
        BindingResult bindingResult = ex.getBindingResult();

        return new ResponseEntity<>(new ApiErrors(bindingResult), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<ApiErrors> handleBusinessRuleException(BusinessRuleException ex) {

        return new ResponseEntity<>(
                new ApiErrors(ex),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiErrors> handlerResponseStatusException(ResponseStatusException ex) {
        return new ResponseEntity<>(new ApiErrors(ex), ex.getStatus());
    }
}
