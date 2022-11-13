import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hwenj.es.learn.T01HistoryValidDateUtil;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.elasticsearch.ElasticsearchStatusException;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @author hwenj
 * @since 2022/9/27
 */
public class BatchInsertES {
    private static final long BATCH_SIZE = 1500;
    private static final String HOST = "localhost";
    private static final int PORT_ONE = 9200;
    private static final int PORT_TWO = 9201;
    private static final String SCHEME = "http";
    private static BulkRequest bulkRequest = new BulkRequest();
    private static final TimeValue TIMEOUT = new TimeValue(3, TimeUnit.MINUTES);

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


    public static void cleanStorage(String index) {
        try {
            DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest(index);
            restHighLevelClient.indices().delete(deleteIndexRequest);
        } catch (IOException | ElasticsearchStatusException e) {
            System.out.println("腾讯情报-全量索引不存在删除失败：" + e);
        }
    }


    public static void createStorage() throws Exception {
        String index = "";
        // 创建建索引，并设置副本数及刷新间隔，提高插入效率
        Settings settings = Settings.builder()
                .put("index.number_of_shards", 3)
                .put("index.number_of_replicas", 0)
                .put("index.refresh_interval", "60s")
                .build();
        CreateIndexRequest createIndexRequest = new CreateIndexRequest(index);
        createIndexRequest.settings(settings);
        try {
            restHighLevelClient.indices().create(createIndexRequest);
        } catch (IOException e) {
            System.out.println("{}情报-全量索引创建失败." + e);
            throw new Exception("全量索引创建失败");
        }
    }

    /**
     * 验证
     * @throws Exception
     */
    @Test
    public void batchInsert() throws Exception {
        String index = "test_batch_insert";
        for (int i = 0; i < 10000; i++) {
            GetRequest getRequest = new GetRequest(index, "_doc", String.valueOf(i));
            if (restHighLevelClient.exists(getRequest) && i % 2 == 0) {

                GetResponse getResponse = null;
                try {
                    getResponse = restHighLevelClient.get(getRequest);
                } catch (java.io.IOException e) {
                    e.getLocalizedMessage();
                }
                TestBean testBeanExist = JSON.parseObject(JSON.toJSONString(getResponse.getSourceAsMap()), TestBean.class);
                Integer[] array2 = testBeanExist.getArray();
                array2[DateUtil.thisMonth() - 1] |= T01HistoryValidDateUtil.getDayInt("2022-09-29");
                testBeanExist.setArray(array2);
                upsert(testBeanExist, String.valueOf(i), index);
            } else {
                TestBean testBean = new TestBean();
                testBean.setId(i);
                testBean.setArray(T01HistoryValidDateUtil.initArray(T01HistoryValidDateUtil.getDayInt(DateUtil.today()), DateUtil.thisMonth()));
                insert(testBean, String.valueOf(i), index);
            }
        }
        flush(0);
        closeConnection();
    }

    private static void flush(int retryTime) {
        if (retryTime > 3) {
            System.out.println("{}情报-批处理异常:超过最大重试次数");
        }
        BulkResponse bulkResponse;
        try {
            bulkResponse = restHighLevelClient.bulk(bulkRequest);
        } catch (IOException e) {
            System.out.println(e);
            return;
        }
        for (BulkItemResponse bulkItemResponse : bulkResponse) {
            if (bulkItemResponse.isFailed()) {
                BulkItemResponse.Failure failure = bulkItemResponse.getFailure();
                System.out.println("{}情报-批处理异常:{} retryTime:{}" + failure.toString() + retryTime);
                flush(retryTime + 1);
            }
        }
        System.out.println("{}情报-全量情报批量index:{},insert数据量:{}" + bulkRequest.numberOfActions());
        bulkRequest = new BulkRequest();
        bulkRequest.timeout(TIMEOUT);
    }

    public static void upsert(Object o, String id, String index) {
        IndexRequest indexRequest = new IndexRequest(index, "_doc", id)
                .source(JSON.toJSONString(o), XContentType.JSON);
        UpdateRequest updateRequest = new UpdateRequest(index, "_doc", id)
                .doc(JSON.toJSONString(o), XContentType.JSON)
                .upsert(indexRequest)
                .scriptedUpsert(true)
                .docAsUpsert(true);
        bulkRequest.add(updateRequest);
        if (bulkRequest.numberOfActions() % BATCH_SIZE == 0) {
            flush(0);
        }
    }

    public static void insert(Object o, String id, String index) {
        IndexRequest indexRequest = new IndexRequest(index, "_doc", id)
                .source(JSON.toJSONString(o), XContentType.JSON);
        bulkRequest.add(indexRequest);
        if (bulkRequest.numberOfActions() % BATCH_SIZE == 0) {
            flush(0);
        }
    }

    private static synchronized void closeConnection() throws IOException {
        restHighLevelClient.close();
        restHighLevelClient = null;
    }
}
