package com.hwenj.wulin;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;

import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;
import java.security.SecureRandom;

/**
 * @author hwenj
 * @since 2022/10/14
 */
public class HttpClientFactory {
    private static CloseableHttpClient client;

//    public static HttpClient getHttpsClient() throws Exception {
//
//        if (client != null) {
//            return client;
//        }
//        SSLContext sslcontext = SSLContexts.custom().useSSL().build();
//        sslcontext.init(null, new X509TrustManager[]{new HttpsTrustManager()}, new SecureRandom());
//        SSLConnectionSocketFactory factory = new SSLConnectionSocketFactory(sslcontext,new String[] { "SSLv3", "TLSv1", "TLSv1.1", "TLSv1.2" }, null,
//                SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
//        client = HttpClients.custom().setSSLSocketFactory(factory).build();
//
//        return client;
//    }

    public static void releaseInstance() {
        client = null;
    }
}
