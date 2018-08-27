package ru.chsergeyg.executor;

import org.testng.Assert;
import org.testng.annotations.Test;
import ru.chsergeyg.executor.exceptions.CliException;
import ru.chsergeyg.executor.exceptions.ExecException;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ExecutorTest {

    @Test
    public void cliStrings() {
        Assert.assertEquals(
                Executor.getInstance().a("runner").o("-e1").o("-e5").getCli(),
                "runner -e1 -e5");
    }

    @Test
    public void cliList() {
        List<String> params = new ArrayList<>();
        params.add("-list16");
        params.add("-list75");
        Assert.assertEquals(
                Executor.getInstance().a("runner").o("-e1").o(params).o("-e5").getCli(),
                "runner -e1 -list16 -list75 -e5");
    }

    @Test
    public void execNormalTest() {
        Assert.assertEquals(
                Executor.getInstance().a("git").o("status").execute(Paths.get(".")),
                0);
    }

    @Test(expectedExceptions = ExecException.class)
    public void execBadCliExceptionTest() {
        Executor.getInstance().a("git").o("status1").execute(Paths.get("."));
    }

    @Test
    public void execBadCliExceptionTextTest() {
        try {
            Executor.getInstance().a("git").o("status1").execute(Paths.get("."));
        } catch (ExecException e) {
            Assert.assertTrue(
                    e.getMessage().contains("Command execution failed (Exit code: 1)\ncmd:git status1 "));
        }
    }
}