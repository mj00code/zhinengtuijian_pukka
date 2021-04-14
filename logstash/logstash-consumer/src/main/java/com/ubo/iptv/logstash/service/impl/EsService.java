package com.ubo.iptv.logstash.service.impl;

import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.rest.RestStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @Author: xuning
 * @Date: 2020-11-03
 */
@Component
public class EsService {

    @Autowired
    private RestHighLevelClient esClient;

    public RestHighLevelClient getClient() {
        return esClient;
    }

    /**
     * 批量写入ES
     *
     * @param request
     * @return
     */
    public boolean bulk(BulkRequest request) {
        try {
            if (request.numberOfActions() > 0) {
                BulkResponse bulkResponse = esClient.bulk(request, RequestOptions.DEFAULT);
                return !bulkResponse.hasFailures();
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    public boolean save(IndexRequest request) {
        try {
            IndexResponse indexResponse = esClient.index(request, RequestOptions.DEFAULT);
            return indexResponse.status().equals(RestStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
