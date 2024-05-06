package org.csu.api.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.google.common.collect.Lists;
import jakarta.annotation.Resource;
import org.apache.commons.collections4.CollectionUtils;
import org.csu.api.common.CONSTANT;
import org.csu.api.common.CommonResponse;
import org.csu.api.common.ResponseCode;
import org.csu.api.domain.CartItem;
import org.csu.api.domain.Product;
import org.csu.api.persistence.CartItemMapper;
import org.csu.api.persistence.ProductMapper;
import org.csu.api.service.CartService;
import org.csu.api.structMapper.ProductStructMapper;
import org.csu.api.util.BigDecimalUtil;
import org.csu.api.util.ImageServerConfig;
import org.csu.api.util.ListBeanUtils;
import org.csu.api.vo.CartItemVO;
import org.csu.api.vo.CartVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

@Service("cartService")
public class CartServiceImpl implements CartService {
    @Autowired
    private CartItemMapper cartItemMapper;

    @Autowired
    private ProductMapper productMapper;

    @Resource
    private ImageServerConfig imageServerConfig;


    @Override
    public CommonResponse<CartVO> addCart(Integer userId, Integer productId, Integer quantity) {
        // 1.由于采用Session方式获取用户登陆状态，User ID通过控制器获得，不需要校验，如果采用Token方式则需要验证

        // 2.Product ID需要判断是否存在
        QueryWrapper<Product> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", productId).eq("status", CONSTANT.ProductStatus.ON_SALE.getCode());
        Product product = productMapper.selectOne(queryWrapper);
        if (product == null) {
            return CommonResponse.createForError("商品ID不存在或已下架");
        }

        // int productStock = product.getStock();// 由于getCartVOAndCheckStock不再需要判断库存

        // 3.查询该用户原购物车中，是否包含该商品
        QueryWrapper<CartItem> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.eq("user_id", userId).eq("product_id", productId);
        CartItem cartItem = cartItemMapper.selectOne(queryWrapper1);

        if (cartItem != null) {
            // 4.包含->修改数量
//            if (productStock < cartItem.getQuantity() + quantity) {
//                // TODO: 逻辑感觉有点问题
//                quantity = productStock;
//            }
            UpdateWrapper<CartItem> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("id", cartItem.getId());
            updateWrapper.set("update_time", LocalDateTime.now());
            updateWrapper.set("quantity", quantity);
            cartItemMapper.update(cartItem, updateWrapper);
        } else {
            // 5.不包含->新建购物车项
            cartItem = new CartItem();
            cartItem.setUserId(userId);
            cartItem.setProductId(productId);
//            if (productStock < quantity) {
//                // TODO: 代码重复，考虑重写
//                quantity = productStock;
//            }
            cartItem.setQuantity(quantity);
            cartItem.setChecked(CONSTANT.CART_ITEM_STATUS.CHECKED);
            cartItem.setCreateTime(LocalDateTime.now());
            cartItem.setUpdateTime(LocalDateTime.now());
            cartItemMapper.insert(cartItem);
        }
        return CommonResponse.createForSuccess(this.getCartVOAndCheckStock(userId));
    }

    @Override
    public CommonResponse<CartVO> updateCart(Integer userId, Integer productId, Integer quantity) {
        // Product ID需要判断是否存在
        QueryWrapper<Product> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", productId).eq("status", CONSTANT.ProductStatus.ON_SALE.getCode());
        Product product = productMapper.selectOne(queryWrapper);
        if (product == null) {
            return CommonResponse.createForError("商品ID不存在或已下架");
        }

        // 查询该用户原购物车中，是否包含该商品
        QueryWrapper<CartItem> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.eq("user_id", userId).eq("product_id", productId);
        CartItem cartItem = cartItemMapper.selectOne(queryWrapper1);
        if (cartItem != null) {
            UpdateWrapper<CartItem> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("id", cartItem.getId());
            updateWrapper.set("update_time", LocalDateTime.now());
            updateWrapper.set("quantity", quantity);
            cartItemMapper.update(cartItem, updateWrapper);
            CartVO cartVO = this.getCartVOAndCheckStock(userId);
            return CommonResponse.createForSuccess(cartVO);
        } else {
            return CommonResponse.createForError(ResponseCode.ARGUMENT_ILLEGAL.getCode(), ResponseCode.ARGUMENT_ILLEGAL.getDescription());
        }
//        return null;
    }

    @Override
    public CommonResponse<CartVO> listCart(Integer userId) {
        return CommonResponse.createForSuccess(this.getCartVOAndCheckStock(userId));
    }

    @Override
    public CommonResponse<CartVO> deleteCart(Integer userId, ArrayList<String> productIds) {
        // 没有对商品ID判断有效性，仅删除有效的
        for (String productId : productIds) {
            QueryWrapper<CartItem> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", userId)
                    .eq("product_id", productId);
            cartItemMapper.delete(queryWrapper);
        }
        return CommonResponse.createForSuccess(this.getCartVOAndCheckStock(userId));
    }

