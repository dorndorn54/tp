package ui;

import skutask.SKUTask;

/**
 * Bundles an SKU task with its parent SKU ID and precomputed Manhattan distance.
 * This class acts as a data transfer object to facilitate the sorting of tasks
 * based on their physical distance from a target location in the warehouse.
 */
public class TaskEntry {
    final String skuId;
    final SKUTask task;
    final int distance;

    /**
     * Constructs a TaskEntry with the specified SKU ID, task, and precomputed distance.
     *
     * @param skuId The unique identifier of the parent SKU.
     * @param task The task associated with the SKU.
     * @param distance The calculated Manhattan distance from the starting location.
     */
    TaskEntry(String skuId, SKUTask task, int distance) {
        this.skuId = skuId;
        this.task = task;
        this.distance = distance;
    }
}
