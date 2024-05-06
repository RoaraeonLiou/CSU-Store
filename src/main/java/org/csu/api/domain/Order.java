package org.csu.api.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;

@Data
@TableName("mystore_order")
public class Order {
    @TableId(type = IdType.AUTO)
    private Integer id;
    @TableField(value = "order_no")
    private BigInteger orderNo;
    @TableField(value = "user_id")
    private Integer userId;
    @TableField(value = "address_id")
    private Integer addressId;
    @TableField(value = "payment_price")
    private BigDecimal paymentPrice;
    @TableField(value = "payment_type")
    private Integer paymentType;
    @TableField(value = "postage")
    private Integer postage;
    @TableField(value = "status")
    private Integer status;
    @TableField(value = "payment_time")
    private LocalDateTime paymentTime;
    @TableField(value = "send_time")
    private LocalDateTime sendTime;
    @TableField(value = "end_time")
    private LocalDateTime endTime;
    @TableField(value = "close_time")
    private LocalDateTime closeTime;
    @TableField(value = "create_time")
    private LocalDateTime createTime;
    @TableField(value = "update_time")
    private LocalDateTime updateTime;
}
