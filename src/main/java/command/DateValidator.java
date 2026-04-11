package command;

import ui.Ui;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * Validates date strings in YYYY-MM-DD format.
 * Rejects invalid formats, impossible calendar dates, and null/empty input.
 *
 * <p>Follows the same validate-and-return pattern as
 * {@link CommandHelper#parseLocation(String)} — returns null on failure
 * and prints an error via {@link Ui#printError(String)}.</p>
 */
//@@author AkshayPranav19
public class DateValidator {
    private static final Logger LOGGER = Logger.getLogger(DateValidator.class.getName());
    private static final Pattern DATE_PATTERN = Pattern.compile("^\\d{4}-\\d{2}-\\d{2}$");
    private static final int MIN_YEAR = 1970;
    private static final int MAX_YEAR = 2100;

    /**
     * Checks whether the given string is a valid date in YYYY-MM-DD format.
     *
     * @param dateStr The date string to validate.
     * @return True if the string is a valid calendar date, false otherwise.
     */
    public static boolean isValidDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return false;
        }
        if (!DATE_PATTERN.matcher(dateStr.trim()).matches()) {
            return false;
        }
        try {
            LocalDate.parse(dateStr.trim());
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    /**
     * Validates a date string and prints an error if invalid.
     * Returns the trimmed date on success, or null on failure.
     * Only years between 1970 and 2100 are accepted; dates before the current
     * system date are accepted but trigger a non-blocking warning.
     *
     * @param dateStr The raw date string from user input.
     * @return The validated and trimmed date string, or null if invalid.
     */
    public static String validateDateOrError(String dateStr) {
        assert dateStr != null : "Date string should not be null at this point";
        assert MIN_YEAR <= MAX_YEAR : "Year range invariant violated: MIN_YEAR must be <= MAX_YEAR";

        String trimmed = dateStr.trim();

        if (!DATE_PATTERN.matcher(trimmed).matches()) {
            LOGGER.log(Level.WARNING, "Date format rejected: " + dateStr);
            Ui.printError("Invalid date format '" + dateStr + "'. Use YYYY-MM-DD (e.g. 2026-03-29).");
            return null;
        }

        LocalDate parsed;
        try {
            parsed = LocalDate.parse(trimmed);
        } catch (DateTimeParseException e) {
            LOGGER.log(Level.WARNING, "Impossible calendar date: " + dateStr, e);
            Ui.printError("Invalid date '" + dateStr + "'. Date does not exist on the calendar.");
            return null;
        }
        assert parsed != null : "LocalDate.parse returned null after successful parse";

        int year = parsed.getYear();
        if (year < MIN_YEAR || year > MAX_YEAR) {
            LOGGER.log(Level.WARNING, "Date year " + year + " outside accepted range ["
                    + MIN_YEAR + ", " + MAX_YEAR + "]: " + dateStr);
            Ui.printError("Invalid year '" + year + "' in date '" + dateStr
                    + "'. Year must be between " + MIN_YEAR + " and " + MAX_YEAR + ".");
            return null;
        }

        LocalDate today = LocalDate.now();
        assert today != null : "LocalDate.now() must never return null";
        if (parsed.isBefore(today)) {
            LOGGER.log(Level.INFO, "Past due date accepted with warning: " + trimmed);
            Ui.printWarning("Due date '" + trimmed + "' is in the past (today is "
                    + today + "). Task will still be added.");
        }

        LOGGER.log(Level.FINE, "Date validated successfully: " + trimmed);
        return trimmed;
    }
}
