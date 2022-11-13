import com.fasterxml.jackson.databind.ObjectMapper;
import com.hwenj.es.learn.Person;
import net.minidev.json.JSONValue;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author hwenj
 * @since 2022/9/24
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = TestEs.class)
public class TestEs {
    private static final String HOST = "localhost";
    private static final int PORT_ONE = 9200;
    private static final int PORT_TWO = 9201;
    private static final String SCHEME = "http";

    private static RestHighLevelClient restHighLevelClient;

    static {
        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials("elastic", "heelastic8888"));
        restHighLevelClient = new RestHighLevelClient(
                RestClient.builder(new HttpHost(HOST, PORT_ONE, SCHEME))
                        .setHttpClientConfigCallback((HttpAsyncClientBuilder httpAsyncClientBuilder)
                                -> httpAsyncClientBuilder.setDefaultCredentialsProvider(credentialsProvider))
        );
    }

    private static ObjectMapper objectMapper = new ObjectMapper();

    private static final String INDEX = "persondata";
    public static final String TYPE = "_doc";


    private static synchronized void closeConnection() throws IOException {
        restHighLevelClient.close();
        restHighLevelClient = null;
    }

    @Test
    public void insertPerson() {

        Person person = new Person();
        person.setName("hewnej");
        person.setNumber("1615");
        person.setPersonId("2");
        Map<String, Object> dataMap = new HashMap<String, Object>();
        dataMap.put("name", person.getName());
        dataMap.put("number", person.getNumber());
        IndexRequest indexRequest = new IndexRequest(INDEX, TYPE)
                .id(person.getPersonId()).source(dataMap);
        try {
            IndexResponse response = restHighLevelClient.index(indexRequest);
            System.out.println(response.getIndex());
            System.out.println(response.status().getStatus());
        } catch (ElasticsearchException e) {
            e.getDetailedMessage();
            System.out.println(e);
        } catch (java.io.IOException ex) {
            ex.getLocalizedMessage();
            System.out.println(ex);
        }
    }

    @Test
    public void insertPerson2() {
        String index = "test_person_2022-11-8";
        Person person = new Person();
        person.setName("hewnej");
        person.setNumber("1615");
        person.setPersonId("2");
        Map<String, Object> dataMap = new HashMap<String, Object>();
        dataMap.put("name", person.getName());
        dataMap.put("number", person.getNumber());
        IndexRequest indexRequest = new IndexRequest(index, TYPE)
                .id(person.getPersonId()).source(dataMap);
        try {
            IndexResponse response = restHighLevelClient.index(indexRequest);
            System.out.println(response.getIndex());
            System.out.println(response.status().getStatus());
        } catch (ElasticsearchException e) {
            e.getDetailedMessage();
            System.out.println(e);
        } catch (java.io.IOException ex) {
            ex.getLocalizedMessage();
            System.out.println(ex);
        }
    }


    @Test
    public void getPersonById() {
        String id = "2";
        GetRequest getPersonRequest = new GetRequest(INDEX, "_doc", id);
        GetResponse getResponse = null;
        try {
            getResponse = restHighLevelClient.get(getPersonRequest);
        } catch (java.io.IOException e) {
            e.getLocalizedMessage();
        }
        Person person = getResponse != null ?
                objectMapper.convertValue(getResponse.getSourceAsMap(), Person.class) : null;
        System.out.println(JSONValue.toJSONString(person));
    }


}
