package ru.fizteh.fivt.students.SergeyAksenov.Storeable;

public interface Command {
    public void run(String[] args, StoreableTableProvider tableProvider) throws Exception;
}
