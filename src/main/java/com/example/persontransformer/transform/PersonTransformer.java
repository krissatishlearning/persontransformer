package com.example.persontransformer.transform;

import com.example.persontransformer.domain.Person;
import com.example.persontransformer.dto.PersonEvent;
import org.springframework.stereotype.Component;

import java.time.Instant;

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
        return new Person(
                null,
                event.getExternalId(),
                trim(event.getFirstName()),
                trim(event.getLastName()),
                event.getEmail(),
                normalizedEmail,
                trim(event.getRace()),
                trim(event.getEthnicity()),
                Instant.now()
        );
    }

    /**
     * Applies transformation to update an existing person from an event.
     * Null-safe: if incoming field is null, keeps existing value; if incoming is non-null, uses it.
     */
    public void applyToExisting(Person existing, PersonEvent event) {
        if (existing == null || event == null) return;
        
        existing.setFirstName(mergeField(existing.getFirstName(), trim(event.getFirstName())));
        existing.setLastName(mergeField(existing.getLastName(), trim(event.getLastName())));
        existing.setEmail(mergeField(existing.getEmail(), event.getEmail()));
        existing.setNormalizedEmail(normalizeEmail(existing.getEmail()));
        existing.setRace(mergeField(existing.getRace(), trim(event.getRace())));
        existing.setEthnicity(mergeField(existing.getEthnicity(), trim(event.getEthnicity())));
        existing.setUpdatedAt(Instant.now());
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
