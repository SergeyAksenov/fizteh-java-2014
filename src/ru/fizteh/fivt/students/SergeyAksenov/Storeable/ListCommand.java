package ru.fizteh.fivt.students.SergeyAksenov.Storeable;


import java.util.List;

public class ListCommand implements Command {
    public void run(String[] args, StoreableTableProvider tableProvider) throws IllegalArgumentException {
        if (!Executor.checkArgNumber(1, args.length, 1)) {
            System.out.println("Invalid number of arguments");
            return;
        }
        StoreableTable currentTable = tableProvider.getCurrentTable();
        if (currentTable == null) {
            System.out.println("no table");
            return;
        }
        List<String> list = currentTable.list();
        System.out.println(String.join(", ", list));
    }
}
