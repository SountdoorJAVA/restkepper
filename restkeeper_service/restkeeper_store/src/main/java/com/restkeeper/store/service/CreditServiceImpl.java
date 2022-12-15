package com.restkeeper.store.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.restkeeper.constants.SystemCode;
import com.restkeeper.exception.BussinessException;
import com.restkeeper.store.entity.Credit;
import com.restkeeper.store.entity.CreditCompanyUser;
import com.restkeeper.store.mapper.CreditMapper;
import lombok.val;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service("creditService")
@Service(version = "1.0.0", protocol = "dubbo")
public class CreditServiceImpl extends ServiceImpl<CreditMapper, Credit> implements ICreditService {

    @Autowired
    @Qualifier("creditCompanyUserService")
    private ICreditCompanyUserService creditCompanyUserService;

    @Override
    @Transactional
    public boolean add(Credit credit, List<CreditCompanyUser> users) {
        //保存挂账用户基本信息
        this.save(credit);
        //判断有没有公司用户
        if (users != null && !users.isEmpty()) {
            //获取用户名
            val userNameList = users.stream().map(d -> d.getUserName()).collect(Collectors.toList());
            //用户名去重
            val count = userNameList.stream().distinct().count();
            if (userNameList.size() != count) {
                throw new BussinessException("用户名重复");
            }
            //设置关联
            users.forEach(d -> {
                d.setCreditId(credit.getCreditId());
            });
            return creditCompanyUserService.saveBatch(users);
        }
        return true;
    }

    @Override
    public IPage<Credit> queryPage(int pageNum, int size, String userName) {
        IPage<Credit> page = new Page<>(pageNum, size);
        QueryWrapper<Credit> creditQueryWrapper = new QueryWrapper<>();

        if (StringUtils.isNotEmpty(userName)) {
            creditQueryWrapper.lambda()
                    .like(Credit::getUserName, userName)
                    .or()
                    .inSql(Credit::getCreditId,
                            "select credit_id from t_credit_company_user where user_name like '%" + StringEscapeUtils.escapeSql(userName) + "%'");
        }

        //根据姓名查询分页
        page = this.page(page, creditQueryWrapper);

        List<Credit> records = page.getRecords();
        //遍历,如果是公司
        records.forEach(record -> {
            if (record.getCreditType() == SystemCode.CREDIT_TYPE_COMPANY) {
                //根据公司账户id查询旗下挂账个人
                QueryWrapper<CreditCompanyUser> queryWrapper = new QueryWrapper<>();
                queryWrapper.lambda().eq(CreditCompanyUser::getCreditId, record.getCreditId());
                //设置挂账人信息
                record.setUsers(creditCompanyUserService.list(queryWrapper));
            }
        });
        return page;
    }

    @Override
    public Credit queryById(String id) {
        val credit = this.getById(id);
        if (credit == null) {
            throw new BussinessException("该挂账账户不存在");
        }

        if (credit.getCreditType() == SystemCode.CREDIT_TYPE_COMPANY) {
            QueryWrapper<CreditCompanyUser> qw = new QueryWrapper<>();
            qw.lambda().eq(CreditCompanyUser::getCreditId, id);
            credit.setUsers(creditCompanyUserService.list(qw));
        }

        return credit;
    }

    @Override
    @Transactional
    public boolean updateInfo(Credit credit, List<CreditCompanyUser> users) {
        //如果是公司账户,删除原有关系
        if (credit.getCreditType() == SystemCode.CREDIT_TYPE_COMPANY) {
            //获取旗下个人账户
            List<CreditCompanyUser> userList = credit.getUsers();
            //如果有个人账户
            if (userList != null && !userList.isEmpty()) {
                //进行删除
                List<String> idList = userList.stream().map(d -> d.getId()).collect(Collectors.toList());
                creditCompanyUserService.removeByIds(idList);
            }
        }
        //重新建立关联关系
        if (users != null && !users.isEmpty()) {
            //获取用户名列表
            List<String> userNameList = users.stream().map(d -> d.getUserName()).collect(Collectors.toList());
            //去重判断
            long count = userNameList.stream().distinct().count();
            if (userNameList.size() != count) {
                throw new BussinessException("用户名重复");
            }
            //设置关联
            users.forEach(d -> {
                d.setCreditId(credit.getCreditId());
            });
            return creditCompanyUserService.saveBatch(users);
        }

        //个人账户
        return this.saveOrUpdate(credit);
    }
}
