package org.csu.api.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.google.common.collect.Lists;
import org.csu.api.common.CommonResponse;
import org.csu.api.domain.Address;
import org.csu.api.dto.AddAddressDTO;
import org.csu.api.dto.UpdateAddressDTO;
import org.csu.api.persistence.AddressMapper;
import org.csu.api.service.AddressService;
import org.csu.api.util.ListBeanUtils;
import org.csu.api.vo.AddressVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service("addressService")
public class AddressServiceImpl implements AddressService {
    @Autowired
    private AddressMapper addressMapper;

    @Override
    public CommonResponse<AddressVO> add(Integer userId, AddAddressDTO addAddressDTO) {
        Address address = new Address();
        BeanUtils.copyProperties(addAddressDTO, address);
        address.setUserId(userId);
        address.setCreateTime(LocalDateTime.now());
        address.setUpdateTime(LocalDateTime.now());
        int rows = addressMapper.insert(address);
        if (rows == 0) {
            return CommonResponse.createForError("添加地址失败");
        }
        AddressVO addressVO = new AddressVO();
        BeanUtils.copyProperties(address, addressVO);
        return CommonResponse.createForSuccess("添加地址成功", addressVO);
    }

    @Override
    public CommonResponse<String> delete(Integer userId, String addressId) {
        QueryWrapper<Address> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                .eq("id", addressId);
        addressMapper.delete(queryWrapper);
        return CommonResponse.createForSuccess("删除地址成功");
    }

    @Override
    public CommonResponse<AddressVO> update(Integer userId, UpdateAddressDTO updateAddressDTO) {
        // 判断地址是否存在且为该用户所属
        QueryWrapper<Address> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                .eq("id", updateAddressDTO.getId());
        Address address = addressMapper.selectOne(queryWrapper);
        if (address == null) {
            return CommonResponse.createForError("地址不存在");
        }
        // 修改地址
        UpdateWrapper<Address> updateWrapper = new UpdateWrapper<>();
        BeanUtils.copyProperties(updateAddressDTO, address);
        updateWrapper.eq("id", updateAddressDTO.getId());
        addressMapper.update(address, updateWrapper);
        AddressVO addressVO = new AddressVO();
        BeanUtils.copyProperties(address, addressVO);
        return CommonResponse.createForSuccess(addressVO);
    }

    @Override
    public CommonResponse<AddressVO> find(Integer userId, String addressId) {
        // 判断地址是否存在且为该用户所属
        QueryWrapper<Address> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                .eq("id", addressId);
        Address address = addressMapper.selectOne(queryWrapper);
        if (address == null) {
            return CommonResponse.createForError("地址不存在");
        }
        AddressVO addressVO = new AddressVO();
        BeanUtils.copyProperties(address, addressVO);
        return CommonResponse.createForSuccess(addressVO);
    }

    @Override
    public CommonResponse<List<AddressVO>> list(Integer userId) {
        QueryWrapper<Address> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        List<Address> addressArrayList = addressMapper.selectList(queryWrapper);
        List<AddressVO> addressVOArrayList = ListBeanUtils.copyListProperties(addressArrayList, AddressVO::new);

        return CommonResponse.createForSuccess("查询成功", addressVOArrayList);
    }
}
