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

    /**
     * Method that provides a product list as a page
     * @param categoryId integer representing the categoryID of a product
     * @param pageNum integer representing the page number
     * @param pageSize integer representing the number of entries per page
     * @return a product service query result indicating whether the product list has been successfully returned
     */
    RespVo<PageInfo> productList(Integer categoryId, Integer pageNum, Integer pageSize);

    /**
     * Not implemented, here for future improvements
     * Method that provides product details
     * @param productId integer representing a unique product ID
     * @return a product service query result indicating whether the product's details have been successfully returned
     */
    RespVo<ProductDetailsVo> productDetail(Integer productId);

    /**
     * Not implemented, here for future improvements
     * Method that finds a product's sales quantity
     * Only permits a successful query where the user is an admin (userRole == 1)
     * @param categoryId integer that represents a product's category ID
     * @param userRole integer that represents the user's role (0 for customer, 1 for admin)
     * @return a product service query result indicating whether the product's sales quantity has been successfully returned
     */
    RespVo<ProductSalesStatisticVo> findProductSalesQuantity(Integer categoryId, Integer userRole);

    /**
     * Not implemented, here for future improvements
     * Method that finds product sales quantities and adds them to a List
     * Only permits a successful query where the user is an admin (userRole == 1)
     * @param userRole integer that represents the user's role (0 for customer, 1 for admin)
     * @return a product service query result indicating whether the list of product sales statistics has been successfully returned
     */
    RespVo<List<ProductSalesStatisticVo>> findProductSalesQuantityList(Integer userRole);


}
