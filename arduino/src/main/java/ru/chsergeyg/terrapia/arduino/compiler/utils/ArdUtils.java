package ru.chsergeyg.terrapia.arduino.compiler.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BinaryOperator;

public class ArdUtils {

    private static File buildDirectory;

    static {
        buildDirectory = new File("./arduino/build");
        if (!buildDirectory.exists()) {
            buildDirectory.mkdirs();
        }
    }

    public Map<String, Long> extractArchiveIndex() {
        Map<String, Long> result = new HashMap<>();
        File input = new File(buildDirectory, "archiveindex.dat");
        if (input.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(input))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] elements = line.split("=", 2);
                    result.put(elements[0], Long.parseLong(elements[1]));
                }
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
        return result;
    }

    public void saveArchiveIndex(Map<String, Long> index) {
        File output = new File(buildDirectory, "archiveindex.dat");
        if (!output.exists()) {
            buildDirectory.mkdirs();
            try {
                output.createNewFile();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(output))) {
            index.entrySet().stream().forEach(e -> {
                try {
                    writer.write(e.getKey() + "=" + e.getValue());
                    writer.newLine();
                } catch (IOException ex) {
                    System.out.println(ex.getMessage());
                }
            });
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void runCmd(String cmd, File baseDir) throws IOException {
        Process exec = Runtime.getRuntime().exec(cmd, new String[0], baseDir);

        BufferedReader sout = new BufferedReader(new InputStreamReader(exec.getInputStream()));
        BufferedReader serr = new BufferedReader(new InputStreamReader(exec.getErrorStream()));
        try {
            exec.waitFor();
        } catch (InterruptedException ex) {
            System.out.println(ex.getMessage());
        }

        if (exec.exitValue() != 0) {
            BinaryOperator<String> operator = (a, b) -> a.concat(b).concat("\n");
//            System.out.println(
            throw new RuntimeException(
                    String.format(
                            "Command execution failed (Exit code: %s)\n%s\n%s\ncmd: %s",
                            exec.exitValue(),
                            sout.lines().reduce("", operator),
                            serr.lines().reduce("", operator),
                            cmd)
            );
        }
    }

    public void execCmd(List<String> cmds) throws IOException {
        runCmd(
                cmds.stream().reduce("", (a, b) -> a.concat(b).concat(" ")),
                buildDirectory
        );
    }

    public void archiveObjectFile(File objectFile) throws IOException {
        List<String> params = new ArrayList<>();
        params.add(PropertyReader.readValue("file.arduinoDir") + "/hardware/tools/avr/bin/avr-ar");
        params.add("rcs");
        params.add(buildDirectory.getAbsolutePath() + "/core.a");
        params.add(objectFile.getAbsolutePath());
        execCmd(params);
    }

}
