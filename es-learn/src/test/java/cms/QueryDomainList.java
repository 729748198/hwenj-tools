package cms;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hwenj.es.learn.MD5Util;
import org.apache.http.HttpHost;
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
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.jupiter.api.Test;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @author : hewenjie
 * @create 2022/11/17
 */
public class QueryDomainList {
    private static final String url = "http://192.168.1.64:3000/api/v1/hosts/detail";
    public static final String basic_local_url = "http://localhost:9888/cloudapi/v2/intelligence/";


    private RestHighLevelClient restHighLevelClient231;

    @Test
    public void testAttack() throws Exception {
        init();
        String index = "k01_attack_src_ip_history";
        SearchRequest searchRequest = new SearchRequest();
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder queryBuilder2 = QueryBuilders.boolQuery();
        queryBuilder2.must(QueryBuilders.termQuery("ip_type", "1"));
        sourceBuilder.size(1000);
        sourceBuilder.query(queryBuilder2);
        sourceBuilder.from(3000);
        searchRequest.indices(index).source(sourceBuilder);
        SearchResponse search = restHighLevelClient231.search(searchRequest);
        SearchHit[] hits = search.getHits().getHits();
        for (int i = 0; i < hits.length; i++) {
            String ip = hits[i].getSourceAsMap().get("ip").toString();
//            String myAppid = "gfd01";
//            long myTimestamp = new Date().getTime();
//            String authCode = "PEXRZMMDDK763MVWYBZAKDPFSY27BHQT";
//            String myToken = MD5Util.MD5String(myAppid + ":" + authCode + ":" + myTimestamp);
//           // System.out.println("普通ip" + ip);
//            String ipInfo = HttpUtil.sendGet(url, "appid=" + myAppid + "&token=" + myToken + "&timestamp=" + myTimestamp + "&host=" + ip.trim());
//            JSONObject jsonObject = JSON.parseObject(ipInfo);
//            boolean status = jsonObject.getBoolean("status");
//            if (status == true) {
//                JSONArray data = jsonObject.getJSONArray("data");
//                JSONArray jsonArray = jsonObject.getJSONArray("domains");
//
//                String domainCount = "0";
//                List<String> domainList = new ArrayList<>();
//                if (jsonArray != null && jsonArray.size() > 0) {
//                    for (int j = 0; i < jsonArray.size(); j++) {
//                        domainList.add(jsonArray.get(j).toString());
//                    }
//                    domainCount = String.valueOf(jsonArray.size());
//                }
//                if (Integer.valueOf(domainCount) > 0) {
//                    System.out.println("有domain的ip" + ip);
//                }
//            }

            String info = basic(ip);
            JSONObject jsonObject = JSON.parseObject(info);
            JSONObject data = jsonObject.getJSONObject("data");
            if (Objects.nonNull(data)) {
                JSONObject c7 = data.getJSONObject("c7");
                if (Objects.nonNull(c7)) {
                  //  System.out.println("有ip: "+ip);
                }else {
                  //  System.out.println("无ip："+ip);
                }
            }else {
                System.out.println(ip);
            }

        }

    }


    @Test
    public void testDomainOut() throws Exception {
        init();
        String index = "k01_external_intelligence_all";
        SearchRequest searchRequest = new SearchRequest();
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder queryBuilder2 = QueryBuilders.boolQuery();
        queryBuilder2.must(QueryBuilders.termQuery("ioc_type_agg", "domain"));
        //queryBuilder2.must(QueryBuilders.wildcardQuery("ioc.keyword", "*.com"));
        sourceBuilder.size(1000);
        sourceBuilder.query(queryBuilder2);
        sourceBuilder.from(0);
        searchRequest.indices(index).source(sourceBuilder);
        SearchResponse search = restHighLevelClient231.search(searchRequest);
        SearchHit[] hits = search.getHits().getHits();
        for (int i = 0; i < hits.length; i++) {
            String ip = hits[i].getSourceAsMap().get("ioc").toString();
            String myAppid = "gfd01";
            long myTimestamp = new Date().getTime();
            String authCode = "PEXRZMMDDK763MVWYBZAKDPFSY27BHQT";
            String myToken = MD5Util.MD5String(myAppid + ":" + authCode + ":" + myTimestamp);
            // System.out.println("普通ip" + ip);
            String ipInfo = HttpUtil.sendGet(url, "appid=" + myAppid + "&token=" + myToken + "&timestamp=" + myTimestamp + "&host=" + ip.trim());
            JSONObject jsonObject = JSON.parseObject(ipInfo);
            boolean status = jsonObject.getBoolean("status");
            if (status == true) {
                JSONArray data = jsonObject.getJSONArray("data");
                if (data.size() > 0) {
                    System.out.println("有"+ip);
                }else {
                    System.out.println("无"+ip);
                }
            }


        }

    }

