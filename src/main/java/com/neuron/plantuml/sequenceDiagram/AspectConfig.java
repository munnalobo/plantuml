package com.neuron.plantuml.sequenceDiagram;

import brave.Tracer;
import org.aspectj.lang.Aspects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class AspectConfig {
    @Autowired
    private Tracer tracer;

    public LogEntryAspect logEntryAspect(){
        LogEntryAspect logEntryAspect = Aspects.aspectOf(LogEntryAspect.class);
        if(Objects.nonNull(tracer)){
            SomeService.getInstance(tracer);
        }
        return logEntryAspect;
    }


}
