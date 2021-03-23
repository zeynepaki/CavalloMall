package com.team6project.cavallo_mall.service;

import com.team6project.cavallo_mall.vo.PaymentVo;
import com.team6project.cavallo_mall.vo.RespVo;

/**
 * description: Interface for payments
 * author: Yuchen Bai
 * email: y.bai19@newcastle.ac.uk
 * date: 2021/3/7 23:37
 */
public interface PaymentService {

    /**
     *  Method that creates a payment for an order using a user ID (uid) and an orderNo
     * @param uid integer that represents a unique user ID
     * @param orderNo String that represents a unique orderNo
     * @return a payment query indicating whether a payment has been created successfully or not
     */
    RespVo<PaymentVo> createPayment(Integer uid, String orderNo);
}
