package com.team6project.cavallo_mall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.team6project.cavallo_mall.constants.RedisKey;
import com.team6project.cavallo_mall.dao.DeliveryMapper;
import com.team6project.cavallo_mall.dao.OrderDetailMapper;
import com.team6project.cavallo_mall.dao.OrderMapper;
import com.team6project.cavallo_mall.dao.ProductMapper;
import com.team6project.cavallo_mall.pojo.*;
import com.team6project.cavallo_mall.service.OrderService;
import com.team6project.cavallo_mall.util.Objects;
import com.team6project.cavallo_mall.util.SeqUtil;
import com.team6project.cavallo_mall.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static com.team6project.cavallo_mall.enums.OrderStatus.CANCELLED;
import static com.team6project.cavallo_mall.enums.OrderStatus.UNPAID;
import static com.team6project.cavallo_mall.enums.ProductStatus.IN_STOCK;
import static com.team6project.cavallo_mall.enums.RespStatusAndMsg.*;
import static com.team6project.cavallo_mall.enums.UserRole.ADMIN;

/**
 * description: Implementing class for order service behaviour
 * author: Yuchen Bai
 * email: y.bai19@newcastle.ac.uk
 * date: 2021/2/24 19:16
 */
@Service
@Slf4j
@Transactional
public class OrderServiceImpl implements OrderService {

    @Resource
    private OrderMapper orderMapper;

    @Resource
    private OrderDetailMapper orderDetailMapper;

    @Resource
    private DeliveryMapper deliveryMapper;

    @Resource
    private CartServiceImpl cartServiceImpl;

    @Resource
    private ProductMapper productMapper;

    @Resource
    private SeqUtil seqUtil;

    /**
     * Not implemented, here for future improvements
     * This method cancels Orders by user id and order number
     * @param uid integer representing a unique user ID
     * @param orderNo String value representing a unique order number
     * @return a query
     */
    @Override
    public RespVo cancelOrderByUidAndOrderNo(Integer uid, String orderNo) {
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order == null || ! uid.equals(order.getUserId())) return RespVo.error(ORDER_NOT_EXIST);
        // 未付款时才可取消
        if (! UNPAID.getCode().equals(order.getStatus())) return RespVo.error(ORDER_STATUS_INCORRECT);
        order.setStatus(CANCELLED.getCode());
        int count = orderMapper.updateByPrimaryKeySelective(order);
        if (count <= 0) return RespVo.error(SERVER_ERROR);

