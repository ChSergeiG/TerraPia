package ru.chsergeyg.executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.chsergeyg.executor.exceptions.CliException;
import ru.chsergeyg.executor.exceptions.ExecException;
import ru.chsergeyg.terrapia.arduino.compiler.exceptions.UtilException;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

public class Cli {

    private Logger logger = LoggerFactory.getLogger(Cli.class);

    private Deque<String> deque;

    Cli(String cliCommand) {
        deque = new ArrayDeque<>();
        deque.add(cliCommand);
    }

    public Cli o(String cliKey) {
        if (deque == null) {
            throw new CliException("Command deque is null");
        }
        deque.add(cliKey);
        return this;
    }

    public Cli o(List<String> cliKeys) {
        deque.addAll(cliKeys);
        return this;
    }

    public String getCli() {
        return deque.stream().reduce("", (a, b) -> a.concat(b).concat(" ")).trim();
    }

    public int execute(Path baseDirectory) {
        Process exec = null;
        String command = getCli();
        File directory = baseDirectory.toFile();
        logger.debug("exec: " + command);
        logger.debug("dir: " + directory.getAbsolutePath());
        try {
            exec = Runtime.getRuntime().exec(command, new String[0], directory);
        } catch (IOException e) {
            logger.error(e.getMessage());
            throw new UtilException(e);
        }
        BufferedReader standardReader = new BufferedReader(new InputStreamReader(exec.getInputStream()));
        BufferedReader errorReader = new BufferedReader(new InputStreamReader(exec.getErrorStream()));
        Thread stdo = new Thread(() -> standardReader.lines().forEach(logger::debug));
        Thread stde = new Thread(() -> errorReader.lines().forEach(logger::error));
        stdo.setName("stdo");
        stde.setName("stde");
        stdo.start();
        stde.start();
        try {
            exec.waitFor();
        } catch (InterruptedException ex) {
            throw new UtilException(ex);
        }
        if (exec.exitValue() != 0) {
            String message = String.format("Command execution failed (Exit code: %s)\ncmd: %s", exec.exitValue(), command);
            logger.error(message);
            throw new ExecException(message);
        }
        stdo.interrupt();
        stde.interrupt();
        return exec.exitValue();
    }
}
