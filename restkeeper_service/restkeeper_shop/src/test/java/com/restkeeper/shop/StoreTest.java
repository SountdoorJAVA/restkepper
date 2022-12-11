package com.restkeeper.shop;

import com.restkeeper.shop.entity.Store;
import com.restkeeper.shop.service.IStoreService;
import org.apache.dubbo.config.annotation.Reference;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author MORRIS --> Java
 * @date 2022-12-12 00:17:19
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class StoreTest extends BaseTest {

    @Reference(version = "1.0.0", check = false)
    private IStoreService storeService;

    @Test
    @Rollback(false)
    public void saveTest() {
        Store store = new Store();
        store.setBrandId("test02");
        store.setStoreName("测试02");
        store.setProvince("北京02");
        store.setCity("昌平区02");
        store.setArea("金燕龙大厦02");
        store.setAddress("北京 昌平区 金燕龙大厦02");
        storeService.save(store);
    }

    @Test
    public void queryTest() {
        Store store = storeService.getById("1601975501444997122");
        System.out.println(store);
    }
}
