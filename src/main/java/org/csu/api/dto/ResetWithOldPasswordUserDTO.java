package org.csu.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
@Data
public class ResetWithOldPasswordUserDTO {
    @NotBlank(message = "用户名不能为空")
    private String username;
    @NotBlank(message = "旧密码不能为空")
    private String oldPassword;
    @NotBlank(message = "新密码不能为空")
    private String newPassword;
}
