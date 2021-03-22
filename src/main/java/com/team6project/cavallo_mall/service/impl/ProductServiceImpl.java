package com.team6project.cavallo_mall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.team6project.cavallo_mall.dao.ProductMapper;
import com.team6project.cavallo_mall.pojo.Product;
import com.team6project.cavallo_mall.service.CategoryService;
import com.team6project.cavallo_mall.service.ProductService;
import com.team6project.cavallo_mall.util.Objects;
import com.team6project.cavallo_mall.vo.ProductDetailsVo;
import com.team6project.cavallo_mall.vo.ProductSalesStatisticVo;
import com.team6project.cavallo_mall.vo.ProductVo;
import com.team6project.cavallo_mall.vo.RespVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.team6project.cavallo_mall.enums.ProductStatus.DELETED;
import static com.team6project.cavallo_mall.enums.ProductStatus.OUT_OF_STOCK;
import static com.team6project.cavallo_mall.enums.RespStatusAndMsg.*;
import static com.team6project.cavallo_mall.enums.UserRole.ADMIN;

/**
 * description: Implementing class for product service behaviour
 * author: Yuchen Bai
 * email: y.bai19@newcastle.ac.uk
 * date: 2021/2/19 0:33
 */
@Service
@Slf4j
public class ProductServiceImpl implements ProductService {

    @Resource
    private ProductMapper productMapper;

    @Resource
    private CategoryService categoryService;

    @Override
    public RespVo<PageInfo> productList(Integer categoryId, Integer pageNum, Integer pageSize) {
        Set<Integer> categoryIdSet = new HashSet<>();
        if (categoryId != null) {
            categoryService.findSubCategoryId(categoryId, categoryIdSet);
            categoryIdSet.add(categoryId);
        }
        PageHelper.startPage(pageNum, pageSize);
        List<Product> productList = productMapper.selectByCategoryIdSet(categoryIdSet);
        List<ProductVo> productVoList = productList.stream().map(e -> {
                    ProductVo productVo = new ProductVo();
                    Objects.fillTargetObject(e, productVo);
                    return productVo;
                }).collect(Collectors.toList());
        PageInfo pageInfo = new PageInfo<>(productList);
        pageInfo.setList(productVoList);
        return RespVo.success(pageInfo);
    }

    @Override
    public RespVo<ProductDetailsVo> productDetail(Integer productId) {
        Product product = productMapper.selectByPrimaryKey(productId);
        if (OUT_OF_STOCK.getCode().equals(product.getStatus()) || DELETED.getCode().equals(product.getStatus()))
            return RespVo.error(PRODUCT_OUT_OF_STOCK_OR_DELETED);
        ProductDetailsVo productDetailsVo = new ProductDetailsVo();
        Objects.fillTargetObject(product, productDetailsVo);
        productDetailsVo.setQuantitySold(product.getQuantitySold());
        return RespVo.success(productDetailsVo);
    }

    @Override
    public RespVo<ProductSalesStatisticVo> findProductSalesQuantity(Integer categoryId, Integer userRole) {
        if (! ADMIN.getCode().equals(userRole)) return RespVo.error(USER_ROLE_ERROR);
        ProductSalesStatisticVo productSalesQuantity = productMapper.findProductSalesQuantity(categoryId);
        if (productSalesQuantity == null) return RespVo.error(SERVER_ERROR);
        return RespVo.success(productSalesQuantity);

    }

    @Override
    public RespVo<List<ProductSalesStatisticVo>> findProductSalesQuantityList(Integer userRole) {
        if (! ADMIN.getCode().equals(userRole)) return RespVo.error(USER_ROLE_ERROR);
        List<ProductSalesStatisticVo> productSalesQuantityList = productMapper.findProductSalesQuantityList();
        if (CollectionUtils.isEmpty(productSalesQuantityList)) return RespVo.error(SERVER_ERROR);
        return RespVo.success(productSalesQuantityList);
    }
}
