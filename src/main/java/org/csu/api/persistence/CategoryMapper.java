package org.csu.api.persistence;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.csu.api.domain.Category;
import org.springframework.stereotype.Repository;

@Repository("categoryMapper")
public interface CategoryMapper extends BaseMapper<Category> {

}
