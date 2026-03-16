package skutask;

/**
 * Represents a task associated with a Stock Keeping Unit (SKU).
 * Each task has an ID, a priority level, a due date, and a completion status.
 */
public class SKUTask {
    private String skuTaskID;
    private Priority priority;
    private String dueDate;
    private boolean isDone;

    /**
     * Constructs a new SKUTask with the specified ID, priority, and due date.
     *
     * @param skuTaskID The unique identifier for this task.
     * @param priority  The priority level of this task.
     * @param dueDate   The due date of this task.
     */
    public SKUTask(String skuTaskID, Priority priority, String dueDate) {
        this.skuTaskID = skuTaskID;
        this.priority = priority;
        this.dueDate = dueDate;
        this.isDone = false;
    }

    /**
     * Constructs a new SKUTask with the specified ID and due date.
     * Priority defaults to HIGH.
     *
     * @param skuTaskID The unique identifier for this task.
     * @param dueDate   The due date of this task.
     */
    public SKUTask(String skuTaskID, String dueDate) {
        this(skuTaskID, Priority.HIGH, dueDate);
    }

    /**
     * Returns the unique identifier of this task.
     *
     * @return The task ID.
     */
    public String getSKUTaskID() {
        return skuTaskID;
    }

    /**
     * Returns the priority level of this task.
     *
     * @return The task priority.
     */
    public Priority getSKUTaskPriority() {
        return priority;
    }

    /**
     * Returns the due date of this task.
     *
     * @return The due date string.
     */
    public String getSKUTaskDueDate() {
        return dueDate;
    }

    /**
     * Returns whether this task is marked as done.
     *
     * @return True if the task is completed, false otherwise.
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Marks this task as done.
     */
    public void mark() {
        this.isDone = true;
    }

    /**
     * Marks this task as not done.
     */
    public void unmark() {
        this.isDone = false;
    }

    /**
     * Returns a string representation of the task including its status, ID, priority, and due date.
     *
     * @return Formatted string of the task details.
     */
    @Override
    public String toString() {
        String status = isDone ? "[X]" : "[ ]";
        return status + " ID: " + skuTaskID + " | Priority: " + priority + " | Due: " + dueDate;
    }
}
