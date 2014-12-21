package ru.fizteh.fivt.students.SergeyAksenov.Parallel;


public class ExitCommand implements Command {
    public void run(String[] args, ParallelTableProvider tableProvider) {
        if (!Executor.checkArgNumber(1, args.length, 1)) {
            System.out.println("Invalid number of arguments");
            return;
        }
        ParallelTable currentTable = tableProvider.getCurrentTable();
        if (currentTable != null) {
            currentTable.rollback();
        }
        System.exit(0);
    }
}
