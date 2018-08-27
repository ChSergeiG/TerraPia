package ru.chsergeyg.terrapia.arduino.compiler.exceptions;

public class UtilException extends RuntimeException {
    public UtilException() {
    }

    public UtilException(String message) {
        super(message);
    }

    public UtilException(Throwable cause) {
        super(cause);
    }

}
