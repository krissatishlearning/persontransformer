package com.example.persontransformer.kafka;

import com.example.persontransformer.dto.PersonEvent;
import com.example.persontransformer.service.PersonUpsertService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PersonEventConsumerTest {

    @Mock
    private PersonUpsertService upsertService;

    private PersonEventConsumer consumer;

    @BeforeEach
    void setUp() {
        consumer = new PersonEventConsumer(upsertService);
    }

    @Test
    void consume_callsUpsertService_withValidEvent() {
        PersonEvent event = new PersonEvent("ext-1", "John", "Doe", "john@example.com");
        ConsumerRecord<String, PersonEvent> record = new ConsumerRecord<>("test-topic", 0, 100L, "key", event);

        consumer.consume(record);

        verify(upsertService).upsert(event, "test-topic", 0, 100L);
    }

    @Test
    void consume_skipsUpsert_whenPayloadIsNull() {
        ConsumerRecord<String, PersonEvent> record = new ConsumerRecord<>("test-topic", 0, 100L, "key", null);

        consumer.consume(record);

        verifyNoInteractions(upsertService);
    }

    @Test
    void consume_callsUpsertService_withCorrectTopicPartitionOffset() {
        PersonEvent event = new PersonEvent("ext-2", "Jane", "Smith", "jane@example.com");
        ConsumerRecord<String, PersonEvent> record = new ConsumerRecord<>("TP.SOURCE.TWO", 3, 999L, "key2", event);

        consumer.consume(record);

        verify(upsertService).upsert(event, "TP.SOURCE.TWO", 3, 999L);
    }
}
