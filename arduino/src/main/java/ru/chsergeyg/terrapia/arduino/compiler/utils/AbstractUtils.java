package ru.chsergeyg.terrapia.arduino.compiler.utils;

import java.util.ArrayList;
import java.util.List;

abstract class AbstractUtils {

    static List<String> extensions;

    static String arduinoFCpu;
    static String arduinoMcu;
    static String arduinoComPort;
    static String sketchMainFile;
    static String arduinoBurnRate;

    static {
        extensions = new ArrayList<>();
        extensions.add("c");
        extensions.add("cpp");
        extensions.add("ino");

        arduinoBurnRate = PropertyReader.readString("dev.burnRate");
        arduinoComPort = PropertyReader.readString("dev.arduinoComPort");
        arduinoFCpu = PropertyReader.readString("cpu.clock");
        arduinoMcu = PropertyReader.readString("cpu.name");
        sketchMainFile = PropertyReader.readString("file.sketchMainFile");
    }
}
