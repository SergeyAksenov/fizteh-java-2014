package ru.fizteh.fivt.students.SergeyAksenov.Storeable;

import org.junit.Test;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;

import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class StoreableTableProviderTest {
    private static StoreableTableProvider tableProvider;
    private static String path;
    private static List<Class<?>> typeList;

    static {
        try {
            path = Files.createTempDirectory("database").toString();
            tableProvider = new StoreableTableProvider(path);
            typeList = new ArrayList<>();
            typeList.add(Integer.class);
            typeList.add(String.class);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    @Test
    public void testGetTable() throws Exception {
        try {
            tableProvider.getTable(null);
            assertTrue(false);
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }
        tableProvider.createTable("First", typeList);
        StoreableTable table = tableProvider.getTable("First");
        assertEquals("First", table.getName());
        tableProvider.removeTable("First");
    }

    @Test
    public void testCreateTable() throws Exception {
        try {
            tableProvider.createTable(null, null);
            assertTrue(false);
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }
        assertNotNull(tableProvider.createTable("First", typeList));
        assertNull(tableProvider.createTable("First", typeList));
        tableProvider.removeTable("First");
    }

    @Test
    public void testRemoveTable() throws Exception {
        try {
            tableProvider.removeTable(null);
            assertTrue(false);
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }
        try {
            tableProvider.removeTable("First");
            assertTrue(false);
        } catch (IllegalStateException e) {
            assertTrue(true);
        }
        tableProvider.createTable("First", typeList);
        assertNotNull(tableProvider.getTable("First"));
        tableProvider.removeTable("First");
        assertNull(tableProvider.getTable("First"));
    }

    @Test
    public void testDeserialize() throws Exception {
        StoreableTable table = (StoreableTable) tableProvider.createTable("SomeTable", typeList);
        MyStoreable result = (MyStoreable) tableProvider.deserialize(table, "[1,\"one\"]");
        assertEquals(1, result.getColumnAt(0));
        assertEquals("one", result.getStringAt(1));
        tableProvider.removeTable("First");
    }

    @Test
    public void testSerialize() throws Exception {
        Table table = tableProvider.createTable("First", typeList);
        MyStoreable stor = new MyStoreable(typeList);
        stor.setColumnAt(0, 1);
        stor.setColumnAt(1, "one");
        assertEquals("[1,\"one\"]", tableProvider.serialize(table, stor));
        tableProvider.removeTable("First");
    }

    @Test
    public void testCreateFor() throws Exception {
        Table table = tableProvider.createTable("First", typeList);
        Storeable stor = tableProvider.createFor(table);
        assertNull(stor.getColumnAt(0));
        try {
            assertNull(stor.getColumnAt(2));
            assertTrue(false);
        } catch (IndexOutOfBoundsException e) {
            assertTrue(true);
        }
        table.remove("First");
    }

    @Test
    public void testCreateFor1() throws Exception {
        Table table = tableProvider.createTable("First", typeList);
        List<Object> data = new ArrayList<>();
        data.add(1);
        data.add("one");
        Storeable stor = tableProvider.createFor(table, data);
        assertEquals(1, stor.getColumnAt(0));
        assertEquals("one", stor.getColumnAt(1));
        try {
            assertNull(stor.getColumnAt(2));
            assertTrue(false);
        } catch (IndexOutOfBoundsException e) {
            assertTrue(true);
        }
        table.remove("First");
    }
}
