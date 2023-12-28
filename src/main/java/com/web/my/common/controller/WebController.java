package com.web.my.common.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController{

    @GetMapping("/error")
    public String errorPage() {
        return "/main/index.html";
    }

}
