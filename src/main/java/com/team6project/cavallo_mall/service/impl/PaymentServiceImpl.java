package com.team6project.cavallo_mall.service.impl;

import com.team6project.cavallo_mall.client.restFul.PaymentClient;
import com.team6project.cavallo_mall.dao.OrderMapper;
import com.team6project.cavallo_mall.dao.PaymentMapper;
import com.team6project.cavallo_mall.dao.UserMapper;
import com.team6project.cavallo_mall.enums.OrderStatus;
import com.team6project.cavallo_mall.enums.PaymentStatus;
import com.team6project.cavallo_mall.model.HorsePayRespModel;
import com.team6project.cavallo_mall.model.HorsePayRespSuccessModel;
import com.team6project.cavallo_mall.pojo.Order;
import com.team6project.cavallo_mall.pojo.Payment;
import com.team6project.cavallo_mall.pojo.User;
import com.team6project.cavallo_mall.service.PaymentService;
import com.team6project.cavallo_mall.util.Objects;
import com.team6project.cavallo_mall.vo.HorsePayVo;
import com.team6project.cavallo_mall.vo.PaymentVo;
import com.team6project.cavallo_mall.vo.RespVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;


import static com.team6project.cavallo_mall.enums.OrderStatus.UNPAID;
import static com.team6project.cavallo_mall.enums.RespStatusAndMsg.*;

/**
 * description: Implementing class for payment service behaviour
 * author: Yuchen Bai
 * email: y.bai19@newcastle.ac.uk
 * date: 2021/3/7 23:37
 */
@Service
@Transactional
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    @Resource
    private OrderMapper orderMapper;

    @Resource
    private PaymentMapper paymentMapper;

    @Resource
    private PaymentClient paymentClient;

    @Resource
    private UserMapper userMapper;

    @Override
    public RespVo<PaymentVo> createPayment(Integer uid, String orderNo) {
        Order order = orderMapper.selectByOrderNoAndUid(uid, orderNo);
        if (order == null) return RespVo.error(ORDER_NOT_EXIST);
        if (! UNPAID.getCode().equals(order.getStatus())) return RespVo.error(ORDER_STATUS_INCORRECT);
        User user = userMapper.selectByPrimaryKey(uid);
        if (user == null) return RespVo.error(USER_NOT_EXIST);
        HorsePayVo horsePayVo = buildHorsePayVo(order, user.getUsername());
        HorsePayRespModel horsePayRespModel = paymentClient.post(horsePayVo);
        Payment payment = buildPayment(horsePayRespModel, order, user.getUsername());
        if (payment == null) return RespVo.error(SERVER_ERROR);
        int countPayment = paymentMapper.insertSelective(payment);
        if (countPayment <= 0) return RespVo.error(SERVER_ERROR);
        order.setStatus(OrderStatus.PAID.getCode());
        order.setPaymentTime(new Timestamp(new Date().getTime()));
        order.setSendTime(new Timestamp(new Date().getTime()));
        int countOrder = orderMapper.updateByPrimaryKeySelective(order);
        if (countOrder <= 0) return RespVo.error(SERVER_ERROR);
        return RespVo.success(buildPaymentVo(payment));
    }

    private HorsePayVo buildHorsePayVo(Order order, String username) {
        HorsePayVo horsePayVo = new HorsePayVo();
        horsePayVo.setForcePaymentSatusReturnType(true);
        horsePayVo.setTimeZone("GMT");
        horsePayVo.setStoreID("Team06");
        horsePayVo.setCustomerID(username);
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        horsePayVo.setDate(currentDate.format(dateTimeFormatter));
        LocalTime currentTime = LocalTime.now();
        DateTimeFormatter dateTimeFormatter1 = DateTimeFormatter.ofPattern("HH:mm");
        horsePayVo.setTime(currentTime.format(dateTimeFormatter1));
        horsePayVo.setCurrencyCode("GBP");
        BigDecimal paymentAmount = order.getPaymentAmount();
        horsePayVo.setTransactionAmount(paymentAmount.floatValue());
        return horsePayVo;
    }

    private Payment buildPayment(HorsePayRespModel horsePayRespModel, Order order, String username) {
        if (horsePayRespModel == null) return null;
        HorsePayRespSuccessModel paymentSuccess = horsePayRespModel.getPaymetSuccess();
        if (! paymentSuccess.getStatus()) return null;
        Payment payment = new Payment();
        payment.setOrderNo(order.getOrderNo());
        payment.setForceType(0);
        payment.setCurrencyCode(horsePayRespModel.getCurrencyCode());
        payment.setPaymentAmount(order.getPaymentAmount());
        payment.setUserId(order.getUserId());
        payment.setUsername(username);
        payment.setPaymentStatus(PaymentStatus.PAID.getCode());
        return payment;
    }

    private PaymentVo buildPaymentVo(Payment payment) {
        PaymentVo paymentVo = new PaymentVo();
        Objects.fillTargetObject(payment, paymentVo);
        return paymentVo;
    }
}
