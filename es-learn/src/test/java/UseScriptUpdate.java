import cn.hutool.core.date.DateUtil;
import com.hwenj.es.learn.EsImportHelp;
import com.hwenj.es.learn.T01HistoryValidDateUtil;
import com.hwenj.es.learn.ValidDate;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

/**
 * @author hwenj
 * @since 2022/10/8
 */
public class UseScriptUpdate {
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

    @Test
    public void testUpdate() {

        EsImportHelp q01Import = new EsImportHelp("test_batch_insert", "_doc", "hwenj", restHighLevelClient);
        Integer month = DateUtil.thisMonth();
        Integer today = ValidDate.getDayInt("2022-10-08");
        for (int i = 0; i < 100; i++) {
            TestBean testBean = new TestBean();
            testBean.setArray(T01HistoryValidDateUtil.initArray());
            testBean.setId(i);
            testBean.setMonth(month);
            testBean.setToday(today);
            testBean.setName("第" + i + "名");
            q01Import.upsert(testBean, String.valueOf(i));
        }
        q01Import.close();
    }
}
