package com.example.persontransformer.service;

import com.example.persontransformer.audit.ProcessedEvent;
import com.example.persontransformer.audit.ProcessedEventRepository;
import com.example.persontransformer.domain.Person;
import com.example.persontransformer.dto.PersonEvent;
import com.example.persontransformer.repository.PersonMongoRepository;
import com.example.persontransformer.transform.PersonTransformer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PersonUpsertServiceTest {

    @Mock
    private PersonMongoRepository mongoRepository;

    @Mock
    private ProcessedEventRepository auditRepository;

    @Mock
    private PersonTransformer transformer;

    private PersonUpsertService upsertService;

    @BeforeEach
    void setUp() {
        upsertService = new PersonUpsertService(mongoRepository, auditRepository, transformer);
    }

    @Test
    void upsert_insertsNewPerson_whenNotFoundInMongo() {
        PersonEvent event = new PersonEvent("ext-1", "John", "Doe", "john@example.com");
        Person newPerson = new Person(null, "ext-1", "John", "Doe", "john@example.com", "john@example.com", Instant.now());

        when(mongoRepository.findByExternalId("ext-1")).thenReturn(Optional.empty());
        when(transformer.transform(event)).thenReturn(newPerson);

        upsertService.upsert(event, "test-topic", 0, 100L);

        verify(mongoRepository).save(newPerson);
        verify(transformer).transform(event);
        verify(transformer, never()).applyToExisting(any(), any());

        ArgumentCaptor<ProcessedEvent> auditCaptor = ArgumentCaptor.forClass(ProcessedEvent.class);
        verify(auditRepository).save(auditCaptor.capture());
        ProcessedEvent audit = auditCaptor.getValue();
        assertThat(audit.getExternalId()).isEqualTo("ext-1");
        assertThat(audit.getAction()).isEqualTo("INSERT");
        assertThat(audit.getKafkaTopic()).isEqualTo("test-topic");
        assertThat(audit.getKafkaPartition()).isEqualTo(0);
        assertThat(audit.getKafkaOffset()).isEqualTo(100L);
        assertThat(audit.getProcessedAt()).isNotNull();
    }

    @Test
    void upsert_updatesExistingPerson_whenFoundInMongo() {
        PersonEvent event = new PersonEvent("ext-1", "Jane", "Smith", "jane@example.com");
        Person existingPerson = new Person("mongo-id", "ext-1", "John", "Doe", "john@example.com", "john@example.com", Instant.now());

        when(mongoRepository.findByExternalId("ext-1")).thenReturn(Optional.of(existingPerson));

        upsertService.upsert(event, "test-topic", 1, 200L);

        verify(transformer).applyToExisting(existingPerson, event);
        verify(mongoRepository).save(existingPerson);
        verify(transformer, never()).transform(any());

        ArgumentCaptor<ProcessedEvent> auditCaptor = ArgumentCaptor.forClass(ProcessedEvent.class);
        verify(auditRepository).save(auditCaptor.capture());
        ProcessedEvent audit = auditCaptor.getValue();
        assertThat(audit.getExternalId()).isEqualTo("ext-1");
        assertThat(audit.getAction()).isEqualTo("UPDATE");
        assertThat(audit.getKafkaTopic()).isEqualTo("test-topic");
        assertThat(audit.getKafkaPartition()).isEqualTo(1);
        assertThat(audit.getKafkaOffset()).isEqualTo(200L);
    }

    @Test
    void upsert_skipsProcessing_whenEventIsNull() {
        upsertService.upsert(null, "test-topic", 0, 0L);

        verifyNoInteractions(mongoRepository);
        verifyNoInteractions(auditRepository);
        verifyNoInteractions(transformer);
    }

    @Test
    void upsert_skipsProcessing_whenExternalIdIsNull() {
        PersonEvent event = new PersonEvent(null, "John", "Doe", "john@example.com");

        upsertService.upsert(event, "test-topic", 0, 0L);

        verifyNoInteractions(mongoRepository);
        verifyNoInteractions(auditRepository);
        verifyNoInteractions(transformer);
    }
}
