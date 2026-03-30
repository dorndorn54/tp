package exception;

/**
 * Represents an error when a provided index is either out of bounds or not a
 * valid integer.
 */
public class InvalidIndexException extends ItemTaskerException {

    /**
     * Constructs an InvalidIndexException when a specific task index is out of
     * range for a particular SKU.
     *
     * @param invalidIndex The index that was out of bounds.
     * @param skuId        The SKU ID associated with the task list.
     */
    public InvalidIndexException(int invalidIndex, String skuId) {
        super("Task index " + invalidIndex + " is out of range for SKU: " + skuId);
    }

    /**
     * Constructs an InvalidIndexException when a provided index string is not a valid integer.
     *
     * @param invalidIndexStr The string that could not be parsed into a valid index.
     */
    public InvalidIndexException(String invalidIndexStr) {
        super("Invalid task index provided: " + invalidIndexStr + ". Please provide a valid positive integer.");
    }
}
