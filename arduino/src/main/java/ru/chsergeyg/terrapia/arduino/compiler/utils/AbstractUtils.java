package ru.chsergeyg.terrapia.arduino.compiler.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

abstract class AbstractUtils {

    static File buildDirectory;
    static List<String> extensions;

    static Long cpuClock;
    static String cpuName;
    static String arduinoComPort;

    static {
        buildDirectory = new File("./arduino/build");
        if (!buildDirectory.exists()) {
            buildDirectory.mkdirs();
        }
        extensions = new ArrayList<>();
        extensions.add("c");
        extensions.add("cpp");
        extensions.add("h");
        extensions.add("ino");

        arduinoComPort = PropertyReader.readString("dev.arduinoComPort");
        cpuName = PropertyReader.readString("cpu.name");
        cpuClock = PropertyReader.readLong("cpu.clock");
    }

}
