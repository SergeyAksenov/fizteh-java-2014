package ru.fizteh.fivt.students.SergeyAksenov.Storeable;


public class CommitCommand implements Command {
    public void run(String[] args, StoreableTableProvider tableProvider) {
        if (!Executor.checkArgNumber(1, args.length, 1)) {
            System.out.println("Invalid number of arguments");
            return;
        }
        StoreableTable table = tableProvider.getCurrentTable();
        if (table == null) {
            System.out.println("no table");
            return;
        }
        int keyNum = table.commit();
        System.out.println(keyNum + " keys saved.");

    }
}
