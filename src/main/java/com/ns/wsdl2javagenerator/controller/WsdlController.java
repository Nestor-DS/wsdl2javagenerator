package com.ns.wsdl2javagenerator.controller;

import com.ns.wsdl2javagenerator.model.WsdlRequest;
import com.ns.wsdl2javagenerator.model.WsdlResponse;
import com.ns.wsdl2javagenerator.service.WsdlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/wsdl")
public class WsdlController {

    @Autowired
    private WsdlService wsdlService;

    @GetMapping("/hi")
    public void hi() {
        System.out.println("HI");
    }

        @PostMapping("/generate")
    public WsdlResponse generate(@RequestBody WsdlRequest request) {
        return wsdlService.processWsdl(request);
    }
}
