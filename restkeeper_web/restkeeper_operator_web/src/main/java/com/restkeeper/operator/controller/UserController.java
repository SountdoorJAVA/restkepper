package com.restkeeper.operator.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.restkeeper.operator.entity.OperatorUser;
import com.restkeeper.operator.service.IOperatorUserService;
import com.restkeeper.operator.vo.LoginVO;
import com.restkeeper.response.vo.PageVO;
import com.restkeeper.utils.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;

/**
 * 管理员的登录接口
 */
@RestController
@RefreshScope //配置中心的自动刷新
@Slf4j
@Api(tags = {"管理员相关接口"})
public class UserController {

    @Value("${server.port}")
    private String port;

    @Reference(version = "1.0.0", check = false)
    private IOperatorUserService operatorUserService;

    @GetMapping(value = "/echo")
    public String echo() {
        log.info("i am from port: {}", port);
        return "i am from port: " + port;
    }

    @ApiOperation("分页列表查询")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", name = "page", value = "当前页", required = true, dataType = "Integer"),
            @ApiImplicitParam(paramType = "path", name = "pageSize", value = "每页数据大小", required = true, dataType = "Integer"),
            @ApiImplicitParam(paramType = "query", name = "name", value = "用户名", required = false, dataType = "String")
    })
    @GetMapping("/pageList/{page}/{pageSize}")
    public IPage<OperatorUser> findListByPage(@PathVariable("page") int pageNum,
                                              @PathVariable("pageSize") int pageSize,
                                              @RequestParam(name = "name", required = false) String name) {
        IPage<OperatorUser> page = operatorUserService.queryPageByName(pageNum, pageSize, name);
        return page;
    }

    @ApiOperation("分页列表查询(按前端要求)")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", name = "page", value = "当前页", required = true, dataType = "Integer"),
            @ApiImplicitParam(paramType = "path", name = "pageSize", value = "每页数据大小", required = true, dataType = "Integer"),
            @ApiImplicitParam(paramType = "query", name = "name", value = "用户名", required = false, dataType = "String")
    })
    @GetMapping("/pagevoList/{page}/{pageSize}")
    public PageVO<OperatorUser> findPageVO(@PathVariable("page") int pageNum,
                                           @PathVariable("pageSize") int pageSize,
                                           @RequestParam(name = "name", required = false) String name) {
        IPage<OperatorUser> page = operatorUserService.queryPageByName(pageNum, pageSize, name);
        PageVO<OperatorUser> pageVO = new PageVO<>(page);
        return pageVO;
    }

    /**
     * 登录校验
     *
     * @param loginVO
     * @return
     */
    @ApiOperation(value = "登录校验")
    @PostMapping("/login")
    @ApiImplicitParam(name = "Authorization", value = "jwt token", required = false, dataType = "String", paramType = "header")
    public Result login(@RequestBody LoginVO loginVO) {
        log.info(loginVO.getLoginName() + " " + loginVO.getLoginPass());
        return operatorUserService.login(loginVO.getLoginName(), loginVO.getLoginPass());
    }

}
