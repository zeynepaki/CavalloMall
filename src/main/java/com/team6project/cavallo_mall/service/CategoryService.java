package com.team6project.cavallo_mall.service;

import com.team6project.cavallo_mall.vo.CategoryVo;
import com.team6project.cavallo_mall.vo.RespVo;

import java.util.List;
import java.util.Set;

/**
 * description:
 * author: Yuchen Bai
 * email: y.bai19@newcastle.ac.uk
 * date: 2021/2/18 16:08
 */
public interface CategoryService {

    /**
     * Method that finds all categories and returns them as a List
     * @return RespVo Collection of Lists of CategoryVo (multidimensional array)
     */
    RespVo<List<CategoryVo>> findAllCategory();

    /**
     * Method that finds all subcategory IDs
     * @param id integer that giving a unique id for the category
     * @param resultSet
     */
    void findSubCategoryId(Integer id, Set<Integer> resultSet);
}
