package exception;

/**
 * Represents an error when a provided index is either out of bounds or not a
 * valid integer.
 */
public class InvalidIndexException extends ItemTaskerException {

    /**
     * Constructs an InvalidIndexException for a non-numeric format error.
     *
     * @param invalidInput The string input that failed to parse as an integer.
     */
    public InvalidIndexException(String invalidInput) {
        super("Invalid index format: '" + invalidInput + "'. Please use a positive number.");
    }

    /**
     * Constructs an InvalidIndexException for an out-of-bounds error.
     *
     * @param max    The maximum valid index allowed.
     * @param isTask True if the index refers to a task, false if it refers to an
     *               SKU.
     */
    public InvalidIndexException(int max, boolean isTask) {
        super(max == 0
                ? "Index out of range. There are no " + (isTask ? "tasks" : "SKUs") + " available."
                : "Index out of range. Please use a number between 1 and " + max + ".");
    }

    /**
     * Constructs an InvalidIndexException when a specific task index is out of
     * range
     * for a particular SKU.
     *
     * @param invalidIndex The index that was out of bounds.
     * @param skuId        The SKU ID associated with the task list.
     */
    public InvalidIndexException(int invalidIndex, String skuId) {
        super("Task index " + invalidIndex + " is out of range for SKU: " + skuId);
    }
}
