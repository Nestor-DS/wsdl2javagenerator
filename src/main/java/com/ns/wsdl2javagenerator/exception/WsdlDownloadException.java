package com.ns.wsdl2javagenerator.exception;

public class WsdlDownloadException extends WsdlProcessingException {
    public WsdlDownloadException(String message) {
        super(message);
    }

    public WsdlDownloadException(String message, Throwable cause) {
        super(message, cause);
    }
}