    @Test
    public void testDomainOutWithRelation() throws Exception {
        init();
        String index = "k01_external_intelligence_all";
        SearchRequest searchRequest = new SearchRequest();
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder queryBuilder2 = QueryBuilders.boolQuery();
        queryBuilder2.must(QueryBuilders.termQuery("ioc_type_agg", "domain"));
        //queryBuilder2.must(QueryBuilders.wildcardQuery("ioc.keyword", "*.com"));
        sourceBuilder.size(1000);
        sourceBuilder.query(queryBuilder2);
        sourceBuilder.from(0);
        searchRequest.indices(index).source(sourceBuilder);
        SearchResponse search = restHighLevelClient231.search(searchRequest);
        SearchHit[] hits = search.getHits().getHits();
        for (int i = 0; i < hits.length; i++) {
            String ip = hits[i].getSourceAsMap().get("ioc").toString();
//             Integer relation = relation(ip);
//             if(relation>100){
//                 System.out.println(ip);
//             }
            System.out.println(ip);

        }

    }


    public String basic(String host) throws Exception {
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
        CloseableHttpClient httpClient = HttpClients.custom().setSslcontext(sslContext).
                setSSLHostnameVerifier(new NoopHostnameVerifier()).build();
        // 创建httpPost远程连接实例
        HttpPost httpPost = new HttpPost(basic_local_url + "basic");
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
        paramMap.put("host", host);
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
        return EntityUtils.toString(httpResponse.getEntity());
        // 从响应对象中获取响应内容
        // EntityUtils.toString()有重载方法
        // 这个静态方法将HttpEntity转换成字符串,防止服务器返回的数据带有中文,所以在转换的时候将字符集指定成utf-8即可
//            HttpEntity entity = httpResponse.getEntity();
//            result = EntityUtils.toString(entity, "UTF-8");

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


    @Test
    public void testIp() throws IOException {
        init();
        String index = "export_internal_info_2022-10-24";
        SearchRequest searchRequest = new SearchRequest();
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(QueryBuilders.termQuery("infoType.keyword", "ip"));
        sourceBuilder.query(boolQueryBuilder);
        sourceBuilder.size(50);
        sourceBuilder.from(100);
        searchRequest.indices(index).source(sourceBuilder);
        SearchResponse search = restHighLevelClient231.search(searchRequest);
        SearchHit[] hits = search.getHits().getHits();
        for (int i = 0; i < hits.length; i++) {
            System.out.println(hits[i].getSourceAsMap().get("infoName"));
        }

    }

    private void init() {

        String[] split = "192.168.1.231:9200".split(",");
        HttpHost[] array = new HttpHost[split.length];
        for (int i = 0; i < split.length; i++) {
            String item = split[i];
            array[i] = new HttpHost(item.split(":")[0], Integer.parseInt(item.split(":")[1]), "http");
        }
        RestClientBuilder builder = RestClient.builder(array);
        builder.setMaxRetryTimeoutMillis(2 * 60 * 1000);
        restHighLevelClient231 = new RestHighLevelClient(builder);
    }

    public Integer relation(String host) throws Exception{
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
        return EntityUtils.toString(httpResponse.getEntity()).length();
    }
}
