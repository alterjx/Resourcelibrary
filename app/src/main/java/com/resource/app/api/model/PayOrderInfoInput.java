package com.resource.app.api.model;

/**
 * TODO: description
 */

public class PayOrderInfoInput extends BaseInput {
    public String productId;
    public String userId;
    public int payId; //1支付宝 2微信
}
