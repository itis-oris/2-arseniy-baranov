package ru.kpfu.game.utils;

import ru.kpfu.game.exceptions.InvalidMessageTypeException;
import ru.kpfu.game.protocol.Message;
import ru.kpfu.game.utils.constants.LogMessages;
import ru.kpfu.game.utils.constants.MessageType;

public class GameMessageProvider {
    public static Message createMessage(int type, byte[] data) {
        if (!MessageType.getAllTypes().contains(type)) {
            throw new InvalidMessageTypeException(String.format(LogMessages.INVALID_MESSAGE_TYPE_EXCEPTION, type));
        }

        return new Message(type, data);
    }
}
