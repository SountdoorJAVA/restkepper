package com.restkeeper.operator.controller;

import com.restkeeper.operator.entity.EnterpriseAccount;
import com.restkeeper.operator.service.IEnterpriseAccountService;
import com.restkeeper.operator.vo.AddEnterpriseAccountVO;
import com.restkeeper.operator.vo.ResetPwdVO;
import com.restkeeper.operator.vo.UpdateEnterpriseAccountVO;
import com.restkeeper.response.vo.PageVO;
import com.restkeeper.utils.AccountStatus;
import com.restkeeper.utils.Result;
import com.restkeeper.utils.ResultCode;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * @author MORRIS --> Java
 * @date 2022-12-11 16:32:06
 */
@Api(tags = {"企业账号管理"})
@RestController
@RequestMapping("/enterprise")
public class EnterpriseAccountController {

    @Reference(version = "1.0.0", check = false)
    private IEnterpriseAccountService enterpriseAccountService;

    @ApiOperation("账号列表查询")
    @GetMapping("/pageList/{page}/{pageSize}")
    public PageVO<EnterpriseAccount> findListByPage(@PathVariable("page") int page,
                                                    @PathVariable("pageSize") int pageSize,
                                                    @RequestParam(value = "enterpriseName", required = false) String enterpriseName) {
        return new PageVO<>(enterpriseAccountService.queryPageByName(page, pageSize, enterpriseName));
    }

    @ApiOperation(value = "账号新增")
    @PostMapping(value = "/add")
    public boolean add(@RequestBody AddEnterpriseAccountVO enterpriseAccountVO) {
        //bean拷贝
        EnterpriseAccount enterpriseAccount = new EnterpriseAccount();
        BeanUtils.copyProperties(enterpriseAccountVO, enterpriseAccount);
        //设置申请时间（当前时间，精准到分）
        LocalDateTime localDateTime = LocalDateTime.now();
        enterpriseAccount.setApplicationTime(localDateTime);
        //设置到期时间 (试用下是默认七天后到期，状态改成停用)
        LocalDateTime expireTime = null;
        Integer status = enterpriseAccountVO.getStatus();
        if (status == 0) {//试用
            expireTime = localDateTime.plusDays(7);
        }
        if (status == 1) {//正式
            expireTime = localDateTime.plusDays(enterpriseAccountVO.getValidityDay());
        }
        if (expireTime != null) {
            enterpriseAccount.setExpireTime(expireTime);
        } else {
            throw new RuntimeException("账号类型信息设置有误");
        }
        return enterpriseAccountService.add(enterpriseAccount);
    }

    @ApiOperation(value = "账户查看")
    @ApiImplicitParam(paramType = "path", name = "id", value = "主键", required = true, dataType = "String")
    @GetMapping(value = "/getById/{id}")
    public EnterpriseAccount getById(@PathVariable("id") String id) {
        return enterpriseAccountService.getById(id);
    }

    @ApiOperation(value = "账户删除")
    @ApiImplicitParam(paramType = "query", name = "id", value = "主键", required = true, dataType = "String")
    @DeleteMapping(value = "/deleteById/{id}")
    public boolean delete(@PathVariable(value = "id") String id) {
        return enterpriseAccountService.removeById(id);
    }

    @ApiOperation(value = "账号编辑")
    @PutMapping(value = "/update")
    public Result update(@RequestBody UpdateEnterpriseAccountVO updateEnterpriseAccountVO) {
        Result result = new Result();
        //查询原有企业账户信息
        EnterpriseAccount enterpriseAccount = enterpriseAccountService.getById(updateEnterpriseAccountVO.getEnterpriseId());
        if (enterpriseAccount == null) {
            result.setStatus(ResultCode.error);
            result.setDesc("修改账户不存在");
            return result;
        }
        //修改状态信息
        if (updateEnterpriseAccountVO.getStatus() != null) {
            //正式期不能修改为试用期
            if (updateEnterpriseAccountVO.getStatus() == 0 && enterpriseAccount.getStatus() == 1) {
                result.setStatus(ResultCode.error);
                result.setDesc("不能将正式账号改为试用账号");
                return result;
            }
            //试用改正式
            if (updateEnterpriseAccountVO.getStatus() == 1 && enterpriseAccount.getStatus() == 0) {
                //到期时间
                LocalDateTime localDateTime = LocalDateTime.now();
                LocalDateTime expireTime = localDateTime.plusDays(updateEnterpriseAccountVO.getValidityDay());
                enterpriseAccount.setApplicationTime(localDateTime);
                enterpriseAccount.setExpireTime(expireTime);
            }
            //正式添加延期
            if (updateEnterpriseAccountVO.getStatus() == 1 && enterpriseAccount.getStatus() == 1) {
                LocalDateTime localDateTime = LocalDateTime.now();
                LocalDateTime expireTime = localDateTime.plusDays(updateEnterpriseAccountVO.getValidityDay());
                enterpriseAccount.setExpireTime(expireTime);
            }
        }
        //其他字段设置
        BeanUtils.copyProperties(updateEnterpriseAccountVO, enterpriseAccount);
        //执行修改
        boolean flag = enterpriseAccountService.updateById(enterpriseAccount);
        if (flag) {
            //修改成功
            result.setStatus(ResultCode.success);
            result.setDesc("修改成功");
            return result;
        } else {
            //修改失败
            result.setStatus(ResultCode.error);
            result.setDesc("修改失败");
            return result;
        }
    }

    @ApiOperation(value = "账号恢复")
    @PutMapping("/recovery/{id}")
    public boolean recovery(@PathVariable("id") String id) {
        return enterpriseAccountService.recovery(id);
    }

    @ApiOperation(value = "账号禁用")
    @PutMapping(value = "/forbidden/{id}")
    public boolean forbidden(@PathVariable("id") String id) {
        EnterpriseAccount enterpriseAccount = enterpriseAccountService.getById(id);
        enterpriseAccount.setStatus(AccountStatus.Forbidden.getStatus());
        return enterpriseAccountService.updateById(enterpriseAccount);
    }

    @ApiOperation(value = "密码重置")
    @PutMapping(value = "/resetPwd")
    public boolean resetPwd(@RequestBody ResetPwdVO resetPwdVO) {
        return enterpriseAccountService.resetPwd(resetPwdVO.getId(), resetPwdVO.getPwd());
    }
}
