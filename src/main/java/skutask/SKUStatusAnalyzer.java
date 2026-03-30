package skutask;

import sku.SKU;
import sku.SKUList;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Computes completion statistics for SKU task lists.
 * Produces a Status report summarising total, completed, pending,
 * high-priority pending, and overdue task counts for a single SKU.
 */
//@@author SeanTLY23
public class SKUStatusAnalyzer {
    private static final Logger LOGGER = Logger.getLogger(SKUStatusAnalyzer.class.getName());

    /**
     * Immutable data object holding the computed status of a single SKU's task list.
     */
    public static class StatusResult {
        private final String skuId;
        private final int totalTasks;
        private final int completedTasks;
        private final int pendingTasks;
        private final int completionPercent;
        private final int pendingHighPriority;
        private final int overdueCount;

        /**
         * Constructs a StatusResult with the given counts.
         *
         * @param skuId              The SKU identifier.
         * @param totalTasks         Total number of tasks.
         * @param completedTasks     Number of completed tasks.
         * @param pendingHighPriority Number of pending HIGH priority tasks.
         * @param overdueCount       Number of overdue pending tasks.
         */
        public StatusResult(String skuId, int totalTasks, int completedTasks,
                            int pendingHighPriority, int overdueCount) {
            this.skuId = skuId;
            this.totalTasks = totalTasks;
            this.completedTasks = completedTasks;
            this.pendingTasks = totalTasks - completedTasks;
            this.completionPercent = totalTasks == 0 ? 0 : (completedTasks * 100) / totalTasks;
            this.pendingHighPriority = pendingHighPriority;
            this.overdueCount = overdueCount;
        }

        public String getSkuId() {
            return skuId;
        }

        public int getTotalTasks() {
            return totalTasks;
        }

        public int getCompletedTasks() {
            return completedTasks;
        }

        public int getPendingTasks() {
            return pendingTasks;
        }

        public int getCompletionPercent() {
            return completionPercent;
        }

        public int getPendingHighPriority() {
            return pendingHighPriority;
        }

        public int getOverdueCount() {
            return overdueCount;
        }
    }

    /**
     * Analyzes a single SKU and returns its task status summary.
     *
     * @param sku The SKU to analyze.
     * @return A StatusResult containing the computed statistics.
     */
    public StatusResult analyze(SKU sku) {
        assert sku != null : "SKU to analyze cannot be null";

        SKUTaskList taskList = sku.getSKUTaskList();
        assert taskList != null : "SKUTaskList should never be null for SKU: " + sku.getSKUID();

        ArrayList<SKUTask> tasks = taskList.getSKUTaskList();
        int total = tasks.size();
        int completed = 0;
        int pendingHigh = 0;
        int overdue = 0;
        LocalDate today = LocalDate.now();

        for (SKUTask task : tasks) {
            if (task.isDone()) {
                completed++;
                continue;
            }
            if (task.getSKUTaskPriority() == Priority.HIGH) {
                pendingHigh++;
            }
            try {
                LocalDate dueDate = LocalDate.parse(task.getSKUTaskDueDate());
                if (dueDate.isBefore(today)) {
                    overdue++;
                }
            } catch (DateTimeParseException e) {
                LOGGER.log(Level.WARNING, "Unparseable due date for task in SKU [{0}]: {1}",
                        new Object[]{sku.getSKUID(), task.getSKUTaskDueDate()});
            }
        }

        LOGGER.log(Level.INFO, "Analyzed SKU [{0}]: total={1}, done={2}, pendingHigh={3}, overdue={4}",
                new Object[]{sku.getSKUID(), total, completed, pendingHigh, overdue});

        return new StatusResult(sku.getSKUID(), total, completed, pendingHigh, overdue);
    }

    /**
     * Analyzes all SKUs in the warehouse and returns a list of status results.
     *
     * @param skuList The master list of all SKUs.
     * @return A list of StatusResult objects, one per SKU.
     */
    public List<StatusResult> analyzeAll(SKUList skuList) {
        assert skuList != null : "SKUList to analyze cannot be null";

        List<StatusResult> results = new ArrayList<>();
        for (SKU sku : skuList.getSKUList()) {
            results.add(analyze(sku));
        }

        LOGGER.log(Level.INFO, "Analyzed all {0} SKUs.", results.size());
        return results;
    }
}

