package org.csu.api.controller.front;

import ch.qos.logback.core.util.InvocationGate;
import com.google.common.collect.Lists;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.apache.ibatis.annotations.DeleteProvider;
import org.csu.api.common.CONSTANT;
import org.csu.api.common.CommonResponse;
import org.csu.api.common.ResponseCode;
import org.csu.api.dto.PostCartDTO;
import org.csu.api.service.CartService;
import org.csu.api.vo.CartVO;
import org.csu.api.vo.UserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HexFormat;


@RestController
@RequestMapping("/cart/")
public class CartController {
    @Autowired
    private CartService cartService;

    // 1.添加购物车
    // POST 方法采用参数Request Body
    @PostMapping("add")
    public CommonResponse<CartVO> addCart(@Valid @RequestBody PostCartDTO postCartDTO, HttpSession session){
        // 获取用户登录状态代码
        UserVO loginUser = (UserVO) session.getAttribute(CONSTANT.LOGIN_USER);
        if(loginUser == null){
            return CommonResponse.createForError(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDescription());
        }
        return cartService.addCart(loginUser.getId(), postCartDTO.getProductId(), postCartDTO.getQuantity());
    }

    // 2.更新购物车
    @PostMapping("update")
    public CommonResponse<CartVO> updateCart(@Valid @RequestBody PostCartDTO postCartDTO, HttpSession session){
        // 获取用户登录状态代码
        UserVO loginUser = (UserVO) session.getAttribute(CONSTANT.LOGIN_USER);
        if(loginUser == null){
            return CommonResponse.createForError(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDescription());
        }
        return cartService.updateCart(loginUser.getId(), postCartDTO.getProductId(), postCartDTO.getQuantity());
    }

    // 3.删除购物车
    // DONE:delete
    // OPTIMIZE: 数据接收方式考虑换成DTO
    @PostMapping("delete")
    public CommonResponse<CartVO> deleteCart(HttpSession session, @RequestParam String productIds){
        UserVO loginUser = (UserVO) session.getAttribute(CONSTANT.LOGIN_USER);
        if(loginUser == null){
            return CommonResponse.createForError(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDescription());
        }

        ArrayList<String> productIdList= Lists.newArrayList(Arrays.asList(productIds.split(",")));
        return cartService.deleteCart(loginUser.getId(), productIdList);
    }

    // 4.获取购物车列表
    @GetMapping("list")
    public CommonResponse<CartVO> updateCart(HttpSession session){
        // 获取用户登录状态代码
        UserVO loginUser = (UserVO) session.getAttribute(CONSTANT.LOGIN_USER);
        if(loginUser == null){
            return CommonResponse.createForError(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDescription());
        }
        return cartService.listCart(loginUser.getId());
    }

    // 5.购物车全选
    // DONE:set_all_checked
    @PostMapping("set_all_checked")
    public CommonResponse<CartVO> setAllCheckedCart(HttpSession session){
        // 获取用户登录状态代码
        UserVO loginUser = (UserVO) session.getAttribute(CONSTANT.LOGIN_USER);
        if(loginUser == null){
            return CommonResponse.createForError(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDescription());
        }
        return cartService.setAllCheckedCart(loginUser.getId());
    }

    // 6.购物车全不选
    // DONE:set_all_unchecked
    @PostMapping("set_all_unchecked")
    public CommonResponse<CartVO> setAllUncheckedCart(HttpSession session){
        // 获取用户登录状态代码
        UserVO loginUser = (UserVO) session.getAttribute(CONSTANT.LOGIN_USER);
        if(loginUser == null){
            return CommonResponse.createForError(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDescription());
        }
        return cartService.setAllUncheckedCart(loginUser.getId());
    }

    // 7.设置某项购物车选中
    // DONE:set_cart_item_checked
    @PostMapping("set_cart_item_checked")
    public CommonResponse<CartVO> setCartItemChecked(HttpSession session, @NotBlank(message = "商品ID不能为空") @RequestParam String productId){
        // 获取用户登录状态代码
        UserVO loginUser = (UserVO) session.getAttribute(CONSTANT.LOGIN_USER);
        if(loginUser == null){
            return CommonResponse.createForError(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDescription());
        }
        return cartService.setCartItemChecked(loginUser.getId(), productId);
    }

    // 8.设置某项购物车不选中
    // DONE:set_cart_item_unchecked
    @PostMapping("set_cart_item_unchecked")
    public CommonResponse<CartVO> setCartItemUnchecked(HttpSession session, @NotBlank(message = "商品ID不能为空") @RequestParam String productId){
        // 获取用户登录状态代码
        UserVO loginUser = (UserVO) session.getAttribute(CONSTANT.LOGIN_USER);
        if(loginUser == null){
            return CommonResponse.createForError(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDescription());
        }
        return cartService.setCartItemUnchecked(loginUser.getId(), productId);
    }

    // 9.获取购物车总数
    // DONE:get_cart_count
    @PostMapping("get_cart_count")
    public CommonResponse<Integer> getCartCount(HttpSession session){
        UserVO loginUser = (UserVO) session.getAttribute(CONSTANT.LOGIN_USER);
        if(loginUser == null){
            return CommonResponse.createForError(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDescription());
        }
        return cartService.getCartCount(loginUser.getId());
    }
}
