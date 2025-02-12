package ru.t1.java.demo.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Slf4j
@Aspect
@Component
public class MetricAspect {

    private final KafkaTemplate<String, Message> kafkaTemplate;

    @Autowired
    public MetricAspect(@Qualifier("metricsKafkaTemplate") KafkaTemplate<String, Message> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Pointcut("@annotation(ru.t1.java.demo.aop.Metric) && @annotation(metricAnnotation)")
    public void metricPointcut(Metric metricAnnotation) {}

    @Around(value = "metricPointcut(metricAnnotation)", argNames = "joinPoint,metricAnnotation")
    public Object measureExecutionTime(ProceedingJoinPoint joinPoint, Metric metricAnnotation) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object result = null;
        try {
            result = joinPoint.proceed();
        } catch (Throwable ex) {
            log.error("Error during method execution: {}", ex.getMessage(), ex);
            throw ex;
        }
        long executionTime = System.currentTimeMillis() - startTime;

        if (executionTime > metricAnnotation.threshold()) {
            String methodName = joinPoint.getSignature().getName();
            String params = Arrays.toString(joinPoint.getArgs());
            String payload = String.format("Method: %s, Execution time: %d ms, Params: %s",
                    methodName, executionTime, params);

            Message<String> message = MessageBuilder
                    .withPayload(payload)
                    .setHeader("error_type", "METRICS")
                    .build();
//
            kafkaTemplate.send(message);
        }

        return result;
    }
}
