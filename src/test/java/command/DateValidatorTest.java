package command;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

//@@author AkshayPranav19
public class DateValidatorTest {

    private ByteArrayOutputStream outputStream;
    private PrintStream originalOut;

    @BeforeEach
    public void setUp() {
        originalOut = System.out;
        outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
    }

    @AfterEach
    public void tearDown() {
        System.setOut(originalOut);
    }

    @Test
    public void isValidDate_validFormat_returnsTrue() {
        assertTrue(DateValidator.isValidDate("2026-03-29"));
        assertTrue(DateValidator.isValidDate("2026-12-31"));
        assertTrue(DateValidator.isValidDate("2026-01-01"));
    }

    @Test
    public void isValidDate_invalidFormat_returnsFalse() {
        assertFalse(DateValidator.isValidDate("29-03-2026"));
        assertFalse(DateValidator.isValidDate("2026/03/29"));
        assertFalse(DateValidator.isValidDate("March 29 2026"));
        assertFalse(DateValidator.isValidDate("20260329"));
    }

    @Test
    public void isValidDate_impossibleDate_returnsFalse() {
        assertFalse(DateValidator.isValidDate("2026-02-30"));
        assertFalse(DateValidator.isValidDate("2026-13-01"));
        assertFalse(DateValidator.isValidDate("2026-04-31"));
    }

    @Test
    public void isValidDate_nullInput_returnsFalse() {
        assertFalse(DateValidator.isValidDate(null));
    }

    @Test
    public void isValidDate_emptyString_returnsFalse() {
        assertFalse(DateValidator.isValidDate(""));
        assertFalse(DateValidator.isValidDate("   "));
    }

    @Test
    public void validateDateOrError_validDate_returnsDate() {
        String result = DateValidator.validateDateOrError("2026-06-15");
        assertNotNull(result);
        assertEquals("2026-06-15", result);
    }

    @Test
    public void validateDateOrError_invalidFormat_returnsNullAndPrintsError() {
        String result = DateValidator.validateDateOrError("not-a-date");
        assertNull(result);
        assertTrue(outputStream.toString().contains("[ERROR]"));
    }

    @Test
    public void validateDateOrError_impossibleDate_returnsNullAndPrintsError() {
        String result = DateValidator.validateDateOrError("2026-02-30");
        assertNull(result);
        assertTrue(outputStream.toString().contains("[ERROR]"));
    }

    @Test
    public void validateDateOrError_yearZero_rejectedAsBelowRange() {
        String result = DateValidator.validateDateOrError("0000-01-01");
        assertNull(result);
        String output = outputStream.toString();
        assertTrue(output.contains("[ERROR]"));
        assertTrue(output.contains("1970"));
    }

    @Test
    public void validateDateOrError_year1969_rejectedBelowRange() {
        String result = DateValidator.validateDateOrError("1969-12-31");
        assertNull(result);
        String output = outputStream.toString();
        assertTrue(output.contains("[ERROR]"));
        assertTrue(output.contains("1970"));
    }

    @Test
    public void validateDateOrError_year2101_rejectedAboveRange() {
        String result = DateValidator.validateDateOrError("2101-01-01");
        assertNull(result);
        String output = outputStream.toString();
        assertTrue(output.contains("[ERROR]"));
        assertTrue(output.contains("2100"));
    }

    @Test
    public void validateDateOrError_year9999_rejectedAboveRange() {
        String result = DateValidator.validateDateOrError("9999-12-31");
        assertNull(result);
        assertTrue(outputStream.toString().contains("[ERROR]"));
    }

    @Test
    public void validateDateOrError_boundaryYear1970_accepted() {
        String result = DateValidator.validateDateOrError("1970-01-01");
        assertNotNull(result);
        assertEquals("1970-01-01", result);
        String output = outputStream.toString();
        assertFalse(output.contains("[ERROR]"));
        assertTrue(output.contains("[WARNING]"));
    }

    @Test
    public void validateDateOrError_boundaryYear2100_accepted() {
        String result = DateValidator.validateDateOrError("2100-12-31");
        assertNotNull(result);
        assertEquals("2100-12-31", result);
        String output = outputStream.toString();
        assertFalse(output.contains("[ERROR]"));
        assertFalse(output.contains("[WARNING]"));
    }

    @Test
    public void validateDateOrError_pastDate_acceptedWithWarning() {
        String yesterday = java.time.LocalDate.now().minusDays(1).toString();
        String result = DateValidator.validateDateOrError(yesterday);
        assertNotNull(result);
        assertEquals(yesterday, result);
        String output = outputStream.toString();
        assertFalse(output.contains("[ERROR]"));
        assertTrue(output.contains("[WARNING]"));
    }

    @Test
    public void validateDateOrError_today_acceptedWithoutWarning() {
        String today = java.time.LocalDate.now().toString();
        String result = DateValidator.validateDateOrError(today);
        assertNotNull(result);
        assertEquals(today, result);
        assertFalse(outputStream.toString().contains("[WARNING]"));
    }

    @Test
    public void validateDateOrError_futureDate_acceptedWithoutWarning() {
        String nextMonth = java.time.LocalDate.now().plusMonths(1).toString();
        String result = DateValidator.validateDateOrError(nextMonth);
        assertNotNull(result);
        assertEquals(nextMonth, result);
        String output = outputStream.toString();
        assertFalse(output.contains("[ERROR]"));
        assertFalse(output.contains("[WARNING]"));
    }
}
