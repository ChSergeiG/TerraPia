package ru.chsergeyg.terrapia.arduino.compiler.utils;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public enum PathsEnum {

    // $/
    ARDUINO_DIR(Paths.get(PropertyReader.readString("file.arduinoDir"))),
    // $/hardware/tools/avr/bin/
    ARDUINO_BINARIES_DIR(ARDUINO_DIR, PropertyReader.readString("file.arduinoBinsDir")),
    // $/hardware/tools/avr/etc/avrdude.conf
    ARDUINO_AVRDUDE_CONF(ARDUINO_DIR, PropertyReader.readString("file.avrdudeConf")),
    // $/hardware/arduino/avr/cores/
    ARDUINO_CORES_DIR(ARDUINO_DIR, PropertyReader.readString("file.arduinoCoresDir")),
    // $/hardware/arduino/avr/cores/arduino
    ARDUINO_SELECTED_CORE_DIR(ARDUINO_CORES_DIR, PropertyReader.readString("file.arduinoCore")),
    // $/libraries/
    ARDUINO_LIBRARIES_DIR(ARDUINO_DIR, PropertyReader.readString("file.arduinoLibrariesDir")),
    // $/hardware/arduino/avr/variants/
    ARDUINO_VARIANTS_DIR(ARDUINO_DIR, PropertyReader.readString("file.arduinoVariantsDir")),
    // $/hardware/arduino/avr/variants/standard/
    ARDUINO_SELECTED_VARIANT_DIR(ARDUINO_VARIANTS_DIR, PropertyReader.readString("file.arduinoVariant")),
    // %/arduino/
    PROJECT_DIR(Paths.get(PropertyReader.readString("file.projectDir"))),
    // %/arduino/compile/
    PROJECT_BUILD_DIR(PROJECT_DIR, PropertyReader.readString("file.projectBuildDir")),
    // %/arduino/compile/core.a
    PROJECT_BUILD_CORE_A_FILE(PROJECT_BUILD_DIR, "core.a"),
    // %/arduino/compile/archiveindex.dat
    PROJECT_ARCHIVE_INDEX_FILE(PROJECT_BUILD_DIR, "archiveindex.dat"),
    // %/arduino/compile/terr.ino.hex
    PROJECT_COMPILE_EEP(PROJECT_BUILD_DIR, PropertyReader.readString("file.sketchMainFile") + ".eep"),
    // %/arduino/compile/terr.ino.elf
    PROJECT_COMPILE_ELF(PROJECT_BUILD_DIR, PropertyReader.readString("file.sketchMainFile") + ".elf"),
    // %/arduino/compile/terr.ino.hex
    PROJECT_COMPILE_HEX(PROJECT_BUILD_DIR, PropertyReader.readString("file.sketchMainFile") + ".hex"),
    // %/arduino/libs/
    PROJECT_LIBRARIES_DIR(PROJECT_DIR, PropertyReader.readString("file.projectLibrariesDir")),
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
        path = Paths.get(first.toString(), more);
    }

    PathsEnum(Path first, String... more) {
        path = Paths.get(first.toFile().getAbsolutePath(), more);
    }

    @Override
    public String toString() {
        return this.getFile().getAbsolutePath();
    }
}
