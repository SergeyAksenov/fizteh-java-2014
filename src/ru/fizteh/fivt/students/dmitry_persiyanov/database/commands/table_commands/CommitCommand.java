package ru.fizteh.fivt.students.dmitry_persiyanov.database.commands.table_commands;

import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.dmitry_persiyanov.database.commands.DbCommand;
import ru.fizteh.fivt.students.dmitry_persiyanov.database.exceptions.TableIsNotChosenException;

import java.io.IOException;
import java.io.PrintStream;

/**
 * Created by drack3800 on 13.11.2014.
 */
public class CommitCommand extends DbCommand {
    public CommitCommand(final String[] args, final TableProvider tableProvider, final Table table) {
        super("commit", 0, args, tableProvider, table);
    }

    @Override
    protected void execute(final PrintStream out) throws TableIsNotChosenException, IOException {
        if (currentTable == null) {
            throw new TableIsNotChosenException();
        } else {
            out.println(currentTable.commit());
        }
    }
}
