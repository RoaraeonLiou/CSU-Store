package org.csu.api.persistence;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.csu.api.domain.CartItem;
import org.springframework.stereotype.Repository;

@Repository("cartItemMapper")
public interface CartItemMapper extends BaseMapper<CartItem> {
}
