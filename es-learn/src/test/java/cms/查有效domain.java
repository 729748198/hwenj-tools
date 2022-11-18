import org.apache.http.HttpHost;
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

import java.io.IOException;

/**
 * @author : hewenjie
 * @create 2022/10/19
 */
public class 查有效domain {
    private RestHighLevelClient restHighLevelClient231;

    @Test
    public void testDomain() throws IOException {
        init();
        String index = "export_internal_info_2022-10-24";
        SearchRequest searchRequest = new SearchRequest();
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(QueryBuilders.termQuery("infoType.keyword", "domain"));
        sourceBuilder.query(boolQueryBuilder);
        sourceBuilder.size(50);
        sourceBuilder.from(50);
        searchRequest.indices(index).source(sourceBuilder);
        SearchResponse search = restHighLevelClient231.search(searchRequest);
        SearchHit[] hits = search.getHits().getHits();
        for (int i = 0; i < hits.length; i++) {
            System.out.println(hits[i].getSourceAsMap().get("infoName"));
        }

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

    private void  init(){

        String[] split = "192.168.1.231:9200".split(",");
        HttpHost[] array = new HttpHost[split.length];
        for (int i = 0; i < split.length; i++) {
            String item = split[i];
            array[i] = new HttpHost(item.split(":")[0], Integer.parseInt(item.split(":")[1]), "http");
        }
        RestClientBuilder builder = RestClient.builder(array);
        builder.setMaxRetryTimeoutMillis(2 * 60 * 1000);
        restHighLevelClient231= new RestHighLevelClient(builder);
    }
}
