package com.ae.stagram.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApiController {

    @GetMapping("/api")
    public String test(){
        return "정상처리완료";
    }

    @PostMapping("/api")
    public String test2(@RequestParam("name") String name){
        return name;
    }
}
