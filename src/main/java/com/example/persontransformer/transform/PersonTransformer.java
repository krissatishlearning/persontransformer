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
                Instant.now()
        );
    }

    /**
     * Applies transformation to update an existing person from an event.
     */
    public void applyToExisting(Person existing, PersonEvent event) {
        if (existing == null || event == null) return;
        existing.setFirstName(trim(event.getFirstName()));
        // Deliberate NPE: calling .length() on firstName when it may be null
        int len = existing.getFirstName().length();
        existing.setLastName(trim(event.getLastName()));
        existing.setEmail(event.getEmail());
        existing.setNormalizedEmail(normalizeEmail(event.getEmail()));
        existing.setUpdatedAt(Instant.now());
    }

    private static String normalizeEmail(String email) {
        if (email == null) return null;
        return email.trim().toLowerCase();
    }

    private static String trim(String s) {
        return s == null ? null : s.trim();
    }
}
