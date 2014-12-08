package ru.fizteh.fivt.students.SergeyAksenov.Parallel;

import org.json.JSONArray;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ParallelTable implements Table {

    public ParallelTable(String name, String path, Class[] types) {
        tableLocker.readLock().lock();
        tablePath = Paths.get(path);
        File table = tablePath.toFile();
        if (table.exists()) {
            this.name = name;
            valueType = types;
            data = read();
            tableLocker.readLock().unlock();
            return;
        }
        data = new HashMap<>();
        if (!table.mkdir()) {
            System.out.println("Cannot create directory for new table");
            tableLocker.readLock().unlock();
            return;
        }
        valueType = types;
        this.name = name;
        try {
            writeSignatures();
        } catch (IOException e) {
            System.out.println("Cannot write signature to file");
        } finally {
            tableLocker.readLock().unlock();
        }
    }


    public int getColumnsCount() {
        return valueType.length;
    }

    public Class<?> getColumnType(int columnIndex)
            throws IndexOutOfBoundsException {
        if (columnIndex < 0 || columnIndex >= valueType.length) {
            throw new IndexOutOfBoundsException("Table: invalid index");
        }
        return valueType[columnIndex];
    }

    public String getName() {
        return name;
    }

    public int size() {
        return data.size();
    }

    public Storeable get(String key)
            throws IllegalArgumentException {
        if (key == null) {
            throw new IllegalArgumentException("get");
        }
        tableLocker.readLock().lock();
        try {
            if (dataChanges.get().containsKey(key)) {
                return dataChanges.get().get(key);
            } else {
                return data.get(key);
            }
        } finally {
            tableLocker.readLock().unlock();
        }
    }

    public Storeable put(String key, Storeable value)
            throws IllegalArgumentException {
        if (key == null || value == null) {
            throw new IllegalArgumentException("put");
        }
        tableLocker.readLock().lock();
        try {
            if (dataChanges.get().containsKey(key)) {
                return dataChanges.get().put(key, value);
            } else if (data.containsKey(key)) {
                dataChanges.get().put(key, value);
                return data.get(key);
            } else {
                return dataChanges.get().put(key, value);
            }
        } finally {
            tableLocker.readLock().unlock();
        }
    }

    public Storeable remove(String key)
            throws IllegalArgumentException {
        if (key == null) {
            throw new IllegalArgumentException("remove");
        }
        tableLocker.readLock().lock();
        try {
            if (dataChanges.get().containsKey(key)) {
                if (dataChanges.get().get(key) == null) {
                    return null;
                } else {
                    return dataChanges.get().put(key, null);
                }
            }

            if (data.containsKey(key)) {
                dataChanges.get().put(key, null);
                return data.get(key);
            }
            return null;
        } finally {
            tableLocker.readLock().unlock();
        }
    }

    public List<String> list() {
        tableLocker.readLock().lock();
        try {
            return new ArrayList<>(getRelevantData().keySet());
        } finally {
            tableLocker.readLock().unlock();
        }
    }

    public int commit() {
        tableLocker.writeLock().lock();
        try {
            data =  getRelevantData();
            write(data);
            int ret = data.size();
            dataChanges.get().clear();
            return ret;
        } catch (Exception e) {
            System.out.println("Error in writing to file");
            return -1;
        } finally {
            tableLocker.writeLock().unlock();
        }
    }

    public int rollback() {
        tableLocker.readLock().lock();
        try {
            int ret = dataChanges.get().size();
            dataChanges.get().clear();
            return ret;
        } finally {
            tableLocker.readLock().unlock();
        }
    }

    public int getNumberOfUncommittedChanges() {
        return dataChanges.get().size();
    }


    private String readUtil(DataInputStream inStream) throws IOException {
        int length = inStream.readInt();
        byte[] word = new byte[length];
        inStream.readFully(word);
        return new String(word, "UTF-8");
    }

    private HashMap<String, MyStoreable> read() {
        HashMap<String, MyStoreable> relevantData = new HashMap<>();
        File[] directories = tablePath.toFile().listFiles();
        for (File dir : directories) {
            if (dir.isDirectory()) {
                File[] files = dir.listFiles();
                if (files == null) {
                    return null;
                }
                try {
                    for (File file : files) {
                        boolean end = false;
                        DataInputStream stream = new DataInputStream(new FileInputStream(file));
                        while (!end) {
                            try {
                                String key = readUtil(stream);
                                String valueStr = readUtil(stream);
                                MyStoreable value = Executor.parseString(valueStr, valueType);
                                relevantData.put(key, value);
                            } catch (IOException e) {
                                end = true;
                            }
                        }

                    }
                } catch (Exception e) {
                    System.out.println("Error in reading");
                    return null;
                }
            }
        }
        return relevantData;
    }

    private void writeUtil(DataOutputStream outStream, String str) throws IOException {
        byte[] byteStr = str.getBytes("UTF-8");
        outStream.writeInt(str.length());
        outStream.write(byteStr);
        outStream.flush();
    }

    private void write(HashMap<String, MyStoreable> dataToWrite) throws Exception {
        clear();
        for (Map.Entry<String, MyStoreable> entry : dataToWrite.entrySet()) {
            String key = entry.getKey();
            MyStoreable value = entry.getValue();
            int hashcode = key.hashCode();
            int ndirectory = hashcode % 16;
            int nfile = hashcode / 16 % 16;
            File directory = new File(tablePath.toString() + File.separator + ndirectory + ".dir");
            if (!directory.exists()) {
                if (!directory.mkdir()) {
                    throw new Exception("Error in writing");
                }
            }
            File file = new File(directory.getCanonicalPath() + File.separator
                    + nfile + ".dat");
            if (!file.exists()) {
                if (!file.createNewFile()) {
                    throw new Exception("Error in writing");
                }
            }
            try (DataOutputStream stream = new DataOutputStream(new FileOutputStream(file))) {
                writeUtil(stream, key);
                JSONArray inputValue = new JSONArray(value.dataToList());
                writeUtil(stream, inputValue.toString());
            } catch (Exception e) {
                throw new Exception("Error in writing");
            }
        }
    }

    private void clear() {
        File[] files = tablePath.toFile().listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                Executor.delete(file);
            }
        }
    }

    private HashMap<String, MyStoreable> getRelevantData() {
        HashMap<String, MyStoreable> relevantData = new HashMap<>();
        relevantData.putAll(data);
        Set<String> keySet = dataChanges.get().keySet();
        for (String key : keySet) {
            if (dataChanges.get().get(key) == null) {
                relevantData.remove(key);
            } else {
                relevantData.put(key, (MyStoreable) dataChanges.get().get(key));
            }
        }
        return relevantData;
    }

    Class[] getValueType() {
        return valueType;
    }

    private void writeSignatures() throws IOException {
        File sigFile = new File(tablePath.toFile().getCanonicalPath() + File.separator + "signature.tsv");
        if (sigFile.exists()) {
            Executor.delete(sigFile);
        }
        if (!sigFile.createNewFile()) {
            throw new IOException("Cannot create signatures file");
        }
        FileWriter writer = new FileWriter(sigFile);
        for (int i = 0; i < valueType.length; ++i) {
            writer.write(valueType[i].getSimpleName() + " ");
        }
        writer.close();
    }


    private Path tablePath;

    private HashMap<String, MyStoreable> data;

    private String name;

    private Class[] valueType;

    private final ThreadLocal<HashMap<String, Storeable>> dataChanges =
            ThreadLocal.withInitial(() -> new HashMap<>());

    private ReadWriteLock tableLocker = new ReentrantReadWriteLock(true);

}
