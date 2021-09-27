package ru.koval.blog.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping(path = "/api/v1/test")
    public String test() {
        return "hello friend";
    }
}
