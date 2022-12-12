package com.restkeeper.shop.config;

import com.baomidou.mybatisplus.core.parser.ISqlParser;
import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import com.baomidou.mybatisplus.extension.plugins.tenant.TenantHandler;
import com.baomidou.mybatisplus.extension.plugins.tenant.TenantSqlParser;
import com.google.common.collect.Lists;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.StringValue;
import org.apache.dubbo.rpc.RpcContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * 每次调用sql会调用
 *
 * @author MORRIS --> Java
 * @date 2022-12-11 23:59:36
 */
@Configuration
public class MybatisPlusTenantConfig {

    //定义当前的多租户标识字段
    private static final String SYSTEM_TENANT_ID = "shop_id";

    //定义当前忽略多租户操作的表
    private static final List<String> IGNORE_TENANT_TABLES = Lists.newArrayList("");

    @Bean
    public PaginationInterceptor paginationInterceptor() {
        System.out.println("---paginationInterceptor---");
        PaginationInterceptor paginationInterceptor = new PaginationInterceptor();
        // SQL解析处理拦截：增加租户处理回调。
        TenantSqlParser tenantSqlParser = new TenantSqlParser().setTenantHandler(new TenantHandler() {
            //设置租户id
            @Override
            public Expression getTenantId(boolean where) {
                //String shopId = "test";
                String shopId = RpcContext.getContext().getAttachment("shopId");
                if (shopId == null) {
                    throw new RuntimeException("get tenantId error");
                }
                return new StringValue(shopId);
            }

            //当前租户id对应的表字段
            @Override
            public String getTenantIdColumn() {
                return SYSTEM_TENANT_ID;
            }

            //表级过滤器
            @Override
            public boolean doTableFilter(String tableName) {
                return IGNORE_TENANT_TABLES.stream().anyMatch(e -> e.equalsIgnoreCase(tableName));
            }
        });
        paginationInterceptor.setSqlParserList(Lists.newArrayList(tenantSqlParser));
        return paginationInterceptor;
    }
}
