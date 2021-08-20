package com.ae.stagram.controller;

import com.ae.stagram.model.Person;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApiController {

    @GetMapping("/api/sample")
    public ResponseEntity<Person> getApi() {
        return ResponseEntity.ok().body(new Person("test", "안녕!"));
    }

}
