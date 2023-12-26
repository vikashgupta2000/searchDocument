package org.hevodata;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


// docker run --name elasticsearch -p 9200:9200 -p 9300:9300 -e "discovery.type=single-node" -e "xpack.security.enabled=false" -t docker.elastic.co/elasticsearch/elasticsearch:8.11.3
// [Not required] docker run --name kibana  -p 5601:5601 docker.elastic.co/kibana/kibana:8.10.4

@SpringBootApplication
public class SearchDocumentApplication {
	public static void main(String[] args) {
		SpringApplication.run(SearchDocumentApplication.class, args);
	}
}
