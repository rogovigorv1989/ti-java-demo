package ru.t1.java.demo.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.aspectj.lang.annotation.*;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.model.DataSourceErrorLog;
import ru.t1.java.demo.model.Transaction;
import ru.t1.java.demo.repository.DataSourceErrorLogRepository;

import java.util.List;

@Slf4j
@Aspect
@Component
@Order(0)
public class LogAspect {
    @Autowired
    DataSourceErrorLogRepository errorLogRepository;

    @Pointcut("within(ru.t1.java.demo.*)")
    public void loggingMethods() {

    }

    @Before("@annotation(LogExecution)")
    @Order(1)
    public void logAnnotationBefore(JoinPoint joinPoint) {
        log.info("ASPECT BEFORE ANNOTATION: Call method: {}", joinPoint.getSignature().getName());
    }

    @Before("execution(public * ru.t1.java.demo.service.TransactionService.*(..))")
    public void logBefore(JoinPoint joinPoint) {
        log.error("ASPECT BEFORE: Call method: {}", joinPoint.getSignature().getName());
    }

    @AfterThrowing(pointcut = "@annotation(LogException)", throwing = "ex")
    @Order(0)
    public void logExceptionAnnotation(JoinPoint joinPoint, Exception ex) {
        DataSourceErrorLog errorLog = new DataSourceErrorLog();
        errorLog.setMessage(ex.getMessage());
        errorLog.setExceptionStackTrace(getStackTraceAsString(ex));
        errorLog.setMethodSignature(joinPoint.getSignature().toShortString());
        Object[] args = joinPoint.getArgs();
        for (Object arg : args) {
            if (arg != null && arg.getClass().getDeclaredFields() != null) {
                try {
                    var field = arg.getClass().getDeclaredField("id");
                    field.setAccessible(true);
                    Object transactionId = field.get(arg);
                    if (transactionId != null) {
                        errorLog.setTransactionId((Long) transactionId);
                        break;
                    }
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    log.error(e.getMessage(), e);
                }
            }
        }

        try {
            errorLogRepository.save(errorLog);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @AfterReturning(
            pointcut = "@annotation(HandlingResult)",
            returning = "result")
    public void handleResult(JoinPoint joinPoint, List<Transaction> result) {
        log.info("В результате выполнения метода {}", joinPoint.getSignature().toShortString());
        log.info("получен результат: {} ", result);
    }

    private String getStackTraceAsString(Exception ex) {
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement element : ex.getStackTrace()) {
            sb.append(element.toString()).append("\n");
        }
        return sb.toString();
    }
}
