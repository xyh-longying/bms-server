package com.longying.bmsserver.component;

import cn.hutool.core.map.MapUtil;
import com.longying.bmsbase.common.api.ApiException;
import com.longying.bmsserver.service.BaseService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Create by chenglong on 2021/2/25
 */
@Aspect
@Component
@Order(3)
@Slf4j
public class ExceptionAspect {

    @Around("execution(public * com.longying.bms.*.service.impl.*.*(..))")
    public Object throwException(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = null;
        //获得类名
        String clazzName = joinPoint.getTarget().getClass().getName();
        //获得方法名
        String methodName = joinPoint.getSignature().getName();
        try{
            result = joinPoint.proceed();
        } catch (ApiException apiException){
            log.error("\r\n"+clazzName+"."+methodName+"发生异常，异常信息：\r\n{}",apiException.getLoginfo());
            throw apiException;
        } catch (Exception e){
            ApiException exception = ApiException.builder()
                    .code("500")
                    .message("系统内部异常，请联系管理员")
                    .loginfo(BaseService.getExceptionLogInfo("500"
                            ,"程序运行时异常"
                            , e.getMessage()
                            ,"请联系管理员查看具体异常日志处理"
                            , null))
                    .build();
            log.error("\r\n"+clazzName+"."+methodName+"发生异常，异常信息：\r\n{}",exception.getLoginfo());
            throw exception;
        }
        return result;
    }
}
