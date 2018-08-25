package ru.chsergeyg.terrapia.arduino.compiler.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.chsergeyg.terrapia.arduino.compiler.exceptions.UtilException;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class ExecUtils extends AbstractUtils {

    private static final Logger logger = LoggerFactory.getLogger(ExecUtils.class);

    static void runCmd(String cmd, File baseDir) {
        Process exec = null;
        logger.debug(cmd);
        logger.debug("dir: " + baseDir);
        try {
            exec = Runtime.getRuntime().exec(cmd, new String[0], baseDir);
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
            String message = String.format("Command execution failed (Exit code: %s)\ncmd: %s", exec.exitValue(), cmd);
            logger.error(message);
            throw new UtilException(message);
        }
        stdo.interrupt();
        stde.interrupt();
    }

    static void execCmd(List<String> commands) {
        runCmd(
                commands.stream().reduce("", (a, b) -> a.concat(b).concat(" ")),
                PathsEnum.PROJECT_COMPILE_DIR.getFile()
        );
    }


}
