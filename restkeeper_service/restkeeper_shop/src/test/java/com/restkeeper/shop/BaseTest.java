package com.restkeeper.shop;

import org.apache.dubbo.rpc.RpcContext;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author MORRIS --> Java
 * @date 2022-12-12 00:38:27
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class BaseTest {

    @Before
    public void init() {
        RpcContext.getContext().setAttachment("shopId", "test02");
    }
}
