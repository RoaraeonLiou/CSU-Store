package org.csu.api.controller.front;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.csu.api.common.CONSTANT;
import org.csu.api.common.CommonResponse;
import org.csu.api.common.ResponseCode;
import org.csu.api.persistence.OrderMapper;
import org.csu.api.service.AliPayService;
import org.csu.api.util.AlipayConfig;

import org.csu.api.vo.UserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.web.bind.annotation.*;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

// xjlugv6874@sandbox.com
// 9428521.24 - 30 = 9428491.24 + 30 = 9428521.24
@RestController
@RequestMapping("/alipay/")
public class AliPayController {

    private static final String GATEWAY_URL = "https://openapi.alipaydev.com/gateway.do";
    private static final String FORMAT = "JSON";
    private static final String CHARSET = "UTF-8";
    //签名方式
    private static final String SIGN_TYPE = "RSA2";

    @Autowired
    private AliPayService aliPayService;

    @Autowired
    private AlipayConfig alipayConfig;

    //    CommonResponse<String>
    @GetMapping("pay")
    public void pay(String orderNo, HttpSession session, HttpServletResponse httpResponse) throws Exception {
        UserVO loginUser = (UserVO) session.getAttribute(CONSTANT.LOGIN_USER);
        CommonResponse<String> commonResponse = null;
        if (loginUser == null) {
            // TODO:跳转到登录
            httpResponse.setContentType("text/html;charset=" + CHARSET);
            httpResponse.getWriter().write("need login");// 直接将完整的表单html输出到页面
            httpResponse.getWriter().flush();
            httpResponse.getWriter().close();
            System.out.println("need login");
//            return CommonResponse.createForError(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDescription());
        }else{
            commonResponse = aliPayService.pay(loginUser.getId(), orderNo);
            String form = commonResponse.getData();
            httpResponse.setContentType("text/html;charset=" + CHARSET);
            httpResponse.getWriter().write(form);// 直接将完整的表单html输出到页面
            httpResponse.getWriter().flush();
            httpResponse.getWriter().close();
        }
    }


    @PostMapping("notify")  // 注意这里必须是POST接口
    public String notify(HttpServletRequest request) {
        if (request.getParameter("trade_status").equals("TRADE_SUCCESS")) {
            System.out.println("=========支付宝异步回调========");

            Map<String, String> params = new HashMap<>();
            Map<String, String[]> requestParams = request.getParameterMap();
            for (String name : requestParams.keySet()) {
                params.put(name, request.getParameter(name));
            }
            try{
                CommonResponse<String> commonResponse = aliPayService.payNotify(params);
                if(commonResponse.getData().equals("SUCCESS")) {
                    System.out.println("=========支付宝回调结束========");
                    return "success";
                }
            }catch (Exception e){
                System.out.println(e.getMessage());
            }
        }
        System.out.println("==failed==");
        return "failed";
    }

//    public void payNotify(@RequestParam Map<String,String> requestParams, HttpServletResponse response) {
//        System.out.println("=========支付宝异步回调========");
//        try {
//            if (requestParams.get("trade_status").equals("TRADE_SUCCESS")) {
//                System.out.println(requestParams);
//                Map<String, String> params = new HashMap<>();
//                for (String key : requestParams.keySet()) {
//                    params.put(key, String.join("", requestParams.get(key)));
//                }
//                System.out.println(params);
//                String content = AlipaySignature.getSignCheckContentV1(requestParams);
//                response.setCharacterEncoding("UTF-8");
//                PrintWriter out = response.getWriter();
//                out.write("success");
//                out.close();
//                aliPayService.payNotify(requestParams);
//            }else {
//                System.out.println("=========支付宝异步回调失败========");
//            }
//        } catch (Exception e) {
//            System.out.println(e.getMessage());
//        }
//        System.out.println("=========支付宝异步回调结束========");
//    }

}