package ru.kpfu.game.utils.constants;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MessageType {
    public static final int START = 1;
    public static final int MOVE = 2;
    public static final int GOAL = 3;
    public static final int WAIT = 4;
    public static final int COUNTDOWN = 5;
    public static final int GAMEOVER = 6;
    public static final int BROADCAST_GAMESTATE = 7;
    public static final int GAME_TIME = 8;

    public static List<Integer> getAllTypes() {
        return Arrays.stream(MessageType.class.getFields()).map(field -> {
            try {
                return field.getInt(MessageType.class.getDeclaredConstructor());
            } catch (IllegalAccessException | NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList());
    }

    private MessageType() {
    }
}
