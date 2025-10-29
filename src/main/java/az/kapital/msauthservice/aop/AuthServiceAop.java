package az.kapital.msauthservice.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class AuthServiceAop {

    @Pointcut(value = "execution(* az.kapital.msauthservice.service.AuthService.*(..))")
    public void authServiceMethods() {
    }

    @Around(value = "authServiceMethods()")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long duration = System.currentTimeMillis() - start;
        log.info("Method: {} | Execution time: {} ms", joinPoint.getSignature().getName(), duration);
        return result;
    }

    @Before(value = "authServiceMethods()")
    public void logBeforeMethod(JoinPoint joinPoint) {
        log.info("Method called: {}", joinPoint.getSignature().getName());
    }

    @After(value = "authServiceMethods()")
    public void logAfterMethod(JoinPoint joinPoint) {
        log.info("Method finished: {}", joinPoint.getSignature().getName());
    }

    @AfterThrowing(value = "authServiceMethods()", throwing = "ex")
    public void logExceptionMethod(JoinPoint joinPoint, Exception ex) {
        log.error("Exception happened: {} | {}", joinPoint.getSignature().getName(), ex.getLocalizedMessage());
    }

    @AfterReturning(value = "authServiceMethods()", returning = "result")
    public void logExceptionMethod(JoinPoint joinPoint, Object result) {
        log.info("Method completed successfully: {} | {}", joinPoint.getSignature().getName(), result);
    }
}
