package exception;

/**
 * Represents an error regarding the completion status of an SKU task.
 */
public class TaskStatusException extends ItemTaskerException {

    /**
     * Constructs a TaskStatusException with a specific error message.
     *
     * @param message The error message (e.g., "This task is already marked as done.").
     */
    public TaskStatusException(String message) {
        super(message);
    }
}