package ru.fizteh.fivt.students.SergeyAksenov.Parallel;

public class RemoveCommand implements Command {
    public void run(String[] args, ParallelTableProvider tableProvider)
            throws IllegalArgumentException {
        if (!Executor.checkArgNumber(2, args.length, 2)) {
            System.out.println("Invalid number of arguments");
            return;
        }
        ParallelTable currentTable = tableProvider.getCurrentTable();
        if (currentTable == null) {
            System.out.println("no table");
            return;
        }
        if (!args[1].isEmpty()) {
            MyStoreable value = (MyStoreable)currentTable.remove(args[1]);
            if (value == null) {
                System.out.println("not found");
                return;
            }
            value.print();
        }
    }
}
