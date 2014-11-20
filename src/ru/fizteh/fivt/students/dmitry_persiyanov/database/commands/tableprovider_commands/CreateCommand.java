package ru.fizteh.fivt.students.dmitry_persiyanov.database.commands.tableprovider_commands;


import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.dmitry_persiyanov.database.commands.DbCommand;
import ru.fizteh.fivt.students.dmitry_persiyanov.database.db_table_provider.utils.TypeStringTranslator;

import java.io.IOException;
import java.io.PrintStream;
import java.util.LinkedList;
import java.util.List;

public class CreateCommand extends DbCommand {
    public CreateCommand(final String[] args, final TableProvider tableProvider) {
        super("create", 2, args, tableProvider);
    }

    @Override
    protected void execute(final PrintStream out) throws IOException {
        String tableToCreate = args[0];
        try {
            if (tableProvider.getTable(tableToCreate) != null) {
                out.println(tableToCreate + "exists");
            } else {
                String typesTuple = args[1];
                String[] types = typesTuple.replace("(", "").replace(")", "").split("\\s+");
                List<Class<?>> signature = new LinkedList<>();
                for (String type : types) {
                    signature.add(TypeStringTranslator.getTypeByStringName(type));
                }
                tableProvider.createTable(tableToCreate, signature);
                out.println("created");
            }
        } catch (IllegalArgumentException e) {
            out.println(e.getMessage());
        }
    }
}
