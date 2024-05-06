package org.csu.api.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductDetailVO {
    private Integer id;
    private Integer categoryId;
    private String name;
    private String subtitle;
    private String mainImage;
    private String subImages;
    private String detail;
    private BigDecimal price;
    private Integer stock;
    private Integer status;

    private Integer parentCategoryId;
    private String imageServer;
}
