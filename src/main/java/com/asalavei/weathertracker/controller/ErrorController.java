package com.asalavei.weathertracker.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ErrorController {

    @RequestMapping("/error/404")
    public String handle404() {
        return "error/404";
    }

    @RequestMapping("/error/500")
    public String handle500() {
        return "error/500";
    }
}
