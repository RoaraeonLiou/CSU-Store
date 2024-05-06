package org.csu.api.controller;

import jakarta.validation.Valid;
import org.csu.api.common.CommonResponse;
import org.csu.api.domain.User;
import org.springframework.web.bind.annotation.*;

@RestController
public class TestController {

//    @GetMapping("/testRequest")
//    public String testRequest(@RequestParam String username,
//                              @RequestParam String password){
//        System.out.println(username + "," + password);
//        return null;
//    }

//    @PostMapping("/testRequest")
//    public String testRequest(@RequestParam String username,
//                              @RequestParam String password){
//        System.out.println(username + "," + password);
//        return null;
//    }

    @PostMapping("/testRequest")
    public CommonResponse<String> testRequest(@Valid @RequestBody User user){
        System.out.println(user.getUsername() + "," + user.getPassword());
        return CommonResponse.createForSuccessMessage("注册成功");
    }

    @GetMapping("/testRequest/{id}")
    public String testRequest(@PathVariable Integer id){
        System.out.println(id);
        return null;
    }

    @GetMapping("/testResponse")
    public CommonResponse<String> testResponse(){
        return CommonResponse.createForError("注册失败");
    }
}
