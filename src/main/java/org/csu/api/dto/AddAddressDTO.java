package org.csu.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AddAddressDTO {
    @NotBlank(message = "收件人姓名不能为空")
    private String addressName;
    @NotBlank(message = "收件人固定电话不能为空")
    private String addressPhone;
    @NotBlank(message = "收件人手机电话不能为空")
    private String addressMobile;
    @NotBlank(message = "省份不能为空")
    private String addressProvince;
    @NotBlank(message = "城市不能为空")
    private String addressCity;
    @NotBlank(message = "区县不能为空")
    private String addressDistrict;
    @NotBlank(message = "详细地址不能为空")
    private String addressDetail;
    @NotBlank(message = "邮编不能为空")
    private String addressZip;
}
