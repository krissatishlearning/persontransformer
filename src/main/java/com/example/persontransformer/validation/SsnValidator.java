package com.example.persontransformer.validation;

import com.example.persontransformer.domain.PersonIdentifier;

import java.util.regex.Pattern;

/**
 * Validates SSN values according to standard US SSN rules.
 * <p>
 * Accepted formats: XXX-XX-XXXX (with dashes) or XXXXXXXXX (9 digits, no dashes).
 * Regex: {@code ^\d{3}-?\d{2}-?\d{4}$}
 * </p>
 * <p>
 * Known invalid values:
 * <ul>
 *   <li>All zeros in any group: 000-XX-XXXX, XXX-00-XXXX, XXX-XX-0000</li>
 *   <li>Area (first 3 digits) 666 — never assigned</li>
 *   <li>Area 900–999 — reserved (ITINs)</li>
 *   <li>All same digit: 111-11-1111, 999-99-9999, etc.</li>
 *   <li>Known test/placeholder: 123-45-6789</li>
 * </ul>
 * </p>
 */
public class SsnValidator {

    private static final Pattern SSN_FORMAT_PATTERN = Pattern.compile("^\\d{3}-?\\d{2}-?\\d{4}$");
    private static final String KNOWN_PLACEHOLDER_NORMALIZED = "123456789";

    /**
     * Validates the given SSN string and returns a {@link PersonIdentifier}
     * with {@code isValid} set appropriately.
     *
     * @param ssnValue the raw SSN string (may be null)
     * @return a {@link PersonIdentifier} with the value and validity flag set
     */
    public PersonIdentifier validate(String ssnValue) {
        PersonIdentifier identifier = new PersonIdentifier(ssnValue, false);
        if (ssnValue == null || ssnValue.isBlank()) {
            return identifier;
        }
        identifier.setValid(isValid(ssnValue));
        return identifier;
    }

    /**
     * Returns {@code true} if the given SSN string is valid per US SSN rules.
     *
     * @param ssn the raw SSN string
     * @return {@code true} if valid, {@code false} otherwise
     */
    public boolean isValid(String ssn) {
        if (ssn == null || ssn.isBlank()) {
            return false;
        }

        if (!SSN_FORMAT_PATTERN.matcher(ssn).matches()) {
            return false;
        }

        // Normalize: remove dashes for rule checks
        String normalized = ssn.replace("-", "");

        String area = normalized.substring(0, 3);
        String group = normalized.substring(3, 5);
        String serial = normalized.substring(5, 9);

        // All zeros in any group
        if (area.equals("000") || group.equals("00") || serial.equals("0000")) {
            return false;
        }

        // Area 666 — never assigned
        if (area.equals("666")) {
            return false;
        }

        // Area 900–999 — reserved (ITINs)
        int areaNum = Integer.parseInt(area);
        if (areaNum >= 900 && areaNum <= 999) {
            return false;
        }

        // All same digit (e.g. 111-11-1111, 999-99-9999)
        if (normalized.chars().distinct().count() == 1) {
            return false;
        }

        // Known test/placeholder: 123-45-6789
        if (normalized.equals(KNOWN_PLACEHOLDER_NORMALIZED)) {
            return false;
        }

        return true;
    }
}
