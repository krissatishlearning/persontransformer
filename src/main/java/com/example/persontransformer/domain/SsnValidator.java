package com.example.persontransformer.domain;

import java.util.regex.Pattern;

/**
 * Validates a Social Security Number (SSN) and returns a {@link PersonIdentifier}
 * with {@code isValid} set according to the known rules.
 *
 * <p>Accepted formats:
 * <ul>
 *   <li>{@code XXX-XX-XXXX} (dashes)</li>
 *   <li>{@code XXXXXXXXX} (9 digits, no dashes)</li>
 * </ul>
 *
 * <p>Known invalid values:
 * <ul>
 *   <li>Any group that is all zeros: area 000, group 00, serial 0000</li>
 *   <li>Area number 666</li>
 *   <li>Area numbers 900–999 (reserved / ITIN range)</li>
 *   <li>All same digit (e.g. 111-11-1111, 999-99-9999)</li>
 *   <li>Known placeholder 123-45-6789</li>
 * </ul>
 */
public class SsnValidator {

    /** Matches {@code XXX-XX-XXXX} or {@code XXXXXXXXX}. */
    private static final Pattern FORMAT_PATTERN =
            Pattern.compile("^\\d{3}-?\\d{2}-?\\d{4}$");

    /** Matches a string where every character is the same digit (length 9). */
    private static final Pattern ALL_SAME_DIGIT_PATTERN =
            Pattern.compile("^(\\d)\\1{8}$");

    private SsnValidator() {
        // utility class
    }

    /**
     * Validates the given raw SSN string and returns a {@link PersonIdentifier}
     * whose {@code value} is the supplied string and {@code isValid} reflects
     * whether the SSN passes all validation rules.
     *
     * @param rawSsn the raw SSN string (may be {@code null})
     * @return a {@link PersonIdentifier} with the result of validation
     */
    public static PersonIdentifier validate(String rawSsn) {
        PersonIdentifier identifier = new PersonIdentifier(rawSsn, false);

        if (rawSsn == null || rawSsn.isBlank()) {
            return identifier;
        }

        if (!FORMAT_PATTERN.matcher(rawSsn).matches()) {
            return identifier;
        }

        // Normalise to 9 digits for semantic checks
        String digits = rawSsn.replace("-", "");

        String area   = digits.substring(0, 3); // first 3
        String group  = digits.substring(3, 5); // middle 2
        String serial = digits.substring(5, 9); // last 4

        // All zeros in any group
        if (area.equals("000") || group.equals("00") || serial.equals("0000")) {
            return identifier;
        }

        // Area 666
        if (area.equals("666")) {
            return identifier;
        }

        // Area 900–999
        int areaNum = Integer.parseInt(area);
        if (areaNum >= 900) {
            return identifier;
        }

        // All same digit (check on the 9-digit string)
        if (ALL_SAME_DIGIT_PATTERN.matcher(digits).matches()) {
            return identifier;
        }

        // Known placeholder
        if (digits.equals("123456789")) {
            return identifier;
        }

        identifier.setValid(true);
        return identifier;
    }
}
