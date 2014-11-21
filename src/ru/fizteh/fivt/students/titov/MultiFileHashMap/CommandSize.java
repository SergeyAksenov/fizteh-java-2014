package ru.fizteh.fivt.students.titov.MultiFileHashMap;

import ru.fizteh.fivt.students.titov.FileMap.FileMap;

public class CommandSize extends CommandMultiFileHashMap {
    public CommandSize() {
        name = "size";
        numberOfArguments = 1;
    }

    @Override
    public boolean run(MFileHashMap myMap, String[] args) {
        FileMap currentTable = myMap.getCurrentTable();
        if (currentTable == null) {
            System.out.println("no table");
        } else {
            System.out.println(currentTable.size());
        }
        return true;
    }
}
