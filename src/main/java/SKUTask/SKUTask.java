package SKUTask;

public class SKUTask {
    private String skuTaskID;
    // private String description;
    private Priority priority;
    private String dueDate;

    public SKUTask(String skuTaskID, Priority priority, String dueDate) {
        this.skuTaskID = skuTaskID;
        this.priority = priority;
        this.dueDate = dueDate;
    }

    public SKUTask(String skuTaskID, String dueDate) {
        this(skuTaskID, Priority.HIGH, dueDate); // default priority set to HIGH
    }

    public String getSKUTaskID() {
        return skuTaskID;
    }

    public Priority getSKUTaskPriority() {
        return priority;
    }

    public String getSKUTaskDueDate() {
        return dueDate;
    }

    @Override
    public String toString() {
        return "ID: " + skuTaskID + " | Priority: " + priority + " | Due: " + dueDate;
    }
}
