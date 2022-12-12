package com.shifu.consumer.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document
public class MetricEvent {

    //private ObjectIdGenerator objectId;
    private String deviceId;
    //private LocalDateTime eventTime;
    private String countryCd;
    private int siteNbr;
    //private UUID metricId;
    private String serialNum;
    private Map<String, String> metrics;
    private Map<String, Long> timeStamps;
}
