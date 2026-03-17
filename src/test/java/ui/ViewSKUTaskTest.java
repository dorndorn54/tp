package ui;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sku.Location;
import sku.SKUList;
import skutask.Priority;
import skutask.SKUTask;
import skutask.SKUTaskList;
import skutask.ViewSKUTask;

import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ViewSKUTaskTest {

    private ViewSKUTask viewer;
    private SKUList skuList;
    private HashMap<String, SKUTaskList> taskMap;

    @BeforeEach
    public void setUp() {
        viewer = new ViewSKUTask();
        skuList = new SKUList();
        taskMap = new HashMap<>();

        // Setup Pallet A at A1 (0,0) with 2 tasks
        skuList.addSKU("PALLET-A", Location.A1);
        SKUTaskList listA = new SKUTaskList();
        listA.addSKUTask("PALLET-A", Priority.HIGH, "2026-04-01");
        listA.addSKUTask("PALLET-A", Priority.MEDIUM, "2026-04-05");
        taskMap.put("PALLET-A", listA);

        // Setup Pallet B at B1 (1,0) with 1 task
        skuList.addSKU("PALLET-B", Location.B1);
        SKUTaskList listB = new SKUTaskList();
        listB.addSKUTask("PALLET-B", Priority.HIGH, "2026-04-10");
        taskMap.put("PALLET-B", listB);
    }

    @Test
    public void listTasks_noFilters_returnsAllTasks() {
        List<SKUTask> results = viewer.listTasks(skuList, taskMap);
        assertEquals(3, results.size());
    }

    @Test
    public void listTasks_skuFilter_returnsOnlyMatchingSku() {
        viewer.setSkuFilter("PALLET-A");
        List<SKUTask> results = viewer.listTasks(skuList, taskMap);

        assertEquals(2, results.size());
        assertTrue(results.stream().allMatch(t -> t.getSKUTaskID().equals("PALLET-A")));
    }

    @Test
    public void listTasks_priorityFilter_returnsOnlyMatchingPriority() {
        viewer.setPriorityFilter("HIGH");
        List<SKUTask> results = viewer.listTasks(skuList, taskMap);

        assertEquals(2, results.size());
        assertTrue(results.stream().allMatch(t -> t.getSKUTaskPriority() == Priority.HIGH));
    }

    @Test
    public void listTasks_locationFilter_sortsByDistance() {
        // A1 is at (0,0). PALLET-A is at A1 (dist 0), PALLET-B is at B1 (dist 1)
        viewer.setLocationFilter("A1");
        List<SKUTask> results = viewer.listTasks(skuList, taskMap);

        assertEquals(3, results.size());
        // First two should be PALLET-A (dist 0)
        assertEquals("PALLET-A", results.get(0).getSKUTaskID());
        assertEquals("PALLET-A", results.get(1).getSKUTaskID());
        // Last should be PALLET-B (dist 1)
        assertEquals("PALLET-B", results.get(2).getSKUTaskID());
    }

    @Test
    public void calculateDistance_validLocations_returnsDistance() {
        // PALLET-B is at B1. Distance from A1 to B1 is |0-1| + |0-0| = 1
        SKUTask taskB = taskMap.get("PALLET-B").getSKUTaskList().get(0);
        int distance = viewer.calculateDistance(taskB, "A1", skuList);

        assertEquals(1, distance);

        // Distance from C3 (2,2) to B1 (1,0) is |2-1| + |2-0| = 3
        int distanceToC3 = viewer.calculateDistance(taskB, "C3", skuList);
        assertEquals(3, distanceToC3);
    }

    @Test
    public void listTasks_nonExistentSkuFilter_returnsEmptyList() {
        viewer.setSkuFilter("GHOST-SKU");
        List<SKUTask> results = viewer.listTasks(skuList, taskMap);

        assertTrue(results.isEmpty());
    }
}
