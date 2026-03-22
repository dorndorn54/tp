package ui;

import org.junit.jupiter.api.Test;
import skutask.SKUTask;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class SKUTaskMarkUnmarkTest {

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
}
