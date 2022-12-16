package com.shifu.consumer.repository;

import com.shifu.consumer.model.MetricEvent;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MetricEventRepository extends MongoRepository<MetricEvent, String> {
    Optional<MetricEvent> findByDeviceId(String deviceId);
}