        return RespVo.success();
    }

    /**
     * This method is used to create an order
     * @param uid integer representing a unique user ID
     * @param deliverId
     * @return a orderVo query
     */
    @Override
    public RespVo<OrderVo> createOrder(Integer uid, Integer deliverId) {
        // 1. Deliver address verification
        Delivery delivery = deliveryMapper.selectByUidAndDeliveryId(uid, deliverId);
        if (delivery == null) return RespVo.error(DELIVERY_ADDRESS_NOT_EXIST);
        // 2. Get the shopping cart (selected product), verify whether the product exists
        // 2.1
        List<ShoppingCart> shoppingCartList = cartServiceImpl.traverseCart(uid)
                .stream().filter(ShoppingCart:: getProductSelected).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(shoppingCartList)) return RespVo.error(PRODUCT_OUT_OF_STOCK_OR_DELETED);
        // 2.2 Get the productIdSet in the cartList
        Set<Integer> productIdSet = shoppingCartList.stream().map(ShoppingCart :: getProductId).collect(Collectors.toSet());
        List<Product> productList = productMapper.selectByProductIdSet(productIdSet);
        // Product list structure Map, Key: productId, Value: product object
        Map<Integer, Product> productMap = productList.stream().collect(Collectors.toMap(Product :: getId, product -> product));
        // Another structure Map, Key： productId, Value: categoryId
        /*Map<Integer, Integer> productMap = productList.stream().collect(Collectors.toMap(Product::getId, Product::getCategoryId));*/
        List<OrderDetail> orderDetailList = new ArrayList<>();
        String orderNo = generateOrderNo();
        for (ShoppingCart shoppingCart : shoppingCartList) {
            Product product = productMap.get(shoppingCart.getProductId());
            // 2.3 whether the product exists
            if (product == null) return RespVo.error(GOODS_NOT_EXIST, "The goods is not exist");
            // 2.4 Is the product status available
            if (! IN_STOCK.getCode().equals(product.getStatus())) return RespVo.error(PRODUCT_OUT_OF_STOCK_OR_DELETED);
            // 2.5 whether the inventory is sufficient
            //if (productModel.getStock() < shoppingCartModel.getQuantity()) return RespVo.error(GOODS_STOCK_ERROR, "ProductName: " + productModel.getName());
            OrderDetail orderDetail = buildOrderDetail(uid, orderNo, shoppingCart.getQuantity(), product);
            orderDetailList.add(orderDetail);
            //productModel.setStock(productModel.getStock() - shoppingCartModel.getQuantity());
            product.setQuantitySold(product.getQuantitySold() + shoppingCart.getQuantity());
            int count = productMapper.updateByPrimaryKeySelective(product);
            if (count <= 0) return RespVo.error(SERVER_ERROR);
        }
        // 3. Calculate the total price, only the selected products
        // 4. Construct an order object and pass in the order details collection
        Order order = buildOrder(orderNo, uid, deliverId, orderDetailList);

        int orderCount = orderMapper.insertSelective(order);
        if (orderCount <= 0) return RespVo.error(SERVER_ERROR);

        int orderDetailCount = orderDetailMapper.batchInsert(orderDetailList);
        if (orderDetailCount <= 0) return RespVo.error(SERVER_ERROR);


        for (ShoppingCart shoppingCart : shoppingCartList) {
            cartServiceImpl.deleteCart(uid, shoppingCart.getProductId());
        }
        OrderVo orderVo = buildOrderVo(order, orderDetailList, delivery);
        return RespVo.success(orderVo);
    }

    /**
     * Not implemented, here for future improvements
     * This method is used to find orders by user id
     * @param uid integer representing a unique user ID
     * @param pageNum integer value of the page where the delivery info is stored
     * @param pageSize integer value of how many delivery entries are displayed per page
     * @return a pageInfo query
     */
    @Override
    public RespVo<PageInfo> findOrderByUid(Integer uid, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Order> orderList = orderMapper.selectOrderByUid(uid);
        Set<String> orderNoSet = orderList.stream().map(Order:: getOrderNo).collect(Collectors.toSet());
        List<OrderDetail> orderDetailList = orderDetailMapper.selectByOrderNoSet(orderNoSet);
        // list转为map的value，使用groupingBy, key为orderNo
        Map<String, List<OrderDetail>> orderDetailMap = orderDetailList.stream().collect(Collectors.groupingBy(OrderDetail :: getOrderNo));
        Set<Integer> deliveryIdSet = orderList.stream().map(Order :: getDeliveryId).collect(Collectors.toSet());
        List<Delivery> deliveryList = deliveryMapper.selectByIdSet(deliveryIdSet);
        Map<Integer, Delivery> deliveryMap = deliveryList.stream().collect(Collectors.toMap(Delivery:: getId, delivery -> delivery));
        List<OrderVo> orderVoList = new LinkedList<>();
        for (Order order : orderList) {
            OrderVo orderVo = buildOrderVo(order, orderDetailMap.get(order.getOrderNo()), deliveryMap.get(order.getDeliveryId()));
            orderVoList.add(orderVo);
        }
        PageInfo pageInfo = new PageInfo<>(orderList);
        pageInfo.setList(orderVoList);
        return RespVo.success(pageInfo);
    }

    /**
     * Not implemented, here for future improvements
     * This method is used to find order details of a given customer and order number
     * @param uid integer representing a unique user ID
     * @param orderNo String value representing a unique order number
     * @return a orderVo query
     */
    @Override
    public RespVo<OrderVo> findOrderDetail(Integer uid, String orderNo) {
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order == null || ! uid.equals(order.getUserId())) return RespVo.error(ORDER_NOT_EXIST);
        List<OrderDetail> orderDetailList = orderDetailMapper.selectByOrderNo(order.getOrderNo());
        Delivery delivery = deliveryMapper.selectByPrimaryKey(order.getDeliveryId());
        OrderVo orderVo = buildOrderVo(order, orderDetailList, delivery);
        return RespVo.success(orderVo);
    }

    /**
     * Not implemented, here for future improvements
     * This method is used to find how many orders have been placed by day
     * @param roleId could be admin 1 or customer 0
     * @return
     */
    @Override
    public RespVo<List<OrderQuantityOfDayVo>> findOrderQuantityByDay(Integer roleId) {
        if (! ADMIN.getCode().equals(roleId)) return RespVo.error(USER_ROLE_ERROR);
        List<OrderQuantityOfDayVo> orderQuantityOfDayVoList = orderMapper.countOrderByDay();
        if (CollectionUtils.isEmpty(orderQuantityOfDayVoList)) return RespVo.error(SERVER_ERROR);
        return RespVo.success(orderQuantityOfDayVoList);
    }

    /**
     * Not implemented, here for future improvements
     * This method counts how many orders have been placed by week
     * @param roleId could be admin 1 customer 0
     * @return orderQuantityOfWeekList query
     */
    @Override
    public RespVo<List<OrderQuantityOfWeekVo>> findOrderQuantityByWeek(Integer roleId) {
        if (! ADMIN.getCode().equals(roleId)) return RespVo.error(USER_ROLE_ERROR);
        List<OrderQuantityOfWeekVo> orderQuantityOfWeekList = orderMapper.countOrderByWeek();
        if (CollectionUtils.isEmpty(orderQuantityOfWeekList)) return RespVo.error(SERVER_ERROR);
        return RespVo.success(orderQuantityOfWeekList);
    }

    /**
     * Not implemented, here for future improvements
     * This method counts how many orders have been placed by month
     * @param roleId could be admin 1 customer 0
     * @return orderQuantityOfMonthList query
     */
    @Override
    public RespVo<List<OrderQuantityOfMonthVo>> findOrderQuantityByMonth(Integer roleId) {
        if (! ADMIN.getCode().equals(roleId)) return RespVo.error(USER_ROLE_ERROR);
        List<OrderQuantityOfMonthVo> orderQuantityOfMonthVoList = orderMapper.countOrderByMonth();
        if (CollectionUtils.isEmpty(orderQuantityOfMonthVoList)) return RespVo.error(SERVER_ERROR);
        return RespVo.success(orderQuantityOfMonthVoList);
    }

    /**
     * This method builds an OrderDetail from given parameters
     * @param uid user id
     * @param orderNo order number
     * @param quantity quantity
     * @param product product
     * @return orderDetail
     */
    private OrderDetail buildOrderDetail(Integer uid, String orderNo, Integer quantity, Product product) {
        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setUserId(uid);
        orderDetail.setOrderNo(orderNo);
        orderDetail.setProductId(product.getId());
        orderDetail.setProductImage(product.getMainImage());
        orderDetail.setProductName(product.getName());
        orderDetail.setQuantity(quantity);
        BigDecimal price = product.getPrice();
        orderDetail.setCurrentUnitPrice(price);
        orderDetail.setTotalPrice(price.multiply(BigDecimal.valueOf(quantity)));
        return orderDetail;
    }

    /**
     * This method is used to create an order with given parameters
     * @param orderNo order number
     * @param uid user id
     * @param deliveryId delivery id
     * @param orderDetailList a list of OrderDetail
     * @return order
     */
    private Order buildOrder(String orderNo, Integer uid, Integer deliveryId, List<OrderDetail> orderDetailList) {
        Order order = new Order();
        order.setOrderNo(orderNo);
        order.setDeliveryId(deliveryId);
        order.setUserId(uid);
        order.setStatus(UNPAID.getCode());
        BigDecimal totalAmount = orderDetailList.stream().map(OrderDetail::getTotalPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
        order.setPaymentAmount(totalAmount);
        return order;
    }

    /**
     * used to create an order number
     * @return order number
     */
    private String generateOrderNo() {
        return seqUtil.nextNum(RedisKey.ORDER_NO_IN_REDIS, 8);
    }

    /**
     * This method is used to create OrderVo
     * @param order order
     * @param orderDetailList a List of OrderDetail
     * @param delivery Delivery
     * @return orderVo
     */
    private OrderVo buildOrderVo(Order order, List<OrderDetail> orderDetailList, Delivery delivery) {
        OrderVo orderVo = new OrderVo();
        Objects.fillTargetObject(order,orderVo);

        List<OrderDetailVo> orderDetailVoList = orderDetailList.stream().map(e -> {
            OrderDetailVo orderDetailVo = new OrderDetailVo();
            Objects.fillTargetObject(e, orderDetailVo);
            return orderDetailVo;
        }).collect(Collectors.toList());
        if (delivery != null) {
            orderVo.setDeliveryId(delivery.getId());
            DeliverySpecVo deliverySpecVo = new DeliverySpecVo();
            Objects.fillTargetObject(delivery, deliverySpecVo);
            orderVo.setDeliverySpecVo(deliverySpecVo);
        }
        orderVo.setOrderDetailVoList(orderDetailVoList);
        return orderVo;
    }
}

