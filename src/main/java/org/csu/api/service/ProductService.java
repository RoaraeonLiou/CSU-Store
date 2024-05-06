package org.csu.api.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.csu.api.common.CommonResponse;
import org.csu.api.domain.Product;
import org.csu.api.vo.ProductDetailVO;
import org.csu.api.vo.ProductListVO;

public interface ProductService {

    //根据商品ID查询商品的详情
    CommonResponse<ProductDetailVO> getProductDetail(Integer productId);

    //查询商品列表
    CommonResponse<Page<ProductListVO>> getProductList(Integer categoryId, String keyword, String orderBy, int pageNum, int pageSize);
}
