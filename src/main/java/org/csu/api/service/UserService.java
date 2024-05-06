package org.csu.api.service;

import org.csu.api.common.CommonResponse;
import org.csu.api.dto.LoginUserDTO;
import org.csu.api.dto.RegisterUserDTO;
import org.csu.api.dto.UpdateUserDTO;
import org.csu.api.vo.UserVO;

public interface UserService {

    //用户登录
    CommonResponse<UserVO> login(LoginUserDTO loginUserDTO);

    //用户注册时的字段校验接口
    CommonResponse<Object> checkField(String fieldName, String fieldValue);

    //用户注册
    CommonResponse<Object> register(RegisterUserDTO registerUserDTO);

    //获取登陆用户信息
//    CommonResponse<UserVO> getUserDetail()

    // 登录状态重设密码
    CommonResponse<String> resetPassword(String username, String oldPassword, String newPassword);

    //获取忘记密码时的问题
    CommonResponse<String> getForgetQuestion(String username);

    //重置密码时验证密码问题答案是否正确
    CommonResponse<String> checkForgetAnswer(String username, String question,String answer);

    //根据token重置用户密码
    CommonResponse<String> resetForgetPassword(String username, String newPassword, String forgetToken);

    // 登录状态修改个人信息
    CommonResponse<Object> update_user_info(UserVO loginUser, UpdateUserDTO updateUserDTO);
}
