package exception;

/**
 * Represents an error that occurs during file read/write operations or data corruption.
 */
public class StorageException extends ItemTaskerException {

    /**
     * Constructs a StorageException with the specified detail message.
     *
     * @param message The detail message explaining the file or parsing error.
     */
    public StorageException(String message) {
        super(message);
    }

    /**
     * Constructs a StorageException with the specified detail message and cause.
     *
     * @param message The detail message explaining the file or parsing error.
     * @param cause The underlying cause of the exception.
     */
    public StorageException(String message, Throwable cause) {
        super(message, cause);
    }
}