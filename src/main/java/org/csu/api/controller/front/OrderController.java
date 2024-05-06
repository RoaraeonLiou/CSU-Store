package org.csu.api.controller.front;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.constraints.NotBlank;
import org.csu.api.common.CONSTANT;
import org.csu.api.common.CommonResponse;
import org.csu.api.common.ResponseCode;
import org.csu.api.service.OrderService;
import org.csu.api.vo.OrderCartVO;
import org.csu.api.vo.OrderVO;
import org.csu.api.vo.UserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/order/")
//@EnableTransactionManagement
public class OrderController {
    @Autowired
    private OrderService orderService;

    // DONE: 1.创建订单
    // OPTIMIZE: 还需要优化
    @PostMapping("create_order_from_product")
    public CommonResponse<OrderVO> createOrderFromProduct(HttpSession session,
                                                          @RequestParam @NotBlank(message = "商品ID不能为空") Integer productId,
                                                          @RequestParam @NotBlank(message = "商品数量不能为空") Integer quantity,
                                                          @RequestParam @NotBlank(message = "地址ID不能为空") Integer addressId) {
        UserVO loginUser = (UserVO) session.getAttribute(CONSTANT.LOGIN_USER);
        if (loginUser == null) {
            return CommonResponse.createForError(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDescription());
        }
        return orderService.createOrderFromProduct(loginUser.getId(), productId, quantity, addressId);
    }

    @PostMapping("create_order_from_cart")
    public CommonResponse<OrderVO> createOrderFromCart(HttpSession session, @RequestParam @NotBlank(message = "地址ID不能为空") Integer addressId) {
        UserVO loginUser = (UserVO) session.getAttribute(CONSTANT.LOGIN_USER);
        if (loginUser == null) {
            return CommonResponse.createForError(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDescription());
        }
        return orderService.createOrderFromCart(loginUser.getId(), addressId);
    }

    // DONE: 2.获取购物车中选中的商品列表
    @GetMapping("cart_item_list")
    public CommonResponse<OrderCartVO> cartItemList(HttpSession session) {
        UserVO loginUser = (UserVO) session.getAttribute(CONSTANT.LOGIN_USER);
        if (loginUser == null) {
            return CommonResponse.createForError(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDescription());
        }
        return orderService.cartItemList(loginUser.getId());
    }

    // DONE: 3.获取订单详情
    @GetMapping("detail")
    public CommonResponse<OrderVO> detail(HttpSession session, @RequestParam @NotBlank(message = "订单号不能为空") String orderNo) {
        UserVO loginUser = (UserVO) session.getAttribute(CONSTANT.LOGIN_USER);
        if (loginUser == null) {
            return CommonResponse.createForError(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDescription());
        }
        return orderService.detail(loginUser.getId(), orderNo);
    }

    // DONE: 4.获取订单列表
    @GetMapping("list")
    public CommonResponse<Page<OrderVO>> list(HttpSession session, @RequestParam @NotBlank(message = "页码不能为空") Integer pageNum, @RequestParam @NotBlank(message = "页面大小不能为空") Integer pageSize) {
        UserVO loginUser = (UserVO) session.getAttribute(CONSTANT.LOGIN_USER);
        if (loginUser == null) {
            return CommonResponse.createForError(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDescription());
        }
        return orderService.list(loginUser.getId(), pageNum, pageSize);
    }

    // DONE: 5.取消订单
    @PostMapping("cancel")
    public CommonResponse<String> cancel(HttpSession session, @RequestParam @NotBlank(message = "订单号不能为空") String orderNo) {
        UserVO loginUser = (UserVO) session.getAttribute(CONSTANT.LOGIN_USER);
        if (loginUser == null) {
            return CommonResponse.createForError(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDescription());
        }
        return orderService.cancel(loginUser.getId(), orderNo);
    }
}
