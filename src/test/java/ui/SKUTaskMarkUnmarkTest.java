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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

//@@author AkshayPranav19
public class SKUTaskMarkUnmarkTest {

    private CommandRunner runner;
    private SKUList skuList;
    private ByteArrayOutputStream outputStream;
    private PrintStream originalOut;

    @BeforeEach
    public void setUp() {
        skuList = new SKUList();
        skuList.addSKU("SKU-001", Location.A1);
        SKU sku = skuList.getSKUList().get(0);
        sku.getSKUTaskList().addSKUTask("SKU-001", Priority.HIGH, "2026-05-01", "test task");

        runner = new CommandRunner(skuList);

        originalOut = System.out;
        outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
    }

    @AfterEach
    public void tearDown() {
        System.setOut(originalOut);
    }

    private ParsedCommand buildCommand(String commandWord, String skuId, String index) {
        Map<String, String> args = new HashMap<>();
        if (skuId != null) {
            args.put("n", skuId);
        }
        if (index != null) {
            args.put("i", index);
        }
        return new ParsedCommand(commandWord, args);
    }

    @Test
    public void mark_task_isDone() {
        SKUTask task = new SKUTask("SKU001", "2025-12-31", "test task");
        task.mark();
        assertTrue(task.isDone());
    }

    @Test
    public void unmark_task_isNotDone() {
        SKUTask task = new SKUTask("SKU001", "2025-12-31", "test task");
        task.mark();
        task.unmark();
        assertFalse(task.isDone());
    }

    @Test
    public void marktask_alreadyMarked_showsInfo() throws ItemTaskerException, IOException {
        runner.run(buildCommand("marktask", "SKU-001", "1"));
        runner.run(buildCommand("marktask", "SKU-001", "1"));
        String output = outputStream.toString();
        assertTrue(output.contains("[INFO]") && output.contains("already marked as done"));
    }

    @Test
    public void unmarktask_alreadyUnmarked_showsInfo() throws ItemTaskerException, IOException {
        runner.run(buildCommand("unmarktask", "SKU-001", "1"));
        assertTrue(outputStream.toString().contains("[INFO]") && outputStream.toString().contains("already unmarked"));
    }

    @Test
    public void marktask_validTask_showsSuccessAndTaskIsDone() throws ItemTaskerException, IOException {
        runner.run(buildCommand("marktask", "SKU-001", "1"));
        assertTrue(outputStream.toString().contains("[OK]"));
        assertTrue(skuList.getSKUList().get(0).getSKUTaskList().getSKUTaskList().get(0).isDone());
    }

    @Test
    public void unmarktask_afterMark_showsSuccessAndTaskIsNotDone() throws ItemTaskerException, IOException {
        runner.run(buildCommand("marktask", "SKU-001", "1"));
        outputStream.reset();
        runner.run(buildCommand("unmarktask", "SKU-001", "1"));
        assertTrue(outputStream.toString().contains("[OK]"));
        assertFalse(skuList.getSKUList().get(0).getSKUTaskList().getSKUTaskList().get(0).isDone());
    }

    @Test
    public void marktask_missingSkuId_showsError() throws ItemTaskerException, IOException {
        runner.run(buildCommand("marktask", null, "1"));
        assertTrue(outputStream.toString().contains("[ERROR]"));
    }

    @Test
    public void marktask_missingIndex_showsError() throws ItemTaskerException, IOException {
        runner.run(buildCommand("marktask", "SKU-001", null));
        assertTrue(outputStream.toString().contains("[ERROR]"));
    }

    @Test
    public void unmarktask_missingSkuId_showsError() throws ItemTaskerException, IOException {
        runner.run(buildCommand("unmarktask", null, "1"));
        assertTrue(outputStream.toString().contains("[ERROR]"));
    }

    @Test
    public void unmarktask_missingIndex_showsError() throws ItemTaskerException, IOException {
        runner.run(buildCommand("unmarktask", "SKU-001", null));
        assertTrue(outputStream.toString().contains("[ERROR]"));
    }

    @Test
    public void marktask_skuNotFound_showsError() throws ItemTaskerException, IOException {
        runner.run(buildCommand("marktask", "GHOST-SKU", "1"));
        assertTrue(outputStream.toString().contains("[ERROR]"));
    }

    @Test
    public void unmarktask_skuNotFound_showsError() throws ItemTaskerException, IOException {
        runner.run(buildCommand("unmarktask", "GHOST-SKU", "1"));
        assertTrue(outputStream.toString().contains("[ERROR]"));
    }

    @Test
    public void marktask_nonNumericIndex_throwsInvalidIndexException() {
        assertThrows(InvalidIndexException.class, () -> runner.run(buildCommand("marktask", "SKU-001", "abc")));
    }

    @Test
    public void unmarktask_nonNumericIndex_throwsInvalidIndexException() {
        assertThrows(InvalidIndexException.class, () -> runner.run(buildCommand("unmarktask", "SKU-001", "abc")));
    }

    @Test
    public void marktask_thenUnmark_taskIsNotDone() throws ItemTaskerException, IOException {
        runner.run(buildCommand("marktask", "SKU-001", "1"));
        runner.run(buildCommand("unmarktask", "SKU-001", "1"));
        assertFalse(skuList.getSKUList().get(0).getSKUTaskList().getSKUTaskList().get(0).isDone());
    }

    @Test
    public void marktask_indexOutOfBounds_throwsInvalidIndexException() {
        assertThrows(InvalidIndexException.class, () -> runner.run(buildCommand("marktask", "SKU-001", "5")));
    }

    @Test
    public void unmarktask_indexOutOfBounds_throwsInvalidIndexException() {
        assertThrows(InvalidIndexException.class, () -> runner.run(buildCommand("unmarktask", "SKU-001", "5")));
    }

    @Test
    public void marktask_negativeIndex_throwsInvalidIndexException() {
        assertThrows(InvalidIndexException.class, () -> runner.run(buildCommand("marktask", "SKU-001", "-1")));
    }

    @Test
    public void unmarktask_negativeIndex_throwsInvalidIndexException() {
        assertThrows(InvalidIndexException.class, () -> runner.run(buildCommand("unmarktask", "SKU-001", "-1")));
    }

    @Test
    public void marktask_zeroIndex_throwsInvalidIndexException() {
        assertThrows(InvalidIndexException.class, () -> runner.run(buildCommand("marktask", "SKU-001", "0")));
    }

    @Test
    public void unmarktask_zeroIndex_throwsInvalidIndexException() {
        assertThrows(InvalidIndexException.class, () -> runner.run(buildCommand("unmarktask", "SKU-001", "0")));
    }

}
