package com.restkeeper.gateway.filter;

import com.alibaba.nacos.client.utils.StringUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import com.restkeeper.utils.JWTUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;


import java.util.List;
import java.util.Map;

/**
 * @author MORRIS --> Java
 * @date 2022-12-11 00:48:11
 */
@Component
@RefreshScope
public class AuthFilter implements GlobalFilter, Ordered {

    @Value("${gateway.secret}")
    private String secret;

    @Value("#{'${gateway.excludedUrls}'.split(',')}")
    private List<String> excludedUrls;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        /**
         * 获取响应对象
         * 获取请求路径
         * //排除不需要令牌的路径
         * 获取令牌信息
         * 校验令牌是否合法
         */
        ServerHttpResponse response = exchange.getResponse();
        //获取请求路径
        String path = exchange.getRequest().getURI().getPath();

        //排除特殊不需要令牌路径
        if (excludedUrls.contains(path)) {
            return chain.filter(exchange);
        }

        //获取令牌信息
        String token = exchange.getRequest().getHeaders().getFirst("Authorization");

        if (StringUtils.isNotEmpty(token)) {
            //校验令牌
            JWTUtil.VerifyResult verifyResult = JWTUtil.verifyJwt(token, secret);
            if (verifyResult.isValidate()) {
                //合法
                return chain.filter(exchange);
            } else {
                //不合法
                Map<String, Object> responseData = Maps.newHashMap();
                responseData.put("code", verifyResult.getCode());
                responseData.put("message", "验证失败");
                return responseError(response, responseData);
            }
        } else {
            //返回错误信息
            Map<String, Object> responseData = Maps.newHashMap();
            responseData.put("code", 401);
            responseData.put("message", "非法请求");
            responseData.put("cause", "Token is empty");
            return responseError(response, responseData);
        }
    }

    //返回数据
    private Mono<Void> responseError(ServerHttpResponse response, Map<String, Object> responseData) {

        //将信息转换为Json
        ObjectMapper objectMapper = new ObjectMapper();
        byte[] data = new byte[0];
        try {
            data = objectMapper.writeValueAsBytes(responseData);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //输出错误信息
        DataBuffer buffer = response.bufferFactory().wrap(data);
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
        return response.writeWith(Mono.just(buffer));
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
