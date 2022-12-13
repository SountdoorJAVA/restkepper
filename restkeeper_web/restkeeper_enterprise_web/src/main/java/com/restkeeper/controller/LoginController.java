package com.restkeeper.controller;

import com.restkeeper.constants.SystemCode;
import com.restkeeper.operator.service.IEnterpriseAccountService;
import com.restkeeper.shop.service.IStoreManagerService;
import com.restkeeper.utils.Result;
import com.restkeeper.utils.ResultCode;
import com.restkeeper.vo.LoginVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author MORRIS --> Java
 * @date 2022-12-12 16:30:21
 */
@RestController
@Slf4j
@Api("登录接口")
public class LoginController {
    //集团管理员
    @Reference(version = "1.0.0", check = false)
    private IEnterpriseAccountService enterpriseAccountService;

    //店长
    @Reference(version = "1.0.0", check = false)
    private IStoreManagerService storeManagerService;

    // 集团 string123  973229
    @ApiOperation(value = "登录入口")
    @ApiImplicitParam(name = "Authorization", value = "jwt token", required = false, dataType = "String", paramType = "header")
    @PostMapping("/login")
    public Result login(@RequestBody LoginVO loginVO) {

        //如果是集团用户(restkeeper_operator服务下)
        if (SystemCode.USER_TYPE_SHOP.equals(loginVO.getType())) {
            return enterpriseAccountService.login(loginVO.getShopId(), loginVO.getPhone(), loginVO.getPassword());
        }

        //如果是店长(restkeeper_shop服务下,有配置多租户功能)
        if (SystemCode.USER_TYPE_STORE_MANAGER.equals(loginVO.getType())) {
            return storeManagerService.login(loginVO.getShopId(), loginVO.getPhone(), loginVO.getPassword());
        }

        Result result = new Result();
        result.setStatus(ResultCode.error);
        result.setDesc("不支持该类型用户登录");
        return result;
    }
}
