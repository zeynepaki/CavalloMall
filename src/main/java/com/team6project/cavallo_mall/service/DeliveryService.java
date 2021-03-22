package com.team6project.cavallo_mall.service;

import com.github.pagehelper.PageInfo;
import com.team6project.cavallo_mall.model.DeliveryReqModel;
import com.team6project.cavallo_mall.vo.DeliveryVo;
import com.team6project.cavallo_mall.vo.RespVo;

/**
 * description:
 * author: Yuchen Bai
 * email: y.bai19@newcastle.ac.uk
 * date: 2021/2/23 1:41
 */
public interface DeliveryService {

    /**
     * adds delivery information
     * @param uid integer value of a unique user ID
     * @param deliveryReqModel DeliveryReqModel
     * @return a delivery query result
     */
    RespVo<DeliveryVo> addDeliveryInfo(Integer uid, DeliveryReqModel deliveryReqModel);


    /**
     * deletes delivery information
     * @param uid integer value of a unique user ID
     * @param deliveryId
     * @return
     */
    RespVo deleteDeliveryInfo(Integer uid, Integer deliveryId);

    /**
     * updates delivery information
     * @param uid integer value of a unique user ID
     * @param deliveryId
     * @param deliveryReqModel
     * @return
     */
    RespVo updateDeliveryInfo(Integer uid, Integer deliveryId, DeliveryReqModel deliveryReqModel);

    /**
     * finds all delivery information
     * @param uid integer value of a unique user ID
     * @param pageNum
     * @param pageSize
     * @return
     */
    RespVo<PageInfo> findAllDeliveryInfo(Integer uid, Integer pageNum, Integer pageSize);

}
