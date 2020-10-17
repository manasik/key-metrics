package com.keymetrics.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DeploymentController {

    @RequestMapping("/")
    public String index() {
        return "Hello world";
    }
}
