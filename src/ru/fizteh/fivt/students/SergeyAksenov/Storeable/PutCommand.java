package ru.fizteh.fivt.students.SergeyAksenov.Storeable;

public class PutCommand implements Command {
    public void run(String[] args, StoreableTableProvider tableProvider)
            throws IllegalArgumentException {
        StoreableTable currentTable = tableProvider.getCurrentTable();
        if (currentTable == null) {
            System.out.println("no table");
            return;
        }
        StringBuilder sb = new StringBuilder("");
        for (int i = 2; i < args.length; ++i) {
            sb.append(args[i]);
        }
        MyStoreable newValue;
        Class[] tableTypes = currentTable.getValueType();
        try {
            newValue = Executor.parseString(sb.toString(), tableTypes);
        } catch (Exception e) {
            System.out.println("Invalid argument");
            return;
        }
        MyStoreable oldValue = currentTable.put(args[1], newValue);
        if (oldValue == null) {
            System.out.println("new");
            return;
        }
        System.out.println("overwrite");
        oldValue.print();
    }
}
