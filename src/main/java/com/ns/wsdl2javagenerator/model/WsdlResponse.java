package com.ns.wsdl2javagenerator.model;

public class WsdlResponse {
    boolean status;

    String message;

    public WsdlResponse() {}

    public WsdlResponse(boolean status, String message) {
        this.status = status;
        this.message = message;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
