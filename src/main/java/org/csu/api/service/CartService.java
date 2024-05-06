package org.csu.api.service;

import jakarta.servlet.http.HttpSession;
import org.csu.api.common.CommonResponse;
import org.csu.api.vo.CartVO;

import java.util.ArrayList;

public interface CartService {

    // 1.添加一个购物车项
    CommonResponse<CartVO> addCart(Integer userId, Integer productId, Integer quantity);
    // 2.更新一个购物车项
    CommonResponse<CartVO> updateCart(Integer userId, Integer productId, Integer quantity);
    // 3.获取购物车列表
    CommonResponse<CartVO> listCart(Integer userId);
    // 4.删除购物车
    CommonResponse<CartVO> deleteCart(Integer userId, ArrayList<String> productIds);
    // 5.购物车全选
    CommonResponse<CartVO> setAllCheckedCart(Integer userId);
    // 6.购物车全不选
    CommonResponse<CartVO> setAllUncheckedCart(Integer userId);
    // 7.设置某项购物车选中
    CommonResponse<CartVO> setCartItemChecked(Integer userId, String productId);
    // 8.设置某项购物车不选中
    CommonResponse<CartVO> setCartItemUnchecked(Integer userId, String productId);
    // 9.获取购物车总数
    CommonResponse<Integer> getCartCount(Integer userId);

}
