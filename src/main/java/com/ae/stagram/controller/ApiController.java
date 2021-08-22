package com.ae.stagram.controller;

import com.ae.stagram.model.Person;
import com.ae.stagram.service.ApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ApiController {

    private final ApiService service;

    @GetMapping("/api/sample")
    public ResponseEntity<String> getApi() {
        return ResponseEntity.ok().body("test");
    }

    @PutMapping("/api/{id}")
    public ResponseEntity<Person> updatePerson(@PathVariable Long id, @RequestBody Person person){

        return ResponseEntity.ok().body(service.insert(id, person));
    }

}
