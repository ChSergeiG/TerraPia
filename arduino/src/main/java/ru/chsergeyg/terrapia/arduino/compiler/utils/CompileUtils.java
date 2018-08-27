package ru.chsergeyg.terrapia.arduino.compiler.utils;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.chsergeyg.executor.Executor;
import ru.chsergeyg.terrapia.arduino.compiler.exceptions.UtilException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class CompileUtils extends AbstractUtils {

    private static final Logger logger = LoggerFactory.getLogger(CompileUtils.class);

    public static File buildFile(File file, List<File> libraries) {
        File result = Paths.get(PathsEnum.PROJECT_BUILD_DIR.toString(), file.getName() + ".o").toFile();
        if (file.lastModified() < result.lastModified()) {
            return result;
        }
        try {
            result.createNewFile();
        } catch (IOException e) {
            logger.error(e.getMessage());
            throw new UtilException(e);
        }
        logger.info("Compiling " + file.getName());
        boolean isCpp = FilenameUtils.isExtension(file.getName(), "cpp") ||
                FilenameUtils.isExtension(file.getName(), "ino") ||
                FilenameUtils.isExtension(file.getName(), "h");
        String compiler = isCpp ? "avr-g++" : "avr-gcc";
        Executor.getInstance()
                .a(PathsEnum.ARDUINO_BINARIES_DIR.toString(), compiler)
                .o(isCpp ? "-x c++" : "")
                .o("-c")
                .o("-g")
                .o("-Os")
                .o("-Wall")
                .o(isCpp ? "-fno-exceptions" : "")
                .o("-ffunction-sections")
                .o("-fdata-sections")
                .o("-mmcu=" + arduinoMcu)
                .o("-DF_CPU=" + arduinoFCpu)
                .o("-MMD")
                .o("-DUSB_VID=null")
                .o("-DUSB_PID=null")
                .o("-DARDUINO=105")
                .o("-I" + PathsEnum.ARDUINO_SELECTED_CORE_DIR.toString())
                .o("-I" + PathsEnum.ARDUINO_SELECTED_VARIANT_DIR.toString())
                .o(libraries.stream().map(lib -> "-I" + lib.getAbsolutePath()).collect(Collectors.toList()))
                .o(file.getAbsolutePath())
                .o("-o" + result.getName())
                .execute(PathsEnum.PROJECT_BUILD_DIR.getPath());
        return result;
    }

    public static void link(List<File> objectFiles) {
        Executor.getInstance()
                .a(PathsEnum.ARDUINO_BINARIES_DIR.toString(), "avr-gcc")
                .o("-Os")
                .o("-Wl,--gc-sections")
                .o("-mmcu=" + arduinoMcu)
                .o("-o" + PathsEnum.PROJECT_COMPILE_ELF.toString())
                .o(objectFiles.stream().map(File::getAbsolutePath).collect(Collectors.toList()))
                .o(PathsEnum.PROJECT_BUILD_CORE_A_FILE.toString())
                .o("-L" + PathsEnum.PROJECT_BUILD_DIR.getFile())
                .o("-lm")
                .execute(PathsEnum.PROJECT_BUILD_DIR.getPath());
    }

    public static void objCopy() {
        Executor.getInstance()
                .a(PathsEnum.ARDUINO_BINARIES_DIR.toString(), "avr-objcopy")
                .o("-O")
                .o("ihex")
                .o("-j")
                .o(".eeprom")
                .o("--set-section-flags=.eeprom=alloc,load")
                .o("--no-change-warnings")
                .o("--change-section-lma")
                .o(".eeprom=0")
                .o(PathsEnum.PROJECT_COMPILE_ELF.toString())
                .o(PathsEnum.PROJECT_COMPILE_EEP.toString())
                .execute(PathsEnum.PROJECT_BUILD_DIR.getPath());
        Executor.getInstance()
                .a(PathsEnum.ARDUINO_BINARIES_DIR.toString(), "avr-objcopy")
                .o("-O")
                .o("ihex")
                .o("-R")
                .o(".eeprom")
                .o(PathsEnum.PROJECT_COMPILE_ELF.toString())
                .o(PathsEnum.PROJECT_COMPILE_EEP.toString())
                .execute(PathsEnum.PROJECT_BUILD_DIR.getPath());
    }
}
