package ru.fizteh.fivt.students.akhtyamovpavel.remotedatabase.commands.table;

import ru.fizteh.fivt.students.akhtyamovpavel.remotedatabase.DataBaseTableProvider;
import ru.fizteh.fivt.students.akhtyamovpavel.remotedatabase.commands.Command;

/**
 * Created by user1 on 07.10.2014.
 */
public abstract class TableCommand implements Command {
    public static final boolean EXIST = true;
    public static final boolean NON_EXIST = false;
    protected DataBaseTableProvider shell;

    public TableCommand(DataBaseTableProvider shell) {
        this.shell = shell;
    }


    public DataBaseTableProvider getShell() {
        return shell;
    }
}
