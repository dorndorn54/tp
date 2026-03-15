package ui;

import exception.DuplicateSKUException;
import exception.EmptyListException;
import exception.InvalidCommandException;
import exception.InvalidIndexException;
import exception.ItemTaskerException;
import exception.MissingArgumentException;
import exception.SKUNotFoundException;
import exception.TaskStatusException;

import sku.Location;
import sku.SKU;
import sku.SKUList;

import skutask.Priority;
import skutask.SKUTask;
import skutask.SKUTaskList;
import skutask.ViewSKUTask;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Receives parsed commands from the user input and routes them to specific handler methods to perform the requested
 * operations, such as creating new SKUs, adding or modifying tasks, and generating sorted or filtered views of the
 * warehouse inventory. Additionally, it manages the running state of the application loop and currently maintains a
 * temporary mapping between SKU identifiers and their respective task lists during execution.
 *
 */
public class CommandRunner {

    /** Set false to stop the main loop. */
    private boolean isRunning;

    private final SKUList skuList;

    /** Internal task storage keyed by SKU_ID. */
    private final HashMap<String, SKUTaskList> taskMap;

    /**
     * Constructs a CommandRunner backed by the given SKU data store.
     *
     * @param skuList The shared SKU data store for the application.
     */
    public CommandRunner(SKUList skuList) {
        this.skuList = skuList;
        this.taskMap = new HashMap<>();
        this.isRunning = true;
    }

    /**
     * Returns the current running state of the application.
     *
     * @return True if the application should continue running, false otherwise.
     */
    public boolean isRunning() {
        return isRunning;
    }

    /**
     * Dispatches a parsed command to the appropriate specific handler based on its command word.
     *
     * @param cmd The parsed command object containing the command word and extracted arguments.
     * @throws ItemTaskerException If a domain-specific error occurs during the execution of the command.
     */
    public void run(ParsedCommand cmd) throws ItemTaskerException {
        assert cmd != null : "ParsedCommand should not be null";

        switch (cmd.getCommandWord()) {
        case "addsku":
            handleAddSku(cmd);
            break;
        case "deletesku":
            handleDeleteSku(cmd);
            break;
        case "addskutask":
            handleAddSkuTask(cmd);
            break;
        case "deletetask":
            handleDeleteTask(cmd);
            break;
        case "marktask":
            handleMarkTask(cmd);
            break;
        case "unmarktask":
            handleUnmarkTask(cmd);
            break;
        case "listtasks":
            handleListTasks(cmd);
            break;
        case "help":
            Ui.printHelp();
            break;
        case "bye":
        case "exit":
            Ui.printGoodbye();
            isRunning = false;
            break;
        case "":
            break;
        default:
            Ui.printUnknownCommand(cmd.getCommandWord());
        }
    }

