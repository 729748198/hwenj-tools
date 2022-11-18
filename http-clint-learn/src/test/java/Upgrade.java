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
 * @since 2022/11/15
 */
@Slf4j
public class Upgrade {

    @Test
    public void  test() throws IOException {
        //String url = "http://192.168.1.127:10888/upgrade/api/query";
        String url = "http://localhost:10888/upgrade/api/query";
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost( url );// 创建httpPost
        httpPost.setHeader( "Accept", "application/json" );
        httpPost.setHeader( "Content-Type", "application/json" );
        String charSet = "UTF-8";
        long  date = new Date().getTime();
        String json ="{\n" +
                "    \"authCode\": \"FQAFM3JL6YN2N8FI5ANZP74W6M4CCBVY\", \n" +
                "    \"machineId\": \"8C2B8ABEEDB4551EFC2BC2119B21E0E0\", \n" +
                "    \"timestamp\": 1668762928, \n" +
                "    \"productType\": 1, \n" +
                "    \"systemVersion\": \"V2.1(8.2.2.24588-cc34f6bf) \", \n" +
                "    \"patchVersion\": \"--\", \n" +
                "    \"ruleVersion\": \"8.243\", \n" +
                "    \"infoVersion\": \"\", \n" +
                "    \"ipdb_version\": \"\"\n" +
                "}";
        JSONObject jsonObject = JSON.parseObject(json);
        jsonObject.put("timestamp",new Date().getTime()/1000);
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
