package org.csu.api.persistence;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.csu.api.domain.OrderItem;
import org.springframework.stereotype.Repository;

@Repository("orderItemMapper")
public interface OrderItemMapper extends BaseMapper<OrderItem> {
}
