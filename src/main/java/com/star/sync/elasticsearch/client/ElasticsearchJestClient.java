package com.star.sync.elasticsearch.client;

import com.google.gson.GsonBuilder;
import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.config.HttpClientConfig;
import io.searchbox.client.http.JestHttpClient;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.net.InetAddress;

/**
 * https://github.com/searchbox-io/Jest/tree/master/jest
 */
@Component
public class ElasticsearchJestClient implements DisposableBean {
    private static final Logger logger = LoggerFactory.getLogger(ElasticsearchJestClient.class);
    private JestClient client;

    @Value("${elasticsearch.jest.uris}")
    private String jestUris;

    @Bean
    public JestClient getClient() {
        if (client == null) {
            JestClientFactory factory = new JestClientFactory();
            factory.setHttpClientConfig(new HttpClientConfig
                    .Builder(jestUris)
                    //.gson(new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").create())
                    .multiThreaded(true)
                    .readTimeout(10000)
                    .build());
            client = factory.getObject();
        }
        return client;
    }

    @Override
    public void destroy() throws Exception {
        if (client != null) {
            client.close();
        }
    }
}
