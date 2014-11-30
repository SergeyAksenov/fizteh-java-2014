package ru.fizteh.fivt.students.standy66_new.server.commands;

import java.io.PrintWriter;

/**
 * @author andrew
 *         Created by andrew on 30.11.14.
 */
public class StartHttpCommand extends ServerContextualCommand {
    protected StartHttpCommand(PrintWriter outputWriter, ServerContext context) {
        super(outputWriter, (x -> x == 1), context);
    }

    @Override
    public void execute(String... arguments) throws Exception {
        super.execute(arguments);
        getContext().getHttpServer().start();
    }
}
