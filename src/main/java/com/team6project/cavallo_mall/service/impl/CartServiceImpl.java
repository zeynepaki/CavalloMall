package com.team6project.cavallo_mall.service.impl;

import com.alibaba.fastjson.JSON;
import com.team6project.cavallo_mall.dao.ProductMapper;
import com.team6project.cavallo_mall.model.CartAddedReqModel;
import com.team6project.cavallo_mall.model.CartUpdateReqModel;
import com.team6project.cavallo_mall.pojo.Product;
import com.team6project.cavallo_mall.pojo.ShoppingCart;
import com.team6project.cavallo_mall.service.CartService;
import com.team6project.cavallo_mall.vo.CartProductVo;
import com.team6project.cavallo_mall.vo.CartVo;
import com.team6project.cavallo_mall.vo.RespVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.team6project.cavallo_mall.constants.RedisKey.CART_KEY;
import static com.team6project.cavallo_mall.enums.ProductStatus.IN_STOCK;
import static com.team6project.cavallo_mall.enums.RespStatusAndMsg.*;

/**
 * description: Implementing class for shopping cart behaviour
 * author: Yuchen Bai
 * email: y.bai19@newcastle.ac.uk
 * date: 2021/2/21 20:21
 */
@Service
@Transactional
@Slf4j
public class CartServiceImpl implements CartService {

