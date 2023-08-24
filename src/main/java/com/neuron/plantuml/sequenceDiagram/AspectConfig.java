package com.neuron.plantuml.sequenceDiagram;

import brave.Tracer;
import org.aspectj.lang.Aspects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Configuration
public class AspectConfig {
    @Autowired
    private Tracer tracer;

    @Bean
    public LogEntryAspect logEntryAspect(){
        LogEntryAspect logEntryAspect = Aspects.aspectOf(LogEntryAspect.class);
        if(Objects.nonNull(tracer)){
            SomeService.getInstance(tracer);
        }
        return logEntryAspect;
    }


}
