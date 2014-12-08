package ru.fizteh.fivt.students.SergeyAksenov.Parallel;

import ru.fizteh.fivt.storage.structured.TableProviderFactory;

import java.io.IOException;

public class ParallelTableProviderFactory implements TableProviderFactory {

    public ParallelTableProviderFactory() {
    }

    public ParallelTableProvider create(String dir) throws IllegalArgumentException, IOException {
        if (dir == null) {
            throw new IllegalArgumentException("create");
        }
        return new ParallelTableProvider(dir);
    }
}
