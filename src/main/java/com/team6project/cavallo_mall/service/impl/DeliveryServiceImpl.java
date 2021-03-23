package com.team6project.cavallo_mall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.team6project.cavallo_mall.dao.DeliveryMapper;
import com.team6project.cavallo_mall.model.DeliveryReqModel;
import com.team6project.cavallo_mall.pojo.Delivery;
import com.team6project.cavallo_mall.service.DeliveryService;
import com.team6project.cavallo_mall.util.Objects;
import com.team6project.cavallo_mall.vo.DeliveryVo;
import com.team6project.cavallo_mall.vo.RespVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import static com.team6project.cavallo_mall.enums.RespStatusAndMsg.DELIVERY_INFO_DELETE_FAIL;
import static com.team6project.cavallo_mall.enums.RespStatusAndMsg.SERVER_ERROR;

/**
 * description: Implementing class for delivery service behaviour
 * author: Yuchen Bai
 * email: y.bai19@newcastle.ac.uk
 * date: 2021/2/23 2:22
 */
@Service
@Transactional
@Slf4j
public class DeliveryServiceImpl implements DeliveryService {

    @Resource
    private DeliveryMapper deliveryMapper;

    /**
     *This method is used to create a delivery and assign it to a user.
     * @param uid user id
     * @param deliveryReqModel DeliveryReqModel
     * @return a deliveryVo query
     */
    @Override
    public RespVo<DeliveryVo> addDeliveryInfo(Integer uid, DeliveryReqModel deliveryReqModel) {
        Delivery delivery = new Delivery();
        Objects.fillTargetObject(deliveryReqModel, delivery);
        delivery.setUserId(uid);
        delivery.setCreateTime(new Timestamp(new Date().getTime()));
        int count = deliveryMapper.insertSelective(delivery);
        if (count <= 0) return RespVo.error(SERVER_ERROR);
        DeliveryVo deliveryVo = new DeliveryVo();
        deliveryVo.setDeliveryId(delivery.getId());
        return RespVo.success(deliveryVo);
    }

    /**
     * This method is used to delete delivery information of a given user and delivery id
     * @param uid user id
     * @param deliverId delivery id
     * @return a query
     */
    @Override
    public RespVo deleteDeliveryInfo(Integer uid, Integer deliverId) {
        int count = deliveryMapper.deleteByIdAndUid(uid, deliverId);
        if (count <= 0) return RespVo.error(DELIVERY_INFO_DELETE_FAIL);
        return RespVo.success();
    }

    /**
     * This method updates delivery information of given user and delivery
     * @param uid user id
     * @param deliverId delivery id
     * @param deliveryReqModel
     * @return
     */
    @Override
    public RespVo updateDeliveryInfo(Integer uid, Integer deliverId, DeliveryReqModel deliveryReqModel) {
        Delivery delivery = new Delivery();
        Objects.fillTargetObject(deliveryReqModel, delivery);
        delivery.setUserId(uid);
        delivery.setId(deliverId);
        int count = deliveryMapper.updateByPrimaryKeySelective(delivery);
        if (count <= 0) return RespVo.error(SERVER_ERROR);
        return RespVo.success();
    }

    /**
     *
     * @param uid
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public RespVo<PageInfo> findAllDeliveryInfo(Integer uid, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Delivery> shippingList = deliveryMapper.selectByUid(uid);
        PageInfo pageInfo = new PageInfo<>(shippingList);
        return RespVo.success(pageInfo);
    }
}
