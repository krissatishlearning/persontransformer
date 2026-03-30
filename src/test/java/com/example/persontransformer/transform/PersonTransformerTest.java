package com.example.persontransformer.transform;

import com.example.persontransformer.domain.Person;
import com.example.persontransformer.dto.PersonEvent;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PersonTransformerTest {

    private final PersonTransformer transformer = new PersonTransformer();

    @Test
    void transform_normalizesEmailAndTrims() {
        PersonEvent event = new PersonEvent("ext-1", "  Jane  ", "  Doe  ", "  Jane.Doe@Example.COM  ", "  Asian  ", "  Hispanic  ");
        Person person = transformer.transform(event);

        assertThat(person.getExternalId()).isEqualTo("ext-1");
        assertThat(person.getFirstName()).isEqualTo("Jane");
        assertThat(person.getLastName()).isEqualTo("Doe");
        assertThat(person.getEmail()).isEqualTo("  Jane.Doe@Example.COM  ");
        assertThat(person.getNormalizedEmail()).isEqualTo("jane.doe@example.com");
        assertThat(person.getRace()).isEqualTo("Asian");
        assertThat(person.getEthnicity()).isEqualTo("Hispanic");
        assertThat(person.getUpdatedAt()).isNotNull();
    }

    @Test
    void transform_returnsNullForNullEvent() {
        assertThat(transformer.transform(null)).isNull();
    }

    @Test
    void transform_withNullOptionalFields_preservesNullsAndNormalizesEmail() {
        PersonEvent event = new PersonEvent("ext-1", null, null, "  Test@Example.COM  ", null, null);
        Person person = transformer.transform(event);

        assertThat(person.getExternalId()).isEqualTo("ext-1");
        assertThat(person.getFirstName()).isNull();
        assertThat(person.getLastName()).isNull();
        assertThat(person.getEmail()).isEqualTo("  Test@Example.COM  ");
        assertThat(person.getNormalizedEmail()).isEqualTo("test@example.com");
        assertThat(person.getRace()).isNull();
        assertThat(person.getEthnicity()).isNull();
        assertThat(person.getUpdatedAt()).isNotNull();
    }

    @Test
    void transform_withRaceAndEthnicity() {
        PersonEvent event = new PersonEvent("ext-2", "John", "Smith", "john@example.com", "White", "Non-Hispanic");
        Person person = transformer.transform(event);

        assertThat(person.getExternalId()).isEqualTo("ext-2");
        assertThat(person.getFirstName()).isEqualTo("John");
        assertThat(person.getLastName()).isEqualTo("Smith");
        assertThat(person.getRace()).isEqualTo("White");
        assertThat(person.getEthnicity()).isEqualTo("Non-Hispanic");
        assertThat(person.getUpdatedAt()).isNotNull();
    }

    @Test
    void transform_withoutRaceAndEthnicity_usesLegacyConstructor() {
        PersonEvent event = new PersonEvent("ext-3", "Jane", "Doe", "jane@example.com");
        Person person = transformer.transform(event);

        assertThat(person.getExternalId()).isEqualTo("ext-3");
        assertThat(person.getFirstName()).isEqualTo("Jane");
        assertThat(person.getLastName()).isEqualTo("Doe");
        assertThat(person.getRace()).isNull();
        assertThat(person.getEthnicity()).isNull();
        assertThat(person.getUpdatedAt()).isNotNull();
    }

    @Test
    void applyToExisting_updatesFields() {
        Person existing = new Person("mongo-id", "ext-1", "Old", "Name", "old@x.com", "old@x.com", "OldRace", "OldEthnicity", null);
        PersonEvent event = new PersonEvent("ext-1", "New", "Name", "new@x.com", "NewRace", "NewEthnicity");

        transformer.applyToExisting(existing, event);

        assertThat(existing.getFirstName()).isEqualTo("New");
        assertThat(existing.getLastName()).isEqualTo("Name");
        assertThat(existing.getEmail()).isEqualTo("new@x.com");
        assertThat(existing.getNormalizedEmail()).isEqualTo("new@x.com");
        assertThat(existing.getRace()).isEqualTo("NewRace");
        assertThat(existing.getEthnicity()).isEqualTo("NewEthnicity");
        assertThat(existing.getUpdatedAt()).isNotNull();
    }

    @Test
    void applyToExisting_doesNothingWhenExistingIsNull() {
        PersonEvent event = new PersonEvent("ext-1", "New", "Name", "new@x.com", "Race", "Ethnicity");
        transformer.applyToExisting(null, event);
        // no exception, no-op
    }

    @Test
    void applyToExisting_doesNothingWhenEventIsNull() {
        Person existing = new Person("mongo-id", "ext-1", "Old", "Name", "old@x.com", "old@x.com", "OldRace", "OldEthnicity", null);
        transformer.applyToExisting(existing, null);
        assertThat(existing.getFirstName()).isEqualTo("Old");
        assertThat(existing.getLastName()).isEqualTo("Name");
        assertThat(existing.getRace()).isEqualTo("OldRace");
        assertThat(existing.getEthnicity()).isEqualTo("OldEthnicity");
    }

    @Test
    void applyToExisting_withNullFirstNameInEvent_keepsExistingValue() {
        Person existing = new Person("mongo-id", "ext-1", "Old", "Name", "old@x.com", "old@x.com", "OldRace", "OldEthnicity", null);
        PersonEvent event = new PersonEvent("ext-1", null, "Surname", "a@b.com", null, "NewEthnicity");
        transformer.applyToExisting(existing, event);
        assertThat(existing.getFirstName()).isEqualTo("Old");
        assertThat(existing.getLastName()).isEqualTo("Surname");
        assertThat(existing.getEmail()).isEqualTo("a@b.com");
        assertThat(existing.getRace()).isEqualTo("OldRace");
        assertThat(existing.getEthnicity()).isEqualTo("NewEthnicity");
    }

    @Test
    void applyToExisting_withNullExistingFirstName_usesIncomingValue() {
        Person existing = new Person("mongo-id", "ext-1", null, "Name", "old@x.com", "old@x.com", null, null, null);
        PersonEvent event = new PersonEvent("ext-1", "New", "Surname", "a@b.com", "NewRace", "NewEthnicity");
        transformer.applyToExisting(existing, event);
        assertThat(existing.getFirstName()).isEqualTo("New");
        assertThat(existing.getLastName()).isEqualTo("Surname");
        assertThat(existing.getEmail()).isEqualTo("a@b.com");
        assertThat(existing.getRace()).isEqualTo("NewRace");
        assertThat(existing.getEthnicity()).isEqualTo("NewEthnicity");
    }

    @Test
    void applyToExisting_withBothFirstNameNull_remainsNull() {
        Person existing = new Person("mongo-id", "ext-1", null, "Name", "old@x.com", "old@x.com", null, null, null);
        PersonEvent event = new PersonEvent("ext-1", null, "Surname", "a@b.com", null, null);
        transformer.applyToExisting(existing, event);
        assertThat(existing.getFirstName()).isNull();
        assertThat(existing.getLastName()).isEqualTo("Surname");
        assertThat(existing.getRace()).isNull();
        assertThat(existing.getEthnicity()).isNull();
    }

    @Test
    void applyToExisting_withAllNullIncoming_keepsExistingValues() {
        Person existing = new Person("mongo-id", "ext-1", "Old", "Name", "old@x.com", "old@x.com", "OldRace", "OldEthnicity", null);
        PersonEvent event = new PersonEvent("ext-1", null, null, null, null, null);
        transformer.applyToExisting(existing, event);
        assertThat(existing.getFirstName()).isEqualTo("Old");
        assertThat(existing.getLastName()).isEqualTo("Name");
        assertThat(existing.getEmail()).isEqualTo("old@x.com");
        assertThat(existing.getRace()).isEqualTo("OldRace");
        assertThat(existing.getEthnicity()).isEqualTo("OldEthnicity");
    }

    @Test
    void applyToExisting_withNullRaceAndEthnicityInEvent_keepsExistingValues() {
        Person existing = new Person("mongo-id", "ext-1", "Old", "Name", "old@x.com", "old@x.com", "ExistingRace", "ExistingEthnicity", null);
        PersonEvent event = new PersonEvent("ext-1", "New", "Name", "new@x.com");
        transformer.applyToExisting(existing, event);
        assertThat(existing.getFirstName()).isEqualTo("New");
        assertThat(existing.getRace()).isEqualTo("ExistingRace");
        assertThat(existing.getEthnicity()).isEqualTo("ExistingEthnicity");
    }

    @Test
    void applyToExisting_withNullExistingRaceAndEthnicity_usesIncomingValues() {
        Person existing = new Person("mongo-id", "ext-1", "Old", "Name", "old@x.com", "old@x.com", null, null, null);
        PersonEvent event = new PersonEvent("ext-1", "New", "Name", "new@x.com", "IncomingRace", "IncomingEthnicity");
        transformer.applyToExisting(existing, event);
        assertThat(existing.getRace()).isEqualTo("IncomingRace");
        assertThat(existing.getEthnicity()).isEqualTo("IncomingEthnicity");
    }
}
