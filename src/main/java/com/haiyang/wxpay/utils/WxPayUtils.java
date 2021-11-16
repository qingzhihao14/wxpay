package com.haiyang.wxpay.utils;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class WxPayUtils implements InitializingBean {

    @Value("${wx.pay.app_id}")
    private String appId;

    @Value("${wx.pay.partner}")
    private String partner;

    @Value("${wx.pay.partnerkey}")
    private String partnerKey;
    @Value("${wx.pay.notifyurl}")
    private String notifyUrl;


    public static String WX_PAY_APP_ID;
    public static String WX_PAY_PARTNER;
    public static String WX_PAY_PARTNER_KEY;
    public static String WX_OPEN_NOTIFY_URL;

    @Override
    public void afterPropertiesSet() throws Exception {
        WX_PAY_APP_ID = appId;
        WX_PAY_PARTNER = partner;
        WX_PAY_PARTNER_KEY = partnerKey;
        WX_OPEN_NOTIFY_URL = notifyUrl;
    }

}
