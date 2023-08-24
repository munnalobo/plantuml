package com.neuron.plantuml.testing;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    @Autowired
    TetService1 service1;
    @GetMapping
    public String getSomeTestString(){
       return service1.getSomeString();
    }
}
