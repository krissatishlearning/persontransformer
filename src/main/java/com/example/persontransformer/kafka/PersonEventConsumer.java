package com.example.persontransformer.kafka;

import com.example.persontransformer.dto.PersonEvent;
import com.example.persontransformer.service.PersonUpsertService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class PersonEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(PersonEventConsumer.class);

    private final PersonUpsertService upsertService;

    public PersonEventConsumer(PersonUpsertService upsertService) {
        this.upsertService = upsertService;
    }

    @KafkaListener(topics = "${app.kafka.topic:person-events}")
    public void consume(ConsumerRecord<String, PersonEvent> record) {
        processRecord(record);
    }

    @KafkaListener(topics = "${app.kafka.topic-source-two:TP.SOURCE.TWO}")
    public void consumeSourceTwo(ConsumerRecord<String, PersonEvent> record) {
        processRecord(record);
    }

    private void processRecord(ConsumerRecord<String, PersonEvent> record) {
        PersonEvent event = record.value();
        if (event == null) {
            log.warn("Received null payload from topic {} partition {} offset {}",
                    record.topic(), record.partition(), record.offset());
            return;
        }
        log.debug("Consumed person event externalId={} from {} partition {} offset {}",
                event.getExternalId(), record.topic(), record.partition(), record.offset());

        upsertService.upsert(event, record.topic(), record.partition(), record.offset());
    }
}
