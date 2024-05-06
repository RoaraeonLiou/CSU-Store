package org.csu.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CheckAnswerUserDTO {
    @NotBlank(message = "用户名不能为空")
    private String username;
    @NotBlank(message = "问题不能为空")
    private String question;
    @NotBlank(message = "答案不能为空")
    private String answer;
}
