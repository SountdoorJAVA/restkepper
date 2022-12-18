package com.restkeeper.aop;

import com.restkeeper.tenant.TenantContext;
import org.apache.dubbo.rpc.RpcContext;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * 微服务多租户本地拦截器支持
 */
@Aspect
@Component
public class TenantAspect {

    @Pointcut("@annotation(com.restkeeper.aop.TenantAnnotation)")
    public void tenantAnnotation() {
    }

    @Before("tenantAnnotation()")
    public void doBeforeAdvice(JoinPoint joinPoint) {
        TenantContext.addAttachment("shopId", RpcContext.getContext().getAttachment("shopId"));
        TenantContext.addAttachment("storeId", RpcContext.getContext().getAttachment("storeId"));
    }

    @After("tenantAnnotation()")
    public void doAfterAdvice(JoinPoint joinPoint) {
        TenantContext.clear();
    }
}

