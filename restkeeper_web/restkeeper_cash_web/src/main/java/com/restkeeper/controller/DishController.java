package com.restkeeper.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.google.common.collect.Lists;
import com.restkeeper.entity.DishEs;
import com.restkeeper.entity.SearchResult;
import com.restkeeper.response.vo.PageVO;
import com.restkeeper.service.IDishSearchService;
import com.restkeeper.store.service.ISellCalculationService;
import com.restkeeper.vo.DishPanelVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author MORRIS --> Java
 * @date 2022-12-17 16:56:36
 */
@Api(tags = {"菜品搜索相关接口"})
@RestController
@RequestMapping("/dish")
public class DishController {

    @Reference(version = "1.0.0", check = false)
    private IDishSearchService dishSearchService;

    @Reference(version = "1.0.0", check = false)
    private ISellCalculationService sellCalculationService;

    @ApiOperation("根据商品编码完成商品搜索")
    @GetMapping("/queryByCode/{code}/{page}/{pageSize}")
    public PageVO<DishPanelVO> searchByCode(@PathVariable String code,
                                            @PathVariable int page,
                                            @PathVariable int pageSize) {
        PageVO<DishPanelVO> pageResult = new PageVO<>();
        //进行业务查询
        SearchResult<DishEs> searchResult = dishSearchService.searchDishByCode(code, page, pageSize);
        //将查询结果封装到vo内
        pageResult.setCounts(searchResult.getTotal());//总个数
        pageResult.setPage(page);//当前页
        long pageCount = searchResult.getTotal() % pageSize == 0 ? searchResult.getTotal() / pageSize : searchResult.getTotal() / pageSize + 1;
        pageResult.setPages(pageCount);//总页数

        List<DishPanelVO> dishVOList = Lists.newArrayList();
        searchResult.getRecords().forEach(es -> {

            DishPanelVO dishPanelVO = new DishPanelVO();
            dishPanelVO.setDishId(es.getId());
            dishPanelVO.setDishName(es.getName());
            dishPanelVO.setPrice(es.getPrice());
            dishPanelVO.setImage(es.getImage());
            dishPanelVO.setRemainder(sellCalculationService.getRemainderCount(es.getId()));

            dishVOList.add(dishPanelVO);
        });
        pageResult.setItems(dishVOList);

        return pageResult;

    }
}
