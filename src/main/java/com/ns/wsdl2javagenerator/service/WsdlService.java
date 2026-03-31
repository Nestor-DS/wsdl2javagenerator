package com.ns.wsdl2javagenerator.service;

import com.ns.wsdl2javagenerator.model.WsdlRequest;
import com.ns.wsdl2javagenerator.model.WsdlResponse;

public interface WsdlService {
    WsdlResponse processWsdl(WsdlRequest request);
}
