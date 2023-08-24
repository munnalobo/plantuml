package com.neuron.plantuml.sequenceDiagram;


import brave.Tracer;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.time.Instant;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Aspect
@Slf4j
public class LogEntryAspect {
    private Tracer tracer;
    private String packageName;

    public LogEntryAspect() {
    }

    public static LogEntryAspect aspectOf() {
        return new LogEntryAspect();
    }

    public static String randomHex() {
        Random random = new Random();
        int nextInt = random.nextInt(0xffffff + 1);
        return String.format("#%06x", nextInt);
    }

    private void saveLog(JoinPoint joinPoint, Instant executionTime, boolean returningCall) {
        List<String> classExclusionList = List.of("Aspect", "Signature", "$", "set", "get", "aroundBody", "<generated>");
        List<StackTraceElement> aspect = Arrays.stream(Thread.currentThread().getStackTrace()).filter(x -> x.getClassName().contains(packageName)).filter(x -> classExclusionList.stream().noneMatch(y -> x.getClassName().contains(y))).collect(Collectors.toList());
        if (!aspect.isEmpty()) {
            String currentMethodName = joinPoint.getSignature().getName();
            String currentClassName = joinPoint.getSignature().getDeclaringType().getSimpleName();
            String callingClassName = aspect.get(0).getFileName().replace(".java", "");
            String callingMethodName = aspect.get(0).getMethodName();
            if (!callingMethodName.equals(currentMethodName)) {
                log.info("current method: {}, Calling class: {}, Calling method: {}", currentMethodName, callingClassName, callingMethodName);

                String key = tracer.currentSpan().context().spanIdString();
                CustomLog build;
                CustomLog.CustomLogBuilder builder;

                if (returningCall) {
                    builder = CustomLog.builder().currentMethodName(callingMethodName).currentClassName(callingClassName).callingClassName(currentClassName).callingMethodName(currentMethodName);
                } else {
                    builder = CustomLog.builder().currentMethodName(currentMethodName).currentClassName(currentClassName).callingClassName(callingClassName).callingMethodName(callingMethodName);
                }
                build = builder.executionTime(executionTime).currentSpan(key).returnCall(returningCall).build();

                SomeService.getInstance().getSpanAndLogsMap().add(key, build);
                generatePumlDiagram();
            }
        }
    }

    @Around("execution(* com.neuron.plantuml..*(..)) && !execution(* com.neuron.plantuml.sequenceDiagram.*.*(..))")
    public Object aspectForLogging(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            if (!joinPoint.getSignature().getDeclaringType().getName().contains("$") && !joinPoint.getSignature().getDeclaringType().getName().contains("models") && !joinPoint.getSignature().getName().contains("$")) {
                if (Objects.isNull(tracer) && Objects.nonNull(SomeService.getInstance()) && Objects.nonNull(SomeService.getInstance().getTracer())) {
                    this.tracer = SomeService.getInstance().getTracer();
                }

                log.info("Before: " + joinPoint.getSignature().getName());
                if (Objects.nonNull(tracer) && Objects.nonNull(tracer.currentSpan()) && Objects.nonNull(tracer.currentSpan().context()) && Objects.nonNull(tracer.currentSpan().context().spanIdString())) {
                    saveLog(joinPoint, Instant.now(), false);
                }

                Object x = joinPoint.proceed();
                log.info("After: " + joinPoint.getSignature().getName());
                if (Objects.nonNull(tracer) && Objects.nonNull(tracer.currentSpan()) && Objects.nonNull(tracer.currentSpan().context()) && Objects.nonNull(tracer.currentSpan().context().spanIdString())) {
                    saveLog(joinPoint, Instant.now(), true);
                }
                return x;
            } else {
                return joinPoint.proceed();
            }
        } catch (Exception e) {
            if (Objects.nonNull(tracer) && Objects.nonNull(tracer.currentSpan()) && Objects.nonNull(tracer.currentSpan().context()) && Objects.nonNull(tracer.currentSpan().context().spanIdString())) {
                saveLog(joinPoint, Instant.now(), true);
            }

            throw e;
        }
    }

    public String generatePumlDiagram() {
        StringBuilder sb = new StringBuilder();
        sb.append("@startuml\n" + "autoactivate on\n");

        Set<String> collect = SomeService.getInstance().getSpanAndLogsMap().values().iterator().next().stream().sorted(Comparator.comparing(CustomLog::getExecutionTime)).flatMap(x -> Stream.of(x.getCurrentClassName(), x.getCallingClassName())).collect(Collectors.toSet());
        collect.forEach(x -> {
            if (x.contains("Controller")) {
                sb.append("control ").append(x).append(" as ").append(x).append("\n");
            }
        });

        List<CustomLog> next = SomeService.getInstance().getSpanAndLogsMap().values().iterator().next();
        next.sort(Comparator.comparing(CustomLog::getExecutionTime));
        next.forEach(x -> {
            String str = Boolean.TRUE.equals(x.getReturnCall()) ? " --> " : " -> ";
            sb.append(x.getCallingClassName()).append(str).append(x.getCurrentClassName()).append(" ").append(randomHex()).append(" : ").append(x.getCallingMethodName()).append(" - ").append(x.getCurrentMethodName()).append("\n");
        });

        sb.append("@enduml");
        whenWriteStringUsingBufferedWriter_thenCorrect(sb.toString());
        return sb.toString();
    }

    @SneakyThrows
    public void whenWriteStringUsingBufferedWriter_thenCorrect(String str) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("hirearchy.puml"))) {
            writer.write(str);
        }
    }
}

