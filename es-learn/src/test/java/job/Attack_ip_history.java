package job;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.ClearScrollRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchScrollRequest;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author hwenj
 * @since 2022/12/26
 */
public class Attack_ip_history {

    private RestHighLevelClient restHighLevelClient231;


    @Test
    public void test() throws IOException {
        List<AttackIp> attackIpList = new ArrayList<>();
        init();
        // 先查询出来type=1的，构造出数据再批量新增
        SearchRequest searchRequest = new SearchRequest("k01_attack_src_ip_history");
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.fetchSource(true);
        //设置每批读取的数据量
        sourceBuilder.size(10000);
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(QueryBuilders.termQuery("ip_type", "1"));
        boolQueryBuilder.must(QueryBuilders.existsQuery("ipl"));
        boolQueryBuilder.mustNot(QueryBuilders.termQuery("status", "-1"));
        sourceBuilder.query(boolQueryBuilder);
        //设置 search context 维护1分钟的有效期
        TimeValue valueMinutes = TimeValue.timeValueMinutes(10);
        searchRequest.scroll(valueMinutes);
        searchRequest.source(sourceBuilder);
        //获得首次的查询结果
        SearchResponse scrollRespX01 = restHighLevelClient231.search(searchRequest);
        for (SearchHit hit : scrollRespX01.getHits().getHits()) {
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            AttackIp attackIp = getAttackIp(sourceAsMap);
            attackIpList.add(attackIp);
        }


        String scrollId = scrollRespX01.getScrollId();
        while (scrollRespX01.getHits().getHits() != null && scrollRespX01.getHits().getHits().length > 0) {
            //将scrollId循环传递
            SearchScrollRequest searchScrollRequest = new SearchScrollRequest(scrollId);
            searchScrollRequest.scroll(valueMinutes);
            scrollRespX01 = restHighLevelClient231.searchScroll(searchScrollRequest);
            for (SearchHit hit : scrollRespX01.getHits().getHits()) {
                Map<String, Object> sourceAsMap = hit.getSourceAsMap();
                AttackIp attackIp = getAttackIp(sourceAsMap);
                attackIpList.add(attackIp);
            }
            scrollId = scrollRespX01.getScrollId();
        }

        //删除scroll
        ClearScrollRequest clearScrollRequestX01 = new ClearScrollRequest();
        clearScrollRequestX01.addScrollId(scrollRespX01.getScrollId());
        restHighLevelClient231.clearScroll(clearScrollRequestX01);



        List<AttackTypeIp> attackTypeIpList = attackIpList.stream()
                .map(attackIp -> {
                    AttackTypeIp attackTypeIp = JSONUtil.toBean(JSON.toJSONString(attackIp), AttackTypeIp.class);
                    attackTypeIp.setInfoId(CollectionUtil.isEmpty(attackIp.getInfo_id_array()) ? 0 : attackIp.getInfo_id_array().get(0));
                    attackTypeIp.setUpdate_datetime(attackIp.getLast_datetime());
                    attackTypeIp.setUpdate_timestamp(attackIp.getLast_timestamp());
                    attackTypeIp.setIndustryCode("WZ");
                    attackTypeIp.setScore(100);
                    attackTypeIp.setTotal(1);
                    return attackTypeIp;
                }).collect(Collectors.toList());
        attackIpList.clear();
        System.out.println(attackTypeIpList.size());
        write(attackTypeIpList);

        saveIncrData(attackTypeIpList);
    }

    private void write(List<? extends Object> list) throws IOException {
        File file = new File("./test_appendfile.txt");

        if (!file.exists()) {
            file.createNewFile();
        }
        //使用true，即进行append file

        FileWriter fileWritter = new FileWriter(file.getName(), true);

        for (int i = 0; i < list.size(); i++) {
            fileWritter.write(JSON.toJSONString(list.get(i)) + "\n");
        }

        fileWritter.close();
    }

