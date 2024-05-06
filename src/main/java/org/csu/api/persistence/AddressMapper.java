package org.csu.api.persistence;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.csu.api.domain.Address;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressMapper extends BaseMapper<Address> {
}
