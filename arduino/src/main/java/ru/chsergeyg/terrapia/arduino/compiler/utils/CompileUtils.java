package ru.chsergeyg.terrapia.arduino.compiler.utils;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.chsergeyg.terrapia.arduino.compiler.exceptions.UtilException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static ru.chsergeyg.terrapia.arduino.compiler.utils.ExecUtils.execCmd;

public class CompileUtils extends AbstractUtils {

    private static final Logger logger = LoggerFactory.getLogger(CompileUtils.class);

    public static File buildFile(File file, List<File> libraries) {
        File result = Paths.get(PathsEnum.PROJECT_COMPILE_DIR.getFile().getAbsolutePath(), file.getName() + ".o").toFile();
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
        List<String> commands = new ArrayList<>();
        commands.add(PathsEnum.ARDUINO_DIR.getFile().getAbsolutePath() + "/hardware/tools/avr/bin/" + compiler);
        commands.add(isCpp ? "-x c++" : "");
        commands.add("-c");
        commands.add("-g");
        commands.add("-Os");
        commands.add("-Wall");
        commands.add(isCpp ? "-fno-exceptions" : "");
        commands.add("-ffunction-sections");
        commands.add("-fdata-sections");
        commands.add("-mmcu=" + cpuName);
        commands.add("-DF_CPU=" + cpuClock);
        commands.add("-MMD");
        commands.add("-DUSB_VID=null");
        commands.add("-DUSB_PID=null");
        commands.add("-DARDUINO=105");
        commands.add("-I" + PathsEnum.ARDUINO_SELECTED_CORE_DIR.getFile().getAbsolutePath());
        commands.add("-I" + PathsEnum.ARDUINO_SELECTED_VARIANT_DIR.getFile().getAbsolutePath());
        libraries.forEach(lib -> commands.add("-I" + lib.getAbsolutePath()));
        commands.add(file.getAbsolutePath());
        commands.add("-o" + result.getName());
        execCmd(commands);
        return result;
    }

    public static void link(List<File> objectFiles) throws IOException {
        List<String> commands = new ArrayList<>();
        commands.add(PathsEnum.ARDUINO_BINS_DIR.getFile().getAbsolutePath() + "avr-gcc");
        commands.add("-Os");
        commands.add("-Wl,--gc-sections");
        commands.add("-mmcu=" + cpuName);
        commands.add("-o" + PathsEnum.PROJECT_COMPILE_ELF.getFile().getAbsolutePath());
        objectFiles.forEach(of -> commands.add(of.getAbsolutePath()));
        commands.add(PathsEnum.PROJECT_COMPILE_CORE_A_FILE.getFile().getAbsolutePath());
        commands.add("-L" + PathsEnum.PROJECT_COMPILE_DIR.getFile());
        commands.add("-lm");
        execCmd(commands);
    }

    public static void objCopy() throws IOException {
        List<String> commands = new ArrayList<>();
        commands.add(PathsEnum.ARDUINO_BINS_DIR.getFile().getAbsolutePath() + "avr-objcopy");
        commands.add("-O");
        commands.add("ihex");
        commands.add("-j");
        commands.add(".eeprom");
        commands.add("--set-section-flags=.eeprom=alloc,load");
        commands.add("--no-change-warnings");
        commands.add("--change-section-lma");
        commands.add(".eeprom=0");
        commands.add(PathsEnum.PROJECT_COMPILE_ELF.getFile().getAbsolutePath());
        commands.add(PathsEnum.PROJECT_COMPILE_EEP.getFile().getAbsolutePath());
        execCmd(commands);

        commands = new ArrayList<>();
        commands.add(PathsEnum.ARDUINO_BINS_DIR.getFile().getAbsolutePath() + "avr-objcopy");
        commands.add("-O");
        commands.add("ihex");
        commands.add("-R");
        commands.add(".eeprom");
        commands.add(PathsEnum.PROJECT_COMPILE_ELF.getFile().getAbsolutePath());
        commands.add(PathsEnum.PROJECT_COMPILE_EEP.getFile().getAbsolutePath());
        execCmd(commands);
    }
}
