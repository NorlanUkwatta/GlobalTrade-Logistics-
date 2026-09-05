package com.globaltrade.logistics.interceptor;

import com.globaltrade.logistics.interceptor.annotation.PerformanceMonitor;
import jakarta.annotation.Priority;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Serializable;

@Interceptor
@PerformanceMonitor
@Priority(Interceptor.Priority.APPLICATION + 300)
public class PerformanceMonitorInterceptor implements Serializable {

    private static final Logger LOG = LogManager.getLogger(PerformanceMonitorInterceptor.class);

    @AroundInvoke
    public Object monitorExecutionTime(InvocationContext ctx) throws Exception {
        long start = System.currentTimeMillis();
        try {
            return ctx.proceed();
        } finally {
            long duration = System.currentTimeMillis() - start;
            String methodName = ctx.getMethod().getDeclaringClass().getSimpleName() + "." + ctx.getMethod().getName();
            
            if (duration > 500) {
                LOG.warn("[PERF-MONITOR] SLOW METHOD DETECTED: {} took {}ms", methodName, duration);
            } else {
                LOG.debug("[PERF-MONITOR] {} executed in {}ms", methodName, duration);
            }
        }
    }
}