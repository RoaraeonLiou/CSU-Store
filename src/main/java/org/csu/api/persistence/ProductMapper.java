package org.csu.api.persistence;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.csu.api.domain.Product;
import org.springframework.stereotype.Repository;

@Repository("productMapper")
public interface ProductMapper extends BaseMapper<Product> {
}
