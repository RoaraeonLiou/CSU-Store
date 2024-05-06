package org.csu.api.service.impl;

import cn.hutool.json.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.servlet.http.HttpServletResponse;
import org.csu.api.common.CONSTANT;
import org.csu.api.common.CommonResponse;
import org.csu.api.domain.Order;
import org.csu.api.persistence.OrderMapper;
import org.csu.api.service.AliPayService;
import org.csu.api.util.AlipayConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service("aliPayService")
public class AliPayServiceImpl implements AliPayService {
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private AlipayConfig alipayConfig;

    @Override
    public CommonResponse<String> pay(Integer userId, String orderNo) {
        QueryWrapper<Order> orderQueryWrapper = new QueryWrapper<>();
        orderQueryWrapper.eq("user_id", userId).eq("order_no", orderNo);
        Order order = orderMapper.selectOne(orderQueryWrapper);
        if (order.getStatus() != CONSTANT.OrderStatus.NON_PAYMENT.getCode()) {
            return CommonResponse.createForError("订单无法支付!");
        }
        // 1. 创建Client，通用SDK提供的Client，负责调用支付宝的API
        AlipayClient alipayClient = new DefaultAlipayClient(
                CONSTANT.ALIPAY.GATEWAY_URL,
                alipayConfig.getAppId(),
                alipayConfig.getAppPrivateKey(),
                CONSTANT.ALIPAY.FORMAT,
                CONSTANT.ALIPAY.CHARSET,
                alipayConfig.getAlipayPublicKey(),
                CONSTANT.ALIPAY.SIGN_TYPE
        );
        // 2. 创建 Request并设置Request参数
        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
        request.setNotifyUrl(alipayConfig.getNotifyUrl());
        JSONObject bizContent = new JSONObject();
        bizContent.set("out_trade_no", order.getOrderNo());
        bizContent.set("total_amount", order.getPaymentPrice());
        bizContent.set("subject", order.getOrderNo());
        bizContent.set("product_code", "FAST_INSTANT_TRADE_PAY");
        request.setBizContent(bizContent.toString());

        String form = "";
        try {
            AlipayTradePagePayResponse response = alipayClient.pageExecute(request);
            form = response.getBody();
            String message = response.getTradeNo();
            System.out.println(message);
            message = response.getMerchantOrderNo();
            System.out.println(message);
            message = response.getSellerId();
            System.out.println(message);
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        return CommonResponse.createForSuccess(form);
    }

    @Override
    public CommonResponse<String> payNotify(Map<String,String> params) throws Exception{
        String outTradeNo = params.get("out_trade_no");
        String gmtPayment = params.get("gmt_payment");
        String alipayTradeNo = params.get("trade_no");

        String sign = params.get("sign");
        String content = AlipaySignature.getSignCheckContentV1(params);

        Boolean checkSignature = AlipaySignature.rsa256CheckContent(content, sign, alipayConfig.getAlipayPublicKey(), CONSTANT.ALIPAY.CHARSET);
        // 支付宝验签
        if (checkSignature) {
            System.out.println("验签通过");
            // 验签通过
            System.out.println("交易名称: " + params.get("subject"));
            System.out.println("交易状态: " + params.get("trade_status"));
            System.out.println("支付宝交易凭证号: " + params.get("trade_no"));
            System.out.println("商户订单号: " + params.get("out_trade_no"));
            System.out.println("交易金额: " + params.get("total_amount"));
            System.out.println("买家在支付宝唯一id: " + params.get("buyer_id"));
            System.out.println("买家付款时间: " + params.get("gmt_payment"));
            System.out.println("买家付款金额: " + params.get("buyer_pay_amount"));

            // 查询订单
            QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("order_no", outTradeNo);
            Order order = orderMapper.selectOne(queryWrapper);

            if (order != null) {
                order.setStatus(CONSTANT.OrderStatus.PAID.getCode());
                order.setUpdateTime(LocalDateTime.now());
                order.setPaymentTime(LocalDateTime.now());
                orderMapper.updateById(order);
            }
            return CommonResponse.createForSuccess("SUCCESS");
        }else {
            System.out.println("验签失败");
            return CommonResponse.createForError("FAILED");
        }

    }
}
