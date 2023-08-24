package com.neuron.plantuml.sequenceDiagram;

import brave.Tracer;
import lombok.Getter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Objects;

@Getter
public class SomeService {
    private static SomeService instance;
    private final MultiValueMap<String, CustomLog> spanAndLogsMap = new LinkedMultiValueMap<>();
    private Tracer tracer;

    public SomeService() {

    }

    public static SomeService getInstance(Tracer tracer) {
        if (instance == null) {
            instance = new SomeService();
            instance.setTracer(tracer);
        }
        return instance;
    }
    public static SomeService getInstance() {

        return instance;
    }

    public void setTracer(Tracer tracer) {
        if (Objects.nonNull(tracer)) {
            this.tracer = tracer;
        }
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }
}
