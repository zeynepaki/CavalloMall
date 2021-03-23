package com.team6project.cavallo_mall.service;

import com.github.pagehelper.PageInfo;
import com.team6project.cavallo_mall.model.DeliveryReqModel;
import com.team6project.cavallo_mall.vo.DeliveryVo;
import com.team6project.cavallo_mall.vo.RespVo;

/**
 * description: Interface for deliveries
 * author: Yuchen Bai
 * email: y.bai19@newcastle.ac.uk
 * date: 2021/2/23 1:41
 */
public interface DeliveryService {

    /**
     * adds delivery information
     * @param uid integer value of a unique user ID
     * @param deliveryReqModel DeliveryReqModel
     * @return a delivery query result indicating whether a delivery has been added to the database or not
     */
    RespVo<DeliveryVo> addDeliveryInfo(Integer uid, DeliveryReqModel deliveryReqModel);


    /**
     * deletes delivery information
     * @param uid integer value of a unique user ID
     * @param deliveryId Integer representing a unique deliveryID
     * @return a delivery query result indicating whether a delivery has been deleted from the database or not
     */
    RespVo deleteDeliveryInfo(Integer uid, Integer deliveryId);

    /**
     * updates delivery information
     * @param uid integer value of a unique user ID
     * @param deliveryId Integer representing a unique deliveryID
     * @param deliveryReqModel a DeliveryReqModel containing requirement constraints for deliveries
     * @return a delivery query result indicating whether a delivery has been updated in the database or not
     */
    RespVo updateDeliveryInfo(Integer uid, Integer deliveryId, DeliveryReqModel deliveryReqModel);

    /**
     * finds all delivery information
     * @param uid integer value of a unique user ID
     * @param pageNum integer value of the page where the delivery info is stored
     * @param pageSize integer value of how many delivery entries are displayed per page
     * @return a delivery query result indicating whether a delivery has been found in the database or not
     */
    RespVo<PageInfo> findAllDeliveryInfo(Integer uid, Integer pageNum, Integer pageSize);

}
