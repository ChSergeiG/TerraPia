package ru.chsergeyg.terrapia.arduino.compiler;


import ru.chsergeyg.terrapia.arduino.compiler.utils.TaskUtils;

public class Main {

    public static void main(String[] args) {
        TaskUtils.listArduinoLibraries();
        TaskUtils.listVariants();
        TaskUtils.listCores();
        TaskUtils.listProjectLibraries();

        TaskUtils.build();

    }
}
