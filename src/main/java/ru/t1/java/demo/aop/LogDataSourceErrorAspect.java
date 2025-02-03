package ru.t1.java.demo.aop;

import lombok.RequiredArgsConstructor;
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
import ru.t1.java.demo.model.DataSourceErrorLog;
import ru.t1.java.demo.repository.DataSourceErrorLogRepository;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class LogDataSourceErrorAspect {

    @Autowired
    @Qualifier("metricsKafkaTemplate")
    private KafkaTemplate<String, Message> template;

    @Autowired
    private DataSourceErrorLogRepository errorLogRepository;

    @Pointcut("@annotation(ru.t1.java.demo.aop.LogDataSourceError)")
    public void logDataSourceErrorPointcut() {}

    @Around("logDataSourceErrorPointcut()")
    public Object handleDataSourceError(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            return joinPoint.proceed();
        } catch (Exception ex) {
            String errorMessage = ex.getMessage();
            try {
                Message<String> message = MessageBuilder
                        .withPayload(errorMessage)
                        .setHeader("error_type", "DATA_SOURCE")
                        .build();

                template.send(message);
            } catch (Exception kafkaEx) {
                DataSourceErrorLog errorLog = new DataSourceErrorLog();
                errorLog.setMessage(ex.getMessage());
                errorLog.setExceptionStackTrace(getStackTraceAsString(ex));
                errorLog.setMethodSignature(joinPoint.getSignature().toShortString());
                errorLogRepository.save(errorLog);
            }

            throw ex;
        }
    }

    private String getStackTraceAsString(Exception ex) {
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement element : ex.getStackTrace()) {
            sb.append(element.toString()).append("\n");
        }
        return sb.toString();
    }
}
