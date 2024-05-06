package org.csu.api.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import org.csu.api.common.CONSTANT;
import org.csu.api.common.CommonResponse;
import org.csu.api.domain.*;
import org.csu.api.persistence.*;
import org.csu.api.service.OrderService;
import org.csu.api.util.*;
import org.csu.api.vo.AddressVO;
import org.csu.api.vo.OrderCartVO;
import org.csu.api.vo.OrderItemVO;
import org.csu.api.vo.OrderVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;

@Service("orderService")
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderItemMapper orderItemMapper;
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private AddressMapper addressMapper;
    @Autowired
    private CartItemMapper cartItemMapper;
    @Autowired
    private ImageServerConfig imageServerConfig;

    @Override
    public CommonResponse<OrderVO> createOrderFromProduct(Integer userId, Integer productId, Integer quantity, Integer addressId) {
        // 1. 判断地址ID是否有效
        QueryWrapper<Address> addressQueryWrapper = new QueryWrapper<>();
        addressQueryWrapper.eq("user_id", userId).eq("id", addressId);
        Address address = addressMapper.selectOne(addressQueryWrapper);
        if (address == null) {
            return CommonResponse.createForError(1, "创建订单失败<地址无效>");
        }
        // 2. 判断产品ID是否有效
        QueryWrapper<Product> productQueryWrapper = new QueryWrapper<>();
        productQueryWrapper.eq("id", productId).eq("status", CONSTANT.ProductStatus.ON_SALE.getCode());
        Product product = productMapper.selectOne(productQueryWrapper);
        if (product == null) {
            return CommonResponse.createForError(1, "创建订单失败<商品ID不存在或已下架>");
        }
        // 3. 判断库存是否有效
        if (product.getStock() < quantity) {
            return CommonResponse.createForError(1, "创建订单失败<商品库存不足>");
        }
        // 4. 获取唯一订单编号
        BigInteger orderNo = new BigInteger(String.valueOf(this.generateOrderNo()));
        // 5. 创建订单项
        int row = 0;
        OrderItem orderItem = new OrderItem();
        orderItem.setUserId(userId);
        orderItem.setProductId(productId);
        orderItem.setOrderNo(orderNo);
        orderItem.setProductName(product.getName());
        orderItem.setProductImage(product.getMainImage());
        orderItem.setCurrentPrice(product.getPrice());
        orderItem.setQuantity(quantity);
        orderItem.setTotalPrice(BigDecimalUtil.multiply(product.getPrice().doubleValue(), quantity));
        orderItem.setCreateTime(LocalDateTime.now());
        orderItem.setCreateTime(LocalDateTime.now());
        row = orderItemMapper.insert(orderItem);
        if (row <= 0) {
            return CommonResponse.createForError(1, "创建订单失败<订单项对象创建失败>");
        }
        // 6.更新库存
        UpdateWrapper<Product> productUpdateWrapper = new UpdateWrapper<>();
        productUpdateWrapper.eq("id", productId);
        productUpdateWrapper.set("stock", product.getStock() - quantity);
        row = productMapper.update(null, productUpdateWrapper);
        if (row == 0) {
            QueryWrapper<OrderItem> orderItemQueryWrapper = new QueryWrapper<>();
            orderItemQueryWrapper.eq("order_no", orderNo);
            orderItemMapper.delete(orderItemQueryWrapper);
            return CommonResponse.createForError(1, "创建订单失败<更新产品库存失败>");
        }
        // 7. 计算实际付款金额
        // TODO: 获取运费
        Integer postage = 0;
        BigDecimal paymentPrice = orderItem.getTotalPrice();
        // 8. 创建订单
        Order order = new Order();
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setAddressId(addressId);
        order.setPaymentPrice(paymentPrice);
        order.setPaymentType(CONSTANT.PaymentType.ALIPAY.getCode());
        order.setPostage(postage);
        order.setStatus(CONSTANT.OrderStatus.NON_PAYMENT.getCode());
        order.setCreateTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        row = orderMapper.insert(order);
        if (row <= 0) {
            this.rollBack(orderNo, Lists.newArrayList(orderItem));
            return CommonResponse.createForError(1, "创建订单失败<订单对象创建失败>");
        }

        return CommonResponse.createForSuccess("创建订单成功", this.generateOrderVO(order, orderItem));
    }

    @Override
    public CommonResponse<OrderVO> createOrderFromCart(Integer userId, Integer addressId) {
        // 1. 选出所有被选中的购物车项
        QueryWrapper<CartItem> cartItemQueryWrapper = new QueryWrapper<>();
        cartItemQueryWrapper.eq("user_id", userId)
                .eq("checked", CONSTANT.CART_ITEM_STATUS.CHECKED);
        List<CartItem> cartItemList = cartItemMapper.selectList(cartItemQueryWrapper);
        if (cartItemList == null | cartItemList.size() == 0) {
            return CommonResponse.createForError(1, "创建订单失败<购物车为空>");
        }
        // 2. 判断地址是否有效
        QueryWrapper<Address> addressQueryWrapper = new QueryWrapper<>();
        addressQueryWrapper.eq("user_id", userId).eq("id", addressId);
        Address address = addressMapper.selectOne(addressQueryWrapper);
        if (address == null) {
            return CommonResponse.createForError(1, "创建订单失败<地址无效>");
        }
        // 3. 获取唯一订单编号
        BigInteger orderNo = new BigInteger(String.valueOf(this.generateOrderNo()));
        // 4. 判断库存是否有效, 并创建订单项
        QueryWrapper<Product> productQueryWrapper = new QueryWrapper<>();
        UpdateWrapper<Product> productUpdateWrapper = new UpdateWrapper<>();
        List<OrderItem> orderItemList = Lists.newArrayList();
        int row = 0;
        for (CartItem cartItem : cartItemList) {
            productQueryWrapper.eq("id", cartItem.getProductId()).eq("status", CONSTANT.ProductStatus.ON_SALE.getCode());
            Product product = productMapper.selectOne(productQueryWrapper);
            if (product == null) {
                this.rollBack(orderNo, orderItemList);
                return CommonResponse.createForError(1, "创建订单失败<商品ID不存在或已下架>");
            }
            if (product.getStock() < cartItem.getQuantity()) {
                this.rollBack(orderNo, orderItemList);
                return CommonResponse.createForError(1, "创建订单失败<商品库存不足>");
            }

            // 创建订单项
            OrderItem orderItem = new OrderItem();
            orderItem.setUserId(userId);
            orderItem.setProductId(product.getId());
            orderItem.setOrderNo(orderNo);
            orderItem.setProductName(product.getName());
            orderItem.setProductImage(product.getMainImage());
            orderItem.setCurrentPrice(product.getPrice());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setTotalPrice(BigDecimalUtil.multiply(product.getPrice().doubleValue(), cartItem.getQuantity()));
            orderItem.setCreateTime(LocalDateTime.now());
            orderItem.setCreateTime(LocalDateTime.now());
            row = orderItemMapper.insert(orderItem);
            if (row <= 0) {
                this.rollBack(orderNo, orderItemList);
                return CommonResponse.createForError(1, "创建订单失败<订单项对象创建失败>");
            }
            orderItemList.add(orderItem);
            // 更新库存
            productUpdateWrapper.eq("id", cartItem.getProductId());
            productUpdateWrapper.set("stock", product.getStock() - cartItem.getQuantity());
            row = productMapper.update(null, productUpdateWrapper);
            if (row <= 0) {
                orderItemList.remove(orderItemList.size() - 1);
                this.rollBack(orderNo, orderItemList);
                return CommonResponse.createForError(1, "创建订单失败<更新库存失败>");
            }
            // 检查库存
            product = productMapper.selectOne(productQueryWrapper);
            if (product.getStock() < 0) {
                this.rollBack(orderNo, orderItemList);
                return CommonResponse.createForError(1, "创建订单失败<商品库存出错>");
            }
            // 清空条件
            productUpdateWrapper.clear();
            productQueryWrapper.clear();
        }
        // 5. 计算实际付款金额
        // TODO: 获取运费
        Integer postage = 0;
        BigDecimal paymentPrice = new BigDecimal(0);
        for (OrderItem orderItem : orderItemList) {
            paymentPrice = BigDecimalUtil.add(paymentPrice.doubleValue(), orderItem.getTotalPrice().doubleValue());
        }
        // 6. 创建订单
        Order order = new Order();
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setAddressId(addressId);
        order.setPaymentPrice(paymentPrice);
        order.setPaymentType(CONSTANT.PaymentType.ALIPAY.getCode());
        order.setPostage(postage);
        order.setStatus(CONSTANT.OrderStatus.NON_PAYMENT.getCode());
        order.setCreateTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        row = orderMapper.insert(order);
        if (row <= 0) {
            this.rollBack(orderNo, orderItemList);
            return CommonResponse.createForError(1, "创建订单失败<订单对象创建失败>");
        }
        // 7. 删除购物车项目
        for (CartItem cartItem : cartItemList) {
            cartItemMapper.deleteById(cartItem.getId());
        }

        return CommonResponse.createForSuccess("创建订单成功", this.generateOrderVO(order, orderItemList));
    }

    @Override
    public CommonResponse<OrderCartVO> cartItemList(Integer userId) {
        OrderCartVO orderCartVO = new OrderCartVO();
        // 1. 选出所有被选中的购物车项
        List<OrderItemVO> orderItemVOList = this.getCartItemList(userId);
        if(orderItemVOList==null){
            return CommonResponse.createForError("商品库存不足或购物车为空");
        }
        orderCartVO.setOrderItemVOList(orderItemVOList);
        // 2. 计算总价
        BigDecimal paymentPrice = this.getPaymentPrice(null, orderItemVOList);
        orderCartVO.setPaymentPrice(paymentPrice);
        // 3. 设置图片服务器url
        orderCartVO.setProductImageServer(imageServerConfig.getUrl());
        return CommonResponse.createForSuccess("SUCCESS",orderCartVO);
    }

    @Override
    public CommonResponse<OrderVO> detail(Integer userId, String orderNo) {
        QueryWrapper<Order> orderQueryWrapper = new QueryWrapper<>();
        orderQueryWrapper.eq("user_id", userId).eq("order_no", orderNo);
        Order order = orderMapper.selectOne(orderQueryWrapper);
        if(order==null){
            return CommonResponse.createForError("订单不存在");
        }
        QueryWrapper<OrderItem> orderItemQueryWrapper = new QueryWrapper<>();
        orderItemQueryWrapper.eq("order_no", orderNo);
        List<OrderItem> orderItemList = orderItemMapper.selectList(orderItemQueryWrapper);

        return CommonResponse.createForSuccess("SUCCESS",this.generateOrderVO(order, orderItemList));
    }

    @Override
    public CommonResponse<Page<OrderVO>> list(Integer userId, Integer pageNum, Integer pageSize) {
        Page<Order> orderPage = new Page<>();
        orderPage.setCurrent(pageNum);
        orderPage.setSize(pageSize);

        QueryWrapper<Order> orderQueryWrapper = new QueryWrapper<>();
        orderQueryWrapper.eq("user_id", userId);

        orderPage = orderMapper.selectPage(orderPage, orderQueryWrapper);

        Page<OrderVO> orderVOPage = ListBeanUtilsForPage.copyPageList(orderPage, OrderVO::new, (order, orderVO)->{
            QueryWrapper<OrderItem> orderItemQueryWrapper = new QueryWrapper<>();
            orderItemQueryWrapper.eq("order_no", order.getOrderNo());
            List<OrderItem> orderItemList = orderItemMapper.selectList(orderItemQueryWrapper);
            List<OrderItemVO> orderItemVOList = Lists.newArrayList();
            for (OrderItem orderItem : orderItemList) {
                orderItemVOList.add(this.generateOrderItemVO(orderItem));
            }
            orderVO.setOrderItemVOList(orderItemVOList);
            orderVO.setImageServer(imageServerConfig.getUrl());
            AddressVO addressVO = this.generateAddressVO(order.getAddressId());
            orderVO.setAddressVO(addressVO);
        });

        return CommonResponse.createForSuccess(orderVOPage);
    }

    @Override
    public CommonResponse<String> cancel(Integer userId, String orderNo) {
        QueryWrapper<Order> orderQueryWrapper = new QueryWrapper<>();
        orderQueryWrapper.eq("user_id", userId).eq("order_no", orderNo);
        Order order = orderMapper.selectOne(orderQueryWrapper);
        if(order==null){
            return CommonResponse.createForError("订单不存在");
        }
        if(order.getStatus()!=CONSTANT.OrderStatus.NON_PAYMENT.getCode()){
            return CommonResponse.createForError("订单不是未支付状态，不能取消");
        }
        UpdateWrapper<Order> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("order_no", orderNo);
        updateWrapper.set("status", CONSTANT.OrderStatus.CANCELED.getCode());
        int row = orderMapper.update(null, updateWrapper);
        if( row ==0 ){
            return CommonResponse.createForError("订单取消失败");
        }
        return CommonResponse.createForSuccess("订单取消成功");
    }

    private List<OrderItemVO> getCartItemList(Integer userId) {
        // 1. 选出所有被选中的购物车项
        QueryWrapper<CartItem> cartItemQueryWrapper = new QueryWrapper<>();
        cartItemQueryWrapper.eq("user_id", userId)
                .eq("checked", CONSTANT.CART_ITEM_STATUS.CHECKED);
        List<CartItem> cartItemList = cartItemMapper.selectList(cartItemQueryWrapper);
        if (cartItemList == null | cartItemList.size() == 0) {
            return null;
        }
        // 2. 检查库存
        QueryWrapper<Product> productQueryWrapper = new QueryWrapper<>();
        List<OrderItemVO> orderItemVOList = Lists.newArrayList();
        for (CartItem cartItem : cartItemList) {
            productQueryWrapper.eq("id", cartItem.getProductId()).eq("status", CONSTANT.ProductStatus.ON_SALE.getCode());
            Product product = productMapper.selectOne(productQueryWrapper);
            if (product == null) {
                return null;
            }
            if (product.getStock() < cartItem.getQuantity()) {
                return null;
            }
            // 创建订单项
            OrderItemVO orderItemVO = new OrderItemVO();
            orderItemVO.setProductId(product.getId());
            orderItemVO.setProductName(product.getName());
            orderItemVO.setProductImage(product.getMainImage());
            orderItemVO.setCurrentPrice(product.getPrice());
            orderItemVO.setQuantity(cartItem.getQuantity());
            orderItemVO.setTotalPrice(BigDecimalUtil.multiply(product.getPrice().doubleValue(), cartItem.getQuantity()));
            orderItemVOList.add(orderItemVO);
            // 清空条件
            productQueryWrapper.clear();
        }
        return orderItemVOList;
    }

    private BigDecimal getPaymentPrice(List<OrderItem> orderItemList, List<OrderItemVO> orderItemVOList) {
        // 初始化paymentPrice
        BigDecimal paymentPrice = new BigDecimal(0);
        if (orderItemList != null) {
            // orderItemList非空，通过orderItemList非空计算
            for (OrderItem orderItem : orderItemList) {
                paymentPrice = BigDecimalUtil.add(paymentPrice.doubleValue(), orderItem.getTotalPrice().doubleValue());
            }
        } else {
            if (orderItemVOList != null) {
                // orderItemVOList非空，通过orderItemVOList计算
                for (OrderItemVO orderItemVO : orderItemVOList) {
                    paymentPrice = BigDecimalUtil.add(paymentPrice.doubleValue(), orderItemVO.getTotalPrice().doubleValue());
                }
            }
        }
        // 计算运费
        return paymentPrice;
    }

    private int rollBack(BigInteger orderNo, List<OrderItem> orderItemList) {
        for (OrderItem orderItem : orderItemList) {
            QueryWrapper<Product> productQueryWrapper = new QueryWrapper<>();
            productQueryWrapper.eq("id", orderItem.getProductId());
            Product product = productMapper.selectOne(productQueryWrapper);
            UpdateWrapper<Product> productUpdateWrapper = new UpdateWrapper<>();
            productUpdateWrapper.eq("id", orderItem.getProductId());
            productUpdateWrapper.set("stock", product.getStock() + orderItem.getQuantity());
            productMapper.update(null, productUpdateWrapper);
        }
        QueryWrapper<OrderItem> orderItemQueryWrapper = new QueryWrapper<>();
        orderItemQueryWrapper.eq("order_no", orderNo);
        return orderItemMapper.delete(orderItemQueryWrapper);
    }

    private OrderVO generateOrderVO(Order order, OrderItem orderItem) {
        OrderVO orderVO = new OrderVO();
        BeanUtils.copyProperties(order, orderVO);

        List<OrderItemVO> orderItemVOList = Lists.newArrayList();
        orderItemVOList.add(this.generateOrderItemVO(orderItem));
        orderVO.setOrderItemVOList(orderItemVOList);

        orderVO.setImageServer(imageServerConfig.getUrl());

        AddressVO addressVO = this.generateAddressVO(order.getAddressId());
        orderVO.setAddressVO(addressVO);

        return orderVO;
    }

    private OrderVO generateOrderVO(Order order, List<OrderItem> orderItemList) {
        OrderVO orderVO = new OrderVO();
        BeanUtils.copyProperties(order, orderVO);

        List<OrderItemVO> orderItemVOList = Lists.newArrayList();
        for (OrderItem orderItem : orderItemList) {
            orderItemVOList.add(this.generateOrderItemVO(orderItem));
        }
        orderVO.setOrderItemVOList(orderItemVOList);

        orderVO.setImageServer(imageServerConfig.getUrl());

        AddressVO addressVO = this.generateAddressVO(order.getAddressId());
        orderVO.setAddressVO(addressVO);

        return orderVO;
    }

    private AddressVO generateAddressVO(Integer addressId) {
        AddressVO addressVO = new AddressVO();
        QueryWrapper<Address> addressQueryWrapper = new QueryWrapper<>();
        addressQueryWrapper.eq("id", addressId);
        Address address = addressMapper.selectOne(addressQueryWrapper);
        BeanUtils.copyProperties(address, addressVO);
        return addressVO;
    }

    private OrderItemVO generateOrderItemVO(OrderItem orderItem) {
        OrderItemVO orderItemVO = new OrderItemVO();
        BeanUtils.copyProperties(orderItem, orderItemVO);
        return orderItemVO;
    }

    private long generateOrderNo() {
        SnowFlake snowFlake = new SnowFlake(2, 3);
        return snowFlake.nextId();
    }
}
