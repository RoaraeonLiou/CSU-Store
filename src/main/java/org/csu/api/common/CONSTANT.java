package org.csu.api.common;

import lombok.Getter;

public class CONSTANT {

    public static final String LOGIN_USER = "loginUser";
    public static final int CATEGORY_ROOT = 0;

    public interface ROLE{
        int CUSTOMER = 1;
        int ADMIN = 0;
    }

    public interface USER_FIELD{
        String USERNAME = "username";
        String EMAIL = "email";
        String PHONE = "phone";
    }

    public interface ALIPAY{
        String GATEWAY_URL = "https://openapi-sandbox.dl.alipaydev.com/gateway.do";
        String FORMAT = "JSON";
        String CHARSET = "utf-8";
        String SIGN_TYPE = "RSA2";
    }

    @Getter
    public enum ProductStatus{

        ON_SALE(1, "on_sale"),
        TAKE_DOWN(2, "take_down"),
        DELETE(3, "delete");

        private final int code;
        private final String description;

        ProductStatus(int code, String description){
            this.code = code;
            this.description = description;
        }
    }

    public static final String PRODUCT_ORDER_BY_PRICE_ASC = "price_asc";
    public static final String PRODUCT_ORDER_BY_PRICE_DESC = "price_desc";

    public interface CART_ITEM_STATUS{
        int CHECKED = 1;
        int UNCHECKED = 0;
    }

    @Getter
    public enum OrderStatus{

        CANCELED(1, "this order is canceled."),
        NON_PAYMENT(2, "this order has not been paid."),
        PAID(3, "this order has been paid for."),
        SHIPPED(4,"this order has been shipped.");

        private final int code;
        private final String description;

        OrderStatus(int code, String description){
            this.code = code;
            this.description = description;
        }
    }

    @Getter
    public enum PaymentType{

        ALIPAY(1, "Alipay"),
        WECHAT(2, "Wechat"),
        OTHER(3, "Other");

        private final int code;
        private final String description;

        PaymentType(int code, String description){
            this.code = code;
            this.description = description;
        }
    }

}
