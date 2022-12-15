package com.restkeeper.controller.store;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.google.common.collect.Lists;
import com.restkeeper.constants.SystemCode;
import com.restkeeper.exception.BussinessException;
import com.restkeeper.response.vo.PageVO;
import com.restkeeper.store.entity.Credit;
import com.restkeeper.store.entity.CreditCompanyUser;
import com.restkeeper.store.service.ICreditService;
import com.restkeeper.utils.BeanListUtils;
import com.restkeeper.vo.store.CreditVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author MORRIS --> Java
 * @date 2022-12-15 23:51:21
 */
@Slf4j
@Api(tags = {"挂账管理"})
@RestController
@RequestMapping("/credit")
public class CreditController {
    @Reference(version = "1.0.0", check = false)
    private ICreditService creditService;

    @ApiOperation(value = "新增挂账单位")
    @PostMapping("/add")
    public boolean add(@RequestBody CreditVO creditvo) {
        val credit = new Credit();
        //拷贝属性,排除users字段
        BeanUtils.copyProperties(creditvo, credit, "users");

        if (creditvo.getUsers() != null && !creditvo.getUsers().isEmpty()) {
            List<CreditCompanyUser> users = new ArrayList<>();
            creditvo.getUsers().forEach(userVO -> {
                val user = new CreditCompanyUser();
                BeanUtils.copyProperties(userVO, user);
                users.add(user);
            });
            return creditService.add(credit, users);
        }
        return creditService.add(credit, null);
    }

    /**
     * 支持挂账人模糊搜索
     */
    @ApiOperation(value = "挂账管理列表")
    @GetMapping("/pageList/{page}/{pageSize}")
    public PageVO<CreditVO> pageList(@RequestParam(value = "name", defaultValue = "") String name,
                                     @PathVariable int page,
                                     @PathVariable int pageSize) {
        //获取分页信息
        IPage<Credit> creditIPage = creditService.queryPage(page, pageSize, name);

        List<CreditVO> voList = Lists.newArrayList();
        try {
            //将分页信息内的记录转换为CreditVO类型
            voList = BeanListUtils.copy(creditIPage.getRecords(), CreditVO.class);
        } catch (Exception e) {
            throw new BussinessException("集合转换出错");
        }

        return new PageVO<CreditVO>(creditIPage, voList);
    }

    @ApiOperation(value = "根据id获取挂账详情")
    @GetMapping("/{id}")
    public CreditVO getCredit(@PathVariable String id) {
        CreditVO creditVO = new CreditVO();
        Credit credit = creditService.queryById(id);
        BeanUtils.copyProperties(credit, creditVO);
        return creditVO;
    }

    @ApiOperation(value = "修改挂账")
    @PutMapping("/update/{id}")
    public boolean updateCredit(@PathVariable String id, @RequestBody CreditVO creditvo) {
        //CreditVO->转化成 Credit
        //获取当前id对象
        Credit credit = creditService.queryById(id);
        //更新对象属性
        BeanUtils.copyProperties(creditvo, credit, "users");

        if (creditvo.getUsers() != null && !creditvo.getUsers().isEmpty()) {
            // List<CreditCompanyUserVO> 转换成 List<CreditCompanyUser>
            List<CreditCompanyUser> companyUsers = Lists.newArrayList();
            creditvo.getUsers().forEach(d -> {
                CreditCompanyUser creditCompany = new CreditCompanyUser();
                BeanUtils.copyProperties(d, creditCompany);
                companyUsers.add(creditCompany);
            });
            return creditService.updateInfo(credit, companyUsers);
        }

        return creditService.updateInfo(credit, null);
    }
}
