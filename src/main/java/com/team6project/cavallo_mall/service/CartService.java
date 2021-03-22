package com.team6project.cavallo_mall.service;

import com.team6project.cavallo_mall.model.CartAddedReqModel;
import com.team6project.cavallo_mall.model.CartUpdateReqModel;
import com.team6project.cavallo_mall.vo.CartVo;
import com.team6project.cavallo_mall.vo.RespVo;

/**
 * description:
 * author: Yuchen Bai
 * email: y.bai19@newcastle.ac.uk
 * date: 2021/2/21 20:21
 */
public interface CartService {

    /**
     *This method adds goods to the current user's cart using the findAll() method
     * @param form CartAddedReqModel to which good are to be added
     * @param uid integer value of a unique user ID
     * @return a cart query result using findAll() method
     */
    RespVo<CartVo> addGoods(CartAddedReqModel form, Integer uid);

    /**
     * This method finds the price of all objects held in the current user's cart
     * @param uid integer value of a unique user ID
     * @return a cart query result
     */
    RespVo<CartVo> findAll(Integer uid);

    /**
     * This method updates the quantity of products in the current user's cart
     * @param uid integer value of a unique user ID
     * @param productId integer value of a product's ID
     * @param form
     * @return a cart query result using findAll() method
     */
    RespVo<CartVo> updateCart(Integer uid, Integer productId, CartUpdateReqModel form);

    /**
     * This method deletes a cart from the database
     * @param uid integer value of a unique user ID
     * @param productId
     * @return a cart query result using findAll() method
     */
    RespVo<CartVo> deleteCart(Integer uid, Integer productId);

    /**
     * This UNIMPLEMENTED method selects all products in a current user's cart
     * @param uid integer value of a unique user ID
     * @return a cart query result using findAll() method
     */
    RespVo<CartVo> selectAll(Integer uid);

    /**
     * This UNIMPLEMENTED method unselects all products in a current user's cart
     * @param uid integer value of a unique user ID
     * @return a cart query result using findAll() method
     */
    RespVo<CartVo> unSelectedAll(Integer uid);

    /**
     * This method finds the sum quantity of objects in the current user's cart
     * @param uid integer value of a unique user ID
     * @return integer of total sum of products in cart
     */
    RespVo<Integer> sumQuantity(Integer uid);
}
