package org.csu.api.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CartVO {
    private List<CartItemVO> cartItemVOList;
    private BigDecimal cartTotalPrice;
    private Boolean allSelected;
    private String productImageServer;
}
