package ru.fizteh.fivt.students.SergeyAksenov.Storeable;


public class GetCommand implements Command {
    public void run(String[] args, StoreableTableProvider tableProvider) throws IllegalArgumentException {
        if (!Executor.checkArgNumber(2, args.length, 2)) {
            System.out.println("Invalid number of arguments");
            return;
        }
        StoreableTable currentTable = tableProvider.getCurrentTable();
        if (currentTable == null) {
            System.out.println("no table");
            return;
        }
        MyStoreable value = currentTable.get(args[1]);
        if (value == null) {
            System.out.println("not found");
            return;
        }
        System.out.println("found");
        value.print();
    }
}
