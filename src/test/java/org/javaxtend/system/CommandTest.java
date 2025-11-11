package org.javaxtend.system;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

class CommandTest {

    @Test
    @DisplayName("should execute a simple command and capture output")
    void execute_simpleCommand() throws IOException, InterruptedException {
        String javaExecutable = System.getProperty("java.home") + "/bin/java";
        ProcessResult result = Command.run(javaExecutable)
                .arg("-version")
                .execute();
        assertTrue(result.isSuccess());
        assertTrue(result.stderr().contains("version"));
        assertTrue(result.stdout().isEmpty());
    }

    @Test
    @DisplayName("should handle a failing command")
    void execute_failingCommand() throws IOException, InterruptedException {
        ProcessResult result = Command.run("non_existent_command_12345")
                .execute();

        assertFalse(result.isSuccess());
        assertNotEquals(0, result.exitCode());
    }

    @Test
    @DisplayName("should time out if the command takes too long")
    void execute_shouldTimeout() throws IOException, InterruptedException {
        ProcessResult result = Command.run("java")
                .arg("-cp").arg("src/test/resources").arg("Sleep")
                .timeout(Duration.ofMillis(100))
                .execute();

        assertFalse(result.isSuccess(), "Process should have failed due to timeout");
        assertTrue(result.stderr().contains("Timeout"), "Error message should indicate a timeout");
    }
}