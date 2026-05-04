package com.ns.wsdl2javagenerator.service;

import com.ns.wsdl2javagenerator.model.WsdlRequest;

public interface WsdlService {
    byte[] processWsdl(WsdlRequest request);
}
