package com.neuron.plantuml.testing;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TetService1 {
    @Autowired
    TetService2 service2;
    public String getSomeString() {
        return service2.getSomeString();
    }
}
