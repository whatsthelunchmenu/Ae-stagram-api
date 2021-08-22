package com.ae.stagram.service;

import com.ae.stagram.model.Person;
import org.springframework.stereotype.Service;

@Service
public class ApiService {

    public Person insert(Long id , Person person){
        return person;
    }
}
