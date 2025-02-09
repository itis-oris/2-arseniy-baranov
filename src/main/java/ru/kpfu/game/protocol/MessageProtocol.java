package ru.kpfu.game.protocol;

import ru.kpfu.game.exceptions.*;
import ru.kpfu.game.utils.GameMessageProvider;
import ru.kpfu.game.utils.constants.LogMessages;
import ru.kpfu.game.utils.constants.MessageType;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

public class MessageProtocol {
    private final static String PROTOCOL_NAME = "APA";
    public static final int MAX_MESSAGE_LENGTH = 100 * 1024;
    public static final int INTEGER_BYTES = 4;

    public static Message readMessage(InputStream in) throws MessageReadException {
        byte[] buffer = new byte[PROTOCOL_NAME.length() + INTEGER_BYTES * 2];
        try {
            in.read(buffer, 0, PROTOCOL_NAME.length());
            String protocolName = new String(buffer).trim();
            if (!protocolName.equals(PROTOCOL_NAME)) {
                throw new InvalidProtocolNameException(LogMessages.INVALID_PROTOCOL_NAME_EXCEPTION);
            }

            in.read(buffer, 0, INTEGER_BYTES);
            int messageType = ByteBuffer.wrap(buffer, 0, INTEGER_BYTES).getInt();
            if (!MessageType.getAllTypes().contains(messageType)) {
                throw new InvalidMessageTypeException(String.format(LogMessages.INVALID_MESSAGE_TYPE_EXCEPTION, messageType));
            }

            in.read(buffer, 0, INTEGER_BYTES);
            int messageLength = ByteBuffer.wrap(buffer, 0, INTEGER_BYTES).getInt();
            if (messageLength > MAX_MESSAGE_LENGTH) {
                throw new InvalidMessageLengthException(String.format(LogMessages.INVALID_MESSAGE_LENGTH_EXCEPTION, messageLength, MAX_MESSAGE_LENGTH));
            }

            buffer = new byte[messageLength];
            in.read(buffer, 0, messageLength);
            return GameMessageProvider.createMessage(messageType, buffer);
        } catch (IOException e) {
            try {
                in.close();
            } catch (IOException ex) {
                throw new MessageReadException(LogMessages.READ_MESSAGE_EXCEPTION, e);
            }
        }
        return null;
    }

    public static void sendMessage(OutputStream os, Message message) throws MessageWriteException {
        try {
            os.write(MessageProtocol.getBytes(message));
            os.flush();
        } catch (IOException e) {
            throw new MessageWriteException(LogMessages.WRITE_MESSAGE_EXCEPTION, e);
        }
    }

    public static byte[] getBytes(Message message) {
        ByteBuffer buffer = ByteBuffer.allocate(PROTOCOL_NAME.length() + INTEGER_BYTES * 2 + message.getData().length);
        buffer.put(PROTOCOL_NAME.getBytes());

        int type = message.getType();
        if (!MessageType.getAllTypes().contains(type)) {
            throw new InvalidMessageTypeException(String.format(LogMessages.INVALID_MESSAGE_TYPE_EXCEPTION, type));
        }
        buffer.putInt(type);

        int length = message.getData().length;
        if (length > MAX_MESSAGE_LENGTH) {
            throw new InvalidMessageLengthException(String.format(LogMessages.INVALID_MESSAGE_LENGTH_EXCEPTION, length, MAX_MESSAGE_LENGTH));
        }
        buffer.putInt(length);

        buffer.put(message.getData());
        return buffer.array();
    }
}
