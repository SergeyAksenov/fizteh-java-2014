package ru.fizteh.fivt.students.SergeyAksenov.Parallel;


import java.util.HashMap;

public class ParallelMain {
    private static HashMap<String, Command> initHashMap() {
        HashMap<String, Command> commands = new HashMap<>();
        commands.put("get", new GetCommand());
        commands.put("put", new PutCommand());
        commands.put("remove", new RemoveCommand());
        commands.put("list", new ListCommand());
        commands.put("exit", new ExitCommand());
        commands.put("drop", new DropCommand());
        commands.put("create", new CreateCommand());
        commands.put("show", new ShowCommand());
        commands.put("use", new UseCommand());
        commands.put("commit", new CommitCommand());
        commands.put("rollback", new RollbackCommand());
        return commands;
    }

    public static void main(String[] args) {
        HashMap<String, Command> commands = initHashMap();
        ParallelTableProviderFactory factory = new ParallelTableProviderFactory();
        try {
            String s = System.getProperty("fizteh.db.dir");
            ParallelTableProvider tableProvider = factory.create(System.getProperty("fizteh.db.dir"));
            Executor.interactiveMode(commands, tableProvider);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(-1);
        }
    }
}
