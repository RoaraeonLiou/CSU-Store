package org.csu.api;

import org.csu.api.common.CommonResponse;
import org.csu.api.domain.User;
import org.csu.api.persistence.UserMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.bind.annotation.GetMapping;

@SpringBootTest
class CsuMallApiApplicationTests {

    @Autowired
    private UserMapper userMapper;

    @Test
    void contextLoads() {

        User user = userMapper.selectById(1);
        System.out.println(user);
    }

    @GetMapping("/testResponse")
    public CommonResponse<String> testResponse(){
        return CommonResponse.createForError("注册失败");
    }

}
