package ru.fizteh.fivt.students.SergeyAksenov.Parallel;

import java.util.ArrayList;
import java.util.List;

public class CreateCommand implements Command {
    public void run(String[] args, ParallelTableProvider tableProvider) {
        if (args.length < 3) {
            System.out.println("Invalid number of arguments");
            return;
        }
        if (args[2].charAt(0) != '(') {
            System.out.println("Invalid arguments");
            return;
        } else {
            args[2] = args[2].substring(1);
        }
        int lastNum = args.length - 1;
        if (args[lastNum].charAt(args[lastNum].length() - 1) != ')') {
            System.out.println("Invalid arguments");
            return;
        } else {
            args[lastNum] = args[lastNum].substring(0, args[lastNum].length() - 1);
        }
        List<Class<?>> typeList = new ArrayList<>();
        try {
            for (int i = 2; i < args.length; ++i) {
                if (args[i].charAt(args[i].length() - 1) == ',') {
                    args[i] = args[i].substring(0, args[i].length() - 1);
                }
                typeList.add(Class.forName("java.lang." + args[i]));
            }
        } catch (ClassNotFoundException e) {
            System.out.println("Create: unknown class");
            return;
        }
        if (tableProvider.createTable(args[1], typeList) == null) {
            System.out.println(args[1] + " exists");
            return;
        }
        System.out.println("created");
    }
}
