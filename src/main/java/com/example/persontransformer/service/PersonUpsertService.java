package com.example.persontransformer.service;

import com.example.persontransformer.audit.ProcessedEvent;
import com.example.persontransformer.audit.ProcessedEventRepository;
import com.example.persontransformer.domain.Person;
import com.example.persontransformer.dto.PersonEvent;
import com.example.persontransformer.repository.PersonMongoRepository;
import com.example.persontransformer.transform.PersonTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

/**
 * Checks MongoDB for existing person by externalId.
 * If not present: saves new person. If present: updates existing.
 * Records the action in PostgreSQL audit table.
 */
@Service
public class PersonUpsertService {

    private static final Logger log = LoggerFactory.getLogger(PersonUpsertService.class);

    private final PersonMongoRepository mongoRepository;
    private final ProcessedEventRepository auditRepository;
    private final PersonTransformer transformer;

    public PersonUpsertService(PersonMongoRepository mongoRepository,
                               ProcessedEventRepository auditRepository,
                               PersonTransformer transformer) {
        this.mongoRepository = mongoRepository;
        this.auditRepository = auditRepository;
        this.transformer = transformer;
    }

    @Transactional
    public void upsert(PersonEvent event, String topic, int partition, long offset) {
        if (event == null || event.getExternalId() == null) {
            log.warn("Skipping event with null externalId");
            return;
        }

        var existingOpt = mongoRepository.findByExternalId(event.getExternalId());
        String action;
        if (existingOpt.isEmpty()) {
            Person newPerson = transformer.transform(event);
            mongoRepository.save(newPerson);
            action = "INSERT";
            log.info("Inserted person externalId={}", event.getExternalId());
        } else {
            Person existing = existingOpt.get();
            transformer.applyToExisting(existing, event);
            mongoRepository.save(existing);
            action = "UPDATE";
            log.info("Updated person externalId={}", event.getExternalId());
        }

        ProcessedEvent audit = new ProcessedEvent(
                event.getExternalId(),
                action,
                Instant.now(),
                topic,
                partition,
                offset
        );
        auditRepository.save(audit);
    }
}
