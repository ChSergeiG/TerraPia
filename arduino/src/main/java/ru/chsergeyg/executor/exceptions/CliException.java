package ru.chsergeyg.executor.exceptions;

public class CliException extends RuntimeException {
    public CliException(String message) {
        super(message);
    }

    public CliException(Throwable cause) {
        super(cause);
    }
}
