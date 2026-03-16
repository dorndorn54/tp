package exception;

/**
 * Represents an error when a provided date string does not conform to the expected format.
 */
public class InvalidDateException extends ItemTaskerException {

    /**
     * Constructs an InvalidDateException with the invalid date string.
     *
     * @param invalidDate The invalid date string provided by the user.
     */
    public InvalidDateException(String invalidDate) {
        super("Invalid date format: '" + invalidDate + "'. Please use YYYY-MM-DD format.");
    }
}
