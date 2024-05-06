package org.csu.api.controller.front;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.csu.api.common.CONSTANT;
import org.csu.api.common.CommonResponse;
import org.csu.api.common.ResponseCode;
import org.csu.api.dto.AddAddressDTO;
import org.csu.api.dto.UpdateAddressDTO;
import org.csu.api.service.AddressService;
import org.csu.api.vo.AddressVO;
import org.csu.api.vo.UserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/address/")
public class AddressController {
    @Autowired
    private AddressService addressService;
    // DONE: 1.新增地址
    @PostMapping("add")
    public CommonResponse<AddressVO> add(HttpSession session, @RequestBody @Valid AddAddressDTO addAddressDTO){
        // 获取用户登录状态代码
        UserVO loginUser = (UserVO) session.getAttribute(CONSTANT.LOGIN_USER);
        if(loginUser == null){
            return CommonResponse.createForError(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDescription());
        }
        return addressService.add(loginUser.getId(), addAddressDTO);
    }
    // DONE: 2.删除地址
    @PostMapping("delete")
    public CommonResponse<String> delete(HttpSession session,  @RequestParam @NotBlank(message = "地址ID不能为空") String addressId){
        UserVO loginUser = (UserVO) session.getAttribute(CONSTANT.LOGIN_USER);
        if(loginUser == null){
            return CommonResponse.createForError(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDescription());
        }
        return addressService.delete(loginUser.getId(), addressId);
    }
    // DONE: 3.修改地址
    @PostMapping("update")
    public CommonResponse<AddressVO> update(HttpSession session, @Valid @RequestBody UpdateAddressDTO updateAddressDTO){
        UserVO loginUser = (UserVO) session.getAttribute(CONSTANT.LOGIN_USER);
        if(loginUser == null){
            return CommonResponse.createForError(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDescription());
        }
        return addressService.update(loginUser.getId(), updateAddressDTO);
    }
    // DONE: 4.查询单个地址信息
    @GetMapping("find")
    public CommonResponse<AddressVO> find(HttpSession session, @RequestParam @NotBlank(message = "地址ID不能为空") String addressId){
        UserVO loginUser = (UserVO) session.getAttribute(CONSTANT.LOGIN_USER);
        if(loginUser == null){
            return CommonResponse.createForError(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDescription());
        }
        return addressService.find(loginUser.getId(), addressId);
    }
    // DONE: 5.查询某个用户的所有地址信息
    @GetMapping("list")
    public CommonResponse<List<AddressVO>> list(HttpSession session){
        UserVO loginUser = (UserVO) session.getAttribute(CONSTANT.LOGIN_USER);
        if(loginUser == null){
            return CommonResponse.createForError(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDescription());
        }
        return addressService.list(loginUser.getId());
    }
    // TODO: ArrayList 换成 List
}
