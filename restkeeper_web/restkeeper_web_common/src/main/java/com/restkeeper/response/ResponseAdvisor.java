package com.restkeeper.response;

import com.restkeeper.response.exception.ExceptionResponse;
import com.restkeeper.response.vo.PageVO;
import com.restkeeper.utils.Result;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * @author MORRIS --> Java
 * @date 2022-12-10 21:58:42
 */
@RestControllerAdvice(basePackages = "com.restkeeper")
public class ResponseAdvisor implements ResponseBodyAdvice<Object> {
    @Override
    public boolean supports(MethodParameter methodParameter, Class<? extends HttpMessageConverter<?>> aClass) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object o, MethodParameter methodParameter, MediaType mediaType, Class<? extends HttpMessageConverter<?>> aClass, ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {

        if (o instanceof Result) {
            return o;
        }
        if (o instanceof Boolean) {
            boolean result = (boolean) o;
            return new BaseResponse<Boolean>(result);
        }
        if (o instanceof PageVO) {
            return new BaseResponse<>(o);
        }
        if (o instanceof ExceptionResponse) {
            return new BaseResponse<>(400, ((ExceptionResponse) o).getMsg());
        }
        return new BaseResponse<>(o);

    }
}
