package com.team6project.cavallo_mall.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * description: cart product
 * author: Yuchen Bai
 * email: y.bai19@newcastle.ac.uk
 * date: 2021/2/21 18:24
 */
@Data
public class CartProductVo {
    private Integer productId;

    /*
    quantity purchased by customers
     */
    private Integer quantity;

    private String productName;

    private String productSubtitle;

    private String productMainImage;

    private BigDecimal productPrice;

    private Integer productStatus;

    /*
    productTotalPrice = quantity * productPrice
     */
    private BigDecimal productTotalPrice;

    // private Integer productStock;

    private Integer quantitySold;

    /*
    Whether the product is selected
     */
    private boolean productSelected;

    public CartProductVo(Integer productId, Integer quantity, String productName, String productSubtitle, String productMainImage, BigDecimal productPrice, Integer productStatus, BigDecimal productTotalPrice, Integer quantitySold, boolean productSelected) {
        this.productId = productId;
        this.quantity = quantity;
        this.productName = productName;
        this.productSubtitle = productSubtitle;
        this.productMainImage = productMainImage;
        this.productPrice = productPrice;
        this.productStatus = productStatus;
        this.productTotalPrice = productTotalPrice;
        this.quantitySold = quantitySold;
        this.productSelected = productSelected;
    }
}
