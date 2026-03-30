package ui;

import org.junit.jupiter.api.Test;
import skutask.Priority;
import skutask.SKUTask;
import skutask.SKUTaskList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

//@@author heehaw1234
public class SKUTaskListTest {

    @Test
    public void addSKUTask_withPriority_taskAddedCorrectly() {
        SKUTaskList taskList = new SKUTaskList();
        taskList.addSKUTask("SKU-100", Priority.HIGH, "2026-04-01", "test task");

        assertEquals(1, taskList.getSize());
        assertEquals("SKU-100", taskList.getSKUTaskList().get(0).getSKUTaskID());
        assertEquals(Priority.HIGH, taskList.getSKUTaskList().get(0).getSKUTaskPriority());
        assertEquals("2026-04-01", taskList.getSKUTaskList().get(0).getSKUTaskDueDate());
    }

    @Test
    public void addSKUTask_withoutPriority_defaultsToHigh() {
        SKUTaskList taskList = new SKUTaskList();
        taskList.addSKUTask("SKU-200", "2026-05-15", "default priority task");

        assertEquals(1, taskList.getSize());
        assertEquals(Priority.HIGH, taskList.getSKUTaskList().get(0).getSKUTaskPriority());
    }

    @Test
    public void addSKUTask_multipleAdds_allTasksPresent() {
        SKUTaskList taskList = new SKUTaskList();
        taskList.addSKUTask("SKU-A", Priority.LOW, "2026-06-01", "task A");
        taskList.addSKUTask("SKU-B", Priority.MEDIUM, "2026-07-01", "task B");
        taskList.addSKUTask("SKU-C", "2026-08-01", "task C");

        assertEquals(3, taskList.getSize());
    }

    @Test
    public void deleteSKUTask_existingTask_taskRemoved() {
        SKUTaskList taskList = new SKUTaskList();
        taskList.addSKUTask("SKU-DEL", Priority.MEDIUM, "2026-04-10", "to delete");
        taskList.deleteSKUTask("SKU-DEL");

        assertEquals(0, taskList.getSize());
    }

    @Test
    public void deleteSKUTask_nonExistingTask_listUnchanged() {
        SKUTaskList taskList = new SKUTaskList();
        taskList.addSKUTask("SKU-KEEP", Priority.LOW, "2026-04-20", "keep me");
        taskList.deleteSKUTask("SKU-NOTFOUND");

        assertEquals(1, taskList.getSize());
    }

    @Test
    public void deleteSKUTask_fromMultipleTasks_onlyTargetRemoved() {
        SKUTaskList taskList = new SKUTaskList();
        taskList.addSKUTask("SKU-1", Priority.HIGH, "2026-01-01", "first");
        taskList.addSKUTask("SKU-2", Priority.MEDIUM, "2026-02-01", "second");
        taskList.addSKUTask("SKU-3", Priority.LOW, "2026-03-01", "third");

        taskList.deleteSKUTask("SKU-2");

        assertEquals(2, taskList.getSize());
        assertEquals("SKU-1", taskList.getSKUTaskList().get(0).getSKUTaskID());
        assertEquals("SKU-3", taskList.getSKUTaskList().get(1).getSKUTaskID());
    }

    @Test
    public void printSKUTaskList_withTasks_correctOutput() {
        SKUTaskList taskList = new SKUTaskList();
        taskList.addSKUTask("SKU-P1", Priority.HIGH, "2026-04-01", "print test");

        String expected = "[ ] ID: SKU-P1 | Priority: HIGH | Due: 2026-04-01 | Desc: print test";
        assertTrue(taskList.getSKUTaskList().get(0).toString().contains("SKU-P1"));
        assertEquals(expected, taskList.getSKUTaskList().get(0).toString());
    }

    @Test
    public void printSKUTaskList_emptyList_noTasks() {
        SKUTaskList taskList = new SKUTaskList();

        assertTrue(taskList.isEmpty());
        assertEquals(0, taskList.getSize());
    }

    @Test
    public void setSKUTaskDescription_updatesDescription() {
        SKUTask task = new SKUTask("SKU-X", Priority.LOW, "2026-05-01", "initial desc");
        task.setSKUTaskDescription("updated desc");

        assertEquals("updated desc", task.getSKUTaskDescription());
    }

    @Test
    public void setSKUTaskDescription_overwritesPreviousValue() {
        SKUTask task = new SKUTask("SKU-Y", Priority.MEDIUM, "2026-06-01", "first");
        task.setSKUTaskDescription("second");
        task.setSKUTaskDescription("third");

        assertEquals("third", task.getSKUTaskDescription());
    }

    @Test
    public void setSKUTaskDueDate_validDate_updatesDate() {
        SKUTask task = new SKUTask("SKU-A", Priority.HIGH, "2026-01-01", "test");
        task.setSKUTaskDueDate("2026-12-31");
        assertEquals("2026-12-31", task.getSKUTaskDueDate());
    }

    @Test
    public void setSKUTaskPriority_validPriority_updatesPriority() {
        SKUTask task = new SKUTask("SKU-A", Priority.HIGH, "2026-01-01", "test");
        task.setSKUTaskPriority(Priority.LOW);
        assertEquals(Priority.LOW, task.getSKUTaskPriority());
    }

    @Test
    public void toString_withoutDescription_excludesDescField() {
        SKUTask task = new SKUTask("SKU-A", Priority.HIGH, "2026-01-01", "");
        String result = task.toString();
        assertFalse(result.contains("Desc:"));
        assertTrue(result.contains("SKU-A"));
    }

    @Test
    public void toString_markedTask_showsXStatus() {
        SKUTask task = new SKUTask("SKU-A", Priority.HIGH, "2026-01-01", "test");
        task.mark();
        assertTrue(task.toString().startsWith("[X]"));
    }

