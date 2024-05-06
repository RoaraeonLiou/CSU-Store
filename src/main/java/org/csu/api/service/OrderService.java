package org.csu.api.service;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.csu.api.common.CommonResponse;
import org.csu.api.vo.OrderCartVO;
import org.csu.api.vo.OrderVO;

public interface OrderService {
    CommonResponse<OrderVO>  createOrderFromProduct(Integer userId, Integer productId, Integer quantity, Integer addressId);

    CommonResponse<OrderVO>  createOrderFromCart(Integer userId, Integer addressId);

    CommonResponse<OrderCartVO>  cartItemList(Integer userId);

    CommonResponse<OrderVO>  detail(Integer userId, String orderNo);

    CommonResponse<Page<OrderVO>>  list(Integer userId, Integer pageNum, Integer pageSize);

    CommonResponse<String>  cancel(Integer userId, String orderNo);
}
