package ui;

import command.CommandRunner;
import command.ParsedCommand;
import exception.InvalidIndexException;
import exception.ItemTaskerException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sku.Location;
import sku.SKU;
import sku.SKUList;
import skutask.Priority;
import skutask.SKUTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

//@@author AkshayPranav19
public class EditTaskCommandTest {

    private CommandRunner runner;
    private SKUList skuList;
    private SKU sku;
    private ByteArrayOutputStream outputStream;
    private PrintStream originalOut;

    @BeforeEach
    public void setUp() {
        skuList = new SKUList();
        skuList.addSKU("PALLET-A", Location.A1);
        sku = skuList.getSKUList().get(0);
        sku.getSKUTaskList().addSKUTask("PALLET-A", Priority.HIGH, "2026-04-01", "original desc");

        runner = new CommandRunner(skuList);

        originalOut = System.out;
        outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
    }

    @AfterEach
    public void tearDown() {
        System.setOut(originalOut);
    }

    private ParsedCommand buildEditTask(String skuId, String index, String date,
            String priority, String desc) {
        Map<String, String> args = new HashMap<>();
        if (skuId != null) {
            args.put("n", skuId);
        }
        if (index != null) {
            args.put("i", index);
        }
        if (date != null) {
            args.put("d", date);
        }
        if (priority != null) {
            args.put("p", priority);
        }
        if (desc != null) {
            args.put("t", desc);
        }
        return new ParsedCommand("edittask", args);
    }

    @Test
    public void edittask_updateDueDate_dateChanged() throws ItemTaskerException, IOException {
        runner.run(buildEditTask("PALLET-A", "1", "2026-12-31", null, null));
        SKUTask task = sku.getSKUTaskList().getSKUTaskList().get(0);
        assertEquals("2026-12-31", task.getSKUTaskDueDate());
    }

    @Test
    public void edittask_updatePriority_priorityChanged() throws ItemTaskerException, IOException {
        runner.run(buildEditTask("PALLET-A", "1", null, "LOW", null));
        SKUTask task = sku.getSKUTaskList().getSKUTaskList().get(0);
        assertEquals(Priority.LOW, task.getSKUTaskPriority());
    }

    @Test
    public void edittask_updateDescription_descriptionChanged() throws ItemTaskerException, IOException {
        runner.run(buildEditTask("PALLET-A", "1", null, null, "new desc"));
        SKUTask task = sku.getSKUTaskList().getSKUTaskList().get(0);
        assertEquals("new desc", task.getSKUTaskDescription());
    }

    @Test
    public void edittask_updateAllFields_allChanged() throws ItemTaskerException, IOException {
        runner.run(buildEditTask("PALLET-A", "1", "2027-01-01", "MEDIUM", "updated"));
        SKUTask task = sku.getSKUTaskList().getSKUTaskList().get(0);
        assertEquals("2027-01-01", task.getSKUTaskDueDate());
        assertEquals(Priority.MEDIUM, task.getSKUTaskPriority());
        assertEquals("updated", task.getSKUTaskDescription());
    }

    @Test
    public void edittask_noFieldsProvided_showsError() throws ItemTaskerException, IOException {
        runner.run(buildEditTask("PALLET-A", "1", null, null, null));
        assertTrue(outputStream.toString().contains("[ERROR]"));
    }

    @Test
    public void edittask_invalidPriority_showsError() throws ItemTaskerException, IOException {
        runner.run(buildEditTask("PALLET-A", "1", null, "URGENT", null));
        assertTrue(outputStream.toString().contains("[ERROR]"));
    }

    @Test
    public void edittask_skuNotFound_showsError() throws ItemTaskerException, IOException {
        runner.run(buildEditTask("GHOST-SKU", "1", "2026-12-31", null, null));
        assertTrue(outputStream.toString().contains("[ERROR]"));
    }

    @Test
    public void edittask_successMessage_shown() throws ItemTaskerException, IOException {
        runner.run(buildEditTask("PALLET-A", "1", "2026-12-31", null, null));
        assertTrue(outputStream.toString().contains("[OK]"));
    }

    @Test
    public void edittask_missingSkuId_showsError() throws ItemTaskerException, IOException {
        runner.run(buildEditTask(null, "1", "2027-01-01", null, null));
        assertTrue(outputStream.toString().contains("[ERROR]"));
    }

    @Test
    public void edittask_missingIndex_showsError() throws ItemTaskerException, IOException {
        runner.run(buildEditTask("PALLET-A", null, "2027-01-01", null, null));
        assertTrue(outputStream.toString().contains("[ERROR]"));
    }

    @Test
    public void edittask_outOfRangeIndex_throwsInvalidIndexException() {
        assertThrows(InvalidIndexException.class, () -> runner.run(buildEditTask("PALLET-A", "99", null, "LOW", null)));
    }

    @Test
    public void edittask_nonNumericIndex_throwsInvalidIndexException() {
        assertThrows(InvalidIndexException.class,
                () -> runner.run(buildEditTask("PALLET-A", "abc", "2027-01-01", null, null)));
    }

    @Test
    public void edittask_updateDateOnly_priorityAndDescUnchanged() throws ItemTaskerException, IOException {
        runner.run(buildEditTask("PALLET-A", "1", "2027-03-15", null, null));
        SKUTask task = sku.getSKUTaskList().getSKUTaskList().get(0);
        assertEquals("2027-03-15", task.getSKUTaskDueDate());
        assertEquals(Priority.HIGH, task.getSKUTaskPriority());
        assertEquals("original desc", task.getSKUTaskDescription());
    }

    @Test
    public void edittask_updatePriorityOnly_dateAndDescUnchanged() throws ItemTaskerException, IOException {
        runner.run(buildEditTask("PALLET-A", "1", null, "LOW", null));
        SKUTask task = sku.getSKUTaskList().getSKUTaskList().get(0);
        assertEquals("2026-04-01", task.getSKUTaskDueDate());
        assertEquals(Priority.LOW, task.getSKUTaskPriority());
        assertEquals("original desc", task.getSKUTaskDescription());
    }

    @Test
    public void edittask_editSecondOfThreeTasks_onlySecondChanges() throws ItemTaskerException, IOException {
        sku.getSKUTaskList().addSKUTask("PALLET-A", Priority.MEDIUM, "2026-05-01", "second task");
        sku.getSKUTaskList().addSKUTask("PALLET-A", Priority.LOW, "2026-06-01", "third task");
        runner.run(buildEditTask("PALLET-A", "2", null, "HIGH", null));
        assertEquals(Priority.HIGH, sku.getSKUTaskList().getSKUTaskList().get(1).getSKUTaskPriority());
        assertEquals(Priority.HIGH, sku.getSKUTaskList().getSKUTaskList().get(0).getSKUTaskPriority());
        assertEquals(Priority.LOW, sku.getSKUTaskList().getSKUTaskList().get(2).getSKUTaskPriority());
    }

    @Test
    public void edittask_negativeIndex_throwsInvalidIndexException() {
        assertThrows(InvalidIndexException.class,
                () -> runner.run(buildEditTask("PALLET-A", "-1", "2027-01-01", null, null)));
    }

    @Test
    public void edittask_zeroIndex_throwsInvalidIndexException() {
        assertThrows(InvalidIndexException.class,
                () -> runner.run(buildEditTask("PALLET-A", "0", "2027-01-01", null, null)));
    }
}
