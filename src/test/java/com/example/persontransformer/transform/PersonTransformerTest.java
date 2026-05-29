package com.example.persontransformer.transform;

import com.example.persontransformer.domain.Address;
import com.example.persontransformer.domain.Person;
import com.example.persontransformer.domain.Phone;
import com.example.persontransformer.dto.AddressDTO;
import com.example.persontransformer.dto.PersonEvent;
import com.example.persontransformer.dto.PhoneDTO;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PersonTransformerTest {

    private static final Instant FIXED_INSTANT = Instant.parse("2024-01-15T10:00:00Z");
    private static final Clock FIXED_CLOCK = Clock.fixed(FIXED_INSTANT, ZoneOffset.UTC);

    private final PersonTransformer transformer = new PersonTransformer(FIXED_CLOCK);

    @Test
    void transform_normalizesEmailAndTrims() {
        PersonEvent event = new PersonEvent("ext-1", "  Jane  ", "  Doe  ", "  Jane.Doe@Example.COM  ");
        Person person = transformer.transform(event);

        assertThat(person.getExternalId()).isEqualTo("ext-1");
        assertThat(person.getFirstName()).isEqualTo("Jane");
        assertThat(person.getLastName()).isEqualTo("Doe");
        assertThat(person.getEmail()).isEqualTo("jane.doe@example.com");
        assertThat(person.getUpdatedAt()).isEqualTo(FIXED_INSTANT);
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
        assertThat(person.getUpdatedAt()).isEqualTo(FIXED_INSTANT);
    }

    // --- Race and Ethnicity field tests ---

    @Test
    void transform_withRaceAndEthnicity_mapsFieldsToPerson() {
        PersonEvent event = new PersonEvent("ext-1", "Jane", "Doe", "jane@example.com",
                "Asian", "Not Hispanic or Latino");
        Person person = transformer.transform(event);

        assertThat(person.getRace()).isEqualTo("Asian");
        assertThat(person.getEthnicity()).isEqualTo("Not Hispanic or Latino");
    }

    @Test
    void transform_trimsRaceAndEthnicity() {
        PersonEvent event = new PersonEvent("ext-1", "Jane", "Doe", "jane@example.com",
                "  White  ", "  Hispanic or Latino  ");
        Person person = transformer.transform(event);

        assertThat(person.getRace()).isEqualTo("White");
        assertThat(person.getEthnicity()).isEqualTo("Hispanic or Latino");
    }

    @Test
    void transform_withNullRaceAndEthnicity_setsNullOnPerson() {
        PersonEvent event = new PersonEvent("ext-1", "Jane", "Doe", "jane@example.com",
                null, null);
        Person person = transformer.transform(event);

        assertThat(person.getRace()).isNull();
        assertThat(person.getEthnicity()).isNull();
    }

    @Test
    void transform_withRaceEthnicityAndAddressesAndPhones_mapsAllFields() {
        List<AddressDTO> addressDTOs = Collections.singletonList(
                new AddressDTO("123 Main St", "Apt 1", "Springfield", "IL", "62704", "US")
        );
        List<PhoneDTO> phoneDTOs = Collections.singletonList(
                new PhoneDTO("555-1234", "MOBILE")
        );
        PersonEvent event = new PersonEvent("ext-1", "Jane", "Doe", "jane@example.com",
                "Asian", "Not Hispanic or Latino", addressDTOs, phoneDTOs);
        Person person = transformer.transform(event);

        assertThat(person.getExternalId()).isEqualTo("ext-1");
        assertThat(person.getFirstName()).isEqualTo("Jane");
        assertThat(person.getLastName()).isEqualTo("Doe");
        assertThat(person.getEmail()).isEqualTo("jane@example.com");
        assertThat(person.getRace()).isEqualTo("Asian");
        assertThat(person.getEthnicity()).isEqualTo("Not Hispanic or Latino");
        assertThat(person.getUpdatedAt()).isEqualTo(FIXED_INSTANT);

        assertThat(person.getAddresses()).hasSize(1);
        Address address = person.getAddresses().get(0);
        assertThat(address.getAddress1()).isEqualTo("123 Main St");
        assertThat(address.getAddress2()).isEqualTo("Apt 1");
        assertThat(address.getCity()).isEqualTo("Springfield");
        assertThat(address.getState()).isEqualTo("IL");
        assertThat(address.getPostalCode()).isEqualTo("62704");
        assertThat(address.getCountry()).isEqualTo("US");

        assertThat(person.getPhones()).hasSize(1);
        Phone phone = person.getPhones().get(0);
        assertThat(phone.getPhoneNumber()).isEqualTo("555-1234");
        assertThat(phone.getPhoneType()).isEqualTo("MOBILE");
    }

    @Test
    void applyToExisting_updatesRaceAndEthnicity() {
        Person existing = new Person("mongo-id", "ext-1", "Old", "Name", "old@x.com", null);
        existing.setRace("White");
        existing.setEthnicity("Not Hispanic or Latino");

        PersonEvent event = new PersonEvent("ext-1", "Old", "Name", "old@x.com",
                "Black or African American", "Hispanic or Latino");
        transformer.applyToExisting(existing, event);

        assertThat(existing.getRace()).isEqualTo("Black or African American");
        assertThat(existing.getEthnicity()).isEqualTo("Hispanic or Latino");
    }

    @Test
    void applyToExisting_withNullRaceInEvent_keepsExistingRace() {
        Person existing = new Person("mongo-id", "ext-1", "Old", "Name", "old@x.com", null);
        existing.setRace("Asian");
        existing.setEthnicity("Not Hispanic or Latino");

        PersonEvent event = new PersonEvent("ext-1", "Old", "Name", "old@x.com",
                null, null);
        transformer.applyToExisting(existing, event);

        assertThat(existing.getRace()).isEqualTo("Asian");
        assertThat(existing.getEthnicity()).isEqualTo("Not Hispanic or Latino");
    }

    @Test
    void applyToExisting_withNullExistingRace_usesIncomingRace() {
        Person existing = new Person("mongo-id", "ext-1", "Old", "Name", "old@x.com", null);
        existing.setRace(null);
        existing.setEthnicity(null);

        PersonEvent event = new PersonEvent("ext-1", "Old", "Name", "old@x.com",
                "  White  ", "  Hispanic or Latino  ");
        transformer.applyToExisting(existing, event);

        assertThat(existing.getRace()).isEqualTo("White");
        assertThat(existing.getEthnicity()).isEqualTo("Hispanic or Latino");
    }

    @Test
    void applyToExisting_updatesFields() {
        Person existing = new Person("mongo-id", "ext-1", "Old", "Name", "old@x.com", null);
        PersonEvent event = new PersonEvent("ext-1", "New", "Name", "new@x.com");

        transformer.applyToExisting(existing, event);

        assertThat(existing.getFirstName()).isEqualTo("New");
        assertThat(existing.getLastName()).isEqualTo("Name");
        assertThat(existing.getEmail()).isEqualTo("new@x.com");
        assertThat(existing.getUpdatedAt()).isEqualTo(FIXED_INSTANT);
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
        assertThat(person.getAddresses().get(0).getAddress1()).isEqualTo("123 Main St");
        assertThat(person.getPhones()).hasSize(1);
        assertThat(person.getPhones().get(0).getPhoneNumber()).isEqualTo("555-1234");
    }
}
