package com.ns.wsdl2javagenerator.exception;

import com.ns.wsdl2javagenerator.model.ErrorDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(WsdlValidationException.class)
    public ResponseEntity<ErrorDTO> handleValidation(WsdlValidationException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorDTO("VALIDATION_ERROR", ex.getMessage(), Instant.now()));
    }

    @ExceptionHandler(WsdlDownloadException.class)
    public ResponseEntity<ErrorDTO> handleDownload(WsdlDownloadException ex) {
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body(new ErrorDTO("DOWNLOAD_ERROR", ex.getMessage(), Instant.now()));
    }

    @ExceptionHandler(WsdlProcessingException.class)
    public ResponseEntity<ErrorDTO> handleGeneric(WsdlProcessingException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorDTO("PROCESSING_ERROR", ex.getMessage(), Instant.now()));
    }
}