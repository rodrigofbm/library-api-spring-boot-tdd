package br.com.rodrigo.exceptions;

public class BusinessRuleException extends RuntimeException {

    public BusinessRuleException(String msg) {
        super(msg);
    }
}
