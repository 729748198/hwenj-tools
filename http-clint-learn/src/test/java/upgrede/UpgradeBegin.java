package upgrede;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

import java.io.IOException;
import java.util.Date;

/**
 * @author hwenj
 * @since 2022/11/25
 */
@Slf4j
public class UpgradeBegin {

    private String getJson() {
        return "{\n" +
                "    \"authCode\": \"6JA82Z7465WVC3F3KU4QLUWX8AMI5X3A\", \n" +
                "    \"machineId\": \"6F3DDDC0739D7C958F57E5AACC20600B\", \n" +
                "    \"timestamp\": 1668762928, \n" +
                "    \"taskId\": 2, \n" +
                "    \"stepId\": \"V2.1(8.2.2.24598-9f9dd927)\"\n" +
                "}";
    }

    @Test
    public void  testBegin() throws IOException {
        //String url = "http://192.168.1.127:10888/upgrade/api/query";
        String url = "http://localhost:10888/upgrade/api/begin";
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost( url );// 创建httpPost
        httpPost.setHeader( "Accept", "application/json" );
        httpPost.setHeader( "Content-Type", "application/json" );
        String charSet = "UTF-8";
        long  date = new Date().getTime();
        String json =getJson();
        JSONObject jsonObject = JSON.parseObject(json);
        jsonObject.put("timestamp",new Date().getTime()/1000);
        jsonObject.put("taskId","1596075504030535680");
        jsonObject.put("stepId","1596075504030535681");
        StringEntity entity = new StringEntity( jsonObject.toJSONString(), charSet );
        httpPost.setEntity( entity );
        CloseableHttpResponse response = null;

        try {

            response = httpclient.execute( httpPost );
            StatusLine status = response.getStatusLine();
            int state = status.getStatusCode();
            if (state == HttpStatus.SC_OK) {
                HttpEntity responseEntity = response.getEntity();
                String jsonString = EntityUtils.toString( responseEntity );
                System.out.println(jsonString);
            } else {
                log.error( "请求返回:" + state + "(" + url + ")" );
            }
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    @Test
    public void  testEnd() throws IOException {
        //String url = "http://192.168.1.127:10888/upgrade/api/query";
        String url = "http://localhost:10888/upgrade/api/end";
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost( url );// 创建httpPost
        httpPost.setHeader( "Accept", "application/json" );
        httpPost.setHeader( "Content-Type", "application/json" );
        String charSet = "UTF-8";
        long  date = new Date().getTime();
        String json =getJson();
        JSONObject jsonObject = JSON.parseObject(json);
        jsonObject.put("timestamp",new Date().getTime()/1000);
        jsonObject.put("taskId","1596075504030535680");
        jsonObject.put("stepId","1596075504030535681");
        jsonObject.put("upgradeResult","false");
        StringEntity entity = new StringEntity( jsonObject.toJSONString(), charSet );
        httpPost.setEntity( entity );
        CloseableHttpResponse response = null;

        try {

            response = httpclient.execute( httpPost );
            StatusLine status = response.getStatusLine();
            int state = status.getStatusCode();
            if (state == HttpStatus.SC_OK) {
                HttpEntity responseEntity = response.getEntity();
                String jsonString = EntityUtils.toString( responseEntity );
                System.out.println(jsonString);
            } else {
                log.error( "请求返回:" + state + "(" + url + ")" );
            }
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
