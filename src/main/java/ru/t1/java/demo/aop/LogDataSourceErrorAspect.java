package ru.t1.java.demo.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.model.DataSourceErrorLog;
import ru.t1.java.demo.repository.DataSourceErrorLogRepository;

@Slf4j
@Aspect
@Component
public class LogDataSourceErrorAspect {
    @Autowired
    DataSourceErrorLogRepository errorLogRepository;

    @Pointcut("within(ru.t1.java.demo.*)")
    public void loggingMethods() {

    }

    @AfterThrowing(pointcut = "@annotation(LogDataSourceError)", throwing = "ex")
    @Order(0)
    public void logExceptionAnnotation(JoinPoint joinPoint, Exception ex) {
        DataSourceErrorLog errorLog = new DataSourceErrorLog();
        errorLog.setMessage(ex.getMessage());
        errorLog.setExceptionStackTrace(getStackTraceAsString(ex));
        errorLog.setMethodSignature(joinPoint.getSignature().toShortString());

        try {
            errorLogRepository.save(errorLog);
        } catch (Exception e) {
            throw new RuntimeException(e);
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
