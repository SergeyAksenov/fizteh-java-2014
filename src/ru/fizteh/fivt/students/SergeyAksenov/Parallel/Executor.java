package ru.fizteh.fivt.students.SergeyAksenov.Parallel;

import com.oracle.javafx.jmx.json.JSONException;
import org.json.JSONArray;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Executor {
    public static boolean checkArgNumber(int from, int value, int to) {
        return from <= value && value <= to;
    }

    public static void execute(final HashMap<String, Command> commandMap,
                               final String[] commands,
                               ParallelTableProvider tableProvider) {
        if (commands[0].equals("")) {
            return;
        }
        Command cmd = commandMap.get(commands[0]);
        if (cmd == null) {
            System.out.println("unknown command");
            return;
        }
        commandMap.get(commands[0]).run(commands, tableProvider);
    }

    public static void execLine(String line,
                                final HashMap<String, Command> commandMap,
                                ParallelTableProvider tableProvider) {
        String[] commands = line.trim().split(";");
        for (String command : commands) {
            command = command.trim();
            String[] splittedCommand = command.split("\\s+");
            execute(commandMap, splittedCommand, tableProvider);
        }
    }

    public static void interactiveMode(
            final HashMap<String, Command> commandMap,
            ParallelTableProvider tableProvider)
        throws Exception {
        try (Scanner scanner = new Scanner(System.in)) {
            for (System.out.print("$ "), System.out.flush();
                 scanner.hasNextLine();
                 System.out.print("$ "), System.out.flush()) {
                String command = scanner.nextLine();
                execLine(command, commandMap, tableProvider);
                System.out.flush();
            }
            throw new Exception();
        } catch (NoSuchElementException e) {
            System.err.println(e.getMessage());
            System.exit(-1);
        }
    }

    public static void delete(File fileToRem) {
        if (fileToRem.isDirectory()) {
            File[] files = fileToRem.listFiles();
            if (files == null) {
                System.out.println("Cannot delete file " + fileToRem.toString());
            }
            for (File file : files) {
                delete(file);
            }
        }
        if (!fileToRem.delete()) {
            System.out.println("Cannot delete file " + fileToRem.toString());
        }
    }

    public static MyStoreable parseString(String str, Class[] types)
            throws ParseException {
        if (str == null || (str.charAt(0) != '[' && str.charAt(str.length() - 1) != ']')) {
            throw new IllegalArgumentException("Parser: illegal argument");
        }
        MyStoreable result = new MyStoreable(types);
        try {
            JSONArray parser = new JSONArray(str);
            for (int i = 0; i < parser.length(); ++i) {
                try {
                    if (parser.get(i) == null) {
                        result.setColumnAt(i, null);
                    } else {
                        result.setColumnAt(i, types[i].cast(parser.get(i)));
                    }
                } catch (ClassCastException e) {
                    throw new ParseException(parser.get(i).toString(), 0);
                }
            }
        } catch (JSONException e) {
            return null;
        }
        return result;
    }

    public static Class[] readSignature(File file) throws IOException {
        FileReader reader = new FileReader(file);
        StringBuilder buffer = new StringBuilder("");
        while (reader.ready()) {
            buffer.append((char) reader.read());
        }
        reader.close();
        String types = new String(buffer);
        String[] typeList = types.split("\\s+");
        Class[] result = new Class[typeList.length];
        try {
            for (int i = 0; i < result.length; ++i) {
                result[i] = Class.forName("java.lang." + typeList[i]);
            }
        } catch (ClassNotFoundException e) {
            throw new IOException("Cannot read signatures");
        }
        return result;
    }
}
