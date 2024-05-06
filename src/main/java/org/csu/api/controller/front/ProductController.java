package org.csu.api.controller.front;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.validation.constraints.NotNull;
import org.csu.api.domain.Product;
import org.csu.api.service.ProductService;
import org.csu.api.vo.ProductDetailVO;
import org.csu.api.vo.ProductListVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.csu.api.common.CommonResponse;

@RestController
@RequestMapping("/product/")
@Validated
public class ProductController {

    @Autowired
    private ProductService productService;

    // 1.商品详情
    @GetMapping("detail")
    public CommonResponse<ProductDetailVO> getProductDetail(
            @RequestParam @NotNull(message = "商品ID不能为空") Integer productId){
        return productService.getProductDetail(productId);
    }

    // 2.商品列表
    @GetMapping("list")
    public CommonResponse<Page<ProductListVO>> getProductList(
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "") String orderBy,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "5") int pageSize){

        return productService.getProductList(categoryId,keyword,orderBy,pageNum,pageSize);
    }
}
