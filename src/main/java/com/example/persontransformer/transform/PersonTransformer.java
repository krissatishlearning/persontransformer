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
     * Null-safe merge logic:
     * - If incoming field is null, keep existing value (don't overwrite with null)
     * - If incoming field is non-null, use the incoming value
     */
    public void applyToExisting(Person existing, PersonEvent event) {
        if (existing == null || event == null) return;
        
        // Merge firstName: use incoming if non-null, otherwise keep existing
        String incomingFirstName = trim(event.getFirstName());
        if (incomingFirstName != null) {
            existing.setFirstName(incomingFirstName);
        }
        
        // Merge lastName: use incoming if non-null, otherwise keep existing
        String incomingLastName = trim(event.getLastName());
        if (incomingLastName != null) {
            existing.setLastName(incomingLastName);
        }
        
        // Merge email: use incoming if non-null, otherwise keep existing
        if (event.getEmail() != null) {
            existing.setEmail(event.getEmail());
            existing.setNormalizedEmail(normalizeEmail(event.getEmail()));
        }
        
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
