
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.hwenj.es.learn.NewESUtil;
import com.hwenj.es.learn.T01HistoryValidDateUtil;
import com.hwenj.es.learn.ValidDate;
import org.apache.commons.collections4.ListUtils;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchAction;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author hwenj
 * @since 2022/10/4
 */
public class BatchQueryAndInsert {
    private static final long BATCH_SIZE = 1500;
    private static final String HOST = "localhost";
    private static final int PORT_ONE = 9200;
    private static final int PORT_TWO = 9201;
    private static final String SCHEME = "http";
    private static BulkRequest bulkRequest = new BulkRequest();
    private static final TimeValue TIMEOUT = new TimeValue(3, TimeUnit.MINUTES);
    private static final String INDEX = "test_validate";

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


    @Test
    public void testBatchQuery() {
        Map<Integer, TestBean> historyEntityMap = new HashMap<>(2 ^ 15);
        List<String> ids = new ArrayList<>();
        for (int i = 100000; i < 100010; i++) {
            ids.add(String.valueOf(i));
        }
        List<List<String>> subs = ListUtils.partition(ids, 1000);

        subs.forEach(idsubs -> {
            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
            boolQueryBuilder.must(QueryBuilders.termsQuery("_id", idsubs));
            SearchHits hits = SearchAction.INSTANCE.newRequestBuilder(NewESUtil.getClient())
                    .setFetchSource(new String[]{"id", "array"}, new String[0])
                    .setIndices(INDEX)
                    .setSize(10000)
                    .setQuery(boolQueryBuilder)
                    .get()
                    .getHits();
            for (SearchHit hit : hits) {
                Map<String, Object> sourceAsMap = hit.getSourceAsMap();
                if (sourceAsMap == null) {
                    continue;
                }
                TestBean testBean = JSON.parseObject(JSON.toJSONString(sourceAsMap), TestBean.class);
                if (testBean.getArray() == null) {
                    continue;
                }
                historyEntityMap.put(testBean.getId(), testBean);
            }
        });
        System.out.println(historyEntityMap.size());
        int toDay = ValidDate.getDayInt(DateUtil.today());
        int month = DateUtil.thisMonth();
        List<TestBean> testBeanList = new ArrayList<>();
        for (int i = 0; i < 10000; i++) {
            if (historyEntityMap.containsKey(i)) {
                TestBean testBean = historyEntityMap.get(i);
                Integer[] array = testBean.getArray();
                array[month] |= toDay;
                testBean.setArray(array);
                testBean.setName("123");
                testBeanList.add(testBean);
            }
        }
        try {
            saveIncrData(testBeanList);
        } catch (IOException e) {
            System.out.println("出现错误" + e);
        }

    }


    private void saveIncrData(List<TestBean> list) throws IOException {
        BulkRequest bulkRequest = new BulkRequest();
        for (TestBean datum : list) {
            bulkRequest = buildEsBulk(bulkRequest, datum, datum.getId().toString());
            if (bulkRequest == null) {
                bulkRequest = new BulkRequest();
            }
        }
        if (bulkRequest.numberOfActions() % 2000 > 0) {
            BulkResponse bulkResponse = restHighLevelClient.bulk(bulkRequest);
            for (BulkItemResponse bulkItemResponse : bulkResponse) {
                if (bulkItemResponse.isFailed()) {
                    BulkItemResponse.Failure failure = bulkItemResponse.getFailure();
                    System.out.println("腾讯情报-批处理异常:{}" + failure.toString());
                }
            }
            System.out.println("腾讯情报-批量新增历史库数据量:{}" + bulkRequest.numberOfActions());
        }
    }


    private BulkRequest buildEsBulk(BulkRequest bulkRequest, TestBean datum, String ioc) throws IOException {
        IndexRequest indexRequest = new IndexRequest(INDEX, "_doc", ioc)
                .source(JSON.toJSONString(datum), XContentType.JSON);
        bulkRequest.add(indexRequest);
        if (bulkRequest.numberOfActions() % 2000 == 0) {
            BulkResponse bulkResponse = restHighLevelClient.bulk(bulkRequest);
            for (BulkItemResponse bulkItemResponse : bulkResponse) {
                if (bulkItemResponse.isFailed()) {
                    BulkItemResponse.Failure failure = bulkItemResponse.getFailure();
                    System.out.println("腾讯情报-批处理异常:{}" + failure.toString());
                }
            }
            bulkRequest = null;
        }
        return bulkRequest;
    }


    /**
     * 创建索引
     *
     * @throws Exception
     */
    @Test
    public void createStorage() throws Exception {
        // 创建建索引，并设置副本数及刷新间隔，提高插入效率
        Settings settings = Settings.builder()
                .put("index.number_of_shards", 3)
                .put("index.number_of_replicas", 0)
                .put("index.refresh_interval", "60s")
                .build();
        CreateIndexRequest createIndexRequest = new CreateIndexRequest(INDEX);
        createIndexRequest.settings(settings);
        try {
            restHighLevelClient.indices().create(createIndexRequest);
        } catch (IOException e) {
            System.out.println("{}情报-全量索引创建失败." + e);
            throw new Exception("全量索引创建失败");
        }
    }

    /**
     * 批量创建数据
     * 验证方式：
     * 创建id1-10w的数据，初始化都为0
     * <p>
     * 再创建一个id为偶数的列表
     * 批量查询后，更新。
     *
     * 验证成功
     */
    @Test
    public void batchInsert() {
        for (int i = 0; i < 100000; i++) {
            TestBean testBean = new TestBean();
            testBean.setId(i);
            testBean.setArray(T01HistoryValidDateUtil.initArray());
            insert(testBean, String.valueOf(i), INDEX);
        }
        flush(0);
    }


    public static void insert(Object o, String id, String index) {
        IndexRequest indexRequest = new IndexRequest(index, "_doc", id)
                .source(JSON.toJSONString(o), XContentType.JSON);
        bulkRequest.add(indexRequest);
        if (bulkRequest.numberOfActions() % BATCH_SIZE == 0) {
            flush(0);
        }
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
}
