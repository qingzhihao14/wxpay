package com.haiyang.wxpay.controller;

import com.github.wxpay.sdk.WXPayUtil;
import com.haiyang.wxpay.utils.HttpClient;
import com.haiyang.wxpay.utils.WxPayUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Controller
@RequestMapping("/wxpay")
public class WxPayController {
    @RequestMapping("/pay")
    public String createPayQRcode(Model model) throws Exception{
        //设置参数
        //价格
        String price = "0.01";
        //生成订单号
        String no = getOrderNo();
        //用map封装参数
        Map m = new HashMap();
        m.put("appid", WxPayUtils.WX_PAY_APP_ID);
        m.put("mch_id", WxPayUtils.WX_PAY_PARTNER);
        m.put("nonce_str", WXPayUtil.generateNonceStr());//生成随机字符串
        m.put("body","微信支付测试"); //主体信息
        m.put("out_trade_no", no); //订单唯一标识
        m.put("total_fee", getMoney(price));//金额
        m.put("spbill_create_ip", "127.0.0.1");//项目的域名
        m.put("notify_url", WxPayUtils.WX_OPEN_NOTIFY_URL);//回调地址
        m.put("trade_type", "NATIVE");//生成二维码的类型
        //发送httpclient请求，传递参数xml格式，微信支付提供的固定的地址
        HttpClient client = new HttpClient("https://api.mch.weixin.qq.com/pay/unifiedorder");
        //设置xml格式的参数
        //把xml格式的数据加密
        client.setXmlParam(WXPayUtil.generateSignedXml(m, WxPayUtils.WX_PAY_PARTNER_KEY));
        client.setHttps(true);
        //执行post请求发送
        client.post();
        //得到发送请求返回结果
        //返回内容，是使用xml格式返回
        String xml = client.getContent();
        //把xml格式转换map集合，把map集合返回
        Map<String,String> resultMap = WXPayUtil.xmlToMap(xml);
        //返回的数据
        Map map = new HashMap();
        map.put("no", no);
        map.put("price", price);
        map.put("code_url", resultMap.get("code_url"));
        model.addAttribute("map",map);
        //指定页面
        return "pay";
    }

    @GetMapping("queryorder/{no}")
    @ResponseBody
    public String queryPayStatus(@PathVariable String no) throws Exception{
        //设置参数
        Map m = new HashMap<>();
        m.put("appid", WxPayUtils.WX_PAY_APP_ID);
        m.put("mch_id", WxPayUtils.WX_PAY_PARTNER);
        m.put("out_trade_no", no);
        m.put("nonce_str", WXPayUtil.generateNonceStr());

        //发送httpclient
        HttpClient client = new HttpClient("https://api.mch.weixin.qq.com/pay/orderquery");
        client.setXmlParam(WXPayUtil.generateSignedXml(m, WxPayUtils.WX_PAY_PARTNER_KEY));
        client.setHttps(true);
        client.post();

        //3.得到订单数据
        String xml = client.getContent();
        Map<String, String> resultMap = WXPayUtil.xmlToMap(xml);

        //4.判断是否支付成功
        if(resultMap.get("trade_state").equals("SUCCESS")) {
            /*
                  改变数据库中的数据等操作
             */
            return "支付成功";
        }
        return "支付中";
    }

    @GetMapping("success")
    public String success(){
        return "success";
    }
    @RequestMapping("test")
    public String test(){
        return "pay";
    }
    /**
     * 生成订单号
     * @return
     */
    public static String getOrderNo() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String newDate = sdf.format(new Date());
        String result = "";
        Random random = new Random();
        for (int i = 0; i < 3; i++) {
            result += random.nextInt(10);
        }
        return newDate + result;
    }
    /**
     * 元转换成分
     * @param amount
     * @return
     */
    public static String getMoney(String amount) {
        if(amount==null){
            return "";
        }
        // 金额转化为分为单位
        // 处理包含, ￥ 或者$的金额
        String currency =  amount.replaceAll("\\$|\\￥|\\,", "");
        int index = currency.indexOf(".");
        int length = currency.length();
        Long amLong = 0l;
        if(index == -1){
            amLong = Long.valueOf(currency+"00");
        }else if(length - index >= 3){
            amLong = Long.valueOf((currency.substring(0, index+3)).replace(".", ""));
        }else if(length - index == 2){
            amLong = Long.valueOf((currency.substring(0, index+2)).replace(".", "")+0);
        }else{
            amLong = Long.valueOf((currency.substring(0, index+1)).replace(".", "")+"00");
        }
        return amLong.toString();
    }
}
