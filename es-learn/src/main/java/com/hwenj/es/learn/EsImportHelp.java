package com.hwenj.es.learn;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.script.Script;
import org.elasticsearch.script.ScriptType;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author jhihjian
 */
@Slf4j
public class EsImportHelp {
    private static final long BATCH_SIZE = 1500;
    private static final TimeValue TIMEOUT = new TimeValue(3, TimeUnit.MINUTES);
    private static final int MAX_RETRY_TIME = 3;
    private final String index;
    private final String type;
    private BulkRequest bulkRequest;
    private final RestHighLevelClient restHighLevelClient231;
    private long count = 0;
    private String company;

    public EsImportHelp(String index, String type, String company, RestHighLevelClient restHighLevelClient231) {
        this.index = index;
        this.type = type;
        bulkRequest = new BulkRequest();
        this.restHighLevelClient231 = restHighLevelClient231;
        this.company = company;
    }

    /**
     * 需要close
     */
    public void upsert(Object o, String id) {
        Script script = new Script(ScriptType.INLINE, "painless", "" +
                "if(!ctx._source.containsKey('valid_date')){ctx._source.valid_date=[0,0,0,0,0,0,0,0,0,0,0,0]}" +
                "ctx._source.valid_date[params.month] = ctx._source.valid_date[params.month]|params.today;" +
                "ctx._source.array[0]=99", (Map) JSON.toJSON(o));
        IndexRequest indexRequest = new IndexRequest(index, type, id)
                .source(JSON.toJSONString(o), XContentType.JSON);
        UpdateRequest updateRequest = new UpdateRequest(index, type, id)
                .upsert(indexRequest)
                .scriptedUpsert(true)
                .script(script)
                .docAsUpsert(false);
        bulkRequest.add(updateRequest);
        if (bulkRequest.numberOfActions() % BATCH_SIZE == 0) {
            flush();
        }
        count++;
    }

    public void upsert2(Object o, String id) {
        Map<String, Object> map = new HashMap<>();
        map.put("month", 4);
        map.put("today", 4);
        Script script = new Script(ScriptType.INLINE, "painless", "" +
                "ctx._source.array[params.month] = ctx._source.array[params.month]|params.today", map);
        IndexRequest indexRequest = new IndexRequest(index, type, id)
                .source(JSON.toJSONString(o), XContentType.JSON);
        UpdateRequest updateRequest = new UpdateRequest(index, type, id)
                .upsert(indexRequest)
                .doc(JSON.toJSONString(o), XContentType.JSON)
                .scriptedUpsert(true)
                .docAsUpsert(false);
        bulkRequest.add(updateRequest);
        if (bulkRequest.numberOfActions() % BATCH_SIZE == 0) {
            flush();
        }
        count++;
    }

    public void insert(Object o, String id) {
        IndexRequest indexRequest = new IndexRequest(index, type, id)
                .source(JSON.toJSONString(o), XContentType.JSON);
        bulkRequest.add(indexRequest);
        if (bulkRequest.numberOfActions() % BATCH_SIZE == 0) {
            flush();
        }
        count++;
    }

    public void delete(String id) {
        DeleteRequest request = new DeleteRequest(index, type, id);
        bulkRequest.add(request);
    }

    private void flush() {
        flush(0);
    }

    private void flush(int retryTime) {
        if (retryTime > MAX_RETRY_TIME) {
            log.error("{}情报-批处理异常:超过最大重试次数", company);
            return;
        }
        BulkResponse bulkResponse;
        try {
            bulkResponse = restHighLevelClient231.bulk(bulkRequest);
        } catch (IOException e) {
            log.error("", e);
            return;
        }
        for (BulkItemResponse bulkItemResponse : bulkResponse) {
            if (bulkItemResponse.isFailed()) {
                BulkItemResponse.Failure failure = bulkItemResponse.getFailure();
                log.error("{}情报-批处理异常:{} retryTime:{}", company, failure.toString(), retryTime);
                flush(retryTime + 1);
            }
        }
        log.debug("{}情报-全量情报批量index:{},insert数据量:{}", company, index, bulkRequest.numberOfActions());
        bulkRequest = new BulkRequest();
        bulkRequest.timeout(TIMEOUT);
    }

    public void close() {
        if (bulkRequest.numberOfActions() != 0) {
            flush();
        }
        log.info("write to {} size:{}", index, count);
    }
}
