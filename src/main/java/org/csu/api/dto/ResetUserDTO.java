package org.csu.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ResetUserDTO {
    @NotBlank(message = "用户名不能为空")
    private String username;
    @NotBlank(message = "新密码不能为空")
    private String newPassword;
    @NotBlank(message = "token不能为空")
    private String forgetToken;
}
