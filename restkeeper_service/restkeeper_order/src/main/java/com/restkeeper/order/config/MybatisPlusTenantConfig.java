package com.restkeeper.order.config;


import com.alibaba.druid.pool.DruidDataSource;

import com.baomidou.mybatisplus.autoconfigure.MybatisPlusProperties;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.core.parser.ISqlParserFilter;
import com.baomidou.mybatisplus.core.parser.SqlParserHelper;
import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import com.baomidou.mybatisplus.extension.plugins.tenant.TenantHandler;
import com.baomidou.mybatisplus.extension.plugins.tenant.TenantSqlParser;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import com.google.common.collect.Lists;
import com.restkeeper.mybatis.GeneralMetaObjectHandler;
import com.restkeeper.tenant.TenantContext;
import io.seata.rm.datasource.DataSourceProxy;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.StringValue;
import org.apache.dubbo.rpc.RpcContext;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.reflection.MetaObject;
import org.mybatis.spring.transaction.SpringManagedTransactionFactory;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;


import javax.sql.DataSource;
import java.util.List;

@Configuration
@AutoConfigureAfter(MybatisPlusTenantConfig.class)
@EnableConfigurationProperties({MybatisPlusProperties.class})
public class MybatisPlusTenantConfig {

    //设置多租户字段
    private static final String SYSTEM_TENANT_SHOPID = "shop_id";
    private static final String SYSTEM_TENANT_STOREID = "store_id";
    //有哪些表会忽略多租户操作
    private static final List<String> INGORE_TENANT_TABLES = Lists.newArrayList("");

    private static boolean isEmpty(String str) {
        return str == null || str.length() == 0 || "null".equals(str);
    }

    private String getShopId() {
        //远程获取
        String shopId = RpcContext.getContext().getAttachment("shopId");
        //如果远程获取为空
        if (isEmpty(shopId)) {
            shopId = TenantContext.getShopId();
        }
        return shopId;
    }

    private String getStoreId() {
        //远程获取
        String shopId = RpcContext.getContext().getAttachment("storeId");
        //如果远程获取为空
        if (isEmpty(shopId)) {
            shopId = TenantContext.getStoreId();
        }
        return shopId;
    }

    @Bean
    public PaginationInterceptor paginationInterceptor() {
        PaginationInterceptor paginationInterceptor = new PaginationInterceptor();
        //shop_id
        TenantSqlParser tenantSqlParser_shop = new TenantSqlParser().setTenantHandler(new TenantHandler() {
            @Override
            public Expression getTenantId(boolean where) {
                //获取租户字段的值
                //从rpccontext中进行获取
                String shopId = getShopId();
                if (shopId == null) {
                    throw new RuntimeException("get shopId error");
                }
                return new StringValue(shopId);
            }

            @Override
            public String getTenantIdColumn() {
                //设置当前的多租户字段
                return SYSTEM_TENANT_SHOPID;
            }

            @Override
            public boolean doTableFilter(String tableName) {
                return INGORE_TENANT_TABLES.stream().anyMatch((e) -> e.equalsIgnoreCase(tableName));
            }
        });

        //store_id
        TenantSqlParser tenantSqlParser_store = new TenantSqlParser().setTenantHandler(new TenantHandler() {
            @Override
            public Expression getTenantId(boolean where) {
                //获取租户字段的值
                //从rpccontext中进行获取
                String storeId = getStoreId();
                if (storeId == null) {
                    throw new RuntimeException("get storeId error");
                }
                return new StringValue(storeId);
            }

            @Override
            public String getTenantIdColumn() {
                //设置当前的多租户字段
                return SYSTEM_TENANT_STOREID;
            }

            @Override
            public boolean doTableFilter(String tableName) {
                return INGORE_TENANT_TABLES.stream().anyMatch((e) -> e.equalsIgnoreCase(tableName));
            }
        });
        paginationInterceptor.setSqlParserList(Lists.newArrayList(tenantSqlParser_shop, tenantSqlParser_store));
        //自定义忽略多租户操作方法
        paginationInterceptor.setSqlParserFilter(new ISqlParserFilter() {
            @Override
            public boolean doFilter(MetaObject metaObject) {
                MappedStatement ms = SqlParserHelper.getMappedStatement(metaObject);
                // 过滤自定义查询此时无租户信息约束
                if ("com.restkeeper.store.mapper.StaffMapper.login".equals(ms.getId())) {
                    return true;
                }
                return false;
            }
        });
        return paginationInterceptor;
    }

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource druidDataSource() {
        DruidDataSource druidDataSource = new DruidDataSource();

        return druidDataSource;
    }

    @Primary//@Primary标识必须配置在代码数据源上，否则本地事务失效
    @Bean("dataSource")
    public DataSourceProxy dataSourceProxy(DataSource druidDataSource) {
        return new DataSourceProxy(druidDataSource);
    }

    private MybatisPlusProperties properties;

    public MybatisPlusTenantConfig(MybatisPlusProperties properties) {
        this.properties = properties;
    }

    @Bean
    public MybatisSqlSessionFactoryBean sqlSessionFactory(DataSourceProxy dataSourceProxy) throws Exception {

        // 这里必须用 MybatisSqlSessionFactoryBean 代替了 SqlSessionFactoryBean，否则 MyBatisPlus 不会生效
        MybatisSqlSessionFactoryBean mybatisSqlSessionFactoryBean = new MybatisSqlSessionFactoryBean();
        mybatisSqlSessionFactoryBean.setDataSource(dataSourceProxy);
        mybatisSqlSessionFactoryBean.setTransactionFactory(new SpringManagedTransactionFactory());

        GlobalConfig globalConfig = new GlobalConfig();
        globalConfig.setMetaObjectHandler(new GeneralMetaObjectHandler());
        mybatisSqlSessionFactoryBean.setGlobalConfig(globalConfig);
        mybatisSqlSessionFactoryBean.setPlugins(paginationInterceptor());

        mybatisSqlSessionFactoryBean.setMapperLocations(new PathMatchingResourcePatternResolver()
                .getResources("classpath*:/mapper/*.xml"));

        MybatisConfiguration configuration = this.properties.getConfiguration();
        if (configuration == null) {
            configuration = new MybatisConfiguration();
        }
        mybatisSqlSessionFactoryBean.setConfiguration(configuration);
        return mybatisSqlSessionFactoryBean;
    }
}
