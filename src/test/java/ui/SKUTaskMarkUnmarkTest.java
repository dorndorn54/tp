package ui;

import command.CommandRunner;
import command.ParsedCommand;
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
import static org.junit.jupiter.api.Assertions.assertTrue;

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
        args.put("n", skuId);
        args.put("i", index);
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
    public void marktask_alreadyMarked_showsError() throws ItemTaskerException, IOException {
        runner.run(buildCommand("marktask", "SKU-001", "1"));
        runner.run(buildCommand("marktask", "SKU-001", "1"));
        assertTrue(outputStream.toString().contains("[INFO]") && outputStream.toString().contains("already marked as done"));
    }

    @Test
    public void unmarktask_alreadyUnmarked_showsError() throws ItemTaskerException, IOException {
        runner.run(buildCommand("unmarktask", "SKU-001", "1"));
        assertTrue(outputStream.toString().contains("[INFO]") && outputStream.toString().contains("already unmarked"));
    }
}
