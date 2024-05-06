package org.csu.api.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderVO {

    private BigInteger orderNo;

    private Integer userId;

    private BigDecimal paymentPrice;

    private Integer paymentType;

    private Integer postage;

    private Integer status;

    private LocalDateTime paymentTime;

    private LocalDateTime sendTime;

    private LocalDateTime endTime;

    private LocalDateTime closeTime;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private AddressVO addressVO;
    private List<OrderItemVO> orderItemVOList;
    private String imageServer;
}
