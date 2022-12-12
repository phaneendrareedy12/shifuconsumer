package com.shifu.consumer.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shifu.consumer.model.MetricEvent;
import com.shifu.consumer.repository.MetricEventRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class KafkaConsumer {

    @Autowired
    private MetricEventRepository metricEventRepository;

    @KafkaListener(topics = "sampletopic", groupId = "shifu-metrics")
    public void consume(String metricEvent) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            MetricEvent event = mapper.readValue(metricEvent, MetricEvent.class);
            log.info(event);
            metricEventRepository.save(event);
        } catch(Exception e) {
            log.error("Exception while saving kafka data to DB ", e);
        }
    }
}
