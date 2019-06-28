package com.metricly.jenkins.plugins.client;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class IngestPayload implements Serializable {

    private String id;
    private String type = "Jenkins";
    private Set<Map<String, String>> metrics = new HashSet<>();
    private List<Map<String, Object>> samples = new ArrayList<>();

    public IngestPayload(String hostname) {
        this.id = hostname;
    }

    public void addSample(String jobName, String metric, Integer value) {
        addSample(jobName, metric, value.doubleValue());
    }

    public void addSample(String jobName, String metric, Long value) {
        addSample(jobName, metric, value.doubleValue());
    }

    public void addSample(String jobName, String metric, Double value) {
        this.samples.add(ImmutableMap.of("metricId", "jenkins." + jobName + "." + metric, "val", value.toString(), "timestamp", new Date().getTime()));
    }

    public String toJson() throws JsonProcessingException {
        this.metrics = this.samples.stream().map(sample -> {
            return ImmutableMap.of("id", (String) sample.get("metricId"));
        }).collect(Collectors.toSet());
        return new ObjectMapper().setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY).writeValueAsString(Arrays.asList(this));
    }
}
