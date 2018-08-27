package ru.chsergeyg.terrapia.arduino.compiler.utils;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

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
                {PathsEnum.ARDUINO_BINARIES_DIR},
                {PathsEnum.PROJECT_DIR},
                {PathsEnum.PROJECT_LIBRARIES_DIR},
                {PathsEnum.PROJECT_BUILD_DIR},
        };
    }

    @Test(dataProvider = "enumDirs")
    public void checkDirs(PathsEnum pathEnum) {
        Assert.assertTrue(pathEnum.getFile().exists() && pathEnum.getFile().isDirectory());
    }

}