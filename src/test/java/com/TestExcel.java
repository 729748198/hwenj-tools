package com;

import org.elasticsearch.action.search.SearchAction;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * @author : hewenjie
 * @create 2023/1/4
 */
public class TestExcel {
//
//    public void test(){
//        File file = new File("C:\\Users\\hewenjie\\Desktop\\各个厂商情报\\腾讯\\腾讯上个月命中情报的IOC.xlsx");
//        List<TestExcel> successList = Lists.newArrayList();
//        ExcelKit.$Import(TestExcel.class)
//                .readXlsx(file, new ExcelReadHandler<TestExcel>() {
//
//                    @Override
//                    public void onSuccess(int sheetIndex, int rowIndex, TestExcel entity) {
//                        successList.add(entity); // 单行读取成功，加入入库队列。
//                    }
//
//                    @Override
//                    public void onError(int sheetIndex, int rowIndex,
//                                        List<ExcelErrorField> errorFields) {
//                        // 读取数据失败，记录了当前行所有失败的数据
//                        System.out.println("sheetIndex " + sheetIndex);
//                    }
//                });
//        int i = 0;
//        for (TestExcel testExcel : successList) {
//            TransportClient client = NewESUtil.getClient();
//            BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
//            queryBuilder.must(QueryBuilders.termQuery("ioc", testExcel.getIoc()));
//            SearchResponse searchResponse = SearchAction.INSTANCE.newRequestBuilder(client)
//                    .setQuery(queryBuilder)
//                    .setIndices("k01_external_tencent_history_new").get();
//            SearchHit[] hits = searchResponse.getHits().getHits();
//            for (SearchHit hit : hits) {
//                Map<String, Object> sourceAsMap = hit.getSourceAsMap();
//                String category = sourceAsMap.getOrDefault("category", "").toString();
//                testExcel.setType(category);
//                testExcel.setMalicious_category(sourceAsMap.getOrDefault("malicious_category", "").toString());
//                testExcel.setMalicious_typename(sourceAsMap.getOrDefault("malicious_typename", "").toString());
//            }
//            i++;
//            if (i % 100 == 0) {
//                System.out.println(i);
//            }
//        }
//        ExcelKit.$Export(TestExcel.class, response).downXlsx(successList, false);
//    }
}
