package com.hwenj.es.learn;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import org.elasticsearch.action.ActionFuture;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.cluster.node.DiscoveryNode;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @description: 125es单节点
 * @author: kzw
 * @date: 2021-06-28 17:04
 **/
public class NewESUtil {
    //k01新es集群
    public final static String K01_ES_HOST = "localhost";
    public final static int K01_ES_PORT = 9300;
    static Logger logger = LoggerFactory.getLogger(NewESUtil.class);
    private static Settings settings = Settings.builder().put("cluster.name", "my-application").build();
    private static volatile TransportClient client;
    // volatile禁止指令重排，保证有序性和可见性
    private static volatile BulkProcessor bulkProcessor = null;

    public static void init() {
        System.out.println("NewESUtil bean init()...");
        if (client != null) {
            return;
        }
        try {

            client = new PreBuiltTransportClient(settings)
                    .addTransportAddress(new TransportAddress(InetAddress.getByName(K01_ES_HOST), K01_ES_PORT));
            List<DiscoveryNode> connectedNodes = client.connectedNodes();
            for (DiscoveryNode node : connectedNodes) {
                System.out.println("集群节点：" + node.getHostName());
            }
            System.out.println("NewESUtil bean init() finished");
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("NewESUtil -> getClient() occur exception:" + e.toString());
        }
    }

    /**
     * 同步synchronized(*.class)代码块的作用和synchronized static方法作用一样,
     * 对当前对应的*.class进行持锁,static方法和.class一样都是锁的该类本身,同一个监听器
     *
     * @return
     * @throws UnknownHostException
     */

    public static TransportClient getClient() {
        if (client == null) {
            synchronized (TransportClient.class) {
                init();
            }
        }
        return client;
    }

    /**
     * 判定索引是否存在
     *
     * @param indexName
     * @return
     */
    public static boolean isExists(String indexName) {
        IndicesExistsRequest request = new IndicesExistsRequest(indexName);
        IndicesExistsResponse response = getClient().admin().indices().exists(request).actionGet();
        if (response.isExists()) {
            return true;
        }
        return false;
    }

    /**
     * 获取当前月的索引名称
     *
     * @param indexPre
     * @return
     */
    public static String getCurrentMonthIndex(String indexPre, String pattern) {

        return indexPre + DateUtil.format(new Date(), pattern);
    }

    /**
     * 获取最近几个x的索引名称
     *
     * @param indexPre 索引前缀
     * @param monthNum 索引个数:默认6个
     * @return
     */
    public static String[] getLatestIndex(String indexPre, String pattern, int... monthNum) {

        int num = 6;

        if (monthNum.length > 0)
            num = monthNum[0];

        List<String> result = new ArrayList<>();
        do {
            DateTime dateTime = DateUtil.offsetMonth(new Date(), -num + 1);
            String index = indexPre + DateUtil.format(dateTime, pattern);
            if (isExists(index))
                result.add(index);
            num--;
        } while (num > 0);

        return result.toArray(new String[0]);
    }

    /**
     * 获取索引管理的IndicesAdminClient
     */
    public static IndicesAdminClient getAdminClient() {

        return getClient().admin().indices();
    }

    /**
     * 创建索引
     *
     * @param indexName
     * @return
     */
    public static boolean createIndex(String indexName) {

        CreateIndexResponse createIndexResponse = getAdminClient()
                .prepareCreate(indexName.toLowerCase())
                .get();
        return createIndexResponse.isAcknowledged();
    }

    public static void main(String[] args) {
       // NewESUtil.init();
        //创建Settings

        Settings settings = Settings.builder().put("cluster.name", "my-application").build();

        try {
            TransportClient client = new PreBuiltTransportClient(settings).addTransportAddress(new TransportAddress(InetAddress.getByName("localhost"), 9300));
            SearchResponse response = client.prepareSearch("test_validate")
                    .setTypes("_doc")
//                    .setQuery(QueryBuilders.termQuery("position", "technique"))                 // Query
                    .setPostFilter(QueryBuilders.rangeQuery("id").from(1).to(10))     // Filter
                    .setFrom(0).setSize(60)
                    .get();
            System.out.println(response.getHits().getHits()[0].getSourceAsMap());
            System.out.println("helloworld");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

}
