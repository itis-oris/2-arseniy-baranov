package ru.kpfu.game.exceptions;

public class InvalidProtocolNameException extends RuntimeException {
    public InvalidProtocolNameException() {
    }

    public InvalidProtocolNameException(String message) {
        super(message);
    }

    public InvalidProtocolNameException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidProtocolNameException(Throwable cause) {
        super(cause);
    }

    public InvalidProtocolNameException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