    /**
     * Handles the addition of a new SKU to the warehouse.
     * Validates that the location is a legal 3x3 sector and that the SKU does not already exist.
     *
     * @param cmd The parsed command containing the SKU ID and location arguments.
     * @throws MissingArgumentException If the SKU ID or location arguments are missing.
     * @throws InvalidCommandException  If the provided location string is invalid.
     * @throws DuplicateSKUException    If an SKU with the same ID already exists in the warehouse.
     */
    private void handleAddSku(ParsedCommand cmd) throws SKUNotFoundException {
        String skuId = cmd.getArg("n");
        String locationStr = cmd.getArg("l");

        if (skuId == null || locationStr == null) {
            Ui.printError("Usage: addsku n/SKU_ID l/LOCATION  (e.g. addsku n/WIDGET-A1 l/B2)");
            return;
        }

        Location location;
        try {
            location = Location.valueOf(locationStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            Ui.printError("Invalid location '" + locationStr + "'. Must be one of: A1 A2 A3 B1 B2 B3 C1 C2 C3");
            return;
        }

        if (findSku(skuId) != null) {
            Ui.printError("SKU already exists: " + skuId);
            return;
        }

        skuList.addSKU(skuId, location);
        taskMap.put(skuId.toUpperCase(), new SKUTaskList());
        Ui.printSuccess("Added SKU [" + skuId.toUpperCase() + "] at location " + location);
    }

    /**
     * Handles the deletion of an existing SKU and all of its associated tasks.
     *
     * @param cmd The parsed command containing the SKU ID argument.
     * @throws MissingArgumentException If the SKU ID argument is missing.
     * @throws SKUNotFoundException     If the specified SKU does not exist in the warehouse.
     */
    private void handleDeleteSku(ParsedCommand cmd) throws MissingArgumentException, SKUNotFoundException {
        String skuId = cmd.getArg("n");
        if (skuId == null) {
            Ui.printError("Usage: deletesku n/SKU_ID");
            return;
        }
        if (findSku(skuId) == null) {
            Ui.printError("SKU not found: " + skuId);
            return;
        }
        skuList.deleteSKU(skuId);
        taskMap.remove(skuId.toUpperCase());
        Ui.printSuccess("Deleted SKU [" + skuId.toUpperCase() + "] and all its tasks.");
    }

    /**
     * Handles the addition of a new task to a specific SKU.
     * Priority defaults to HIGH if the priority flag is omitted.
     *
     * @param cmd The parsed command containing the SKU ID, due date, and optional priority arguments.
     * @throws MissingArgumentException If the SKU ID or due date arguments are missing.
     * @throws SKUNotFoundException     If the specified SKU does not exist in the warehouse.
     * @throws InvalidCommandException  If the provided priority value is invalid.
     */
    private void handleAddSkuTask(ParsedCommand cmd) throws MissingArgumentException, SKUNotFoundException {
        String skuId = cmd.getArg("n");
        String dueDate = cmd.getArg("d");

        if (skuId == null || dueDate == null) {
            Ui.printError("Usage: addskutask n/SKU_ID d/DUE_DATE [p/PRIORITY]");
            return;
        }
        if (findSku(skuId) == null) {
            Ui.printError("SKU not found: " + skuId
                    + ". Use 'addsku' to register it first.");
            return;
        }

        Priority priority = Priority.HIGH;
        if (cmd.hasArg("p")) {
            try {
                priority = Priority.valueOf(cmd.getArg("p").toUpperCase());
            } catch (IllegalArgumentException e) {
                Ui.printError("Invalid priority '" + cmd.getArg("p") + "'. Use HIGH, MEDIUM, or LOW.");
                return;
            }
        }

        SKUTaskList taskList = getOrCreateTaskList(skuId);
        taskList.addSKUTask(skuId.toUpperCase(), priority, dueDate);
        int newIndex = taskList.getSize();
        Ui.printSuccess("Added task #" + newIndex + " to SKU [" + skuId.toUpperCase()
                + "] | Priority: " + priority + " | Due: " + dueDate);
    }

    /**
     * Handles the deletion of a specific task from an SKU using its 1-based index.
     *
     * @param cmd The parsed command containing the SKU ID and task index arguments.
     * @throws MissingArgumentException If the SKU ID or index arguments are missing.
     * @throws InvalidIndexException    If the provided index format is invalid or out of range.
     */
    private void handleDeleteTask(ParsedCommand cmd) throws MissingArgumentException, InvalidIndexException {
        String skuId = cmd.getArg("n");
        String indexStr = cmd.getArg("i");

        if (skuId == null || indexStr == null) {
            Ui.printError("Usage: deletetask n/SKU_ID i/TASK_INDEX");
            return;
        }

        int index = parseIndex(indexStr);
        if (index == -1) {
            return;
        }

        SKUTaskList taskList = taskMap.get(skuId.toUpperCase());
        if (taskList == null || index < 1 || index > taskList.getSize()) {
            Ui.printError("Task index " + index + " is out of range for SKU: " + skuId);
            return;
        }

        taskList.getSKUTaskList().remove(index - 1);
        Ui.printSuccess("Deleted task #" + index + " from SKU [" + skuId.toUpperCase() + "].");
    }

    /**
     * Handles marking a specific task as completed using its 1-based index.
     *
     * @param cmd The parsed command containing the SKU ID and task index arguments.
     * @throws MissingArgumentException If the SKU ID or index arguments are missing.
     * @throws InvalidIndexException    If the provided index format is invalid or out of range.
     * @throws TaskStatusException      If the task is already marked as completed.
     */
    private void handleMarkTask(ParsedCommand cmd) throws MissingArgumentException, InvalidIndexException {
        String skuId = cmd.getArg("n");
        String indexStr = cmd.getArg("i");

        if (skuId == null || indexStr == null) {
            Ui.printError("Usage: marktask n/SKU_ID i/TASK_INDEX");
            return;
        }

        int index = parseIndex(indexStr);
        if (index == -1) {
            return;
        }

        SKUTaskList taskList = taskMap.get(skuId.toUpperCase());
        if (taskList == null || index < 1 || index > taskList.getSize()) {
            Ui.printError("Task index " + index + " is out of range for SKU: " + skuId);
            return;
        }

        taskList.markTask(index);
        Ui.printSuccess("Marked task #" + index + " as done for SKU [" + skuId.toUpperCase() + "].");
    }

    /**
     * Handles unmarking a previously completed task using its 1-based index.
     *
     * @param cmd The parsed command containing the SKU ID and task index arguments.
     * @throws MissingArgumentException If the SKU ID or index arguments are missing.
     * @throws InvalidIndexException    If the provided index format is invalid or out of range.
     * @throws TaskStatusException      If the task is already marked as incomplete.
     */
    private void handleUnmarkTask(ParsedCommand cmd) throws MissingArgumentException, InvalidIndexException {
        String skuId = cmd.getArg("n");
        String indexStr = cmd.getArg("i");

        if (skuId == null || indexStr == null) {
            Ui.printError("Usage: unmarktask n/SKU_ID i/TASK_INDEX");
            return;
        }

        int index = parseIndex(indexStr);
        if (index == -1) {
            return;
        }

        SKUTaskList taskList = taskMap.get(skuId.toUpperCase());
        if (taskList == null || index < 1 || index > taskList.getSize()) {
            Ui.printError("Task index " + index + " is out of range for SKU: " + skuId);
            return;
        }

        taskList.unmarkTask(index);
        Ui.printSuccess("Unmarked task #" + index + " for SKU [" + skuId.toUpperCase() + "].");
    }

    /**
     * Dispatches the list view generation to the appropriate sub-handler based on the provided flags.
     * If no flags are provided, it defaults to showing all tasks across all SKUs.
     *
     * @param cmd The parsed command containing the view filter arguments.
     * @throws InvalidCommandException  If the provided view filter arguments are invalid.
     * @throws EmptyListException       If the resulting view contains no tasks.
     * @throws SKUNotFoundException     If a requested SKU filter cannot be found.
     */
    private void handleListTasks(ParsedCommand cmd) throws InvalidCommandException, EmptyListException,
            SKUNotFoundException {
        if (cmd.hasArg("n")) {
            listTasksBySku(cmd.getArg("n"));
        } else if (cmd.hasArg("p")) {
            listTasksByPriority(cmd.getArg("p"));
        } else if (cmd.hasArg("l")) {
            listTasksByDistance(cmd.getArg("l"));
        } else {
            listAllTasks();
        }
    }

    /**
     * Prints all tasks associated with a specific SKU.
     *
     * @param skuId The ID of the SKU to look up.
     * @throws SKUNotFoundException If the specified SKU cannot be found.
     * @throws EmptyListException   If the specified SKU currently has no tasks.
     */
    private void listTasksBySku(String skuId) throws SKUNotFoundException, EmptyListException {
        SKUTaskList taskList = taskMap.get(skuId.toUpperCase());
        if (taskList == null || taskList.isEmpty()) {
            Ui.printInfo("No tasks found for SKU: " + skuId.toUpperCase());
            return;
        }
        System.out.println(" Tasks for SKU [" + skuId.toUpperCase() + "]:");
        Ui.printDivider();
        taskList.printSKUTaskList();
        Ui.printDivider();
    }

    /**
     * Prints all tasks across all SKUs that match the specified priority level.
     *
     * @param priorityStr The priority string to filter by (HIGH, MEDIUM, or LOW).
     * @throws InvalidCommandException If the priority format is invalid.
     * @throws EmptyListException      If no tasks in the warehouse match the filter.
     */
    private void listTasksByPriority(String priorityStr) throws InvalidCommandException, EmptyListException {
        Priority filter;
        try {
            filter = Priority.valueOf(priorityStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            Ui.printError("Invalid priority '" + priorityStr + "'. Use HIGH, MEDIUM, or LOW.");
            return;
        }

        System.out.println(" Tasks with priority [" + filter + "]:");
        Ui.printDivider();

        int count = 0;
        for (Map.Entry<String, SKUTaskList> entry : taskMap.entrySet()) {
            for (SKUTask task : entry.getValue().getSKUTaskList()) {
                if (task.getSKUTaskPriority() == filter) {
                    System.out.println("  [SKU: " + entry.getKey() + "] " + task);
                    count++;
                }
            }
        }

        if (count == 0) {
            Ui.printInfo("No tasks found with priority: " + filter);
        }
        Ui.printDivider();
    }

    /**
     * Prints all tasks across all SKUs sorted by their Manhattan distance from a specified warehouse sector.
     *
     * @param fromStr The starting location sector (e.g. "B2").
     * @throws InvalidCommandException If the location format is invalid.
     * @throws EmptyListException      If there are no tasks available to sort.
     */
    private void listTasksByDistance(String fromStr) throws SKUNotFoundException {
        Location from;
        try {
            from = Location.valueOf(fromStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            Ui.printError("Invalid location '" + fromStr + "'. Must be one of: A1 A2 A3 B1 B2 B3 C1 C2 C3");
            return;
        }

        List<TaskEntry> entries = new ArrayList<>();

        for (Map.Entry<String, SKUTaskList> entry : taskMap.entrySet()) {
            SKU sku = findSku(entry.getKey());
            if (sku == null) {
                continue;
            }
            int dist = manhattanDistance(from, sku.getSKULocation());
            for (SKUTask task : entry.getValue().getSKUTaskList()) {
                entries.add(new TaskEntry(entry.getKey(), task, dist));
            }
        }

        entries.sort(Comparator.comparingInt(e -> e.distance));

        System.out.println(" Tasks sorted by distance from [" + from + "]:");
        Ui.printDivider();

        if (entries.isEmpty()) {
            Ui.printInfo("No tasks found.");
        } else {
            for (TaskEntry e : entries) {
                System.out.printf("  [SKU: %-25s | dist=%-2d] %s%n",
                        e.skuId, e.distance, e.task);
            }
        }
        Ui.printDivider();
    }

    /**
     * Prints every task currently tracked in the system, grouped by their respective SKUs.
     *
     * @throws EmptyListException If the system currently tracks no tasks across any SKUs.
     */
    private void listAllTasks() throws EmptyListException {
        System.out.println(" All tasks:");
        Ui.printDivider();

        boolean anyTasks = false;
        for (Map.Entry<String, SKUTaskList> entry : taskMap.entrySet()) {
            if (!entry.getValue().isEmpty()) {
                System.out.println(" SKU [" + entry.getKey() + "]:");
                entry.getValue().printSKUTaskList();
                anyTasks = true;
            }
        }

        if (!anyTasks) {
            Ui.printInfo("No tasks in the system yet.");
        }
        Ui.printDivider();
    }

    /**
     * Looks up an SKU by its ID from the main SKU list. Comparison is case-insensitive.
     *
     * @param skuId The ID of the SKU to search for.
     * @return The matching {@link SKU}, or null if the SKU is not found.
     * @throws SKUNotFoundException If the SKU does not exist (Exception staged for future implementation).
     */
    private SKU findSku(String skuId) throws SKUNotFoundException {
        for (SKU sku : skuList.getSKUList()) {
            if (sku.getSKUID().equalsIgnoreCase(skuId)) {
                return sku;
            }
        }
        return null;
    }

    /**
     * Retrieves the task list associated with a given SKU ID, creating a new one if it does not exist.
     *
     * @param skuId The ID of the SKU acting as the key.
     * @return The existing or newly created {@link SKUTaskList} for the specified SKU.
     */
    private SKUTaskList getOrCreateTaskList(String skuId) {
        return taskMap.computeIfAbsent(skuId.toUpperCase(), k -> new SKUTaskList());
    }

    /**
     * Parses a string representation of a 1-based task index into an integer.
     * Prints an error to the UI if the parsing fails.
     *
     * @param indexStr The string representing the numeric index.
     * @return The parsed integer, or -1 if the parsing failed due to an invalid format.
     * @throws InvalidIndexException If the string format cannot be successfully parsed as an integer.
     */
    private int parseIndex(String indexStr) throws InvalidIndexException {
        try {
            return Integer.parseInt(indexStr.trim());
        } catch (NumberFormatException e) {
            Ui.printError("Task index must be a number, got: '" + indexStr + "'");
            return -1;
        }
    }

    /**
     * Computes the Manhattan distance between two locations on the 3x3 warehouse grid.
     * The grid uses letters (A, B, C) for rows and digits (1, 2, 3) for columns.
     *
     * @param a The starting location sector.
     * @param b The target location sector.
     * @return The calculated Manhattan distance as an integer.
     */
    private int manhattanDistance(Location a, Location b) {
        int rowA = a.name().charAt(0) - 'A';
        int colA = Character.getNumericValue(a.name().charAt(1)) - 1;
        int rowB = b.name().charAt(0) - 'A';
        int colB = Character.getNumericValue(b.name().charAt(1)) - 1;
        return Math.abs(rowA - rowB) + Math.abs(colA - colB);
    }
}