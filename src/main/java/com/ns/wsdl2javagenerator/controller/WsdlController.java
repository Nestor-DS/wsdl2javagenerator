package com.ns.wsdl2javagenerator.controller;

import com.ns.wsdl2javagenerator.model.WsdlRequest;
import com.ns.wsdl2javagenerator.service.WsdlService;
import com.ns.wsdl2javagenerator.util.Constants;
import com.ns.wsdl2javagenerator.util.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<byte[]> generate(@RequestBody WsdlRequest request) {
        byte[] zip = wsdlService.processWsdl(request);
        return ResponseUtil.zipDownload(zip, Constants.ZIP_NAME);
    }
}
