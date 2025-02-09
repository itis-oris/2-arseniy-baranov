package ru.kpfu.game.utils.constants;

public class LogMessages {
    public static final String INVALID_PROTOCOL_NAME_EXCEPTION = "Names of the protocols don't match.";
    public static final String INVALID_MESSAGE_TYPE_EXCEPTION = "Wrong message type: %d.";
    public static final String INVALID_MESSAGE_LENGTH_EXCEPTION = "Protocol doesn't support this message length: %d. Message length can't be greater than %d bytes length.";
    public static final String READ_MESSAGE_EXCEPTION = "Can't read message.";
    public static final String WRITE_MESSAGE_EXCEPTION = "Can't write message.";


    private LogMessages() {}
}
