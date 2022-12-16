package com.shifu.consumer.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shifu.consumer.model.MetricEvent;
import com.shifu.consumer.repository.MetricEventRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Log4j2
public class KafkaConsumer {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private MetricEventRepository metricEventRepository;

    @KafkaListener(topics = "sampletopic", groupId = "shifu-metrics")
    public void consume(String metricEvent) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            MetricEvent event = mapper.readValue(metricEvent, MetricEvent.class);
            log.info(event);
            Optional<MetricEvent> eventPresentInDB = metricEventRepository.findByDeviceId(event.getDeviceId());
            if(eventPresentInDB.isPresent()) {
                log.info("metric data present with given device id");
                if(!checkEventInfo(event, eventPresentInDB.get())) {
                    updateMetricEvent(event);
                }
            } else {
                metricEventRepository.save(event);
            }
        } catch(Exception e) {
            log.error("Exception while saving kafka data to DB ", e);
        }
    }

    private boolean checkEventInfo(MetricEvent event, MetricEvent eventFromDB) {
        if(event.getMetrics().size() != eventFromDB.getMetrics().size() || event.getTimeStamps().size() != eventFromDB.getTimeStamps().size())
            return false;
        return event.getMetrics().entrySet().stream()
                .allMatch(e -> e.getValue().equals(eventFromDB.getMetrics().get(e.getKey())))
                && event.getTimeStamps().entrySet().stream()
                .allMatch(e -> e.getValue().equals(eventFromDB.getTimeStamps().get(e.getKey())))
                && event.getCountryCd().equals(eventFromDB.getCountryCd())
                && event.getSerialNum().equals(eventFromDB.getSerialNum())
                && event.getSiteNbr() == eventFromDB.getSiteNbr();
    }

    private void updateMetricEvent(MetricEvent metricEvent) {
        Query query = new Query();
        query.addCriteria(Criteria.where("deviceId").is(metricEvent.getDeviceId()));
        Update update = new Update();
        update.set("metrics", metricEvent.getMetrics());
        update.set("timeStamps", metricEvent.getTimeStamps());
        update.set("countryCd", metricEvent.getCountryCd());
        update.set("siteNbr", metricEvent.getSiteNbr());
        update.set("serialNum", metricEvent.getSerialNum());
        mongoTemplate.findAndModify(query, update, MetricEvent.class);
    }
}
