package ru.chsergeyg.terrapia.arduino.compiler.utils;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.chsergeyg.terrapia.arduino.compiler.exceptions.UtilException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ru.chsergeyg.terrapia.arduino.compiler.utils.ExecUtils.execCmd;

public class FileUtils extends AbstractUtils {

    private static final Logger logger = LoggerFactory.getLogger(FileUtils.class);

    private static final Pattern HEADER_PATTERN = Pattern.compile("^\\s*#include\\s+[<\"](\\S+)[\">]");

    static Map<String, Long> extractArchiveIndex() {
        Map<String, Long> result = new HashMap<>();
        if (PathsEnum.PROJECT_INDEX_FILE.getFile().exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(PathsEnum.PROJECT_INDEX_FILE.getFile()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] elements = line.split("=", 2);
                    result.put(elements[0], Long.parseLong(elements[1]));
                }
            } catch (IOException e) {
                logger.error(e.getMessage());
                throw new UtilException(e);
            }
        }
        return result;
    }

    static void saveArchiveIndex(Map<String, Long> index) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(PathsEnum.PROJECT_INDEX_FILE.getFile()))) {
            index.entrySet().stream().forEach(e -> {
                try {
                    writer.write(e.getKey() + "=" + e.getValue());
                    writer.newLine();
                } catch (IOException ex) {
                    logger.error(ex.getMessage());
                    throw new UtilException(ex);
                }
            });
            writer.flush();
        } catch (IOException e) {
            logger.error(e.getMessage());
            throw new UtilException(e);
        }
    }


    static void archiveObjectFile(File objectFile) throws IOException {
        List<String> params = new ArrayList<>();
        params.add(PathsEnum.ARDUINO_BINS_DIR.getFile().getAbsolutePath() + "avr-ar");
        params.add("rcs");
        params.add(PathsEnum.PROJECT_COMPILE_CORE_A_FILE.getFile().getAbsolutePath());
        params.add(objectFile.getAbsolutePath());
        execCmd(params);
    }

    public static void enumSourceFiles(List<File> fileList, File directory) {
        for (File file : directory.listFiles()) {
            if (file.isDirectory() &&
                    !file.getAbsolutePath().contains("example") &&
                    !file.getAbsolutePath().contains(".svn")) {
                enumSourceFiles(fileList, file);
            } else {
                if (extensions.contains(FilenameUtils.getExtension(file.getName()))) {
                    fileList.add(file);
                }
            }
        }
    }

    public static boolean canSkipArchive(Map<String, Long> index, List<File> arduinoObjectFiles) {
        AtomicBoolean result = new AtomicBoolean(true);
        arduinoObjectFiles.forEach(f -> {
            if (!index.containsKey(f.getName()) || f.lastModified() > index.get(f.getName())) {
                result.set(false);
            }
        });
        return result.get();
    }

    public static List<String> enumIncludes(File file) {
        List<String> result = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Matcher matcher = HEADER_PATTERN.matcher(line);
                if (matcher.matches()) {
                    String header = matcher.group(1);
                    if (header.endsWith(".h")) {
                        header = header.substring(0, header.length() - 2);
                        result.add(header);
                    }
                }
            }
        } catch (IOException ex) {
            logger.error(ex.getMessage());
            throw new UtilException(ex);
        }
        return result;
    }
/**
 def printSubDirs(File dir) {
 dir.listFiles().each {
 if (it.isDirectory() && !it.name.startsWith(".")) println(it.name)
 }
 }
 */
}
