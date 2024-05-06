package org.csu.api.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.csu.api.common.CONSTANT;
import org.csu.api.common.CommonResponse;
import org.csu.api.common.ResponseCode;
import org.csu.api.domain.Category;
import org.csu.api.domain.Product;
import org.csu.api.persistence.CategoryMapper;
import org.csu.api.persistence.ProductMapper;
import org.csu.api.service.CategoryService;
import org.csu.api.service.ProductService;
import org.csu.api.util.ImageServerConfig;
import org.csu.api.util.ListBeanUtils;
import org.csu.api.util.ListBeanUtilsForPage;
import org.csu.api.vo.ProductDetailVO;
import org.csu.api.vo.ProductListVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("productService")
@Slf4j
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private CategoryService categoryService;

    @Resource
    private ImageServerConfig imageServerConfig;

    @Override
    public CommonResponse<ProductDetailVO> getProductDetail(Integer productId) {
        if(productId == null){
            return CommonResponse.createForError(ResponseCode.ARGUMENT_ILLEGAL.getCode(),ResponseCode.ARGUMENT_ILLEGAL.getDescription());
        }

        Product product = productMapper.selectById(productId);

        if(product == null){
            return CommonResponse.createForError("产品不存在或已删除");
        }
        //此处判断商品是否处于在售状态，如果未以后后台管理系统要复用代码的话，要加以区分
        if(product.getStatus() != CONSTANT.ProductStatus.ON_SALE.getCode()){
            return CommonResponse.createForError("产品不在售，下架或其他情况");
        }

        ProductDetailVO productDetailVO = productToProductDetailVO(product);
        return CommonResponse.createForSuccess(productDetailVO);
    }

    @Override
    public CommonResponse<Page<ProductListVO>> getProductList(Integer categoryId, String keyword, String orderBy, int pageNum, int pageSize) {
        //客户端提交的keyword和分类ID都为空
        if(StringUtils.isBlank(keyword) && categoryId == null){
            return CommonResponse.createForError(ResponseCode.ARGUMENT_ILLEGAL.getCode(),ResponseCode.ARGUMENT_ILLEGAL.getDescription());
        }

        if(categoryId != null){
            Category category = categoryMapper.selectById(categoryId);
            //查不到前端传来的Category信息，并且keyword也为空，则表明数据库中没有该分类，关键字也为空，则返回空的结果集，打日志
            if(category == null && StringUtils.isBlank(keyword)){
                log.info("没有查到分类ID为{}的商品信息", categoryId);
                return CommonResponse.createForSuccessMessage("没有查到商品信息");
            }
        }

        Page<Product> result = new Page<>();
        result.setCurrent(pageNum);
        result.setSize(pageSize);

        QueryWrapper<Product> queryWrapper = new QueryWrapper<>();

        //查询条件中增加分类查询
        List<Integer> categoryIdList = categoryService.getCategoryAndAllChildren(categoryId).getData();
        if(categoryIdList.size() != 0){
            queryWrapper.in("category_id", categoryIdList);
        }

        //查询条件中增加按分类名称模糊查询
        if(StringUtils.isNotBlank(keyword)){
            //keyword = new StringBuilder().append("%").append(keyword).append("%").toString();
            queryWrapper.like("name", "%"+keyword+"%");
        }

        //查询条件中增加排序条件
        if(StringUtils.isNotBlank(orderBy)){
            if(StringUtils.equals(orderBy, CONSTANT.PRODUCT_ORDER_BY_PRICE_ASC)){
                queryWrapper.orderByAsc("price");
            }else if(StringUtils.equals(orderBy, CONSTANT.PRODUCT_ORDER_BY_PRICE_ASC)){
                queryWrapper.orderByDesc("price");
            }
        }

        result = productMapper.selectPage(result, queryWrapper);
//        List<Product> productList = result.getRecords();
//        List<ProductListVO> productListVOList = ListBeanUtils.copyListProperties(productList, ProductListVO::new, (product, productListVO) -> {
//            productListVO.setImageServer(imageServerConfig.getUrl());
//        } );

        Page<ProductListVO> result1 = ListBeanUtilsForPage.copyPageList(result, ProductListVO::new, (product, productListVO) -> {
            productListVO.setImageServer(imageServerConfig.getUrl());
        } );

        return CommonResponse.createForSuccess(result1);
    }

    private ProductDetailVO productToProductDetailVO(Product product){
        ProductDetailVO productDetailVO = new ProductDetailVO();
        BeanUtils.copyProperties(product, productDetailVO);

        Category category = categoryMapper.selectById(product.getCategoryId());
        productDetailVO.setParentCategoryId(category.getParentId());

        productDetailVO.setImageServer(imageServerConfig.getUrl());
        return productDetailVO;
    }
}
