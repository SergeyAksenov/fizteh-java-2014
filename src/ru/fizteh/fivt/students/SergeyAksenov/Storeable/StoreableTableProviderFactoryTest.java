package ru.fizteh.fivt.students.SergeyAksenov.Storeable;

import org.junit.Test;

import static org.junit.Assert.*;

public class StoreableTableProviderFactoryTest {
    private static StoreableTableProviderFactory factory;

    static {
        factory = new StoreableTableProviderFactory();
    }

    @Test
    public void testCreate() throws Exception {
        try {
            factory.create(null);
            assertTrue(false);
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }
    }
}
