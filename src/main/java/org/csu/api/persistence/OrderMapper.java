package org.csu.api.persistence;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.csu.api.domain.Order;
import org.springframework.stereotype.Repository;

@Repository("orderMapper")
public interface OrderMapper extends BaseMapper<Order> {
}
