package register;

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
 * @since 2022/11/21
 */
@Slf4j
public class RegisterTest {
     public static final String basic_local_url = "https://106.37.174.36/api/cloudapi/v2/register";

    @Test
    public void  test() throws IOException {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost( basic_local_url );// 创建httpPost
        httpPost.setHeader( "Accept", "application/json" );
        httpPost.setHeader( "Content-Type", "application/json" );
        String charSet = "UTF-8";
        long  date = new Date().getTime();
        String json ="{\n" +
                "  \"license_code\": \"23AAFVPTLM2FKBW6UCDZZQEIB7L5CZEE\",\n" +
                "  \"serial_number\": \"70E891B6A71490B0D4A714FDCA557D71\",\n" +
                "  \"expiration_date\": \"2021-01-15 00:00:00 至 2023-01-15 23:59:59\",\n" +
                "  \"interdict\": \"开启\",\n" +
                "  \"company\": \"一所秦城机房ceshi\",\n" +
                "  \"industry\": \"DX\",\n" +
                "  \"province\": \"110000\",\n" +
                "  \"city\": \"110100\",\n" +
                "  \"area\": \"110101\",\n" +
                "  \"contact_name\": \"宋玉斌\",\n" +
                "  \"contact_phone\": \"15313779331\",\n" +
                "  \"contact_mail\": \"songyb@gov110.cn\",\n" +
                "  \"hostname\": \"k01\",\n" +
                "  \"timeout\": 30\n" +
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
                log.error( "请求返回: state = " + state + "(" + response.getEntity().toString() + ")" );
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
