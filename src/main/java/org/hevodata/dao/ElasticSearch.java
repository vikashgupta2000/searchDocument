package org.hevodata.dao;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import co.elastic.clients.elasticsearch.core.DeleteRequest;
import co.elastic.clients.elasticsearch.core.GetResponse;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hevodata.model.FileData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ElasticSearch {

    final static Logger logger = LogManager.getLogger(ElasticSearch.class);
    @Autowired
    private ElasticsearchClient esClient;

    public FileData get(String index, String id, String... excludeFields) throws IOException {
        try {
            GetResponse<FileData> response = esClient.get(g -> g
                            .index(index)
                            .id(id)
                            .sourceExcludes(List.of(excludeFields)),
                    FileData.class
            );

            if (response.found()) {
                logger.info("Data found in ES");
                return response.source();
            } else {
                logger.info("No-data present for the index {} with id {}", index, id);
            }
        } catch (ElasticsearchException e) {
            logger.error("Error while using GET of ES", e);
            if (!e.response().error().type().equals("index_not_found_exception")) {
                throw e;
            }
        }
        return null;
    }

    public List<FileData> match(String index, String field, String searchText) throws IOException {
        SearchResponse<FileData> response = esClient.search(s -> s
                        .index(index)
                        .query(q -> q
                                .match(t -> t
                                        .field(field)
                                        .query(searchText)
                                )
                        ),
                FileData.class
        );

        List<FileData> result = new ArrayList<>();
        List<Hit<FileData>> hits = response.hits().hits();
        for (Hit<FileData> hit : hits) {
            result.add(hit.source());
        }
        return result;
    }

    public void post(String index, FileData fileData) throws IOException {
        IndexResponse response = esClient.index(i -> i
                .index(index)
                .id(fileData.getMetadata().getFileId())
                .document(fileData)
        );

        logger.info("Successfully indexed document : {}", response);
    }

    public void delete(String index, String id) throws IOException {
        DeleteRequest deleteRequest = DeleteRequest.of(d -> d.index(index).id(id));
        esClient.delete(deleteRequest);
    }

    public List<FileData> getAllExcept(List<String> excludedIds, String index, String... excludeFields) throws IOException {
        SearchResponse<FileData> response = esClient.search(s -> s
                        .index(index)
                        .query(q -> q
                                .bool(b -> b
                                        .mustNot(q2 -> q2
                                                .ids(idq -> idq.
                                                        values(excludedIds)
                                                )
                                        )
                                )
                        )
                        .source(so -> so
                                .filter(f -> f
                                        .excludes(List.of(excludeFields))
                                )
                        ),
                FileData.class
        );

        List<FileData> result = new ArrayList<>();
        List<Hit<FileData>> hits = response.hits().hits();
        for (Hit<FileData> hit : hits) {
            result.add(hit.source());
        }
        return result;
    }
}
