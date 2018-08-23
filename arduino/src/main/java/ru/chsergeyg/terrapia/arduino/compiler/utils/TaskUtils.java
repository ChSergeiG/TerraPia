package ru.chsergeyg.terrapia.arduino.compiler.utils;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ru.chsergeyg.terrapia.arduino.compiler.utils.CompileUtils.buildFile;
import static ru.chsergeyg.terrapia.arduino.compiler.utils.FileUtils.enumIncludes;
import static ru.chsergeyg.terrapia.arduino.compiler.utils.FileUtils.enumSourceFiles;

public class TaskUtils extends AbstractUtils {

   static Logger logger = LoggerFactory.getLogger(TaskUtils.class);

    public static void init() {
        clean();
        PathsEnum.PROJECT_DIR.getFile().mkdirs();
        PathsEnum.PROJECT_COMPILE_DIR.getFile().mkdirs();
    }


    public static void build() {
        init();
        List<File> sketchFiles = new ArrayList<>();
        List<File> arduinoFiles = new ArrayList<>();
        List<File> sketchObjectFiles = new ArrayList<>();
        List<File> arduinoObjectFiles = new ArrayList<>();
        List<File> libraries = new ArrayList<>();

        enumSourceFiles(sketchFiles, PathsEnum.PROJECT_DIR.getFile());
        logger.debug(sketchFiles.toString());
        enumSourceFiles(arduinoFiles, PathsEnum.ARDUINO_SELECTED_CORE_DIR.getFile());
        logger.debug(arduinoFiles.toString());

        List<String> includes = enumIncludes(PathsEnum.PROJECT_INO_FILE.getFile());
        logger.debug(includes.toString());

        includes.forEach(i -> {
            File libDir = new File(PathsEnum.ARDUINO_LIBRARIES_DIR.getFile().getAbsoluteFile(), i);
            if (libDir.exists() && libDir.isDirectory()) {
                libraries.add(libDir);
                enumSourceFiles(arduinoFiles, libDir);
            }
            libDir = new File(PathsEnum.PROJECT_LIBRARIES_DIR.getFile().getAbsoluteFile(), i);
            if (libDir.exists() && libDir.isDirectory()) {
                libraries.add(libDir);
                enumSourceFiles(arduinoFiles, libDir);
            }
        });
        logger.info("Compiling source files");

        sketchFiles.stream().map(e -> buildFile(e,libraries)).collect(Collectors.toList());
        /**



         println "Compiling source files"
         sketch_files.each {
         sketch_object_files << build_file(it, libraries)
         }

         arduino_files.each {
         arduino_object_files << build_file(it, libraries)
         }

         def archive_index = extract_archive_index()
         if (!can_skip_archive(archive_index, arduino_object_files)) {
         println "Archiving object files"
         archive_index.clear()
         arduino_object_files.each {
         archive_object_file(it)
         archive_index[it.name] = it.lastModified()
         }
         save_archive_index(archive_index)
         }

         println "Linking"
         link(sketch_object_files)

         objcopy()

         */
    }

    public static void upload() {
        /**
         task uploadIno(dependsOn: build) << {
         def cmd = ["$arduinoDir/hardware/tools/avr/bin/avrdude",
         "-C$arduinoDir/hardware/tools/avr/etc/avrdude.conf",
         "-v",
         "-p$cpuName",
         "-carduino",
         "-P$arduinoComPort",
         "-b115200",
         "-D",
         "-Uflash:w:$buildDir/${sketchMainFile}.hex:i"]
         exec_cmd(cmd)
         }

         */
    }

    public static void listVariants() {
//        task listVariants <<{
//            printSubDirs(new File(arduinoVariantsDir))
//        }
    }

    public static void listCores() {
//            task listCores <<{
//                printSubDirs(new File(arduinoCoresDir))
//            }
    }

    public static void listArduinoLibraries() {
//                task listArduinoLibraries <<{
//                    printSubDirs(new File(librariesDir))
//                }
    }

    public static void listProjectLibraries() {
//        task listProjectLibraries <<{
//            printSubDirs(new File(projectLibrariesDir))
//        }
    }

    private static void clean() {
        PathsEnum.PROJECT_COMPILE_DIR.getFile().delete();
    }
}