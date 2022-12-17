package com.restkeeper.response.interceptor;

import com.alibaba.nacos.client.utils.StringUtils;
import com.restkeeper.tenant.TenantContext;
import com.restkeeper.utils.JWTUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.rpc.RpcContext;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * 前端拦截器
 *
 * @author MORRIS --> Java
 * @date 2022-12-12 16:45:45
 */
@Slf4j
@Component
public class WebHandlerInterceptor implements HandlerInterceptor {
    //handler执行之前
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String tokenInfo = request.getHeader("Authorization");
        if (StringUtils.isNotEmpty(tokenInfo)) {
            try {
                //解析jwt令牌
                Map<String, Object> tokenMap = JWTUtil.decode(tokenInfo);
                //String shopId = (String) tokenMap.get("shopId");
                //将shopId存入RpcContext,RpcContext是dubbo提供的隐式传参的上下文类
                //RpcContext.getContext().setAttachment("shopId", shopId);
                //因为RpcContext的声明周期是单次调用,所有我们需要自定义一个上下文类TenantContext来存放身份信息
                TenantContext.addAttachments(tokenMap);
            } catch (IOException e) {
                log.error("解析token出错");
            }
        }
        return true;
    }
}
