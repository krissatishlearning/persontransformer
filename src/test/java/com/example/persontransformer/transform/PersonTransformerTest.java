package com.example.persontransformer.transform;

import com.example.persontransformer.domain.Address;
import com.example.persontransformer.domain.Person;
import com.example.persontransformer.domain.Phone;
import com.example.persontransformer.dto.AddressDTO;
import com.example.persontransformer.dto.PersonEvent;
import com.example.persontransformer.dto.PhoneDTO;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
        assertThat(person.getEmail()).isEqualTo("jane.doe@example.com");
        assertThat(person.getUpdatedAt()).isNotNull();
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
        assertThat(person.getEmail()).isEqualTo("test@example.com");
        assertThat(person.getUpdatedAt()).isNotNull();
    }

    @Test
    void transform_doesNotContainNormalizedEmail() {
        // Ensure no normalizedEmail field exists in Person via reflection
        assertThat(java.util.Arrays.stream(Person.class.getDeclaredFields())
                .map(java.lang.reflect.Field::getName)
                .filter(name -> name.equals("normalizedEmail"))
                .count()).isZero();
    }

    @Test
    void transform_withRaceAndEthnicity_mapsToPersonCorrectly() {
        PersonEvent event = new PersonEvent("ext-1", "Jane", "Doe", "jane@example.com",
                "  Asian  ", "  Hispanic  ", null, null);
        Person person = transformer.transform(event);

        assertThat(person.getRace()).isEqualTo("Asian");
        assertThat(person.getEthnicity()).isEqualTo("Hispanic");
    }

    @Test
    void transform_withNullRaceAndEthnicity_personHasNullRaceAndEthnicity() {
        PersonEvent event = new PersonEvent("ext-1", "Jane", "Doe", "jane@example.com");
        Person person = transformer.transform(event);

        assertThat(person.getRace()).isNull();
        assertThat(person.getEthnicity()).isNull();
    }

    @Test
    void personEvent_containsRaceAndEthnicity() {
        // Ensure race and ethnicity fields exist in PersonEvent via reflection
        List<String> fieldNames = java.util.Arrays.stream(PersonEvent.class.getDeclaredFields())
                .map(java.lang.reflect.Field::getName)
                .toList();
        assertThat(fieldNames).contains("race", "ethnicity");
    }

    @Test
    void person_containsRaceAndEthnicity() {
        // Ensure race and ethnicity fields exist in Person via reflection
        List<String> fieldNames = java.util.Arrays.stream(Person.class.getDeclaredFields())
                .map(java.lang.reflect.Field::getName)
                .toList();
        assertThat(fieldNames).contains("race", "ethnicity");
    }

    @Test
    void applyToExisting_updatesFields() {
        Person existing = new Person("mongo-id", "ext-1", "Old", "Name", "old@x.com", null);
        PersonEvent event = new PersonEvent("ext-1", "New", "Name", "new@x.com");

        transformer.applyToExisting(existing, event);

        assertThat(existing.getFirstName()).isEqualTo("New");
        assertThat(existing.getLastName()).isEqualTo("Name");
        assertThat(existing.getEmail()).isEqualTo("new@x.com");
        assertThat(existing.getUpdatedAt()).isNotNull();
    }

    @Test
    void applyToExisting_doesNothingWhenExistingIsNull() {
        PersonEvent event = new PersonEvent("ext-1", "New", "Name", "new@x.com");
        transformer.applyToExisting(null, event);
        // no exception, no-op
    }

    @Test
    void applyToExisting_doesNothingWhenEventIsNull() {
        Person existing = new Person("mongo-id", "ext-1", "Old", "Name", "old@x.com", null);
        transformer.applyToExisting(existing, null);
        assertThat(existing.getFirstName()).isEqualTo("Old");
        assertThat(existing.getLastName()).isEqualTo("Name");
    }

    @Test
    void applyToExisting_withNullFirstNameInEvent_keepsExistingValue() {
        Person existing = new Person("mongo-id", "ext-1", "Old", "Name", "old@x.com", null);
        PersonEvent event = new PersonEvent("ext-1", null, "Surname", "a@b.com");
        transformer.applyToExisting(existing, event);
        assertThat(existing.getFirstName()).isEqualTo("Old");
        assertThat(existing.getLastName()).isEqualTo("Surname");
        assertThat(existing.getEmail()).isEqualTo("a@b.com");
    }

    @Test
    void applyToExisting_withNullExistingFirstName_usesIncomingValue() {
        Person existing = new Person("mongo-id", "ext-1", null, "Name", "old@x.com", null);
        PersonEvent event = new PersonEvent("ext-1", "New", "Surname", "a@b.com");
        transformer.applyToExisting(existing, event);
        assertThat(existing.getFirstName()).isEqualTo("New");
        assertThat(existing.getLastName()).isEqualTo("Surname");
        assertThat(existing.getEmail()).isEqualTo("a@b.com");
    }

    @Test
    void applyToExisting_withBothFirstNameNull_remainsNull() {
        Person existing = new Person("mongo-id", "ext-1", null, "Name", "old@x.com", null);
        PersonEvent event = new PersonEvent("ext-1", null, "Surname", "a@b.com");
        transformer.applyToExisting(existing, event);
        assertThat(existing.getFirstName()).isNull();
        assertThat(existing.getLastName()).isEqualTo("Surname");
    }

    @Test
    void applyToExisting_withAllNullIncoming_keepsExistingValues() {
        Person existing = new Person("mongo-id", "ext-1", "Old", "Name", "old@x.com", null);
        PersonEvent event = new PersonEvent("ext-1", null, null, null);
        transformer.applyToExisting(existing, event);
        assertThat(existing.getFirstName()).isEqualTo("Old");
        assertThat(existing.getLastName()).isEqualTo("Name");
        assertThat(existing.getEmail()).isEqualTo("old@x.com");
    }

    @Test
    void applyToExisting_updatesRaceAndEthnicity() {
        Person existing = new Person("mongo-id", "ext-1", "Old", "Name", "old@x.com", null);
        existing.setRace("White");
        existing.setEthnicity("Non-Hispanic");

        PersonEvent event = new PersonEvent("ext-1", "Old", "Name", "old@x.com",
                "  Asian  ", "  Hispanic  ", null, null);
        transformer.applyToExisting(existing, event);

        assertThat(existing.getRace()).isEqualTo("Asian");
        assertThat(existing.getEthnicity()).isEqualTo("Hispanic");
    }

    @Test
    void applyToExisting_withNullRaceInEvent_keepsExistingRace() {
        Person existing = new Person("mongo-id", "ext-1", "Old", "Name", "old@x.com", null);
        existing.setRace("White");
        existing.setEthnicity("Non-Hispanic");

        PersonEvent event = new PersonEvent("ext-1", "Old", "Name", "old@x.com");
        transformer.applyToExisting(existing, event);

        assertThat(existing.getRace()).isEqualTo("White");
        assertThat(existing.getEthnicity()).isEqualTo("Non-Hispanic");
    }

    @Test
    void applyToExisting_withNullExistingRace_usesIncomingRace() {
        Person existing = new Person("mongo-id", "ext-1", "Old", "Name", "old@x.com", null);

        PersonEvent event = new PersonEvent("ext-1", "Old", "Name", "old@x.com",
                "Asian", "Hispanic", null, null);
        transformer.applyToExisting(existing, event);

        assertThat(existing.getRace()).isEqualTo("Asian");
        assertThat(existing.getEthnicity()).isEqualTo("Hispanic");
    }

    // --- Address tests ---

    @Test
    void transform_withAddresses_transformsAddressList() {
        List<AddressDTO> addressDTOs = Arrays.asList(
                new AddressDTO("  123 Main St  ", "  Apt 4  ", "  Springfield  ", "  IL  ", "  62704  ", "  US  "),
                new AddressDTO("456 Oak Ave", null, "Chicago", "IL", "60601", "US")
        );
        PersonEvent event = new PersonEvent("ext-1", "Jane", "Doe", "jane@example.com", addressDTOs, null);
        Person person = transformer.transform(event);

        assertThat(person.getAddresses()).hasSize(2);
        Address first = person.getAddresses().get(0);
        assertThat(first.getAddress1()).isEqualTo("123 Main St");
        assertThat(first.getAddress2()).isEqualTo("Apt 4");
        assertThat(first.getCity()).isEqualTo("Springfield");
        assertThat(first.getState()).isEqualTo("IL");
        assertThat(first.getPostalCode()).isEqualTo("62704");
        assertThat(first.getCountry()).isEqualTo("US");

        Address second = person.getAddresses().get(1);
        assertThat(second.getAddress1()).isEqualTo("456 Oak Ave");
        assertThat(second.getAddress2()).isNull();
        assertThat(second.getCity()).isEqualTo("Chicago");
    }

    @Test
    void transform_withNullAddresses_returnsEmptyAddressList() {
        PersonEvent event = new PersonEvent("ext-1", "Jane", "Doe", "jane@example.com", null, null);
        Person person = transformer.transform(event);
        assertThat(person.getAddresses()).isNotNull().isEmpty();
    }

    @Test
    void transform_withEmptyAddresses_returnsEmptyAddressList() {
        PersonEvent event = new PersonEvent("ext-1", "Jane", "Doe", "jane@example.com", Collections.emptyList(), null);
        Person person = transformer.transform(event);
        assertThat(person.getAddresses()).isNotNull().isEmpty();
    }

    // --- Phone tests ---

    @Test
    void transform_withPhones_transformsPhoneList() {
        List<PhoneDTO> phoneDTOs = Arrays.asList(
                new PhoneDTO("  555-1234  ", "  MOBILE  "),
                new PhoneDTO("555-5678", "HOME")
        );
        PersonEvent event = new PersonEvent("ext-1", "Jane", "Doe", "jane@example.com", null, phoneDTOs);
        Person person = transformer.transform(event);

        assertThat(person.getPhones()).hasSize(2);
        Phone first = person.getPhones().get(0);
        assertThat(first.getPhoneNumber()).isEqualTo("555-1234");
        assertThat(first.getPhoneType()).isEqualTo("MOBILE");

        Phone second = person.getPhones().get(1);
        assertThat(second.getPhoneNumber()).isEqualTo("555-5678");
        assertThat(second.getPhoneType()).isEqualTo("HOME");
    }

    @Test
    void transform_withNullPhones_returnsEmptyPhoneList() {
        PersonEvent event = new PersonEvent("ext-1", "Jane", "Doe", "jane@example.com", null, null);
        Person person = transformer.transform(event);
        assertThat(person.getPhones()).isNotNull().isEmpty();
    }

    @Test
    void transform_withEmptyPhones_returnsEmptyPhoneList() {
        PersonEvent event = new PersonEvent("ext-1", "Jane", "Doe", "jane@example.com", null, Collections.emptyList());
        Person person = transformer.transform(event);
        assertThat(person.getPhones()).isNotNull().isEmpty();
    }

    @Test
    void transform_withAddressesAndPhones_transformsBoth() {
        List<AddressDTO> addressDTOs = Collections.singletonList(
                new AddressDTO("123 Main St", null, "Springfield", "IL", "62704", "US")
        );
        List<PhoneDTO> phoneDTOs = Collections.singletonList(
                new PhoneDTO("555-1234", "MOBILE")
        );
        PersonEvent event = new PersonEvent("ext-1", "Jane", "Doe", "jane@example.com", addressDTOs, phoneDTOs);
        Person person = transformer.transform(event);

        assertThat(person.getAddresses()).hasSize(1);
        assertThat(person.getPhones()).hasSize(1);
        assertThat(person.getAddresses().get(0).getAddress1()).isEqualTo("123 Main St");
        assertThat(person.getPhones().get(0).getPhoneNumber()).isEqualTo("555-1234");
    }

    // --- applyToExisting with addresses and phones ---

    @Test
    void applyToExisting_withAddresses_replacesExistingAddresses() {
        Person existing = new Person("mongo-id", "ext-1", "Old", "Name", "old@x.com", null);
        existing.setAddresses(Collections.singletonList(new Address("Old St", null, "OldCity", "OS", "00000", "US")));

        List<AddressDTO> newAddresses = Collections.singletonList(
                new AddressDTO("New St", "Suite 5", "NewCity", "NS", "11111", "CA")
        );
        PersonEvent event = new PersonEvent("ext-1", "Old", "Name", "old@x.com", newAddresses, null);
        transformer.applyToExisting(existing, event);

        assertThat(existing.getAddresses()).hasSize(1);
        assertThat(existing.getAddresses().get(0).getAddress1()).isEqualTo("New St");
        assertThat(existing.getAddresses().get(0).getCity()).isEqualTo("NewCity");
        assertThat(existing.getAddresses().get(0).getCountry()).isEqualTo("CA");
    }

    @Test
    void applyToExisting_withNullAddressesInEvent_keepsExistingAddresses() {
        Person existing = new Person("mongo-id", "ext-1", "Old", "Name", "old@x.com", null);
        List<Address> originalAddresses = Collections.singletonList(new Address("Old St", null, "OldCity", "OS", "00000", "US"));
        existing.setAddresses(originalAddresses);

        PersonEvent event = new PersonEvent("ext-1", "Old", "Name", "old@x.com", null, null);
        transformer.applyToExisting(existing, event);

        assertThat(existing.getAddresses()).hasSize(1);
        assertThat(existing.getAddresses().get(0).getAddress1()).isEqualTo("Old St");
    }

    @Test
    void applyToExisting_withPhones_replacesExistingPhones() {
        Person existing = new Person("mongo-id", "ext-1", "Old", "Name", "old@x.com", null);
        existing.setPhones(Collections.singletonList(new Phone("000-0000", "HOME")));

        List<PhoneDTO> newPhones = Collections.singletonList(new PhoneDTO("555-1234", "MOBILE"));
        PersonEvent event = new PersonEvent("ext-1", "Old", "Name", "old@x.com", null, newPhones);
        transformer.applyToExisting(existing, event);

        assertThat(existing.getPhones()).hasSize(1);
        assertThat(existing.getPhones().get(0).getPhoneNumber()).isEqualTo("555-1234");
        assertThat(existing.getPhones().get(0).getPhoneType()).isEqualTo("MOBILE");
    }

    @Test
    void applyToExisting_withNullPhonesInEvent_keepsExistingPhones() {
        Person existing = new Person("mongo-id", "ext-1", "Old", "Name", "old@x.com", null);
        existing.setPhones(Collections.singletonList(new Phone("000-0000", "HOME")));

        PersonEvent event = new PersonEvent("ext-1", "Old", "Name", "old@x.com", null, null);
        transformer.applyToExisting(existing, event);

        assertThat(existing.getPhones()).hasSize(1);
        assertThat(existing.getPhones().get(0).getPhoneNumber()).isEqualTo("000-0000");
    }
}
