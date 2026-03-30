package skutask;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

//@@author heehaw1234
public class SKUTaskTest {

    @Test
    public void constructor_allFieldsProvided_fieldsAreSet() {
        SKUTask task = new SKUTask("SKU-1", Priority.MEDIUM, "2026-05-01", "Full setup");
        assertEquals("SKU-1", task.getSKUTaskID());
        assertEquals(Priority.MEDIUM, task.getSKUTaskPriority());
        assertEquals("2026-05-01", task.getSKUTaskDueDate());
        assertEquals("Full setup", task.getSKUTaskDescription());
        assertFalse(task.isDone());
    }

    @Test
    public void constructor_noDescriptionProvided_defaultsToEmpty() {
        SKUTask task = new SKUTask("SKU-2", Priority.LOW, "2026-06-01");
        assertEquals("SKU-2", task.getSKUTaskID());
        assertEquals(Priority.LOW, task.getSKUTaskPriority());
        assertEquals("2026-06-01", task.getSKUTaskDueDate());
        assertEquals("", task.getSKUTaskDescription());
    }

    @Test
    public void constructor_noPriorityProvided_defaultsToHigh() {
        SKUTask task = new SKUTask("SKU-3", "2026-07-01", "Missing priority");
        assertEquals(Priority.HIGH, task.getSKUTaskPriority());
        assertEquals("Missing priority", task.getSKUTaskDescription());
    }

    @Test
    public void mark_changesIsDoneToTrue() {
        SKUTask task = new SKUTask("SKU-4", "2026-08-01");
        assertFalse(task.isDone());
        task.mark();
        assertTrue(task.isDone());
    }

    @Test
    public void mark_alreadyMarkedTask_throwsAssertionError() {
        SKUTask task = new SKUTask("SKU-4", "2026-08-01");
        task.mark();
        assertThrows(AssertionError.class, task::mark);
    }

    @Test
    public void unmark_changesIsDoneToFalse() {
        SKUTask task = new SKUTask("SKU-5", "2026-09-01");
        task.mark();
        assertTrue(task.isDone());
        task.unmark();
        assertFalse(task.isDone());
    }

    @Test
    public void unmark_alreadyUnmarkedTask_throwsAssertionError() {
        SKUTask task = new SKUTask("SKU-5", "2026-09-01");
        assertThrows(AssertionError.class, task::unmark);
    }

    @Test
    public void setters_updateFieldsCorrectly() {
        SKUTask task = new SKUTask("SKU-6", Priority.HIGH, "2026-01-01", "Old desc");

        task.setSKUTaskPriority(Priority.LOW);
        assertEquals(Priority.LOW, task.getSKUTaskPriority());

        task.setSKUTaskDueDate("2026-12-12");
        assertEquals("2026-12-12", task.getSKUTaskDueDate());

        task.setSKUTaskDescription("New desc");
        assertEquals("New desc", task.getSKUTaskDescription());
    }

    @Test
    public void toString_withDescription_includesDesc() {
        SKUTask task = new SKUTask("SKU-8", Priority.MEDIUM, "2026-01-01", "Valid Desc");
        assertEquals("[ ] ID: SKU-8 | Priority: MEDIUM | Due: 2026-01-01 | Desc: Valid Desc", task.toString());
    }

    @Test
    public void toString_withoutDescription_excludesDesc() {
        SKUTask task = new SKUTask("SKU-9", Priority.HIGH, "2027-01-01");
        assertEquals("[ ] ID: SKU-9 | Priority: HIGH | Due: 2027-01-01", task.toString());
    }
}

