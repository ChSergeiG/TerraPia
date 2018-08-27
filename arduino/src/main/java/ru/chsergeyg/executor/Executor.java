package ru.chsergeyg.executor;

import java.nio.file.Paths;

public class Executor {

    public static Executor getInstance() {
        return new Executor();
    }

    public Cli a(String cliCommand) {
        return new Cli(cliCommand);
    }

    public Cli a(String path, String... appends) {
        return a(Paths.get(path, appends).toFile().getAbsolutePath());
    }

}
