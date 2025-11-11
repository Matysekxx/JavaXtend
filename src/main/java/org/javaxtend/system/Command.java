package org.javaxtend.system;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * A fluent builder for executing external processes, inspired by Rust's `std::process::Command`.
 * <p>
 * It simplifies the creation and execution of system commands, capturing their output
 * and exit status in a clean way.
 *
 * <h2>Example of Usage:</h2>
 * <blockquote><pre>{@code
 * ProcessResult result = Command.run("git")
 *     .arg("status")
 *     .workingDir("/path/to/repo")
 *     .timeout(Duration.ofSeconds(10))
 *     .execute();
 *
 * if (result.isSuccess()) {
 *     System.out.println(result.stdout());
 * } else {
 *     System.err.println(result.stderr());
 * }
 * }</pre></blockquote>
 */
public final class Command {

    private final ProcessBuilder processBuilder;
    private Duration timeout = null;

    private Command(String command) {
        this.processBuilder = new ProcessBuilder(command);
    }

    public static Command run(String command) {
        return new Command(command);
    }

    public Command arg(String argument) {
        this.processBuilder.command().add(argument);
        return this;
    }

    public Command workingDir(String path) {
        this.processBuilder.directory(new File(path));
        return this;
    }

    public Command timeout(Duration timeout) {
        this.timeout = timeout;
        return this;
    }

    /**
     * Executes the command and waits for it to complete.
     *
     * @return A {@link ProcessResult} containing the exit code and output.
     * @throws IOException          if an I/O error occurs.
     * @throws InterruptedException if the current thread is interrupted while waiting.
     */
    public ProcessResult execute() throws IOException, InterruptedException {
        try {
            final Process process = this.processBuilder.start();
            if (this.timeout != null) {
                final boolean finished = process.waitFor(this.timeout.toMillis(), TimeUnit.MILLISECONDS);
                if (!finished) {
                    process.destroyForcibly();
                    return new ProcessResult(
                            -1,
                            "",
                            "Process timed out after " + this.timeout.toMillis() + " ms. Timeout was set."
                    );
                }
            } else {
                process.waitFor();
            }
            final String stdout = new String(process.getInputStream().readAllBytes());
            final String stderr = new String(process.getErrorStream().readAllBytes());
            final int exitValue = process.exitValue();
            return new ProcessResult(exitValue, stdout, stderr);
        } catch (IOException e) {
            return new ProcessResult(127, "", e.getMessage());
        }
    }
}

/**
 * A record to hold the result of an executed process.
 *
 * @param exitCode The exit code of the process. 0 typically indicates success.
 * @param stdout   The content of the standard output stream.
 * @param stderr   The content of the standard error stream.
 */
record ProcessResult(int exitCode, String stdout, String stderr) {
    public boolean isSuccess() {
        return exitCode == 0;
    }
}