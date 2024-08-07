package middle_point_search.backend.common.aop;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Slf4j
@Component
public class LogAspect {

    @Pointcut("execution(* middle_point_search.backend..*.*(..)) && !execution(* middle_point_search.backend.common..*(..))")
    public void all() {
    }

    @Pointcut("execution(* middle_point_search.backend..*Controller.*(..))")
    public void controller() {
    }

    @Around("all()")
    public Object logging(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        try {
            Object result = joinPoint.proceed();
            return result;
        } finally {
            long end = System.currentTimeMillis();
            long timeinMs = end - start;
            log.info("{} | time = {}ms", joinPoint.getSignature(), timeinMs);
        }
    }

    @Around("controller()")
    public Object loggingBefore(ProceedingJoinPoint joinPoint) throws Throwable {
        final HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        final String ipAddr = request.getRemoteAddr();
        final String method = request.getMethod();
        final String requestURI = request.getRequestURI();
        final Object[] args = joinPoint.getArgs();

        log.info("[REQUEST] {} {} {} args={}", ipAddr, method, requestURI, args);
        try {
            Object result = joinPoint.proceed();
            log.info("[RESPONSE] {}", result);
            return result;
        } catch (Exception e) {
            log.error("[RESPONSE] exception message = {} {}", e.getMessage(), e.getStackTrace()[0]);
            throw e;
        }
    }

}
