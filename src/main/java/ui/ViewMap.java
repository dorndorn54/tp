package ui;

import sku.SKU;
import sku.SKUList;

import java.util.HashMap;
import java.util.Map;

public class ViewMap {

    public void printTaskMap(SKUList fullList) {
        assert fullList != null : "Internal Error: ViewMap received a null SKUList pointer";

        Map<String, Integer> counts = new HashMap<>();
        for (SKU sku : fullList.getSKUList()) {
            assert sku.getSKULocation() != null : "Data Error: SKU " + sku.getSKUID() + " has no location";

            String loc = sku.getSKULocation().name();
            int size = sku.getSKUTaskList().getSize();
            counts.put(loc, counts.getOrDefault(loc, 0) + size);
        }

        System.out.println("\n--- Warehouse Task Distribution (A1-C3) ---");
        for (char row = 'A'; row <= 'C'; row++) {
            for (int col = 1; col <= 3; col++) {
                String loc = "" + row + col;
                System.out.printf("[%s: %02d] ", loc, counts.getOrDefault(loc, 0));
            }
            System.out.println();
        }
        System.out.println("-------------------------------------------\n");
    }
}
