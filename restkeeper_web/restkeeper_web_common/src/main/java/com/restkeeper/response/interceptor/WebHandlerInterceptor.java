package com.restkeeper.response.interceptor;

import com.alibaba.nacos.client.utils.StringUtils;
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
                Map<String, Object> tokenMap = JWTUtil.decode(tokenInfo);
                String shopId = (String) tokenMap.get("shopId");
                RpcContext.getContext().setAttachment("shopId", shopId);
            } catch (IOException e) {
                log.error("解析token出错");
            }
        }
        return true;
    }
}
