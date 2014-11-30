package ru.fizteh.fivt.students.SergeyAksenov.Storeable;


public class ExitCommand implements Command {
    public void run(String[] args, StoreableTableProvider tableProvider) {
        if (!Executor.checkArgNumber(1, args.length, 1)) {
            System.out.println("Invalid number of arguments");
            return;
        }
        StoreableTable currentTable = tableProvider.getCurrentTable();
        if (currentTable != null) {
            currentTable.rollback();
        }
        System.exit(0);
    }
}
