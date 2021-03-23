package com.team6project.cavallo_mall.service;

import com.github.pagehelper.PageInfo;
import com.team6project.cavallo_mall.vo.ProductDetailsVo;
import com.team6project.cavallo_mall.vo.ProductSalesStatisticVo;
import com.team6project.cavallo_mall.vo.RespVo;

import java.util.List;

/**
 * description: Interface for products and their sales
 * author: Yuchen Bai
 * email: y.bai19@newcastle.ac.uk
 * date: 2021/2/19 0:31
 */
public interface ProductService {

    RespVo<PageInfo> productList(Integer categoryId, Integer pageNum, Integer pageSize);

    RespVo<ProductDetailsVo> productDetail(Integer productId);

    RespVo<ProductSalesStatisticVo> findProductSalesQuantity(Integer categoryId, Integer userRole);

    RespVo<List<ProductSalesStatisticVo>> findProductSalesQuantityList(Integer userRole);


}
