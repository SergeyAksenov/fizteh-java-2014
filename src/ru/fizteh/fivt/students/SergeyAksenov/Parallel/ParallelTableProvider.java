package ru.fizteh.fivt.students.SergeyAksenov.Parallel;

import org.json.JSONArray;
import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ParallelTableProvider implements TableProvider {

    public ParallelTableProvider(String Dir)
            throws IOException {
        directory = Paths.get(Dir);
        File[] tableDirectories = directory.toFile().listFiles();
        tables = new HashMap<>();
        for (File it : tableDirectories) {
            if (it.getName().charAt(0) != '.') {
                if (!it.isDirectory()) {
                    throw new IOException(it.getName() + " is not a table");
                }
                File signFile = new File(it.getCanonicalPath() + File.separator + "signature.tsv");
                if (!signFile.exists()) {
                    throw new IOException("Signature for table " + it.getName() + " not found");
                }
                Class[] signature = Executor.readSignature(signFile);
                tables.put(it.getName(), signature);
            }
        }
        usingTable = null;
    }


    public ParallelTable getTable(String name) throws IllegalArgumentException {
        if (name == null) {
            throw new IllegalArgumentException("invalid argument");
        }
        locker.readLock().lock();
        try {
            String tablePath = directory.toString() + File.separator + name;
            File tableDir = new File(tablePath);
            if (!tableDir.exists()) {
                return null;
            }
            return new ParallelTable(name, tablePath, tables.get(name));
        } finally {
            locker.readLock().unlock();
        }
    }

    public Table createTable(String name, List<Class<?>> types) throws IllegalArgumentException {
        if (name == null) {
            throw new IllegalArgumentException("invalid argument");
        }
        locker.writeLock().lock();
        try {
            String tablePath = directory.toString() + File.separator + name;
            if (tables.containsKey(name)) {
                return null;
            }
            Class[] typeArray = new Class[types.size()];
            for (int i = 0; i < types.size(); ++i) {
                typeArray[i] = types.get(i);
            }
            tables.put(name, typeArray);
            return new ParallelTable(name, tablePath, typeArray);
        } finally {
            locker.writeLock().unlock();
        }
    }

    public void removeTable(String name) throws IllegalArgumentException, IllegalStateException {
        if (name == null) {
            throw new IllegalArgumentException("invalid argument");
        }
        locker.writeLock().lock();
        try {
            String tablePath = directory.toString() + File.separator + name;
            File tableDir = new File(tablePath);
            if (!tables.containsKey(name) || !tableDir.exists()) {
                throw new IllegalStateException(name + "does not exist");
            }
            Executor.delete(tableDir);
            if (usingTable.getName().equals(name)) {
                usingTable = null;
            }
            tables.remove(name);
        } finally {
            locker.writeLock().unlock();
        }
    }

    public void setUsingTable(String tablename) throws Exception {
        ParallelTable newTable = getTable(tablename);
        if (newTable == null) {
            throw new Exception(tablename + " does not exist");
        }
        if (usingTable != null) {
            if (usingTable.getNumberOfUncommittedChanges() != 0) {
                throw new Exception(usingTable.getNumberOfUncommittedChanges() + " uncommited changes");
            }
        }
        usingTable = newTable;
    }

    public List<String> getTableNames() {
        if (tables == null) {
            return null;
        }
        return new ArrayList<>(tables.keySet());
    }

    public ParallelTable getCurrentTable() {
        return usingTable;
    }

    public Storeable deserialize(Table table, String value)
            throws ParseException {
        MyStoreable ret =  Executor.parseString(value, tables.get(table.getName()));
        return ret;
    }

    public String serialize(Table table, Storeable value)
            throws ColumnFormatException {
        return (new JSONArray(((MyStoreable) value).dataToList())).toString();
    }

    public Storeable createFor(Table table) {
        return new MyStoreable(tables.get(table.getName()));
    }

    public Storeable createFor(Table table, List<?> values)
            throws ColumnFormatException, IndexOutOfBoundsException {
        MyStoreable newStoreable = new MyStoreable(tables.get(table.getName()));
        for (int i = 0; i < values.size(); ++i) {
            newStoreable.setColumnAt(i, values.get(i));
        }
        return newStoreable;
    }

    private final ReadWriteLock locker = new ReentrantReadWriteLock(true);

    private HashMap<String, Class[]> tables;

    private ParallelTable usingTable;

    private Path directory;

}
