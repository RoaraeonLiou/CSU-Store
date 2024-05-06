package org.csu.api.service;

import org.csu.api.common.CommonResponse;
import org.csu.api.dto.AddAddressDTO;
import org.csu.api.dto.UpdateAddressDTO;
import org.csu.api.vo.AddressVO;

import java.util.ArrayList;
import java.util.List;

public interface AddressService {
    CommonResponse<AddressVO> add(Integer userId, AddAddressDTO addAddressDTO);
    CommonResponse<String> delete(Integer userId, String addressId);
    CommonResponse<AddressVO> update(Integer userID, UpdateAddressDTO updateAddressDTO);
    CommonResponse<AddressVO> find(Integer userId, String addressId);
    CommonResponse<List<AddressVO>> list(Integer userId);
}
