package com.example.webapptraining.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

public class HelloController {
    public String hello() {
        return "hello";
    }
}
