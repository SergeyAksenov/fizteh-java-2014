package ru.fizteh.fivt.students.SergeyAksenov.Parallel;

import java.util.List;

public class ShowCommand implements Command {
    public void run(String[] args, ParallelTableProvider tableProvider) {
        if (!Executor.checkArgNumber(1, args.length, 1)) {
            System.out.println("Invalid number of arguments");
            return;
        }
        List<String> tableNames = tableProvider.getTableNames();
        if (tableNames == null) {
            return;
        }
        for (String tableName : tableNames) {
            if (tableName.charAt(0) != '.') {
                System.out.println(tableName);
            }
        }
    }
}