    @Override
    public CommonResponse<CartVO> setAllCheckedCart(Integer userId) {
        QueryWrapper<CartItem> queryWrapper = new QueryWrapper<>();
        UpdateWrapper<CartItem> updateWrapper = new UpdateWrapper<>();
        queryWrapper.eq("user_id", userId);
        List<CartItem> cartItemList = cartItemMapper.selectList(queryWrapper);
        for (CartItem cartItem : cartItemList){
            cartItem.setChecked(CONSTANT.CART_ITEM_STATUS.CHECKED);
            updateWrapper.eq("id", cartItem.getId());
            cartItemMapper.update(cartItem, updateWrapper);
            updateWrapper.clear();
        }
        return CommonResponse.createForSuccess(this.getCartVOAndCheckStock(userId));
    }

    @Override
    public CommonResponse<CartVO> setAllUncheckedCart(Integer userId) {
        QueryWrapper<CartItem> queryWrapper = new QueryWrapper<>();
        UpdateWrapper<CartItem> updateWrapper = new UpdateWrapper<>();
        queryWrapper.eq("user_id", userId);
        List<CartItem> cartItemList = cartItemMapper.selectList(queryWrapper);
        for (CartItem cartItem : cartItemList){
            cartItem.setChecked(CONSTANT.CART_ITEM_STATUS.UNCHECKED);
            updateWrapper.eq("id", cartItem.getId());
            cartItemMapper.update(cartItem, updateWrapper);
            updateWrapper.clear();
        }
        return CommonResponse.createForSuccess(this.getCartVOAndCheckStock(userId));
    }

    @Override
    public CommonResponse<CartVO> setCartItemChecked(Integer userId, String productId) {
        UpdateWrapper<CartItem> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("user_id", userId).eq("product_id", productId);
        updateWrapper.set("checked", CONSTANT.CART_ITEM_STATUS.CHECKED);
        cartItemMapper.update(null, updateWrapper);
        return CommonResponse.createForSuccess(this.getCartVOAndCheckStock(userId));
    }

    @Override
    public CommonResponse<CartVO> setCartItemUnchecked(Integer userId, String productId) {
        UpdateWrapper<CartItem> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("user_id", userId).eq("product_id", productId);
        updateWrapper.set("checked", CONSTANT.CART_ITEM_STATUS.UNCHECKED);
        cartItemMapper.update(null, updateWrapper);
        return CommonResponse.createForSuccess(this.getCartVOAndCheckStock(userId));
    }

    @Override
    public CommonResponse<Integer> getCartCount(Integer userId) {
        QueryWrapper<CartItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        List<CartItem> cartItemList = cartItemMapper.selectList(queryWrapper);
        return CommonResponse.createForSuccess("SUCCESS", cartItemList.size());
    }

    private CartVO getCartVOAndCheckStock(Integer userId) {
        CartVO cartVO = new CartVO();
        // 从数据库中将该用户的购物车信息查询出来
        QueryWrapper<CartItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        List<CartItem> cartItemList = cartItemMapper.selectList(queryWrapper);

        List<CartItemVO> cartItemVOList = Lists.newArrayList();
        // 使用该方法创建ArrayList，没有并发的问题
        // 集合类pom.xml文件中apache.commons.commons-collections4, 字符串采用lang3
        AtomicReference<BigDecimal> cartTotalPrice = new AtomicReference<>(new BigDecimal("0"));
        AtomicBoolean allSelected = new AtomicBoolean(true);

        // 判断用户购物车是否为空
        if (CollectionUtils.isNotEmpty(cartItemList)) {
            // 非空
            cartItemVOList = ListBeanUtils.copyListProperties(cartItemList, CartItemVO::new, (cartItem, cartItemVO) -> {
                Product product = productMapper.selectById(cartItem.getProductId());
                if (product != null) {
                    BeanUtils.copyProperties(product, cartItemVO);
                    cartItemVO = ProductStructMapper.INSTANCE.productAndCartItemVOTOCartItemVO(cartItemVO, product);
//                    cartItemVO.setProductPrice(product.getPrice());
                    // 处理库存问题
                    if (product.getStock() >= cartItem.getQuantity()) {
                        cartItemVO.setQuantity(cartItem.getQuantity());
                        cartItemVO.setCheckStock(true);
                    } else {
                        cartItemVO.setQuantity(product.getStock());
                        // OPTIMIZE:updateStockCart好像可以设置为Null
                        CartItem updateStockCart = new CartItem();
                        UpdateWrapper<CartItem> updateWrapper = new UpdateWrapper<>();
                        updateWrapper.eq("id", cartItem.getId());
                        updateWrapper.set("quantity", product.getStock());
                        cartItemMapper.update(updateStockCart, updateWrapper);

                        cartItemVO.setCheckStock(false);
                    }
                    // 处理购物车项总价
                    cartItemVO.setProductTotalPrice(BigDecimalUtil.multiply(cartItemVO.getProductPrice().doubleValue(), cartItemVO.getQuantity()));
                }
                // 计算总价，并判断是否全选
                if (cartItem.getChecked() == CONSTANT.CART_ITEM_STATUS.CHECKED) {
                    // STICKER:lambda中不允许使用括号表达式内变量之外的其他外部变量
                    cartTotalPrice.set(BigDecimalUtil.add(cartTotalPrice.get().doubleValue(), cartItemVO.getProductTotalPrice().doubleValue()));
                } else {
                    allSelected.set(false);
                }
                return cartItemVO;
            });
        }

        cartVO.setCartItemVOList(cartItemVOList);
        cartVO.setCartTotalPrice(cartTotalPrice.get());
        cartVO.setAllSelected(allSelected.get());
        cartVO.setProductImageServer(imageServerConfig.getUrl());
        return cartVO;
    }
}
