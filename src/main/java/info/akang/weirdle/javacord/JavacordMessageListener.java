package info.akang.weirdle.javacord;

import info.akang.weirdle.game.MessageListener;
import info.akang.weirdle.game.Play;
import info.akang.weirdle.game.User;
import info.akang.weirdle.ui.Message;
import info.akang.weirdle.ui.Messages;
import lombok.extern.slf4j.Slf4j;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.event.message.MessageCreateEvent;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class JavacordMessageListener implements MessageListener {
    public static final String WEIRDLE_NAME = "weirdle";
    public static final String COMMAND_PREFIX = "w ";

    private final Play play;
    private final DiscordApi api;

    public JavacordMessageListener(Play play, DiscordApi api) {
        this.play = play;
        this.api = api;
    }

    @Override
    public boolean onMessage(MessageCreateEvent event) {
        if (isBotUserMentioned(event)) {
            return handleBotMentionedMessages(event);
        } else {
            return handleNonBotMentionedMessages(event);
        }
    }

    private boolean handleBotMentionedMessages(MessageCreateEvent event) {
        String msg = extractMessage(event.getMessage().getContent());
        User user = getAuthor(event);

        if (msg.startsWith("start")) {
            sendMessage(play.startGame(user), event);
        } else if (msg.startsWith("end")) {
            sendMessage(play.end(user), event);
        } else if (msg.startsWith("sessions")) {
            sendMessage(play.sessions(user), event);
        } else if (msg.startsWith("help")) {
            sendMessage(play.usage(user), event);
        } else if (msg.startsWith("status")) {
            sendMessage(play.status(user), event);
        } else if (msg.startsWith("give up")) {
            sendMessage(play.giveUp(user), event);
        } else if (msg.startsWith("invite")) {
            sendMessage(new Messages().msg(api.createBotInvite()), event);
        } else if (play.hasSession(user)) {
            sendMessage(play.guess(user, msg), event);
        } else {
            sendMessage(play.usage(user), event);
        }

        return true;
    }

    private boolean handleNonBotMentionedMessages(MessageCreateEvent event) {
        String message = extractMessage(event.getMessage().getContent());

        if (!message.startsWith(COMMAND_PREFIX)) {
            return false;
        }

        User user = getAuthor(event);
        String msg = message.substring(COMMAND_PREFIX.length()).trim();

        if (! play.hasSession(user)) {
            return false;
        }

        if (msg.startsWith("help")) {
            sendMessage(play.usage(user), event);
        } else if (msg.startsWith("status")) {
            sendMessage(play.status(user), event);
        } else if (msg.startsWith("give up")) {
            sendMessage(play.giveUp(user), event);
        } else {
            sendMessage(play.guess(user, msg), event);
        }

        return true;
    }

    protected String extractMessage(String message) {
        return message.replaceAll("<@.+>", "").trim().toLowerCase();
    }

    protected Set<Long> extractMentionedUserIds(String message) {
        Pattern MENTION_PATTERN = Pattern.compile("<@\\D?(\\d+)>");
        Matcher matcher = MENTION_PATTERN.matcher(message);

        Set<Long> mentionedUserIds = new HashSet<>();

        while (matcher.find()) {
            mentionedUserIds.add(Long.parseLong(matcher.group(1)));
        }

        return mentionedUserIds;
    }

    private boolean isBotUserMentioned(MessageCreateEvent event) {
        List<org.javacord.api.entity.user.User> mentionedUsers = event.getMessage().getMentionedUsers();
        boolean mentionedUserInJavacordLib = mentionedUsers.stream().anyMatch(user -> user.isBot() && user.getName().startsWith(WEIRDLE_NAME));

        boolean mentionedUserFoundInExtraction = false;

        // TODO: Seems like there is a bug possible, if I type `@weirdle something` fully with the space, without
        //  tabbing or enter for auto complete, then doesn't show up in mentioned users above in Javacord library.
        //  In the message it appears as <!&1234567> - (note the ampersand, not exclamation point)
//        Set<Long> extractedMentionedUserIds = extractMentionedUserIds(event.getMessage().getContent());
//        mentionedUserFoundInExtraction = extractedMentionedUserIds.contains(weirdleUser.get().getId());

        return mentionedUserInJavacordLib || mentionedUserFoundInExtraction;
    }

    private void sendMessage(Messages messages, MessageCreateEvent event) {
        MessageBuilder messageBuilder = new MessageBuilder();

        for (Message message: messages.getMessages()) {
            switch (message.getType()) {
                case DEFAULT:
                    messageBuilder.append(message.getMsg());
                    break;
                case TYPE_1:
                    messageBuilder.appendCode("java", message.getMsg());
                    break;
            }
        }

        messageBuilder.send(event.getChannel());
    }

    private User getAuthor(MessageCreateEvent event) {
        return new User(event.getMessageAuthor().getId(), event.getMessageAuthor().getName());
    }
}
