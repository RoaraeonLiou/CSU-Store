package org.csu.api.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderItemVO {
    private Integer productId;

    private String productName;

    private String productImage;

    private BigDecimal currentPrice;

    private Integer quantity;

    private BigDecimal totalPrice;
}
