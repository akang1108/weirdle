package info.akang.weirdle.game;

import org.javacord.api.event.message.MessageCreateEvent;

public interface MessageListener {
    boolean onMessage(MessageCreateEvent event);
}
