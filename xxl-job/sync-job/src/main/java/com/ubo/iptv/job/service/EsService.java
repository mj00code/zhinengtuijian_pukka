package com.ubo.iptv.job.service;

import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Author: xuning
 * @Date: 2020-11-03
 */
@Component
@Slf4j
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
            log.error("bulk error", e);
        }
        return false;
    }
}
