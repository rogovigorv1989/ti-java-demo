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
import org.springframework.stereotype.Component;
import ru.t1.java.demo.model.DataSourceErrorLog;
import ru.t1.java.demo.model.dto.DataSourceErrorLogDTO;
import ru.t1.java.demo.repository.DataSourceErrorLogRepository;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class LogDataSourceErrorAspect {

    @Autowired
    @Qualifier("metricsKafkaTemplate")
    private KafkaTemplate<String, DataSourceErrorLogDTO> template;

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
                DataSourceErrorLogDTO errorLog = DataSourceErrorLogDTO.builder()
                        .message(ex.getMessage())
                        .exceptionStackTrace(getStackTraceAsString(ex))
                        .methodSignature(joinPoint.getSignature().toShortString())
                        .build();

                template.send("t1_demo_metrics", "DATA_SOURCE", errorLog);
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

//    @Pointcut("within(ru.t1.java.demo.*)")
//    public void loggingMethods() {
//
//    }

//    @AfterThrowing(pointcut = "@annotation(LogDataSourceError)", throwing = "ex")
//    @Order(0)
//    public void logExceptionAnnotation(JoinPoint joinPoint, Exception ex) {
//        DataSourceErrorLog errorLog = new DataSourceErrorLog();
//        errorLog.setMessage(ex.getMessage());
//        errorLog.setExceptionStackTrace(getStackTraceAsString(ex));
//        errorLog.setMethodSignature(joinPoint.getSignature().toShortString());
//
//        try {
//            errorLogRepository.save(errorLog);
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }
//
    private String getStackTraceAsString(Exception ex) {
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement element : ex.getStackTrace()) {
            sb.append(element.toString()).append("\n");
        }
        return sb.toString();
    }
}
