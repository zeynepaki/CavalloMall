package com.team6project.cavallo_mall.service.impl;

import com.team6project.cavallo_mall.dao.CategoryMapper;
import com.team6project.cavallo_mall.pojo.Category;
import com.team6project.cavallo_mall.service.CategoryService;
import com.team6project.cavallo_mall.util.Objects;
import com.team6project.cavallo_mall.vo.CategoryVo;
import com.team6project.cavallo_mall.vo.RespVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import static com.team6project.cavallo_mall.constants.CavalloConstant.ROOT_PARENT_ID;

/**
 * description: Implementing class for category behaviour
 * author: Yuchen Bai
 * email: y.bai19@newcastle.ac.uk
 * date: 2021/2/18 16:15
 */
@Service
@Slf4j
@Transactional
public class CategoryServiceImpl implements CategoryService {

    @Resource
    private CategoryMapper categoryMapper;

    @Override
    public RespVo<List<CategoryVo>> findAllCategory() {
        List<Category> categories = categoryMapper.selectAll();
        List<CategoryVo> categoryVoList = new ArrayList<>();
        for (Category category : categories) {
            if (ROOT_PARENT_ID.equals(category.getParentId())) {
                CategoryVo categoryVo = categoryToCategoryVo(category);
                categoryVoList.add(categoryVo);
            }
        }
        categoryVoList.sort(Comparator.comparing(CategoryVo :: getSortedLevel).reversed());// Descend sorting
        // query sub categories
        findSubCategory(categories, categoryVoList);
        return RespVo.success(categoryVoList);
    }

    @Override
    public void findSubCategoryId(Integer id, Set<Integer> resultSet) {
        List<Category> categories = categoryMapper.selectAll();
        findSubCategoryId(id, resultSet, categories);
    }

    private void findSubCategoryId(Integer id, Set<Integer> resultSet, List<Category> categories) {
        for (Category category : categories) {
            if (category.getParentId().equals(id)) {
                Integer categoryId = category.getId();
                resultSet.add(categoryId);
                findSubCategoryId(categoryId, resultSet, categories);
            }
        }
    }

    private void findSubCategory(List<Category> categories, List<CategoryVo> categoryVoList) {
        for (CategoryVo categoryVo : categoryVoList) {
            List<CategoryVo> subCategoryVoList = new ArrayList<>();
            for (Category category : categories) {
                if (categoryVo.getId().equals(category.getParentId())) {
                    CategoryVo subCategoryVo = categoryToCategoryVo(category);
                    subCategoryVoList.add(subCategoryVo);
                }
                subCategoryVoList.sort(Comparator.comparing(CategoryVo :: getSortedLevel).reversed());// Descend sorting
                categoryVo.setSubCategories(subCategoryVoList);// set sub categoryList
                // recursively query sub categories
                findSubCategory(categories, subCategoryVoList);
            }
        }
    }

    private CategoryVo categoryToCategoryVo(Category category) {
        CategoryVo categoryVo = new CategoryVo();
        Objects.fillTargetObject(category, categoryVo);
        return categoryVo;
    }
}