    @Test
    public void deleteSKUTaskByIndex_validIndex_taskRemoved() {
        SKUTaskList taskList = new SKUTaskList();
        taskList.addSKUTask("SKU-A", Priority.HIGH, "2026-01-01", "first");
        taskList.addSKUTask("SKU-B", Priority.LOW, "2026-02-01", "second");
        taskList.deleteSKUTaskByIndex(1);

        assertEquals(1, taskList.getSize());
        assertEquals("SKU-B", taskList.getSKUTaskList().get(0).getSKUTaskID());
    }

    @Test
    public void deleteSKUTaskByIndex_invalidIndex_throwsExceptions() {
        // With assertions enabled, this throws AssertionError.
        // With assertions disabled, it throws IndexOutOfBoundsException.
        // We capture any Throwable here strictly to ensure out-of-bounds are caught,
        // but normally this would be prevented by the Command validation.
        SKUTaskList taskList = new SKUTaskList();
        taskList.addSKUTask("SKU-A", Priority.HIGH, "2026-01-01", "only task");
        assertThrows(Throwable.class, () -> taskList.deleteSKUTaskByIndex(5));
    }

    @Test
    public void addSKUTask_twoArgs_defaultsPriorityAndDescription() {
        SKUTaskList taskList = new SKUTaskList();
        taskList.addSKUTask("SKU-Z", "2026-09-01");

        assertEquals(1, taskList.getSize());
        assertEquals(Priority.HIGH, taskList.getSKUTaskList().get(0).getSKUTaskPriority());
        assertEquals("", taskList.getSKUTaskList().get(0).getSKUTaskDescription());
    }

    // --- Equivalence Partitions for editSKUTask ---
    
    @Test
    public void editSKUTask_allFieldsProvided_updatesAllFields() {
        SKUTaskList taskList = new SKUTaskList();
        taskList.addSKUTask("SKU-EDIT", Priority.LOW, "2026-01-01", "Old desc");
        
        taskList.editSKUTask(1, "2026-12-31", Priority.HIGH, "New desc");
        SKUTask task = taskList.getSKUTaskList().get(0);
        
        assertEquals("2026-12-31", task.getSKUTaskDueDate());
        assertEquals(Priority.HIGH, task.getSKUTaskPriority());
        assertEquals("New desc", task.getSKUTaskDescription());
    }

    @Test
    public void editSKUTask_onlyCertainFields_leavesOthersUnchanged() {
        SKUTaskList taskList = new SKUTaskList();
        taskList.addSKUTask("SKU-EDIT2", Priority.LOW, "2026-01-01", "Old desc");
        
        taskList.editSKUTask(1, null, Priority.HIGH, null);
        SKUTask task = taskList.getSKUTaskList().get(0);
        
        assertEquals("2026-01-01", task.getSKUTaskDueDate());
        assertEquals(Priority.HIGH, task.getSKUTaskPriority());
        assertEquals("Old desc", task.getSKUTaskDescription());
    }

    @Test
    public void editSKUTask_outOfBoundsIndex_throwsExceptions() {
        SKUTaskList taskList = new SKUTaskList();
        taskList.addSKUTask("SKU-OOB", Priority.HIGH, "2026-01-01", "Task 1");
        assertThrows(Throwable.class, () -> taskList.editSKUTask(2, "2026-02-02", null, null));
    }

    // --- Boundaries for markTask and unmarkTask ---
    
    @Test
    public void markTask_validIndex_marksTask() {
        SKUTaskList taskList = new SKUTaskList();
        taskList.addSKUTask("SKU-M", Priority.HIGH, "2026-01-01");
        taskList.markTask(1);
        assertTrue(taskList.getSKUTaskList().get(0).isDone());
    }

    @Test
    public void markTask_outOfBoundsIndex_throwsExceptions() {
        SKUTaskList taskList = new SKUTaskList();
        taskList.addSKUTask("SKU-M", Priority.HIGH, "2026-01-01");
        assertThrows(Throwable.class, () -> taskList.markTask(2));
    }

    @Test
    public void unmarkTask_validIndex_unmarksTask() {
        SKUTaskList taskList = new SKUTaskList();
        taskList.addSKUTask("SKU-U", Priority.HIGH, "2026-01-01");
        taskList.markTask(1);
        taskList.unmarkTask(1);
        assertFalse(taskList.getSKUTaskList().get(0).isDone());
    }

    @Test
    public void unmarkTask_outOfBoundsIndex_throwsExceptions() {
        SKUTaskList taskList = new SKUTaskList();
        taskList.addSKUTask("SKU-U", Priority.HIGH, "2026-01-01");
        assertThrows(Throwable.class, () -> taskList.unmarkTask(0));
    }

    @Test
    public void deleteSKUTask_nullID_throwsAssertionError() {
        SKUTaskList taskList = new SKUTaskList();
        assertThrows(AssertionError.class, () -> taskList.deleteSKUTask(null));
    }

    @Test
    public void deleteSKUTask_emptyID_throwsAssertionError() {
        SKUTaskList taskList = new SKUTaskList();
        assertThrows(AssertionError.class, () -> taskList.deleteSKUTask("   "));
    }

    @Test
    public void addAndImmediatelyDelete_listIsEmpty() {
        SKUTaskList taskList = new SKUTaskList();
        taskList.addSKUTask("TEMP", Priority.LOW, "2025-01-01", "temp");
        assertEquals(1, taskList.getSKUTaskList().size());
        taskList.deleteSKUTask("TEMP");
        assertTrue(taskList.getSKUTaskList().isEmpty());
    }
}
