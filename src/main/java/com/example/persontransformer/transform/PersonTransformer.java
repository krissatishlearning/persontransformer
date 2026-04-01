package com.example.persontransformer.transform;

import com.example.persontransformer.domain.Address;
import com.example.persontransformer.domain.Person;
import com.example.persontransformer.domain.Phone;
import com.example.persontransformer.dto.AddressDTO;
import com.example.persontransformer.dto.PersonEvent;
import com.example.persontransformer.dto.PhoneDTO;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Transforms incoming Kafka person events into the domain Person model.
 * Normalizes email (lowercase, trim) and trims string fields.
 */
@Component
public class PersonTransformer {

    public Person transform(PersonEvent event) {
        if (event == null) {
            return null;
        }
        String normalizedEmail = normalizeEmail(event.getEmail());
        Person person = new Person(
                null,
                event.getExternalId(),
                trim(event.getFirstName()),
                trim(event.getLastName()),
                normalizedEmail,
                Instant.now()
        );
        person.setRace(trim(event.getRace()));
        person.setEthnicity(trim(event.getEthnicity()));
        person.setAddresses(transformAddresses(event.getAddresses()));
        person.setPhones(transformPhones(event.getPhones()));
        return person;
    }

    /**
     * Applies transformation to update an existing person from an event.
     * Null-safe: if incoming field is null, keeps existing value; if incoming is non-null, uses it.
     */
    public void applyToExisting(Person existing, PersonEvent event) {
        if (existing == null || event == null) return;

        existing.setFirstName(mergeField(existing.getFirstName(), trim(event.getFirstName())));
        existing.setLastName(mergeField(existing.getLastName(), trim(event.getLastName())));
        String mergedEmail = mergeField(existing.getEmail(), event.getEmail());
        existing.setEmail(normalizeEmail(mergedEmail));
        existing.setRace(mergeField(existing.getRace(), trim(event.getRace())));
        existing.setEthnicity(mergeField(existing.getEthnicity(), trim(event.getEthnicity())));
        existing.setUpdatedAt(Instant.now());

        if (event.getAddresses() != null) {
            existing.setAddresses(transformAddresses(event.getAddresses()));
        }
        if (event.getPhones() != null) {
            existing.setPhones(transformPhones(event.getPhones()));
        }
    }

    List<Address> transformAddresses(List<AddressDTO> addressDTOs) {
        if (addressDTOs == null) {
            return new ArrayList<>();
        }
        return addressDTOs.stream()
                .map(this::transformAddress)
                .collect(Collectors.toList());
    }

    Address transformAddress(AddressDTO dto) {
        if (dto == null) {
            return null;
        }
        return new Address(
                trim(dto.getAddress1()),
                trim(dto.getAddress2()),
                trim(dto.getCity()),
                trim(dto.getState()),
                trim(dto.getPostalCode()),
                trim(dto.getCountry())
        );
    }

    List<Phone> transformPhones(List<PhoneDTO> phoneDTOs) {
        if (phoneDTOs == null) {
            return new ArrayList<>();
        }
        return phoneDTOs.stream()
                .map(this::transformPhone)
                .collect(Collectors.toList());
    }

    Phone transformPhone(PhoneDTO dto) {
        if (dto == null) {
            return null;
        }
        return new Phone(
                trim(dto.getPhoneNumber()),
                trim(dto.getPhoneType())
        );
    }

    /**
     * Merge logic: if incoming is non-null, use it; otherwise keep existing.
     */
    private static String mergeField(String existingValue, String incomingValue) {
        return incomingValue != null ? incomingValue : existingValue;
    }

    private static String normalizeEmail(String email) {
        if (email == null) return null;
        return email.trim().toLowerCase();
    }

    private static String trim(String s) {
        return s == null ? null : s.trim();
    }
}
