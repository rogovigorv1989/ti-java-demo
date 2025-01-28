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
import ru.t1.java.demo.model.dto.DataSourceErrorLogDTO;

import java.util.Arrays;

@Slf4j
@Aspect
@Component
public class MetricAspect {
    @Autowired
    @Qualifier("metricsKafkaTemplate")
    private KafkaTemplate<String, DataSourceErrorLogDTO> kafkaTemplate;

    @Pointcut("@annotation(ru.t1.java.demo.aop.Metric) && @annotation(metricAnnotation)")
    public void metricPointcut(Metric metricAnnotation) {}

    @Around(value = "metricPointcut(metricAnnotation)", argNames = "joinPoint,metricAnnotation")
    public Object measureExecutionTime(ProceedingJoinPoint joinPoint, Metric metricAnnotation) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object result = joinPoint.proceed();
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
//            kafkaTemplate.send("t1_demo_metrics", message);
        }

        return result;
    }
}
