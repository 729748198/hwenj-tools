import com.hwenj.wulin.tools.MD5Util;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.ssl.TrustStrategy;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

import javax.net.ssl.SSLContext;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author hwenj
 * @since 2022/11/14
 */

public class Cms_Api_pro {

    //public static final String basic_local_url = "https://k01.weishi110.cn:9994/cloudapi/v2/intelligence/";
   // public static final String basic_local_url = "http://192.168.1.87/api/cloudapi/v2/intelligence/";
    public static final String basic_local_url = "https://106.37.174.36/api/cloudapi/v2/intelligence/";
    //public static final String basic_local_url = "https://k01.weishi110.cn:19994/cloudapi/v2/intelligence/";
   // public static final String basic_local_url = "https://106.37.174.18:19994/cloudapi/v2/intelligence/";
   // public static final String basic_local_url = "http://localhost:9888/cloudapi/v2/intelligence/";


    @Test
    public void queryDomain() throws Exception {
        String host = "btpnav.com";
        System.out.println("基础信息:");
        basic(host);
        System.out.println("攻击情报信息:");
        attack(host);
        System.out.println("外联情报信息:");
        out(host);
        System.out.println("外联统计列表信息:");
        relation(host);
    }


    public void basic(String host) throws Exception {
        //配置，发送https请求时，忽略ssl证书认证（否则会报错没有证书）
        SSLContext sslContext = null;

        sslContext = SSLContexts.custom().loadTrustMaterial(null, new TrustStrategy() {
            @Override
            public boolean isTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                return true;
            }
        }).build();
        //创建httpClient
        CloseableHttpResponse httpResponse = null;
        String result = "";
        // 创建httpClient实例
        CloseableHttpClient httpClient =  HttpClients.custom().setSslcontext(sslContext).
                setSSLHostnameVerifier(new NoopHostnameVerifier()).build();
        // 创建httpPost远程连接实例
        HttpPost httpPost = new HttpPost(basic_local_url+"basic");
        // 配置请求参数实例(不需要可忽略)
        RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(35000)// 设置连接主机服务超时时间
                .setConnectionRequestTimeout(35000)// 设置连接请求超时时间
                .setSocketTimeout(60000)// 设置读取数据连接超时时间
                .build();
        // 为httpPost实例设置配置(不需要可忽略)
        httpPost.setConfig(requestConfig);
        // 设置请求头
        httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        // 封装表单参数
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        Map<String, Object> paramMap = buildParam();
        paramMap.put("host",host);
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
        System.out.println(EntityUtils.toString(httpResponse.getEntity()));
        // 从响应对象中获取响应内容
        // EntityUtils.toString()有重载方法
        // 这个静态方法将HttpEntity转换成字符串,防止服务器返回的数据带有中文,所以在转换的时候将字符集指定成utf-8即可
//            HttpEntity entity = httpResponse.getEntity();
//            result = EntityUtils.toString(entity, "UTF-8");

    }


    public void attack(String host) throws Exception{
        //配置，发送https请求时，忽略ssl证书认证（否则会报错没有证书）
        SSLContext sslContext = null;

        sslContext = SSLContexts.custom().loadTrustMaterial(null, new TrustStrategy() {
            @Override
            public boolean isTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                return true;
            }
        }).build();
        //创建httpClient
        CloseableHttpResponse httpResponse = null;
        String result = "";
        // 创建httpClient实例
        CloseableHttpClient httpClient =  HttpClients.custom().setSslcontext(sslContext).
                setSSLHostnameVerifier(new NoopHostnameVerifier()).build();
        // 创建httpPost远程连接实例
        HttpPost httpPost = new HttpPost(basic_local_url+"attack");
        // 配置请求参数实例(不需要可忽略)
        RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(35000)// 设置连接主机服务超时时间
                .setConnectionRequestTimeout(35000)// 设置连接请求超时时间
                .setSocketTimeout(60000)// 设置读取数据连接超时时间
                .build();
        // 为httpPost实例设置配置(不需要可忽略)
        httpPost.setConfig(requestConfig);
        // 设置请求头
        httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        // 封装表单参数
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        Map<String, Object> paramMap = buildParam();
        paramMap.put("host",host);
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
        System.out.println(EntityUtils.toString(httpResponse.getEntity()));
    }


    public void out(String host) throws Exception{
        //配置，发送https请求时，忽略ssl证书认证（否则会报错没有证书）
        SSLContext sslContext = null;

        sslContext = SSLContexts.custom().loadTrustMaterial(null, new TrustStrategy() {
            @Override
            public boolean isTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                return true;
            }
        }).build();
        //创建httpClient
        CloseableHttpResponse httpResponse = null;
        String result = "";
        // 创建httpClient实例
        CloseableHttpClient httpClient =  HttpClients.custom().setSslcontext(sslContext).
                setSSLHostnameVerifier(new NoopHostnameVerifier()).build();
        // 创建httpPost远程连接实例
        HttpPost httpPost = new HttpPost(basic_local_url+"outreach");
        // 配置请求参数实例(不需要可忽略)
        RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(35000)// 设置连接主机服务超时时间
                .setConnectionRequestTimeout(35000)// 设置连接请求超时时间
                .setSocketTimeout(60000)// 设置读取数据连接超时时间
                .build();
        // 为httpPost实例设置配置(不需要可忽略)
        httpPost.setConfig(requestConfig);
        // 设置请求头
        httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        // 封装表单参数
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        Map<String, Object> paramMap = buildParam();
        paramMap.put("host",host);
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
        System.out.println(EntityUtils.toString(httpResponse.getEntity()));
    }


    public void relation(String host) throws Exception{
        //配置，发送https请求时，忽略ssl证书认证（否则会报错没有证书）
        SSLContext sslContext = null;

        sslContext = SSLContexts.custom().loadTrustMaterial(null, new TrustStrategy() {
            @Override
            public boolean isTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                return true;
            }
        }).build();
        //创建httpClient
        CloseableHttpResponse httpResponse = null;
        String result = "";
        // 创建httpClient实例
        CloseableHttpClient httpClient =  HttpClients.custom().setSslcontext(sslContext).
                setSSLHostnameVerifier(new NoopHostnameVerifier()).build();
        // 创建httpPost远程连接实例
        HttpPost httpPost = new HttpPost(basic_local_url+"relation");
        // 配置请求参数实例(不需要可忽略)
        RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(35000)// 设置连接主机服务超时时间
                .setConnectionRequestTimeout(35000)// 设置连接请求超时时间
                .setSocketTimeout(60000)// 设置读取数据连接超时时间
                .build();
        // 为httpPost实例设置配置(不需要可忽略)
        httpPost.setConfig(requestConfig);
        // 设置请求头
        httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        // 封装表单参数
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        Map<String, Object> paramMap = buildParam();
        paramMap.put("host",host);
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
        System.out.println(EntityUtils.toString(httpResponse.getEntity()));
    }

    @Test
    public void batchOutReach() throws Exception{
        //配置，发送https请求时，忽略ssl证书认证（否则会报错没有证书）
        SSLContext sslContext = null;

        sslContext = SSLContexts.custom().loadTrustMaterial(null, new TrustStrategy() {
            @Override
            public boolean isTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                return true;
            }
        }).build();
        //创建httpClient
        CloseableHttpResponse httpResponse = null;
        String result = "";
        // 创建httpClient实例
        CloseableHttpClient httpClient =  HttpClients.custom().setSslcontext(sslContext).
                setSSLHostnameVerifier(new NoopHostnameVerifier()).build();
        // 创建httpPost远程连接实例
        HttpPost httpPost = new HttpPost(basic_local_url+"outreach/batch");
        // 配置请求参数实例(不需要可忽略)
        RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(35000)// 设置连接主机服务超时时间
                .setConnectionRequestTimeout(35000)// 设置连接请求超时时间
                .setSocketTimeout(60000)// 设置读取数据连接超时时间
                .build();
        // 为httpPost实例设置配置(不需要可忽略)
        httpPost.setConfig(requestConfig);
        // 设置请求头
        httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        // 封装表单参数
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        Map<String, Object> paramMap = buildParam();
        paramMap.put("hostList","61.158.219.41,171.43.44.54,113.88.167.214");
        paramMap.put("appid","2DBF69F1B58B0AB9DC4EA7CCBFDB4361");
        paramMap.put("token","5f07a34385586deb7a0933162e94de1c");
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
        System.out.println(EntityUtils.toString(httpResponse.getEntity()));
    }


    public static Map<String, Object> buildParam() {
        long timestamp = new Date().getTime();
        String machineId = "F028A28DEE9E71AACA6E20E04C81F400";
        String authCode = "FQAFM3JL6YN2N8FI5ANZP74W6M4CCBVY";
        String vToken = MD5Util.MD5String(machineId + ":" + authCode + ":" + timestamp);
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("timestamp", timestamp);
        paramMap.put("token", vToken);
        paramMap.put("appid", machineId);
        paramMap.put("host", "3.3.3.3");
        return paramMap;
    }
}
