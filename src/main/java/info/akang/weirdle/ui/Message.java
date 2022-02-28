package info.akang.weirdle.ui;

import lombok.*;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@ToString
@EqualsAndHashCode
public class Message {
    private MessageType type;
    private String msg;

    public static Message create(String msg) {
        return new Message(MessageType.DEFAULT, msg);
    }

    public static Message create1(String msg) {
        return new Message(MessageType.TYPE_1, msg);
    }
}
