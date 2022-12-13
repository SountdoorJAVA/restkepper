package com.restkeeper.shop;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.restkeeper.shop.entity.Brand;
import com.restkeeper.shop.service.IBrandService;
import org.apache.dubbo.config.annotation.Reference;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Map;

@SpringBootTest
@RunWith(SpringRunner.class)
public class BrandTest extends BaseTest{

    @Reference(version = "1.0.0",check = false)
    private IBrandService brandService;

    @Test
    public void queryPage(){
        IPage<Brand> result = brandService.queryPage(1, 100);

        result.getRecords().forEach(b->{
            System.out.println(b.getBrandName()+":"+b.getInfo());
        });
    }

    @Test
    public void getBrandList(){
        List<Map<String, Object>> brandList = brandService.getBrandList();
        System.out.println(brandList);
    }
}
