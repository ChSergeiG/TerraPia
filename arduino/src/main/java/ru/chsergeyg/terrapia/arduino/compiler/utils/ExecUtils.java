package ru.chsergeyg.terrapia.arduino.compiler.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.chsergeyg.terrapia.arduino.compiler.exceptions.UtilException;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.function.BinaryOperator;

public class ExecUtils extends AbstractUtils {

    private static final Logger logger = LoggerFactory.getLogger(ExecUtils.class);

    static void runCmd(String cmd, File baseDir) {
        Process exec;
        try {
            exec = Runtime.getRuntime().exec(cmd, new String[0], baseDir);
        } catch (IOException e) {
            logger.error(e.getMessage());
            throw new UtilException(e);
        }

        BufferedReader standardReader = new BufferedReader(new InputStreamReader(exec.getInputStream()));
        BufferedReader errorReader = new BufferedReader(new InputStreamReader(exec.getErrorStream()));
        try {
            exec.waitFor();
        } catch (InterruptedException ex) {
            logger.error(ex.getMessage());
            throw new UtilException(ex);
        }

        if (exec.exitValue() != 0) {
            BinaryOperator<String> operator = (a, b) -> a.concat(b).concat("\n");
            throw new UtilException(
                    String.format(
                            "Command execution failed (Exit code: %s)\n%s\n%s\ncmd: %s",
                            exec.exitValue(),
                            standardReader.lines().reduce("", operator),
                            errorReader.lines().reduce("", operator),
                            cmd)
            );
        }
    }

    static void execCmd(List<String> commands) {
        runCmd(
                commands.stream().reduce("", (a, b) -> a.concat(b).concat(" ")),
                buildDirectory
        );
    }


}
