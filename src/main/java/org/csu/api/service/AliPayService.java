package org.csu.api.service;

import org.csu.api.common.CommonResponse;

import java.util.Map;

public interface AliPayService {
    CommonResponse<String> pay(Integer userId, String orderNo);
    CommonResponse<String> payNotify(Map<String,String> params) throws Exception;
}