    public static void main(String[] args) {
        String s = "[14, 30]";
        if (StringUtils.isNotBlank(s)) {
            s = s.substring(1, s.length() - 1);
        }
        final String[] split = s.split(",");
        ArrayList list = ListUtil.toList(split);
        list.add("");
        List<Integer> result = (List<Integer>) list.stream().map(
                o -> {
                    String s1 = o.toString();
                    return s1.trim();
                }
        ).filter(Objects::nonNull).filter(ss -> !"".equals(ss)).map(o -> Integer.valueOf(String.valueOf(o))).collect(Collectors.toList());
        System.out.println(result);

    }

    private AttackIp getAttackIp(Map<String, Object> sourceAsMap) {
        AttackIp attackIp = new AttackIp();
        attackIp.setFirst_datetime((String) sourceAsMap.get("first_datetime"));
        attackIp.setCountry((String) sourceAsMap.get("country"));
        String idarray = String.valueOf(sourceAsMap.get("info_id_array"));
        if (StringUtils.isNotBlank(idarray) && !"null".equals(idarray)) {
            idarray = idarray.substring(1, idarray.length() - 1);
            final String[] split = idarray.split(",");
            ArrayList list = ListUtil.toList(split);
            List<Integer> result = (List<Integer>) list.stream().map(
                    o -> {
                        String s1 = o.toString();
                        return s1.trim();
                    }
            ).filter(ss -> !"".equals(ss)).map(o -> Integer.valueOf(String.valueOf(o))).collect(Collectors.toList());
            attackIp.setInfo_id_array(result);
        }

        attackIp.setLast_timestamp((Integer) sourceAsMap.get("last_timestamp"));
        attackIp.setFirst_timestamp((Integer) sourceAsMap.get("first_timestamp"));
        attackIp.setIpl(Long.valueOf(sourceAsMap.get("ipl").toString()));
        attackIp.setCity((String) sourceAsMap.get("city"));
        attackIp.setLast_datetime((String) sourceAsMap.get("last_datetime"));
        attackIp.setExpire_timestamp((Integer) sourceAsMap.get("expire_timestamp"));
        attackIp.setIp((String) sourceAsMap.get("ip"));
        attackIp.setIp_type((Integer) sourceAsMap.get("ip_type"));
        attackIp.setIpl_v6(Long.valueOf(sourceAsMap.getOrDefault("ipl_v6","0").toString()));
        attackIp.setProvince((String) sourceAsMap.get("province"));
        attackIp.setStatus((Integer) sourceAsMap.get("status"));
        attackIp.setDown_timestamp((Integer) sourceAsMap.get("down_timestamp"));
        attackIp.setDown_datetime((String) sourceAsMap.get("down_datetime"));
        attackIp.setProvince((String) sourceAsMap.get("province"));
        attackIp.setBlock_duration((Integer) sourceAsMap.get("block_duration"));
        return attackIp;

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


    private void saveIncrData(List<AttackTypeIp> list) throws IOException {
        BulkRequest bulkRequest = new BulkRequest();
        for (AttackTypeIp datum : list) {
            bulkRequest = buildEsBulk(bulkRequest, datum, datum.getIpl().toString());
            if (bulkRequest == null) {
                bulkRequest = new BulkRequest();
            }
        }
        if (bulkRequest.numberOfActions() % 2000 > 0) {
            BulkResponse bulkResponse = restHighLevelClient231.bulk(bulkRequest);
            for (BulkItemResponse bulkItemResponse : bulkResponse) {
                if (bulkItemResponse.isFailed()) {
                    BulkItemResponse.Failure failure = bulkItemResponse.getFailure();
                    System.out.println("腾讯情报-批处理异常:{}" + failure.toString());
                }
            }
            System.out.println("腾讯情报-批量新增历史库数据量:{}" + bulkRequest.numberOfActions());
        }
    }


    private BulkRequest buildEsBulk(BulkRequest bulkRequest, AttackTypeIp datum, String ioc) throws IOException {
        IndexRequest indexRequest = new IndexRequest("k01_attack_ip_type_industry_history", "_doc", ioc)
                .source(JSON.toJSONString(datum), XContentType.JSON);
        bulkRequest.add(indexRequest);
        if (bulkRequest.numberOfActions() % 2000 == 0) {
            BulkResponse bulkResponse = restHighLevelClient231.bulk(bulkRequest);
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

}
