package com.neuron.plantuml.sequenceDiagram;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class CustomLog {
    private String currentClassName, currentMethodName, callingMethodName, callingClassName, timeTaken, currentSpan;
    private Instant executionTime;
    private Boolean returnCall;
}
