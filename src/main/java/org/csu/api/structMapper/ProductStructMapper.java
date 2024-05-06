package org.csu.api.structMapper;

import org.csu.api.domain.Product;
import org.csu.api.vo.CartItemVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ProductStructMapper {
    ProductStructMapper INSTANCE = Mappers.getMapper(ProductStructMapper.class);

    @Mapping(source = "name", target = "productName")
    @Mapping(source = "subtitle", target = "productSubtitle")
    @Mapping(source = "price", target = "productPrice")
    @Mapping(source = "stock", target = "productStock")
    CartItemVO productToCartItemVO(Product product);

    @Mapping(source = "product.name", target = "productName")
    @Mapping(source = "product.subtitle", target = "productSubtitle")
    @Mapping(source = "product.price", target = "productPrice")
    @Mapping(source = "product.stock", target = "productStock")
    @Mapping(source = "product.mainImage", target = "productMainImage")
    @Mapping(source = "cartItemVO.id", target = "id")
    @Mapping(source = "cartItemVO.checked", target = "checked")
    CartItemVO productAndCartItemVOTOCartItemVO(CartItemVO cartItemVO, Product product);
}
