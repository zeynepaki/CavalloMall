package com.team6project.cavallo_mall.service;

import com.github.pagehelper.PageInfo;
import com.team6project.cavallo_mall.vo.*;

import java.util.List;

/**
 * description: Interface for orders
 * author: Yuchen Bai
 * email: y.bai19@newcastle.ac.uk
 * date: 2021/2/24 19:14
 */
public interface OrderService {

    /**
     * Not implemented, here for future improvements
     * Method that cancels an order by using a user ID (uid) and an order number (orderNo)
     * @param uid integer representing a unique user ID
     * @param orderNo String value representing a unique order number
     * @return order query result identifying whether the order has been cancelled or not
     */
    RespVo cancelOrderByUidAndOrderNo(Integer uid, String orderNo);

    /**
     * Method that creates an order given a user ID (uid) and a shipping ID
     * @param uid integer representing a unique user ID
     * @param shippingId integer representing a unique shipping ID
     * @return order query result identifying whether an order has been created or not
     */
    RespVo<OrderVo> createOrder(Integer uid, Integer shippingId);

    /**
     * Not implemented, here for future improvements
     * Method that finds an order given a user ID (uid)
     * @param uid integer representing a unique user ID
     * @param pageNum integer value of the page where the delivery info is stored
     * @param pageSize integer value of how many delivery entries are displayed per page
     * @return order query result identifying whether an order has been found by uid or not
     */
    RespVo<PageInfo> findOrderByUid(Integer uid, Integer pageNum, Integer pageSize);

    /**
     * Not implemented, here for future improvements
     * Method that finds an order's details given a user ID (uid) and an order number (orderNo)
     * @param uid integer representing a unique user ID
     * @param orderNo String value representing a unique order number
     * @return order query result identifying whether an OrderDetail has been found or not
     */
    RespVo<OrderVo> findOrderDetail(Integer uid, String orderNo);

    /**
     * Not implemented, here for future improvements
     * This method counts how many orders have been placed by day.
     * @param roleId could be admin 1 or customer 0
     * @return order query result
     */
    RespVo<List<OrderQuantityOfDayVo>> findOrderQuantityByDay(Integer roleId);

    /**
     * Not implemented, here for future improvements
     * The method counts how many orders have been placed by week
     * @param roleId could be admin 1 customer 0
     * @return order query result
     */
    RespVo<List<OrderQuantityOfWeekVo>> findOrderQuantityByWeek(Integer roleId);

    /**
     * Not implemented, here for future improvements
     * The method counts how many orders have been placed by month
     * @param roleId could be admin 1 customer 0
     * @return order query result
     */
    RespVo<List<OrderQuantityOfMonthVo>> findOrderQuantityByMonth(Integer roleId);
}
