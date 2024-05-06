package org.csu.api.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

@Data
public class PostCartDTO {
    @NotNull(message="商品ID不能为空")
    private Integer productId;
    @Range(min=1, message="商品数量不能小于1")
    private Integer quantity;
}
