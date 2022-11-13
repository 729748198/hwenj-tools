package com.hwenj.wulin;

import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author hwenj
 * @since 2022/10/14
 */
public class 武林三国 {

    private static String openClass = "http://wz106.sg.9wee.com/modules/military/shop_box.php";

    private static String cookie = "wuL=%u6B8B%u79CB%u5BBF%u8BED; tmp_user_add1=5q6L56eL5a6%2F6K%2Bt; tmp_show_alert=123; tmp_user_add2=MTUzNio4NjQ%3D; tmp_msg_time=2022-10-05%2022%3A48%3A58; __utma=1831918.1440570666.1664772464.1665399270.1665572482.10; __utmz=1831918.1665572482.10.9.utmcsr=wz106.sg.9wee.com|utmccn=(referral)|utmcmd=referral|utmcct=/; weeCookie=%7B%22loginFlag%22%3Atrue%2C%22uid%22%3A%22128070092%22%2C%22username%22%3A%22%5Cu6b8b%5Cu79cb%5Cu5bbf%5Cu8bed%22%2C%22nickname%22%3A%22%5Cu8d3a%5Cu6587%5Cu6770%22%2C%22usertype%22%3A%220%22%2C%22email%22%3A%22%22%2C%22urb%22%3A%221997-04-23%22%2C%22mac%22%3A%22cecc3f0ba313f4062f8aa57d1a1c7143%22%7D; tmp_passport_reg_time=1373991799; user_account=%E6%AE%8B%E7%A7%8B%E5%AE%BF%E8%AF%AD; user_nickname=%E8%B4%BA%E6%96%87%E6%9D%B0; user_add=7fbb8061d3cc8f159c4bed568d651b09; user_id=7655; login_times=11; belong_country=2; union_id=7; 503g_login=1; user_register_time=2022-10-01+23%3A42%3A30; server_create_time=2008-03-09+14%3A00%3A00; tmp_del_time=0; arr_map_id=a%3A1%3A%7Bi%3A0%3Bs%3A6%3A%22381634%22%3B%7D; current_map_id=381634; city_name=%E5%A4%A9%E4%B8%8A%E7%99%BD%E7%8E%89%E4%BA%AC; task_id=20; login_key=acc231b442c7024ac4cbfe94aa07ded5; CWSSESSID=fd618d573c1611471cb8c74e0a19521d";

    public static void main(String[] args) {

        int i = 1;
        int fail = 0;
        while (true) {
            try {
                String s = postformRequest(openClass, buildParam());
                if (!"200".equals(s)) {
                    break;
                }
                System.out.println("成功次数" + i++ + "    失败次数" + fail);
            } catch (Exception e) {
                fail++;
                System.out.println(e);
            }
        }

//        for (int j = 0; j < 60000; j++) {
//            try {
//                String s = postformRequest(openClass, buildParam());
//                if (!"200".equals(s)) {
//                    break;
//                }
//                System.out.println("成功次数" + j);
//            } catch (Exception e) {
//                System.out.println(e);
//            }
//        }
    }

    public static Map<String, Object> buildParam() {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("ajaxId", "_" + new Date().getTime());
        paramMap.put("act", "shop_box");
        paramMap.put("type", "e");
        paramMap.put("cache", "false");
        paramMap.put("choice", "2");
        paramMap.put("ptyle", "2");
        paramMap.put("pkey", "886e0f22c6b30b95574378708260b5f2");
        paramMap.put("pid", "55");
        paramMap.put("action", "insert");
        paramMap.put("_", "");
        paramMap.put("r", new Date().getTime());
        return paramMap;
    }

    public static synchronized String postformRequest(String url, Map<String, Object> paramMap) throws Exception {
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse httpResponse = null;
        String result = "";
        // 创建httpClient实例
        httpClient = HttpClients.createDefault();
        // 创建httpPost远程连接实例
        HttpPost httpPost = new HttpPost(url);
        // 配置请求参数实例(不需要可忽略)
        RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(35000)// 设置连接主机服务超时时间
                .setConnectionRequestTimeout(35000)// 设置连接请求超时时间
                .setSocketTimeout(60000)// 设置读取数据连接超时时间
                .build();
        // 为httpPost实例设置配置(不需要可忽略)
        httpPost.setConfig(requestConfig);
        // 设置请求头
        httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        httpPost.addHeader("Cookie", cookie);
        httpPost.addHeader("Cache-Control", "no-cache");
        httpPost.addHeader("Connection", "keep-alive");
        httpPost.addHeader("Accept", "text/javascript, text/html, application/xml, text/xml, */*");
        httpPost.addHeader("Accept-Encoding", "gzip, deflate");
        httpPost.addHeader("Host", "wz106.sg.9wee.com");
        httpPost.addHeader("Origin", "wz106.sg.9wee.com");
        httpPost.addHeader("Referer", "wz106.sg.9wee.com");
        httpPost.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/106.0.0.0 Safari/537.36");
        httpPost.addHeader("X-Prototype-Version", "1.6.0.2");
        httpPost.addHeader("X-Requested-With", "XMLHttpRequest");
        // 封装表单参数
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        if (null != paramMap && paramMap.size() > 0) {
            // 以下代码使用实现类BasicNameValuePair生成NameValuePair
            // 通过map集成entrySet方法获取entity
            Set<Map.Entry<String, Object>> entrySet = paramMap.entrySet();
            // 循环遍历，获取迭代器
            Iterator<Map.Entry<String, Object>> iterator = entrySet.iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, Object> mapEntry = iterator.next();
                nvps.add(new BasicNameValuePair(mapEntry.getKey(), mapEntry.getValue().toString()));
            }
        }
        // 为httpPost设置封装好的请求参数

        httpPost.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
        // 服务器返回的所有信息都在HttpResponse中, httpClient对象执行post请求,并返回响应参数对象
        httpResponse = httpClient.execute(httpPost);
        // 先取出服务器返回的状态码,如果等于200说明success
        int code = httpResponse.getStatusLine().getStatusCode();
        return String.valueOf(code);
        // 从响应对象中获取响应内容
        // EntityUtils.toString()有重载方法
        // 这个静态方法将HttpEntity转换成字符串,防止服务器返回的数据带有中文,所以在转换的时候将字符集指定成utf-8即可
//            HttpEntity entity = httpResponse.getEntity();
//            result = EntityUtils.toString(entity, "UTF-8");

    }
}

