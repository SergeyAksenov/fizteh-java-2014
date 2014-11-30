package ru.fizteh.fivt.students.SergeyAksenov.Storeable;

import ru.fizteh.fivt.storage.structured.TableProviderFactory;

import java.io.IOException;

public class StoreableTableProviderFactory implements TableProviderFactory {

    public StoreableTableProviderFactory() {
    }

    public StoreableTableProvider create(String dir) throws IllegalArgumentException, IOException {
        if (dir == null) {
            throw new IllegalArgumentException("create");
        }
        return new StoreableTableProvider(dir);
    }
}
