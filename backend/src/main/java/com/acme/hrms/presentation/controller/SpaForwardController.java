package com.acme.hrms.presentation.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SpaForwardController {

    @GetMapping(value = {
        "/dashboard",
        "/employees",
        "/simulator",
        "/audit-trail"
    })
    public String forwardSpaRoutes() {
        return "forward:/index.html";
    }
}

