package com.team6project.cavallo_mall.controller;

import com.team6project.cavallo_mall.service.impl.CategoryServiceImpl;
import com.team6project.cavallo_mall.vo.CategoryVo;
import com.team6project.cavallo_mall.vo.RespVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * description:
 * author: Yuchen Bai
 * email: y.bai19@newcastle.ac.uk
 * date: 2021/2/18 21:23
 */
@RestController
@Slf4j
@RequestMapping("/category")
public class CategoryController {

    @Resource
    private CategoryServiceImpl categoryService;

    // TODO: 23/03/2021 finish this javadocs 
    /**
     * 
     * @return
     */
    @GetMapping("/findAllCategories")
    public RespVo<List<CategoryVo>> findAllCategory() {
        return categoryService.findAllCategory();
    }

}
