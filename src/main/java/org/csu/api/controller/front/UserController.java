package org.csu.api.controller.front;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.StringUtils;
import org.csu.api.common.CONSTANT;
import org.csu.api.common.CommonResponse;
import org.csu.api.common.ResponseCode;
import org.csu.api.domain.User;
import org.csu.api.dto.*;
import org.csu.api.service.UserService;
import org.csu.api.vo.UserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user/")
public class UserController {

    @Autowired
    private UserService userService;

    //DTO:客户端提交数据时数据的封装对象，以及各个层之间传递数据
    //VO：View Object，服务端向客户端返回数据时的封装对象
    //VO：Value Object，业务逻辑层和DAO层交换的封装对象
    //BO：业务对象，领域对象DO

    // 1.登录
    @PostMapping("login")
    public CommonResponse<UserVO> login(@Valid @RequestBody LoginUserDTO loginUserDTO,
                                        HttpSession session) {
        CommonResponse<UserVO> result = userService.login(loginUserDTO);
        if (result.isSuccess()) {
            session.setAttribute(CONSTANT.LOGIN_USER, result.getData());
        }
        return result;
    }
    // 2.注册
    @PostMapping("register")
    public CommonResponse<Object> register(@Valid @RequestBody RegisterUserDTO registerUserDTO) {
        if(!StringUtils.equals(registerUserDTO.getPassword(),registerUserDTO.getConfirmPassword())){
            return CommonResponse.createForError("两次密码不一致");
        }
        return userService.register(registerUserDTO);
    }

    // 3.检查用户相关字段是否可用
    @PostMapping("check_field")
    public CommonResponse<Object> checkField(@Valid @RequestBody CheckUserFieldDTO checkUserFieldDTO) {
        return userService.checkField(checkUserFieldDTO.getFieldName(), checkUserFieldDTO.getFieldValue());
    }

    // 4.获取登录用户信息
    // DONE: get_user_detail
    @PostMapping("get_user_detail")
    public CommonResponse<UserVO> getUserDetail(HttpSession session){
        UserVO loginUser = (UserVO) session.getAttribute(CONSTANT.LOGIN_USER);
        if(loginUser == null){
            return CommonResponse.createForError(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDescription());
        }
        UserVO userVO = (UserVO) session.getAttribute(CONSTANT.LOGIN_USER);
        return CommonResponse.createForSuccess(userVO);
    }

    // 5.忘记密码
    @GetMapping("get_forget_question")
    public CommonResponse<String> getForgetQuestion(
            @RequestParam @NotBlank(message = "用户名不能为空") String username) {
        return userService.getForgetQuestion(username);
    }

    // 6.提交忘记密码问题的答案
    // 使用Json传递
    @PostMapping("check_forget_answer")
    public CommonResponse<String> checkForgetAnswer(
            @Valid @RequestBody CheckAnswerUserDTO checkAnswerUserDTO){
        return userService.checkForgetAnswer(
                checkAnswerUserDTO.getUsername(), checkAnswerUserDTO.getQuestion(), checkAnswerUserDTO.getAnswer());
    }

    // 使用form-data传递
//    @PostMapping("check_forget_answer")
//    public CommonResponse<String> checkForgetAnswer(
//            @RequestParam @NotBlank(message = "用户名不能为空") String username,
//            @RequestParam @NotBlank(message = "忘记密码问题不能为空") String question,
//            @RequestParam @NotBlank(message = "忘记密码问题答案不能为空") String answer){
//        return userService.checkForgetAnswer(username, question, answer);
//    }

    // 7.通过忘记密码问题答案重设密码
    @PostMapping("reset_forget_password")
    public CommonResponse<String> resetForgetPassword(
            @Valid @RequestBody ResetUserDTO resetUserDTO){
        return userService.resetForgetPassword(
                resetUserDTO.getUsername(), resetUserDTO.getNewPassword(), resetUserDTO.getForgetToken());
    }
    // 8.登录状态重设密码
    // DONE: reset_password
    @PostMapping("reset_password")
    public CommonResponse<String> resetPassword(@Valid @RequestBody ResetWithOldPasswordUserDTO resetUserDTO, HttpSession session){
        UserVO loginUser = (UserVO) session.getAttribute(CONSTANT.LOGIN_USER);
        if(loginUser == null){
            return CommonResponse.createForError(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDescription());
        }
        return userService.resetPassword(resetUserDTO.getUsername(), resetUserDTO.getOldPassword(), resetUserDTO.getNewPassword());
    }

    // 9.登录状态修改个人信息
    // DONE: update_user_info
    @PostMapping("update_user_info")
    public CommonResponse<Object> updateUserInfo(@Valid @RequestBody UpdateUserDTO updateUserDTO, HttpSession session){
        UserVO loginUser = (UserVO) session.getAttribute(CONSTANT.LOGIN_USER);
        if(loginUser == null){
            return CommonResponse.createForError(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDescription());
        }
        return userService.update_user_info(loginUser, updateUserDTO);
    }

    // 10.退出登录
    @PostMapping("logout")
    public CommonResponse<Object> logout(HttpSession session) {
        session.removeAttribute(CONSTANT.LOGIN_USER);
        return CommonResponse.createForSuccess("退出登录成功");
    }


}
