package ru.chsergeyg.terrapia.arduino.compiler.utils;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import ru.chsergeyg.terrapia.arduino.compiler.exceptions.UtilException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static ru.chsergeyg.terrapia.arduino.compiler.utils.TaskUtils.init;

public class UtilsTest {

    @BeforeClass
    public void before() {
        init();
    }

    @DataProvider(name = "enumDirs")
    public Object[][] enumDirs() {
        return new Object[][]{
                {PathsEnum.ARDUINO_DIR},
                {PathsEnum.ARDUINO_CORES_DIR},
                {PathsEnum.ARDUINO_SELECTED_CORE_DIR},
                {PathsEnum.ARDUINO_VARIANTS_DIR},
                {PathsEnum.ARDUINO_SELECTED_VARIANT_DIR},
                {PathsEnum.ARDUINO_LIBRARIES_DIR},
                {PathsEnum.PROJECT_DIR},
                {PathsEnum.PROJECT_LIBRARIES_DIR},
                {PathsEnum.PROJECT_COMPILE_DIR},
        };
    }

    @DataProvider(name = "enumFiles")
    public Object[][] enumFiles() {
        return new Object[][]{
                {PathsEnum.PROJECT_INO_FILE},
        };
    }


    @Test
    public void gitStatusRunCmd() throws Exception {
        ExecUtils.runCmd("git status", new File("."));
    }

    @Test(expectedExceptions = UtilException.class)
    public void gitStatusRunCmdExc() throws Exception {
        ExecUtils.runCmd("git status1", new File("."));
    }

    @Test
    public void gitStatusRunCmdExcCatch() throws Exception {
        try {
            ExecUtils.runCmd("git status1", new File("."));
        } catch (UtilException ex) {
            String message = ex.getMessage();
            Assert.assertTrue(message.contains("Exit code: 1"));
            Assert.assertTrue(message.contains("git: 'status1' is not a git command. See 'git --help'."));
        }

    }

    @Test
    public void gitStatusExecCmd() throws Exception {
        List<String> cmd = new ArrayList<>();
        cmd.add("git");
        cmd.add("status");
        ExecUtils.execCmd(cmd);
    }

    @Test(expectedExceptions = UtilException.class)
    public void gitStatusExecCmdExc() throws Exception {
        List<String> cmd = new ArrayList<>();
        cmd.add("git");
        cmd.add("status1");
        ExecUtils.execCmd(cmd);
    }

    @Test
    public void gitStatusExecCmdExcCatch() throws Exception {
        List<String> cmd = new ArrayList<>();
        cmd.add("git");
        cmd.add("status1");
        try {
            ExecUtils.execCmd(cmd);
        } catch (UtilException ex) {
            String message = ex.getMessage();
            Assert.assertTrue(message.contains("Exit code: 1"));
            Assert.assertTrue(message.contains("git: 'status1' is not a git command. See 'git --help'."));
        }
    }

    @Test(dataProvider = "enumDirs")
    public void checkPaths(PathsEnum pathEnum) {
        Assert.assertTrue(pathEnum.getFile().exists() && pathEnum.getFile().isDirectory());
    }

    @Test(dataProvider = "enumFiles")
    public void checkFiles(PathsEnum pathEnum) {
        Assert.assertTrue(pathEnum.getFile().exists() && !pathEnum.getFile().isDirectory());
    }

}