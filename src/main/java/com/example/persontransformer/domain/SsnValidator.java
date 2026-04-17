package com.example.persontransformer.domain;

import java.util.regex.Pattern;

/**
 * Validates a Social Security Number (SSN) string and returns a {@link PersonIdentifier}
 * with the value set and {@code isValid} reflecting whether the SSN passes all rules.
 *
 * <p>Validation rules:
 * <ul>
 *   <li>Format: {@code XXX-XX-XXXX} (with dashes) or {@code XXXXXXXXX} (9 digits, no dashes)</li>
 *   <li>Area (first 3 digits) must not be {@code 000}</li>
 *   <li>Area must not be {@code 666}</li>
 *   <li>Area must not be in the range {@code 900–999} (reserved for ITINs)</li>
 *   <li>Group (digits 4–5) must not be {@code 00}</li>
 *   <li>Serial (last 4 digits) must not be {@code 0000}</li>
 *   <li>All nine digits must not be the same (e.g. {@code 111-11-1111})</li>
 *   <li>Known test/placeholder {@code 123-45-6789} (normalized) is invalid</li>
 * </ul>
 */
public class SsnValidator {

    private static final Pattern SSN_FORMAT = Pattern.compile("^\\d{3}-?\\d{2}-?\\d{4}$");

    private SsnValidator() {
        // utility class
    }

    /**
     * Validates the given SSN string and returns a {@link PersonIdentifier} whose
     * {@code isValid} flag reflects whether the SSN is valid.
     *
     * @param ssn the raw SSN string (may be {@code null})
     * @return a {@link PersonIdentifier} with {@code isValid} set appropriately
     */
    public static PersonIdentifier validate(String ssn) {
        if (ssn == null) {
            return new PersonIdentifier(null, false);
        }

        String trimmed = ssn.trim();

        if (!SSN_FORMAT.matcher(trimmed).matches()) {
            return new PersonIdentifier(trimmed, false);
        }

        // Normalize to digits-only for rule checks
        String digits = trimmed.replace("-", "");

        String area   = digits.substring(0, 3);
        String group  = digits.substring(3, 5);
        String serial = digits.substring(5, 9);

        // Rule: area 000 is invalid
        if (area.equals("000")) {
            return new PersonIdentifier(trimmed, false);
        }

        // Rule: area 666 is invalid
        if (area.equals("666")) {
            return new PersonIdentifier(trimmed, false);
        }

        // Rule: area 900-999 reserved (ITINs)
        int areaInt = Integer.parseInt(area);
        if (areaInt >= 900) {
            return new PersonIdentifier(trimmed, false);
        }

        // Rule: group 00 is invalid
        if (group.equals("00")) {
            return new PersonIdentifier(trimmed, false);
        }

        // Rule: serial 0000 is invalid
        if (serial.equals("0000")) {
            return new PersonIdentifier(trimmed, false);
        }

        // Rule: all same digit (e.g. 111111111, 999999999)
        if (digits.chars().distinct().count() == 1) {
            return new PersonIdentifier(trimmed, false);
        }

        // Rule: known test/placeholder 123-45-6789
        if (digits.equals("123456789")) {
            return new PersonIdentifier(trimmed, false);
        }

        return new PersonIdentifier(trimmed, true);
    }
}
