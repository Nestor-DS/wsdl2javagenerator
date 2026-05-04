package com.ns.wsdl2javagenerator.exception;

public class WsdlProcessingException extends RuntimeException {
    public WsdlProcessingException(String message) {
        super(message);
    }

    public WsdlProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}