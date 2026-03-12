package com.example.persontransformer.transform;

import com.example.persontransformer.domain.Person;
import com.example.persontransformer.dto.PersonEvent;
import org.junit.jupiter.api.Test;

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
    }

    @Test
    void transform_returnsNullForNullEvent() {
        assertThat(transformer.transform(null)).isNull();
    }

    @Test
    void applyToExisting_updatesFields() {
        Person existing = new Person("mongo-id", "ext-1", "Old", "Name", "old@x.com", "old@x.com", null);
        PersonEvent event = new PersonEvent("ext-1", "New", "Name", "new@x.com");

        transformer.applyToExisting(existing, event);

        assertThat(existing.getFirstName()).isEqualTo("New");
        assertThat(existing.getLastName()).isEqualTo("Name");
        assertThat(existing.getEmail()).isEqualTo("new@x.com");
        assertThat(existing.getNormalizedEmail()).isEqualTo("new@x.com");
        assertThat(existing.getUpdatedAt()).isNotNull();
    }

    @Test
    void applyToExisting_whenExistingFirstNameIsNull_updatesFromEvent() {
        Person existing = new Person("mongo-id", "ext-1", null, "Doe", "jane@x.com", "jane@x.com", null);
        PersonEvent event = new PersonEvent("ext-1", "Jane", "Doe", "jane@x.com");

        transformer.applyToExisting(existing, event);

        assertThat(existing.getFirstName()).isEqualTo("Jane");
        assertThat(existing.getLastName()).isEqualTo("Doe");
    }

    @Test
    void applyToExisting_whenEventFirstNameIsNull_keepsExistingValue() {
        Person existing = new Person("mongo-id", "ext-1", "Jane", "Doe", "jane@x.com", "jane@x.com", null);
        PersonEvent event = new PersonEvent("ext-1", null, "Doe", "jane@x.com");

        transformer.applyToExisting(existing, event);

        assertThat(existing.getFirstName()).isEqualTo("Jane");
        assertThat(existing.getLastName()).isEqualTo("Doe");
    }

    @Test
    void applyToExisting_whenEventLastNameIsNull_keepsExistingValue() {
        Person existing = new Person("mongo-id", "ext-1", "Jane", "Doe", "jane@x.com", "jane@x.com", null);
        PersonEvent event = new PersonEvent("ext-1", "Jane", null, "jane@x.com");

        transformer.applyToExisting(existing, event);

        assertThat(existing.getFirstName()).isEqualTo("Jane");
        assertThat(existing.getLastName()).isEqualTo("Doe");
    }

    @Test
    void applyToExisting_whenEventEmailIsNull_keepsExistingValue() {
        Person existing = new Person("mongo-id", "ext-1", "Jane", "Doe", "jane@x.com", "jane@x.com", null);
        PersonEvent event = new PersonEvent("ext-1", "Jane", "Doe", null);

        transformer.applyToExisting(existing, event);

        assertThat(existing.getEmail()).isEqualTo("jane@x.com");
        assertThat(existing.getNormalizedEmail()).isEqualTo("jane@x.com");
    }

    @Test
    void applyToExisting_whenBothExistingAndEventAreNull_noNPE() {
        Person existing = new Person("mongo-id", "ext-1", null, null, null, null, null);
        PersonEvent event = new PersonEvent("ext-1", null, null, null);

        transformer.applyToExisting(existing, event);

        assertThat(existing.getFirstName()).isNull();
        assertThat(existing.getLastName()).isNull();
        assertThat(existing.getEmail()).isNull();
        assertThat(existing.getUpdatedAt()).isNotNull();
    }

    @Test
    void applyToExisting_trimsIncomingValues() {
        Person existing = new Person("mongo-id", "ext-1", "Old", "Name", "old@x.com", "old@x.com", null);
        PersonEvent event = new PersonEvent("ext-1", "  New  ", "  Updated  ", "  new@x.com  ");

        transformer.applyToExisting(existing, event);

        assertThat(existing.getFirstName()).isEqualTo("New");
        assertThat(existing.getLastName()).isEqualTo("Updated");
        assertThat(existing.getEmail()).isEqualTo("  new@x.com  ");
        assertThat(existing.getNormalizedEmail()).isEqualTo("new@x.com");
    }
}
