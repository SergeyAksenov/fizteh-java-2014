package ru.fizteh.fivt.students.SergeyAksenov.Parallel;

public class RollbackCommand implements Command {
    public void run(String[] args, ParallelTableProvider tableProvider) {
        if (!Executor.checkArgNumber(1, args.length, 1)) {
            System.out.println("Invalid number of arguments");
            return;
        }
        ParallelTable table = tableProvider.getCurrentTable();
        if (table == null) {
            System.out.println("no table");
            return;
        }
        int keyNum = table.rollback();
        System.out.println(keyNum + " changes cancelled.");

    }
}
