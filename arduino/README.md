#How to compile arduino sketch via CLI

###### Based on [gradle-arduino](https://github.com/jfklingler/gradle-arduino)

| var                            | example value                                      |
| -----------------------------: | :-------------------------------------------       |
| `SKETCH_MAIN_FILE`             | `terr.ino`, _for example_                          |
| `ARDUINO_MCU`                  | `atmega168`, _for example_                         |
| `ARDUINO_FCPU`                 | `16000000`, _for example_                          |
| `ARDUINO_COMPORT`              | `/dev/ttyUSB0` or `COM1`, _for example_            |
| `ARDUINO_BURNRATE`             | `115200`, _for example_                            |
|                                |                                                    |
| `ARDUINO_DIR`                  | `C:\arduino`                                       |
| `ARDUINO_BINARIES_DIR`         | `$ARDUINO_DIR\hardware\tools\avr\bin\`             |
| `ARDUINO_AVRDUDE_CONF`         | `$ARDUINO_DIR\hardware\tools\avr\etc\avrdude.conf` |
| `ARDUINO_CORES_DIR`            | `$ARDUINO_DIR\hardware\arduino\avr\cores\`         |
| `ARDUINO_CORE_SELECTED_DIR`    | `$ARDUINO_CORES_DIR\arduino\`, _for example_       |
| `ARDUINO_LIBRARIES_DIR`        | `$ARDUINO_DIR\libraries`                           |
| `ARDUINO_VARIANTS_DIR`         | `$ARDUINO_DIR\hardware\arduino\avr\variants`       |
| `ARDUINO_VARIANT_SELECTED_DIR` | `ARDUINO_VARIANTS_DIR\standard\`                   |
| ``                             |                                                    |
| `PROJECT_DIR`                  | `~\$PROJECT\arduino`                               |
| `PROJECT_BUILD_DIR`            | `$PROJECT_DIR\build`                               |
| `PROJECT_BUILD_CORE_A`         | `$PROJECT_BUILD_DIR\core.a`                        |
| `PROJECT_ARCHIVE_INDEX_FILE`   | `$PROJECT_BUILD_DIR\archiveindex.dat`              |
| `PROJECT_EEP_FILE`             | `$PROJECT_BUILD_DIR\$SKETCH_MAIN_FILE.eep`         |
| `PROJECT_ELF_FILE`             | `$PROJECT_BUILD_DIR\$SKETCH_MAIN_FILE.elf`         |
| `PROJECT_HEX_FILE`             | `$PROJECT_BUILD_DIR\$SKETCH_MAIN_FILE.hex`         |
| `PROJECT_LIBRARIES_DIR`        | `$BUILD_DIR\..\..\libraries`                       |
                                                                                      
#### Explanation

###### Methods:
1) `enumSourceFiles(List<File> list, File directory)`. For each `*.ino`, `*.cpp` and`*.c` in `directory` and all subdirectories (exclude `/examples` and `/.svn`) - and place it into `list`
2) `enumIncludes(List<File> list)`. Get all `#include ...` parameters from files in `list` and return it as `includes` list
3) `buildFile(File target, List<File> libs)`. Return compiled file link.
	- for *.cpp and *.ino files execute
    -     $ARDUINO_BINARIES_DIR\avr-g++ -x c++ -c -g -Os -Wall -fno-exceptions -ffunction-sections -fdata-sections -mmcu=$ARDUINO_MCU -DF_CPU=$ARDUINO_FCPU -MMD -DUSB_VID=null -DUSB_PID=null -DARDUINO=105 -I$ARDUINO_CORE_SELECTED_DIR -I$ARDUINO_VARIANT_SELECTED_DIR -I{every entry from $libs} $target -o$target.o
	- for other files execute
    -     $ARDUINO_BINARIES_DIR\avr-gcc -c -g -Os -Wall -ffunction-sections -fdata-sections -mmcu=$ARDUINO_MCU -DF_CPU=$ARDUINO_FCPU -MMD -DUSB_VID=null -DUSB_PID=null -DARDUINO=105 -I$ARDUINO_CORE_SELECTED_DIR -I$ARDUINO_VARIANT_SELECTED_DIR -I{every entry from $libs} $target -o$target.o
4) `extractArchiveIndex()`. If exists file `$PROJECT_ARCHIVE_INDEX_FILE` - return it consistency as LinkedHashMap<String, String>. If not exists - return empty map
5) `canSkipArchive(Map<String, String> index, List<File> files)`. Return boolean - can we skip reindex `$files` according `$index`?
6) `archiveObjectFile(File file)`. Execute
	-     $ARDUINO_BINARIES_DIR\avr-ar rcs $PROJECT_BUILD_CORE_A $file.getAbsolutePath()
7) `saveArchiveIndex(Map<String, String> index)`. Write $index to `$PROJECT_ARCHIVE_INDEX_FILE`
8) `link(List<File> files)`. Execute
	-     $ARDUINO_BINARIES_DIR\avr-gcc -Os -Wl,--gc-sections -mmcu=$ARDUINO_MCU -o$PROJECT_ELF_FILE {every entry from $sketchObjectFiles}.getAbsolutePath() $PROJECT_BUILD_CORE_A -L$PROJECT_BUILD_DIR -lm 
9) `objcopy()`
	-     $ARDUINO_BINARIES_DIR\avr-objcopy -O ihex -j .eeprom --set-section-flags=.eeprom=alloc,load --no-change-warnings --change-section-lma .eeprom=0 $PROJECT_ELF_FILE $PROJECT_EEP_FILE
	-     $ARDUINO_BINARIES_DIR\avr-objcopy -O ihex -R .eeprom $PROJECT_ELF_FILE $PROJECT_HEX_FILE
	
###### Build:
1) `enumSourceFiles(sketchFiles, $PROJECT_DIR)`. Enumerating all sketch files
2) `enumSourceFiles(arduinoFiles, $ARDUINO_CORE_SELECTED_DIR)`. Enumerating all files in core directory
3) `enumIncludes($SKETCH_MAIN_FILE)`. Return includes in sketch file as `includes` list
4) `enumSourceFiles(arduinoFiles, ...)`. For each `$include` entry in `includes` do next
	- get directory with name `$ARDUINO_LIBRARIES_DIR\$include`, and - if it exists - add it to `libraries` list and do `enumSourceFiles()` for it, adding results to `$arduinoFiles` list
	- get directory with name `$PROJECT_LIBRARIES_DIR\$include`, and - if it exists - add it to `libraries` list and do `enumSourceFiles()` for it, adding results to `$arduinoFiles` list
5) `buildFile({every file from  $sketchFiles}, $libraries)`. Placing result to `$sketchObjectFiles` list.
6) `buildFile({every file from  $arduinoFiles}, $libraries)`. Placing result to `$arduinoObjectFiles` list.
7) `extractArchiveIndex()`. Save execution result to `archiveIndex` map.
8) `canSkipArchive($archiveIndex, $arduinoObjectFiles)`?
	- if not - for each `file` in `$arduinoObjectFiles` - execute `archiveObjectFile($file)` and update `$archiveIndex` with `file.name:file.lastModified()` entry.
	- `saveArchiveIndex($archiveIndex)`
9) `link($sketchObjectFiles)`
10) `objcopy()`

###### Upload:
1) Execute
	-     $ARDUINO_BINARIES_DIR\avrdude -C$ARDUINO_AVRDUDE_CONF -v -p$ARDUINO_MCU -carduino" -P$ARDUINO_COMPORT -b$ARDUINO_BURNRATE -D -Uflash:w:$PROJECT_HEX_FILE:i