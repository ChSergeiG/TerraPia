package ru.chsergeyg.terrapia.arduino.compiler.utils;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.chsergeyg.executor.Executor;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static ru.chsergeyg.terrapia.arduino.compiler.utils.CompileUtils.buildFile;
import static ru.chsergeyg.terrapia.arduino.compiler.utils.CompileUtils.link;
import static ru.chsergeyg.terrapia.arduino.compiler.utils.CompileUtils.objCopy;
import static ru.chsergeyg.terrapia.arduino.compiler.utils.FileUtils.archiveObjectFile;
import static ru.chsergeyg.terrapia.arduino.compiler.utils.FileUtils.canSkipArchive;
import static ru.chsergeyg.terrapia.arduino.compiler.utils.FileUtils.enumIncludes;
import static ru.chsergeyg.terrapia.arduino.compiler.utils.FileUtils.enumSourceFiles;
import static ru.chsergeyg.terrapia.arduino.compiler.utils.FileUtils.extractArchiveIndex;
import static ru.chsergeyg.terrapia.arduino.compiler.utils.FileUtils.saveArchiveIndex;

public class TaskUtils extends AbstractUtils {

    static Logger logger = LoggerFactory.getLogger(TaskUtils.class);

    public static void init() {
        clean();
        PathsEnum.PROJECT_DIR.getFile().mkdirs();
        PathsEnum.PROJECT_BUILD_DIR.getFile().mkdirs();
    }

    public static void build() {
        init();
        List<File> sketchFiles = new ArrayList<>();
        List<File> arduinoFiles = new ArrayList<>();
        List<File> sketchObjectFiles = new ArrayList<>();
        List<File> arduinoObjectFiles = new ArrayList<>();
        List<File> libraries = new ArrayList<>();
        List<String> includes = new ArrayList<>();
        enumSourceFiles(sketchFiles, PathsEnum.PROJECT_DIR.getFile());
        logger.debug("sketchFiles:");
        logger.debug(sketchFiles.toString());
        enumSourceFiles(arduinoFiles, PathsEnum.ARDUINO_SELECTED_CORE_DIR.getFile());
        logger.debug("arduinoFiles:");
        logger.debug(arduinoFiles.toString());
        enumIncludes(includes, PathsEnum.PROJECT_INO_FILE.getFile());
        logger.debug("includes:");
        logger.debug(includes.toString());
        includes.forEach(entry -> {
            File libDir = Paths.get(PathsEnum.ARDUINO_LIBRARIES_DIR.toString(), entry).toFile();
            if (libDir.exists() && libDir.isDirectory()) {
                libraries.add(libDir);
                enumSourceFiles(arduinoFiles, libDir);
            }
            libDir = Paths.get(PathsEnum.PROJECT_LIBRARIES_DIR.toString(), entry).toFile();
            if (libDir.exists() && libDir.isDirectory()) {
                libraries.add(libDir);
                enumSourceFiles(arduinoFiles, libDir);
            }
        });
        logger.info("Compiling source files");
        sketchFiles.forEach(file -> sketchObjectFiles.add(buildFile(file, libraries)));
        arduinoFiles.forEach(file -> arduinoObjectFiles.add(buildFile(file, libraries)));
        logger.debug("sketchFiles:");
        logger.debug(sketchFiles.toString());
        logger.debug("arduinoFiles:");
        logger.debug(arduinoFiles.toString());
        Map<String, Long> archiveIndex = extractArchiveIndex();
        if (!canSkipArchive(archiveIndex, arduinoObjectFiles)) {
            logger.info("Archiving object files");
            archiveIndex.clear();
            arduinoObjectFiles.forEach(file -> {
                archiveObjectFile(file);
                archiveIndex.put(file.getName(), file.lastModified());
            });
            saveArchiveIndex(archiveIndex);
        }
        logger.info("Linking");
        link(sketchObjectFiles);
        logger.info("ObjCopy");
        objCopy();
    }

    public static void upload() {
        build();
        Executor.getInstance()
                .a(PathsEnum.ARDUINO_BINARIES_DIR.toString(), "avrdude")
                .o(PathsEnum.ARDUINO_AVRDUDE_CONF.toString())
                .o("-v")
                .o("-p" + arduinoMcu)
                .o("-carduino")
                .o("-P" + arduinoComPort)
                .o("-b" + arduinoBurnRate)
                .o("-D")
                .o("-Uflash:w:" + PathsEnum.PROJECT_COMPILE_HEX.toString() + ":i")
                .execute(PathsEnum.PROJECT_BUILD_DIR.getPath());
    }

    public static void listVariants() {
        logger.info("Variants list:");
        Stream.of(PathsEnum.ARDUINO_VARIANTS_DIR.getFile().listFiles())
                .forEach(file -> logger.info("[variants] " + file.getName()));
    }

    public static void listCores() {
        logger.info("Cores list:");
        Stream.of(PathsEnum.ARDUINO_CORES_DIR.getFile().listFiles())
                .forEach(file -> logger.info("[cores] " + file.getName()));
    }

    public static void listArduinoLibraries() {
        logger.info("Arduino core libraries list:");
        Stream.of(PathsEnum.ARDUINO_LIBRARIES_DIR.getFile().listFiles())
                .forEach(file -> logger.info("[arduino_core_libraries] " + file.getName()));
    }

    public static void listProjectLibraries() {
        logger.info("Project libraries list:");
        File[] files = PathsEnum.PROJECT_LIBRARIES_DIR.getFile().listFiles();
        if (files == null) {
            return;
        }
        Stream.of(files).forEach(file -> logger.info("[proj_libraries] " + file.getName()));
    }

    private static void clean() {
        delDir(PathsEnum.PROJECT_BUILD_DIR.getFile());
    }

    private static void delDir(File dir) {
        if (dir.isFile()) {
            dir.delete();
        }
        File[] files = dir.listFiles();
        if (files == null || files.length == 0) {
            dir.delete();
        } else {
            for (File f : files) {
                delDir(f);
            }
        }
    }
}