package info.akang.weirdle.javacord;

import info.akang.weirdle.game.MessageListener;
import info.akang.weirdle.game.Play;
import info.akang.weirdle.game.User;
import lombok.extern.slf4j.Slf4j;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.event.message.MessageCreateEvent;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class JavacordMessageListener implements MessageListener {
    public static final String WEIRDLE_NAME = "weirdle";

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
        }
        return false;
    }

    private boolean handleBotMentionedMessages(MessageCreateEvent event) {
        String message = extractMessage(event.getMessage().getContent());
        User user = getAuthor(event);

        if (message.startsWith("start")) {
            sendMessage(play.startGame(user), event);
        } else if (message.startsWith("end")) {
            sendMessage(play.end(user), event);
        } else if (message.startsWith("sessions")) {
            sendMessage(play.sessions(user), event);
        } else if (message.startsWith("help")) {
            String msg = String.format("...%n**Bot invite:** %s%n%s", api.createBotInvite(), play.usage(user));
            sendMessage(msg, event);
        } else if (message.startsWith("status")) {
            sendMessage(play.status(user), event);
        } else if (play.gameInProgress(user)) {
            sendMessage(play.guess(user, message), event);
        } else {
            sendMessage("Huh? 🍣🍣🍣🍣🍣🍣 \n" + play.usage(user), event);
        }

        return true;
    }

    static final String COMMAND_PREFIX = "w ";

    private boolean handleNonBotMessages(MessageCreateEvent event) {
        String message = extractMessage(event.getMessage().getContent());

        if (!message.startsWith(COMMAND_PREFIX)) {
            return false;
        }

        User user = getAuthor(event);
        String msg = message.substring(COMMAND_PREFIX.length()).trim();

        if (msg.startsWith("guess")) {
            String guess = msg.substring("guess".length()).trim();
            String m = play.guess(user, guess);
            sendMessage(m, event);
            return true;
        } else if (msg.startsWith("end")) {
            String m = play.end(user);
            sendMessage(m, event);
            return true;
        }

        return false;
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
        boolean mentionedUserInJavacordLib = mentionedUsers.stream().anyMatch(user -> user.isBot() && user.getName().equals(WEIRDLE_NAME));

        boolean mentionedUserFoundInExtraction = false;
        // TODO: Seems like there is a bug possible, if I type `@weirdle something` fully with the space, without
        //  tabbing or enter for auto complete, then doesn't show up in mentioned users above in Javacord library.
        //  In the message it appears as <!&1234567> - (note the ampersand, not exclamation point)
//        Set<Long> extractedMentionedUserIds = extractMentionedUserIds(event.getMessage().getContent());
//        mentionedUserFoundInExtraction = extractedMentionedUserIds.contains(weirdleUser.get().getId());

        return mentionedUserInJavacordLib || mentionedUserFoundInExtraction;
    }

    private void sendMessage(String msg, MessageCreateEvent event) {
        new MessageBuilder()
                .append(msg)
                .send(event.getChannel());
    }

    private User getAuthor(MessageCreateEvent event) {
        return new User(event.getMessageAuthor().getId(), event.getMessageAuthor().getName());
    }
}