    @Resource
    private ProductMapper productMapper;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * A method that adds goods to the current user's cart.
     * 1) Validity check for whether the goods exist and whether the goods are available
     * 2) HashOperations of name hashOperations created with a Key of type String, HashKey of type String,
     * and Value of type String in JSON format
     * 3) String named redisBk formed from CART_KEY and uid
     * 4) String named redisSk formed from productID
     * 5) shoppingCart declared but not initialised with parameters of productID and quantity
     * 6) If redisBk and redisSk are NOT assigned to an object within hashOperations, a new shopping cart is initialised
     * with the given productID and uid; else, the shopping cart is initialised by parsing the String value passed
     * from hashOperations in JSON format
     * 7) Using  redisBk and redisSk and the updated/created information of the shopping cart object, the shopping cart is
     * put into hashOperations in JSON format
     * @param form CartAddedReqModel to which good are to be added
     * @param uid integer value of a unique user ID
     * @return a cart query result using findAll() method
     */
    @Override
    public RespVo<CartVo> addGoods(CartAddedReqModel form, Integer uid) {
        Product product = productMapper.selectByPrimaryKey(form.getProductId());
        // 1. whether the goods exist.
        if (product == null) {
            return RespVo.error(GOODS_NOT_EXIST);
        }
        // 2. whether the status of the goods is available.
        if (! IN_STOCK.getCode().equals(product.getStatus())) {
            return RespVo.error(PRODUCT_OUT_OF_STOCK_OR_DELETED);
        }
        // 3. whether the goods stock is sufficient.
        /*if (productModel.getStock() <= 0) {
            return RespVo.error(GOODS_STOCK_ERROR);
        }*/
        Integer quantity = 1;
        HashOperations<String, String, String> hashOperations = stringRedisTemplate.opsForHash();
        String redisBk = String.format(CART_KEY, uid);// Big key
        String redisSk = String.valueOf(product.getId());// Small key

        ShoppingCart shoppingCart;
        // in order to update quantity, first query the cart from redis
        String cartJson = hashOperations.get(redisBk, redisSk);
        if (Strings.isEmpty(cartJson)) {
            // create new cart object
            shoppingCart = new ShoppingCart(product.getId(), quantity, form.getSelected());
        } else {
            // update count for redis
            shoppingCart = JSON.parseObject(cartJson, ShoppingCart.class);
            shoppingCart.setQuantity(shoppingCart.getQuantity() + quantity);// update count
        }
        hashOperations.put(redisBk, redisSk, JSON.toJSONString(shoppingCart));
        return findAll(uid);// return cart query result
    }

    /**
     * Method that finds the sum total of prices for all products in the current user's cart
     * 1) HashOperations named hashOperations is created with a String Key, String HashKey, and a String Value
     * which is in JSON format
     * 2) String named redisBk formed from CART_KEY and uid
     * 3) Map (String, String)
     * @param uid integer value of a unique user ID
     * @return a cart query result
     */
    @Override
    public RespVo<CartVo> findAll(Integer uid) {
        HashOperations<String, String, String> hashOperations = stringRedisTemplate.opsForHash();
        String redisBk = String.format(CART_KEY, uid);
        Map<String, String> entries = hashOperations.entries(redisBk);

        List<CartProductVo> cartProductVoList = new ArrayList<>();

        boolean isSelected = true;
        int totalQuantity = 0;
        BigDecimal cartTotalPrice = BigDecimal.ZERO;
        for (Map.Entry<String, String> entry : entries.entrySet()) {
            Integer productId = Integer.valueOf(entry.getKey());
            // Deserialization to cart object
            ShoppingCart shoppingCart = JSON.parseObject(entry.getValue(), ShoppingCart.class);
            // TODO use 'in' to substitute
            Product product = productMapper.selectByPrimaryKey(productId);
            if (product != null) {
                // calculate total price
                BigDecimal totalPrice = product.getPrice().multiply(BigDecimal.valueOf(shoppingCart.getQuantity()));
                CartProductVo cartProductVo = new CartProductVo(productId,
                        shoppingCart.getQuantity(),
                        product.getName(),
                        product.getSubtitle(),
                        product.getMainImage(),
                        product.getPrice(),
                        product.getStatus(),
                        totalPrice,
                        product.getQuantitySold(),
                        shoppingCart.getProductSelected()
                        );
                cartProductVoList.add(cartProductVo);
                if (! shoppingCart.getProductSelected()) isSelected = false;

                if (shoppingCart.getProductSelected()) cartTotalPrice = cartTotalPrice.add(cartProductVo.getProductTotalPrice());// Accumulate
            }
            totalQuantity += shoppingCart.getQuantity();// quantity stored in redis

        }
        CartVo cartVo = new CartVo();
        cartVo.setSelectedAll(isSelected);
        cartVo.setCartTotalQuantity(totalQuantity);
        cartVo.setCartTotalPrice(cartTotalPrice);
        cartVo.setCartProductVoList(cartProductVoList);
        return RespVo.success(cartVo);
    }

    /**
     *
     * @param uid integer value of a unique user ID
     * @param productId integer value of a product's ID
     * @param form
     * @return a cart query result using findAll() method
     */
    @Override
    public RespVo<CartVo> updateCart(Integer uid, Integer productId, CartUpdateReqModel form) {
        HashOperations<String, String, String> hashOperations = stringRedisTemplate.opsForHash();
        String[] keyArray = redisKeyOperation(uid, productId);
        String cartJson = hashOperations.get(keyArray[0], keyArray[1]);
        if (Strings.isEmpty(cartJson)) return RespVo.error(CART_GOODS_NOT_EXIST);
        ShoppingCart shoppingCart = JSON.parseObject(cartJson, ShoppingCart.class);
        if (form.getQuantity() != null && form.getQuantity() >= 0) shoppingCart.setQuantity(form.getQuantity());

        if (form.getSelected() != null) shoppingCart.setProductSelected(form.getSelected());

        hashOperations.put(keyArray[0], keyArray[1], JSON.toJSONString(shoppingCart));
        return findAll(uid);
    }

    /**
     *
      * @param uid integer value of a unique user ID
     * @param productId
     * @return a cart query result using findAll() method
     */
    @Override
    public RespVo<CartVo> deleteCart(Integer uid, Integer productId) {
        HashOperations<String, String, String> hashOperations = stringRedisTemplate.opsForHash();
        String[] keyArray = redisKeyOperation(uid, productId);
        String cartJson = hashOperations.get(keyArray[0], keyArray[1]);
        if (Strings.isEmpty(cartJson)) {
            return RespVo.error(CART_GOODS_NOT_EXIST);
        }
        hashOperations.delete(keyArray[0], keyArray[1]);
        return findAll(uid);
    }

    /**
     *
     * @param uid integer value of a unique user ID
     * @return a cart query result using findAll() method
     */
    @Override
    public RespVo<CartVo> selectAll(Integer uid) {
        HashOperations<String, String, String> hashOperations = stringRedisTemplate.opsForHash();
        String redisBk = String.format(CART_KEY, uid);
        List<ShoppingCart> shoppingCartList = traverseCart(uid);
        for (ShoppingCart shoppingCart : shoppingCartList) {
            shoppingCart.setProductSelected(true);
            String redisSk = String.valueOf(shoppingCart.getProductId());
            // update redis data
            hashOperations.put(redisBk, redisSk, JSON.toJSONString(shoppingCart));
        }
        return findAll(uid);
    }

    /**
     *
     * @param uid integer value of a unique user ID
     * @return a cart query result using findAll() method
     */
    @Override
    public RespVo<CartVo> unSelectedAll(Integer uid) {
        HashOperations<String, String, String> hashOperations = stringRedisTemplate.opsForHash();
        String redisBk = String.format(CART_KEY, uid);
        List<ShoppingCart> shoppingCartList = traverseCart(uid);
        for (ShoppingCart shoppingCart : shoppingCartList) {
            shoppingCart.setProductSelected(false);
            String redisSk = String.valueOf(shoppingCart.getProductId());
            // update redis data
            hashOperations.put(redisBk, redisSk, JSON.toJSONString(shoppingCart));
        }
        return findAll(uid);
    }

    /**
     *
     * @param uid integer value of a unique user ID
     * @return a cart query result using findAll() method
     */
    @Override
    public RespVo<Integer> sumQuantity(Integer uid) {
        // Accumulate
        Integer sum = traverseCart(uid).stream().map(ShoppingCart :: getQuantity).reduce(0, Integer::sum);// start from 0
        return RespVo.success(sum);
    }

    /**
     *
     * @param uid
     * @param productId
     * @return a cart query result using findAll() method
     */
    private String[] redisKeyOperation(Integer uid, Integer productId) {
        String redisBk = String.format(CART_KEY, uid);
        String redisSk = String.valueOf(productId);
        return new String[]{redisBk, redisSk};
    }

    /**
     *
     * @param uid
     * @return a cart query result using findAll() method
     */
    List<ShoppingCart> traverseCart(Integer uid) {
        HashOperations<String, String, String> hashOperations = stringRedisTemplate.opsForHash();
        String redisBk = String.format(CART_KEY, uid);
        Map<String, String> entries = hashOperations.entries(redisBk);
        List<ShoppingCart> shoppingCartList = new ArrayList<>();
        for (Map.Entry<String, String> entry : entries.entrySet()) {
            shoppingCartList.add(JSON.parseObject(entry.getValue(), ShoppingCart.class));
        }
        return shoppingCartList;
    }
}
