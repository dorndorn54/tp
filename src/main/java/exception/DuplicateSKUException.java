package exception;

/**
 * Represents an error when attempting to add an SKU that already exists in the system.
 */
public class DuplicateSKUException extends ItemTaskerException {

    /**
     * Constructs a DuplicateSKUException with the conflicting SKU ID.
     *
     * @param skuId The ID of the SKU that caused the conflict.
     */
    public DuplicateSKUException(String skuId) {
        super("An SKU with the ID '" + skuId + "' already exists in the warehouse.");
    }
}
