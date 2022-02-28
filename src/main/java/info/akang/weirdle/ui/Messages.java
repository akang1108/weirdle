package info.akang.weirdle.ui;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@ToString
@EqualsAndHashCode
public class Messages {
    @Getter
    private List<Message> messages = new ArrayList<>();

    public Messages msg(String msg) {
        messages.add(Message.create(msg));
        return this;
    }

    public Messages msg1(String msg) {
        messages.add(Message.create1(msg));
        return this;
    }

    public Messages prependMsg(String msg) {
        messages.add(0, Message.create(msg));
        return this;
    }

    public Messages prependMsg1(String msg) {
        messages.add(0, Message.create1(msg));
        return this;
    }

}
