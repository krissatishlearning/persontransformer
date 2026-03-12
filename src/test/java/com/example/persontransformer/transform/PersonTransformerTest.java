package com.example.persontransformer.transform;

import com.example.persontransformer.domain.Person;
import com.example.persontransformer.dto.PersonEvent;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class PersonTransformerTest {

    private final PersonTransformer transformer = new PersonTransformer();

    @Test
    void transform_normalizesEmailAndTrims() {
        PersonEvent event = new PersonEvent("ext-1", "  Jane  ", "  Doe  ", "  Jane.Doe@Example.COM  ");
        Person person = transformer.transform(event);

        assertThat(person.getExternalId()).isEqualTo("ext-1");
        assertThat(person.getFirstName()).isEqualTo("Jane");
        assertThat(person.getLastName()).isEqualTo("Doe");
        assertThat(person.getEmail()).isEqualTo("  Jane.Doe@Example.COM  ");
        assertThat(person.getNormalizedEmail()).isEqualTo("jane.doe@example.com");
        assertThat(person.getUpdatedAt()).isNotNull();
        assertThat(person.getResourceId()).isNotNull();
    }

    @Test
    void transform_returnsNullForNullEvent() {
        assertThat(transformer.transform(null)).isNull();
    }

    @Test
    void transform_withNullOptionalFields_preservesNullsAndNormalizesEmail() {
        PersonEvent event = new PersonEvent("ext-1", null, null, "  Test@Example.COM  ");
        Person person = transformer.transform(event);

        assertThat(person.getExternalId()).isEqualTo("ext-1");
        assertThat(person.getFirstName()).isNull();
        assertThat(person.getLastName()).isNull();
        assertThat(person.getEmail()).isEqualTo("  Test@Example.COM  ");
        assertThat(person.getNormalizedEmail()).isEqualTo("test@example.com");
        assertThat(person.getUpdatedAt()).isNotNull();
        assertThat(person.getResourceId()).isNotNull();
    }

    @Test
    void applyToExisting_updatesFields() {
        UUID existingResourceId = UUID.randomUUID();
        Person existing = new Person("mongo-id", "ext-1", existingResourceId, "Old", "Name", "old@x.com", "old@x.com", null);
        PersonEvent event = new PersonEvent("ext-1", "New", "Name", "new@x.com");

        transformer.applyToExisting(existing, event);

        assertThat(existing.getFirstName()).isEqualTo("New");
        assertThat(existing.getLastName()).isEqualTo("Name");
        assertThat(existing.getEmail()).isEqualTo("new@x.com");
        assertThat(existing.getNormalizedEmail()).isEqualTo("new@x.com");
        assertThat(existing.getUpdatedAt()).isNotNull();
        assertThat(existing.getResourceId()).isEqualTo(existingResourceId);
    }

    @Test
    void applyToExisting_doesNothingWhenExistingIsNull() {
        PersonEvent event = new PersonEvent("ext-1", "New", "Name", "new@x.com");
        transformer.applyToExisting(null, event);
        // no exception, no-op
    }

    @Test
    void applyToExisting_doesNothingWhenEventIsNull() {
        UUID existingResourceId = UUID.randomUUID();
        Person existing = new Person("mongo-id", "ext-1", existingResourceId, "Old", "Name", "old@x.com", "old@x.com", null);
        transformer.applyToExisting(existing, null);
        assertThat(existing.getFirstName()).isEqualTo("Old");
        assertThat(existing.getLastName()).isEqualTo("Name");
        assertThat(existing.getResourceId()).isEqualTo(existingResourceId);
    }

    @Test
    void applyToExisting_withNullFirstNameInEvent_keepsExistingValue() {
        UUID existingResourceId = UUID.randomUUID();
        Person existing = new Person("mongo-id", "ext-1", existingResourceId, "Old", "Name", "old@x.com", "old@x.com", null);
        PersonEvent event = new PersonEvent("ext-1", null, "Surname", "a@b.com");
        transformer.applyToExisting(existing, event);
        assertThat(existing.getFirstName()).isEqualTo("Old");
        assertThat(existing.getLastName()).isEqualTo("Surname");
        assertThat(existing.getEmail()).isEqualTo("a@b.com");
        assertThat(existing.getResourceId()).isEqualTo(existingResourceId);
    }

    @Test
    void applyToExisting_withNullExistingFirstName_usesIncomingValue() {
        UUID existingResourceId = UUID.randomUUID();
        Person existing = new Person("mongo-id", "ext-1", existingResourceId, null, "Name", "old@x.com", "old@x.com", null);
        PersonEvent event = new PersonEvent("ext-1", "New", "Surname", "a@b.com");
        transformer.applyToExisting(existing, event);
        assertThat(existing.getFirstName()).isEqualTo("New");
        assertThat(existing.getLastName()).isEqualTo("Surname");
        assertThat(existing.getEmail()).isEqualTo("a@b.com");
        assertThat(existing.getResourceId()).isEqualTo(existingResourceId);
    }

    @Test
    void applyToExisting_withBothFirstNameNull_remainsNull() {
        UUID existingResourceId = UUID.randomUUID();
        Person existing = new Person("mongo-id", "ext-1", existingResourceId, null, "Name", "old@x.com", "old@x.com", null);
        PersonEvent event = new PersonEvent("ext-1", null, "Surname", "a@b.com");
        transformer.applyToExisting(existing, event);
        assertThat(existing.getFirstName()).isNull();
        assertThat(existing.getLastName()).isEqualTo("Surname");
        assertThat(existing.getResourceId()).isEqualTo(existingResourceId);
    }

    @Test
    void applyToExisting_withAllNullIncoming_keepsExistingValues() {
        UUID existingResourceId = UUID.randomUUID();
        Person existing = new Person("mongo-id", "ext-1", existingResourceId, "Old", "Name", "old@x.com", "old@x.com", null);
        PersonEvent event = new PersonEvent("ext-1", null, null, null);
        transformer.applyToExisting(existing, event);
        assertThat(existing.getFirstName()).isEqualTo("Old");
        assertThat(existing.getLastName()).isEqualTo("Name");
        assertThat(existing.getEmail()).isEqualTo("old@x.com");
        assertThat(existing.getResourceId()).isEqualTo(existingResourceId);
    }
}
