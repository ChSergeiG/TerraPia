package ru.chsergeyg.executor.exceptions;

public class ExecException extends RuntimeException {

    public ExecException(String message) {
        super(message);
    }

    public ExecException(Throwable cause) {
        super(cause);
    }
}
