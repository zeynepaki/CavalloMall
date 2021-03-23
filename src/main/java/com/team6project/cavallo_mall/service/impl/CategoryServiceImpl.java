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

    /**
     *This method finds all the Categories present in the database by,
     * 1) A list of Categories called categories is initialised by selecting all categories
     * from the category mapper.
     * 2) A new list of CategoryVo called categoryVoList is created.
     * 3) For each category in categories, if the ROOT_PARENT_ID is equal to the category parent ID,
     * CategoryVO is initialised as the category.
     * 4) CategoryVOLIST is sorted in descending order
     * 5) subcategories are queried.
     * @return category query  result
     */

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

    /**
     *This method is used to invoke the other findSubCategoryID method without the categories List.
     * @param id int categoryID
     * @param resultSet set of results of Integer type.
     */

    @Override
    public void findSubCategoryId(Integer id, Set<Integer> resultSet) {
        List<Category> categories = categoryMapper.selectAll();
        findSubCategoryId(id, resultSet, categories);
    }

    /**
     * This method finds all the sub-categories of the given category id. The category sub-tree is searched
     * recursively and a shallow set of all sub categories are collected in the given result set.
     *
     * @param id int categoryID
     * @param resultSet set of results of Integer type
     * @param categories List of Categories
     */

    private void findSubCategoryId(Integer id, Set<Integer> resultSet, List<Category> categories) {
        for (Category category : categories) {
            if (category.getParentId().equals(id)) {
                Integer categoryId = category.getId();
                resultSet.add(categoryId);
                findSubCategoryId(categoryId, resultSet, categories);
            }
        }
    }

    /**
     * 1) For each categoryVo in the given categoryVoList, a new ArrayList of CategoryVo type called subCategoryVoList.
     * 2) For each category in categories, if the id of categoryVo is equal to the parent id of the category,
     * category is converted into categoryVo and the categoryVo created is added to the subCategoryVoList.
     * 3) subCategoryVoList is sorted in descending order
     * 4) subCategoryList is set to subCategoryVoList
     * 5) sub categories are recursively queried.
     * @param categories a List of Categories
     * @param categoryVoList a List of CategoryVos
     */

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

    /**
     * This method converts Category objects to CategoryVo objects.
     * @param category Category object to be converted
     * @return CategoryVo object
     */

    private CategoryVo categoryToCategoryVo(Category category) {
        CategoryVo categoryVo = new CategoryVo();
        Objects.fillTargetObject(category, categoryVo);
        return categoryVo;
    }
}
