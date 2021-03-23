package com.team6project.cavallo_mall.dao;

import com.team6project.cavallo_mall.pojo.Category;

import java.util.List;

public interface CategoryMapper {

    /**
     *
     * @param id
     * @return
     */
    int deleteByPrimaryKey(Integer id);

    int insert(Category record);

    int insertSelective(Category record);

    Category selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Category record);

    int updateByPrimaryKey(Category record);

    List<Category> selectAll();
}