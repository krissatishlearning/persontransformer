package com.example.persontransformer.audit;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class ProcessedEventTest {

    @Test
    void defaultConstructor_createsEmptyInstance() {
        ProcessedEvent event = new ProcessedEvent();
        assertThat(event.getId()).isNull();
        assertThat(event.getExternalId()).isNull();
        assertThat(event.getAction()).isNull();
        assertThat(event.getProcessedAt()).isNull();
        assertThat(event.getKafkaTopic()).isNull();
        assertThat(event.getKafkaPartition()).isNull();
        assertThat(event.getKafkaOffset()).isNull();
    }

    @Test
    void parameterizedConstructor_setsAllFields() {
        Instant now = Instant.now();
        ProcessedEvent event = new ProcessedEvent("ext-1", "INSERT", now, "topic", 2, 500L);

        assertThat(event.getExternalId()).isEqualTo("ext-1");
        assertThat(event.getAction()).isEqualTo("INSERT");
        assertThat(event.getProcessedAt()).isEqualTo(now);
        assertThat(event.getKafkaTopic()).isEqualTo("topic");
        assertThat(event.getKafkaPartition()).isEqualTo(2);
        assertThat(event.getKafkaOffset()).isEqualTo(500L);
    }

    @Test
    void settersAndGetters_workCorrectly() {
        ProcessedEvent event = new ProcessedEvent();
        Instant now = Instant.now();

        event.setId(1L);
        event.setExternalId("ext-2");
        event.setAction("UPDATE");
        event.setProcessedAt(now);
        event.setKafkaTopic("my-topic");
        event.setKafkaPartition(5);
        event.setKafkaOffset(1000L);

        assertThat(event.getId()).isEqualTo(1L);
        assertThat(event.getExternalId()).isEqualTo("ext-2");
        assertThat(event.getAction()).isEqualTo("UPDATE");
        assertThat(event.getProcessedAt()).isEqualTo(now);
        assertThat(event.getKafkaTopic()).isEqualTo("my-topic");
        assertThat(event.getKafkaPartition()).isEqualTo(5);
        assertThat(event.getKafkaOffset()).isEqualTo(1000L);
    }
}
