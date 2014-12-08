package ru.fizteh.fivt.students.SergeyAksenov.Parallel;

public interface Command {
    public void run(String[] args, ParallelTableProvider tableProvider);
}
