package org.hevodata.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.message.BasicHeader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ElasticsearchClientConfig {
    final static Logger logger = LogManager.getLogger(ElasticsearchClientConfig.class);
    @Value("${elastic.host}")
    private String host;
    @Value("${elastic.port}")
    private int port;

    @Bean
    public ElasticsearchClient getElasticsearchClient() {
        logger.info("Loading ES config host {}, port {}", host, port);
        RestClient restClient = RestClient
                .builder(new HttpHost(host, port))
                .build();
        ElasticsearchTransport transport = new RestClientTransport(
                restClient, new JacksonJsonpMapper());
        return new ElasticsearchClient(transport);
    }
}
