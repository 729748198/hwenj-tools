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
public class UpgradeTest {

    private String getJson(String type){
        switch (type){
            case "6F3DDDC0739D7C958F57E5AACC20600B":
                return "{\n" +
                        "    \"authCode\": \"6JA82Z7465WVC3F3KU4QLUWX8AMI5X3A\", \n" +
                        "    \"machineId\": \"6F3DDDC0739D7C958F57E5AACC20600B\", \n" +
                        "    \"timestamp\": 1668762928, \n" +
                        "    \"productType\": 1, \n" +
                        "    \"systemVersion\": \"V2.1(8.2.2.24588-cc34f6bf)\", \n" +
                        "    \"patchVersion\": \"20210521194020\", \n" +
                        "    \"ruleVersion\": \"\", \n" +
                        "    \"infoVersion\": \"\", \n" +
                        "    \"ipdb_version\": \"\"\n" +
                        "}";
            case "2DBF69F1B58B0AB9DC4EA7CCBFDB4361":
                return "{\n" +
                        "    'authCode': 'FQAFM3JL6YN2N8FI5ANZP74W6M4CCBVY',\n" +
                        "    'machineId': '2DBF69F1B58B0AB9DC4EA7CCBFDB4361',\n" +
                        "    'timestamp': 1669169403,\n" +
                        "    'productType': 1,\n" +
                        "    'systemVersion': 'V2.1(8.2.2.24598-9f9dd927)',\n" +
                        "    'patchVersion': '20210521194020',\n" +
                        "    'ruleVersion': '8.243',\n" +
                        "    'infoVersion': '',\n" +
                        "    'ipdb_version': ''\n" +
                        "}";
            case "C85E6BC967DB28646F2BD633FDC495D3":
                return "{\n" +
                        "    \"authCode\": \"FQAFM3JL6YN2N8FI5ANZP74W6M4CCBVY\", \n" +
                        "    \"machineId\": \"C85E6BC967DB28646F2BD633FDC495D3\", \n" +
                        "    \"timestamp\": 1669275982, \n" +
                        "    \"productType\": 1, \n" +
                        "    \"systemVersion\": \"V2.1(8.2.2.24641-6f9e9161) \", \n" +
                        "    \"patchVersion\": \"20210521194020\", \n" +
                        "    \"ruleVersion\": \"8.243\", \n" +
                        "    \"infoVersion\": \"\", \n" +
                        "    \"ipdb_version\": \"\"\n" +
                        "}";
            case "D571341A3269B2AA8A609F38FA9DE384":
                return "{\n" +
                        "    \"authCode\": \"FQAFM3JL6YN2N8FI5ANZP74W6M4CCBVY\", \n" +
                        "    \"machineId\": \"D571341A3269B2AA8A609F38FA9DE384\", \n" +
                        "    \"timestamp\": 1670318147, \n" +
                        "    \"productType\": 1, \n" +
                        "    \"systemVersion\": \"V2.1(8.2.2.24697-9cf91bf3) \", \n" +
                        "    \"patchVersion\": \"--\", \n" +
                        "    \"ruleVersion\": \"8.243\", \n" +
                        "    \"infoVersion\": \"\", \n" +
                        "    \"ipdb_version\": \"\"\n" +
                        "}";
        }
        return "";
    }

    @Test
    public void  testLocal() throws IOException {
        //String url = "http://192.168.1.127:10888/upgrade/api/query";
        String url = "http://localhost:10888/upgrade/api/query";
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost( url );// 创建httpPost
        httpPost.setHeader( "Accept", "application/json" );
        httpPost.setHeader( "Content-Type", "application/json" );
        String charSet = "UTF-8";
        long  date = new Date().getTime();
        String json =getJson("D571341A3269B2AA8A609F38FA9DE384");
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

    @Test
    public void  testPro() throws IOException {
        String url = "http://192.168.1.127:10888/upgrade/api/query";
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost( url );// 创建httpPost
        httpPost.setHeader( "Accept", "application/json" );
        httpPost.setHeader( "Content-Type", "application/json" );
        String charSet = "UTF-8";
        long  date = new Date().getTime();
        String json ="{\n" +
                "    \"authCode\": \"FQAFM3JL6YN2N8FI5ANZP74W6M4CCBVY\", \n" +
                "    \"machineId\": \"FF8FA08BF601C4ADD5E09A41DE2DF83E\", \n" +
                "    \"timestamp\": 1668762928, \n" +
                "    \"productType\": 2, \n" +
                "    \"systemVersion\": \"V2.1(8.2.2.24598-9f9dd927)\", \n" +
                "    \"patchVersion\": \"noreboot\", \n" +
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
