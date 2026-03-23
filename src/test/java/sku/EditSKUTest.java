package sku;

import command.CommandRunner;
import command.ParsedCommand;
import exception.ItemTaskerException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EditSKUTest {

    private CommandRunner runner;
    private SKUList skuList;
    private SKU sku;
    private ByteArrayOutputStream outputStream;
    private PrintStream originalOut;

    @BeforeEach
    public void setUp() {
        skuList = new SKUList();
        skuList.addSKU("PALLET-A", Location.A1);
        sku = skuList.getSKUList().get(0);
        runner = new CommandRunner(skuList);

        originalOut = System.out;
        outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
    }

    @AfterEach
    public void tearDown() {
        System.setOut(originalOut);
    }

    private ParsedCommand buildEditSku(String skuId, String location) {
        Map<String, String> args = new HashMap<>();
        if (skuId != null) {
            args.put("n", skuId);
        }
        if (location != null) {
            args.put("l", location);
        }
        return new ParsedCommand("editsku", args);
    }

    @Test
    public void editsku_validLocation_locationUpdated() throws ItemTaskerException, IOException {
        runner.run(buildEditSku("PALLET-A", "C2"));
        assertEquals(Location.C2, sku.getSKULocation());
    }

    @Test
    public void editsku_validLocation_successMessageShown() throws ItemTaskerException, IOException {
        runner.run(buildEditSku("PALLET-A", "B3"));
        assertTrue(outputStream.toString().contains("[OK]"));
        assertTrue(outputStream.toString().contains("PALLET-A"));
        assertTrue(outputStream.toString().contains("B3"));
    }

    @Test
    public void editsku_sameLocation_locationUnchanged() throws ItemTaskerException, IOException {
        runner.run(buildEditSku("PALLET-A", "A1"));
        assertEquals(Location.A1, sku.getSKULocation());
        assertTrue(outputStream.toString().contains("[OK]"));
    }

    @Test
    public void editsku_skuNotFound_showsError() throws ItemTaskerException, IOException {
        runner.run(buildEditSku("GHOST-SKU", "B1"));
        assertTrue(outputStream.toString().contains("[ERROR]"));
        assertEquals(Location.A1, sku.getSKULocation());
    }

    @Test
    public void editsku_invalidLocation_showsError() throws ItemTaskerException, IOException {
        runner.run(buildEditSku("PALLET-A", "Z9"));
        assertTrue(outputStream.toString().contains("[ERROR]"));
        assertEquals(Location.A1, sku.getSKULocation());
    }

    @Test
    public void editsku_missingSkuId_showsError() throws ItemTaskerException, IOException {
        runner.run(buildEditSku(null, "B2"));
        assertTrue(outputStream.toString().contains("[ERROR]"));
        assertEquals(Location.A1, sku.getSKULocation());
    }

    @Test
    public void editsku_missingLocation_showsError() throws ItemTaskerException, IOException {
        runner.run(buildEditSku("PALLET-A", null));
        assertTrue(outputStream.toString().contains("[ERROR]"));
        assertEquals(Location.A1, sku.getSKULocation());
    }

    @Test
    public void editsku_caseInsensitiveSkuId_locationUpdated() throws ItemTaskerException, IOException {
        runner.run(buildEditSku("pallet-a", "C3"));
        assertEquals(Location.C3, sku.getSKULocation());
    }

    @Test
    public void editsku_caseInsensitiveLocation_locationUpdated() throws ItemTaskerException, IOException {
        runner.run(buildEditSku("PALLET-A", "b2"));
        assertEquals(Location.B2, sku.getSKULocation());
    }
}
