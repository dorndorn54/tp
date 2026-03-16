package ui;

import org.junit.jupiter.api.Test;
import skutask.Priority;
import skutask.SKUTaskList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SKUTaskListTest {

    @Test
    public void addSKUTask_withPriority_taskAddedCorrectly() {
        SKUTaskList taskList = new SKUTaskList();
        taskList.addSKUTask("SKU-100", Priority.HIGH, "2026-04-01");

        assertEquals(1, taskList.getSize());
        assertEquals("SKU-100", taskList.getSKUTaskList().get(0).getSKUTaskID());
        assertEquals(Priority.HIGH, taskList.getSKUTaskList().get(0).getSKUTaskPriority());
        assertEquals("2026-04-01", taskList.getSKUTaskList().get(0).getSKUTaskDueDate());
    }

    @Test
    public void addSKUTask_withoutPriority_defaultsToHigh() {
        SKUTaskList taskList = new SKUTaskList();
        taskList.addSKUTask("SKU-200", "2026-05-15");

        assertEquals(1, taskList.getSize());
        assertEquals(Priority.HIGH, taskList.getSKUTaskList().get(0).getSKUTaskPriority());
    }

    @Test
    public void addSKUTask_multipleAdds_allTasksPresent() {
        SKUTaskList taskList = new SKUTaskList();
        taskList.addSKUTask("SKU-A", Priority.LOW, "2026-06-01");
        taskList.addSKUTask("SKU-B", Priority.MEDIUM, "2026-07-01");
        taskList.addSKUTask("SKU-C", "2026-08-01");

        assertEquals(3, taskList.getSize());
    }

    @Test
    public void deleteSKUTask_existingTask_taskRemoved() {
        SKUTaskList taskList = new SKUTaskList();
        taskList.addSKUTask("SKU-DEL", Priority.MEDIUM, "2026-04-10");
        taskList.deleteSKUTask("SKU-DEL");

        assertEquals(0, taskList.getSize());
    }

    @Test
    public void deleteSKUTask_nonExistingTask_listUnchanged() {
        SKUTaskList taskList = new SKUTaskList();
        taskList.addSKUTask("SKU-KEEP", Priority.LOW, "2026-04-20");
        taskList.deleteSKUTask("SKU-NOTFOUND");

        assertEquals(1, taskList.getSize());
    }

    @Test
    public void deleteSKUTask_fromMultipleTasks_onlyTargetRemoved() {
        SKUTaskList taskList = new SKUTaskList();
        taskList.addSKUTask("SKU-1", Priority.HIGH, "2026-01-01");
        taskList.addSKUTask("SKU-2", Priority.MEDIUM, "2026-02-01");
        taskList.addSKUTask("SKU-3", Priority.LOW, "2026-03-01");

        taskList.deleteSKUTask("SKU-2");

        assertEquals(2, taskList.getSize());
        assertEquals("SKU-1", taskList.getSKUTaskList().get(0).getSKUTaskID());
        assertEquals("SKU-3", taskList.getSKUTaskList().get(1).getSKUTaskID());
    }

    @Test
    public void printSKUTaskList_withTasks_correctOutput() {
        SKUTaskList taskList = new SKUTaskList();
        taskList.addSKUTask("SKU-P1", Priority.HIGH, "2026-04-01");

        String expected = "[ ] ID: SKU-P1 | Priority: HIGH | Due: 2026-04-01";
        assertTrue(taskList.getSKUTaskList().get(0).toString().contains("SKU-P1"));
        assertEquals(expected, taskList.getSKUTaskList().get(0).toString());
    }

    @Test
    public void printSKUTaskList_emptyList_noTasks() {
        SKUTaskList taskList = new SKUTaskList();

        assertTrue(taskList.isEmpty());
        assertEquals(0, taskList.getSize());
    }
}
