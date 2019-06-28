package com.metricly.jenkins.plugins.client;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

public class MetriclyClient {

    private static final Logger logger =  Logger.getLogger(MetriclyClient.class.getName());

    private String apiKey;
    private String apiLocation;

    public MetriclyClient(String apiKey, String apiLocation) {
        this.apiKey = apiKey;
        this.apiLocation = apiLocation;
    }

    public void submit(IngestPayload ingestPayload) {
        if (StringUtils.isBlank(apiKey)) {
            logger.info("A Metricly API key must be provided");
            return;
        }
        try {
            HttpClient httpClient = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost(apiLocation + "/ingest/" + apiKey);
            httpPost.setEntity(new StringEntity(ingestPayload.toJson()));
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");
            HttpResponse response = httpClient.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 202) {
                logger.info("Successfully posted data to Metricly");
            } else {
                logger.warning(String.format("Unable to post data to Metricly. Status: %s, Content: %s", statusCode, IOUtils.toString(response.getEntity().getContent(), StandardCharsets.UTF_8)));
            }
        } catch (IOException e) {
            logger.severe(String.format("Unable to post data to Metricly: %s", e));
        }
    }
}
