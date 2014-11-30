package ru.fizteh.fivt.students.SergeyAksenov.Storeable;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;

import java.util.ArrayList;
import java.util.List;

public class MyStoreable implements Storeable {

    public MyStoreable(Class[] types) {
        typeList = types;
        dataList = new Object[types.length];
    }

    public MyStoreable(List<Class<?>> listOfTypes) {
        int len = listOfTypes.size();
        typeList = new Class[len];
        dataList = new Object[len];
        for (int i = 0; i < len; i++) {
            typeList[i] = listOfTypes.get(i);
        }
    }

    public void setColumnAt(int columnIndex, Object value)
            throws ColumnFormatException, IndexOutOfBoundsException {
        if (columnIndex < 0 || columnIndex >= typeList.length) {
            throw new IndexOutOfBoundsException("Storeable: invalid index");
        }
        if (value == null) {
            dataList[columnIndex] = null;
            return;
        }
        if (value.getClass() != typeList[columnIndex]) {
            throw new ColumnFormatException("Storeable: invalid type");
        }
        dataList[columnIndex] = value;
    }

    public Object getColumnAt(int columnIndex)
            throws IndexOutOfBoundsException {
        if (columnIndex < 0 || columnIndex >= typeList.length) {
            throw new IndexOutOfBoundsException("Storeable: invalid index");
        }
        return dataList[columnIndex];
    }

    Class<?> getColumnType(int columnIndex) {
        return typeList[columnIndex];
    }

    public Integer getIntAt(int columnIndex)
            throws ColumnFormatException, IndexOutOfBoundsException {
        if (columnIndex < 0 || columnIndex >= typeList.length) {
            throw new IndexOutOfBoundsException("Storeable: invalid index");
        }
        if (!(dataList[columnIndex] instanceof Integer)) {
            throw new ColumnFormatException("Storeable: cannot cast to Integer");
        }
        return (Integer) dataList[columnIndex];
    }

    public Long getLongAt(int columnIndex)
            throws ColumnFormatException, IndexOutOfBoundsException {
        if (columnIndex < 0 || columnIndex >= typeList.length) {
            throw new IndexOutOfBoundsException("Storeable: invalid index");
        }
        if (!(dataList[columnIndex] instanceof Long)) {
            throw new ColumnFormatException("Storeable: cannot cast to Long");
        }
        return (Long) dataList[columnIndex];
    }

    public Byte getByteAt(int columnIndex)
            throws ColumnFormatException, IndexOutOfBoundsException {
        if (columnIndex < 0 || columnIndex >= typeList.length) {
            throw new IndexOutOfBoundsException("Storeable: invalid index");
        }
        if (!(dataList[columnIndex] instanceof Byte)) {
            throw new ColumnFormatException("Storeable: cannot cast to Byte");
        }
        return (Byte) dataList[columnIndex];
    }

    public Float getFloatAt(int columnIndex)
            throws ColumnFormatException, IndexOutOfBoundsException {
        if (columnIndex < 0 || columnIndex >= typeList.length) {
            throw new IndexOutOfBoundsException("Storeable: invalid index");
        }
        if (!(dataList[columnIndex] instanceof Float)) {
            throw new ColumnFormatException("Storeable: cannot cast to Float");
        }
        return (Float) dataList[columnIndex];
    }

    public Double getDoubleAt(int columnIndex)
            throws ColumnFormatException, IndexOutOfBoundsException {
        if (columnIndex < 0 || columnIndex >= typeList.length) {
            throw new IndexOutOfBoundsException("Storeable: invalid index");
        }
        if (!(dataList[columnIndex] instanceof Double)) {
            throw new ColumnFormatException("Storeable: cannot cast to Double");
        }
        return (Double) dataList[columnIndex];
    }

    public Boolean getBooleanAt(int columnIndex)
            throws ColumnFormatException, IndexOutOfBoundsException {
        if (columnIndex < 0 || columnIndex >= typeList.length) {
            throw new IndexOutOfBoundsException("Storeable: invalid index");
        }
        if (!(dataList[columnIndex] instanceof Boolean)) {
            throw new ColumnFormatException("Storeable: cannot cast to Boolean");
        }
        return (Boolean) dataList[columnIndex];
    }

    public String getStringAt(int columnIndex)
            throws ColumnFormatException, IndexOutOfBoundsException {
        if (columnIndex < 0 || columnIndex >= typeList.length) {
            throw new IndexOutOfBoundsException("Storeable: invalid index");
        }
        if (!(dataList[columnIndex] instanceof Integer)) {
            throw new ColumnFormatException("Storeable: cannot cast to String");
        }
        return (String) dataList[columnIndex];
    }

    public List<Object> dataToList() {
        List<Object> result = new ArrayList<>(dataList.length);
        for (Object it : dataList) {
            result.add(it);
        }
        return result;
    }

    public void print() {
        System.out.print("[");
        for (int i = 0; i < dataList.length; ++i) {
            System.out.print(dataList[i]);
            if (i < dataList.length - 1) {
                System.out.print(", ");
            }
        }
        System.out.print("]");
        System.out.println();
    }

    private Object[] dataList;

    private Class[] typeList;

}
