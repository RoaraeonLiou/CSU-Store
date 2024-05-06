package org.csu.api.vo;

import lombok.Data;

@Data
public class UserVO {
    private Integer id;
    private String username;
    private String email;
    private String phone;
    private String question;
    private String answer;
    private Integer role;
}
