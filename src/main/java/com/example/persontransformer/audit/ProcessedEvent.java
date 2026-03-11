package com.example.persontransformer.audit;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "processed_events", indexes = {
    @Index(name = "idx_external_id", columnList = "externalId"),
    @Index(name = "idx_processed_at", columnList = "processedAt")
})
public class ProcessedEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String externalId;

    @Column(nullable = false)
    private String action; // INSERT or UPDATE

    @Column(nullable = false)
    private Instant processedAt;

    private String kafkaTopic;
    private Integer kafkaPartition;
    private Long kafkaOffset;

    public ProcessedEvent() {
    }

    public ProcessedEvent(String externalId, String action, Instant processedAt, String kafkaTopic, Integer kafkaPartition, Long kafkaOffset) {
        this.externalId = externalId;
        this.action = action;
        this.processedAt = processedAt;
        this.kafkaTopic = kafkaTopic;
        this.kafkaPartition = kafkaPartition;
        this.kafkaOffset = kafkaOffset;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Instant getProcessedAt() {
        return processedAt;
    }

    public void setProcessedAt(Instant processedAt) {
        this.processedAt = processedAt;
    }

    public String getKafkaTopic() {
        return kafkaTopic;
    }

    public void setKafkaTopic(String kafkaTopic) {
        this.kafkaTopic = kafkaTopic;
    }

    public Integer getKafkaPartition() {
        return kafkaPartition;
    }

    public void setKafkaPartition(Integer kafkaPartition) {
        this.kafkaPartition = kafkaPartition;
    }

    public Long getKafkaOffset() {
        return kafkaOffset;
    }

    public void setKafkaOffset(Long kafkaOffset) {
        this.kafkaOffset = kafkaOffset;
    }
}
