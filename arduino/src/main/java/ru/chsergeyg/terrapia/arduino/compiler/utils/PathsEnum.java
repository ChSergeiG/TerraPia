package ru.chsergeyg.terrapia.arduino.compiler.utils;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public enum PathsEnum {

    // $/
    ARDUINO_DIR(PropertyReader.readString("file.arduinoDir")),
    // $/hardware/arduino/avr/cores/
    ARDUINO_CORES_DIR(ARDUINO_DIR, PropertyReader.readString("file.arduinoCoresDir")),
    // $/hardware/arduino/avr/cores/arduino
    ARDUINO_SELECTED_CORE_DIR(ARDUINO_CORES_DIR, PropertyReader.readString("file.arduinoCore")),
    // $/hardware/arduino/avr/variants/
    ARDUINO_VARIANTS_DIR(ARDUINO_DIR, PropertyReader.readString("file.arduinoVariantsDir")),
    // $/hardware/arduino/avr/variants/standard/
    ARDUINO_SELECTED_VARIANT_DIR(ARDUINO_VARIANTS_DIR, PropertyReader.readString("file.arduinoVariant")),
    // $/libraries/
    ARDUINO_LIBRARIES_DIR(ARDUINO_DIR, PropertyReader.readString("file.arduinoLibrariesDir")),
    // %/arduino/
    PROJECT_DIR(Paths.get(PropertyReader.readString("file.projectDir"))),
    // %/arduino/libs/
    PROJECT_LIBRARIES_DIR(PROJECT_DIR, PropertyReader.readString("file.projectLibrariesDir")),
    // %/arduino/compile/
    PROJECT_COMPILE_DIR(PROJECT_DIR, PropertyReader.readString("file.projectBuildDir")),
    // %/arduino/compile/archiveindex.dat
    PROJECT_INDEX_FILE(PROJECT_COMPILE_DIR, "archiveindex.dat"),
    // %/arduino/compile/core.a
    PROJECT_COMPILE_CORE_A_FILE(PROJECT_COMPILE_DIR, "core.a"),
    // %/arduino/compile/terr.ino.elf
    PROJECT_COMPILE_ELF(PROJECT_COMPILE_DIR, PropertyReader.readString("file.sketchMainFile") + ".elf"),
    // %/arduino/compile/terr.ino.hex
    PROJECT_COMPILE_HEX(PROJECT_COMPILE_DIR, PropertyReader.readString("file.sketchMainFile") + ".hex"),
    // %/arduino/compile/terr.ino.hex
    PROJECT_COMPILE_EEP(PROJECT_COMPILE_DIR, PropertyReader.readString("file.sketchMainFile") + ".eep"),
    // %/arduino/terr.ino
    PROJECT_INO_FILE(PROJECT_DIR, PropertyReader.readString("file.sketchMainFile")),;

    Path path;

    public Path getPath() {
        return path;
    }

    public File getFile() {
        return path.toFile();
    }


    PathsEnum(String first, String... more) {
        path = Paths.get(first, more);
    }

    PathsEnum(PathsEnum first, String... more) {
        path = Paths.get(first.getFile().getAbsolutePath(), more);
    }

    PathsEnum(Path first, String... more) {
        path = Paths.get(first.toFile().getAbsolutePath(), more);
    }

}
