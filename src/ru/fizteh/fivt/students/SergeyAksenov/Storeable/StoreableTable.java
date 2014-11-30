package ru.fizteh.fivt.students.SergeyAksenov.Storeable;

import org.json.JSONArray;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class StoreableTable implements Table {

    public StoreableTable(String name, String path, Class[] types) {
        tablePath = Paths.get(path);
        File table = tablePath.toFile();
        changesCounter = 0;
        dataBase = new HashMap<>();
        if (table.exists()) {
            this.name = name;
            valueType = types;
            read();
            return;
        }
        if (!table.mkdir()) {
            System.out.println("Cannot create directory for new table");
            return;
        }
        valueType = types;
        this.name = name;
        try {
            writeSignatures();
        } catch (IOException e) {
            System.out.println("Cannot write signature to file");
        }
    }

    public String getName() {
        return name;
    }

    public int size() {
        return dataBase.size();
    }

    public MyStoreable get(String key)
            throws IllegalArgumentException {
        if (key == null) {
            throw new IllegalArgumentException("get");
        }
        if (dataBase.containsKey(key)) {
            return dataBase.get(key);
        } else {
            return null;
        }
    }

    public MyStoreable put(String key, Storeable value)
            throws IllegalArgumentException {
        if (key == null || value == null) {
            throw new IllegalArgumentException("put");
        }
        MyStoreable ret = null;
        if (dataBase.containsKey(key)) {
            ret = dataBase.get(key);
            dataBase.remove(key);
        }
        dataBase.put(key, (MyStoreable) value);
        changesCounter++;
        return ret;
    }

    public MyStoreable remove(String key)
            throws IllegalArgumentException {
        if (key == null) {
            throw new IllegalArgumentException("remove");
        }
        MyStoreable ret = null;
        if (dataBase.containsKey(key)) {
            ret = dataBase.get(key);
            dataBase.remove(key);
            changesCounter++;
        } else {
            ret = null;
        }
        return ret;
    }

    public List<String> list() {
        Set<String> keySet = dataBase.keySet();
        return new ArrayList<>(keySet);
    }

    public int commit() {
        try {
            clear();
            write();
            int ret = dataBase.size();
            changesCounter = 0;
            return ret;
        } catch (Exception e) {
            System.out.println("Error in writing to file");
            return -1;
        }
    }

    public int rollback() {
        int ret = changesCounter;
        dataBase.clear();
        read();
        changesCounter = 0;
        return ret;
    }

    public int getNumberOfUncommittedChanges() {
        return changesCounter;
    }


    private String readUtil(DataInputStream inStream) throws IOException {
        int length = inStream.readInt();
        byte[] word = new byte[length];
        inStream.readFully(word);
        return new String(word, "UTF-8");
    }

    private void read() {
        File[] directories = tablePath.toFile().listFiles();
        for (File dir : directories) {
            File[] files = dir.listFiles();
            if (files == null) {
                return;
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
                            dataBase.put(key, value);
                        } catch (IOException e) {
                            end = true;
                        }
                    }

                }
            } catch (Exception e) {
                System.out.println("Error in reading");
                return;
            }
        }
    }

    private void writeUtil(DataOutputStream outStream, String str) throws IOException {
        byte[] byteStr = str.getBytes("UTF-8");
        outStream.writeInt(str.length());
        outStream.write(byteStr);
        outStream.flush();
        //   outStream.close();
    }

    private void write() throws Exception {
        for (Map.Entry<String, MyStoreable> entry : dataBase.entrySet()) {
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

    public Class<?> getColumnType(int columnIndex)
            throws IndexOutOfBoundsException {
        if (columnIndex < 0 || columnIndex >= valueType.length) {
            throw new IndexOutOfBoundsException("Table: invalid index");
        }
        return valueType[columnIndex];
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

    public int getColumnsCount() {
        return valueType.length;
    }


    private Path tablePath;

    private HashMap<String, MyStoreable> dataBase;

    private int changesCounter;

    private String name;

    private Class[] valueType;

}
