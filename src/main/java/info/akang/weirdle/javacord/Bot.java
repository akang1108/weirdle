package info.akang.weirdle.javacord;

import info.akang.weirdle.game.MessageListener;
import info.akang.weirdle.game.Play;
import lombok.extern.slf4j.Slf4j;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;

@Slf4j
public class Bot {
    public static void main(String[] args) throws Exception {
        String token = System.getenv("DISCORD_BOT_TOKEN");
        Bot bot = new Bot(token);
    }

    private final DiscordApi api;
    private final Play play;
    private final MessageListener listener;

    public Bot(String token) {
        api = new DiscordApiBuilder().setToken(token).login().join();
        play = new Play();
        listener = new JavacordMessageListener(play, api);

        //printInvite();
        addListener();
    }

    public void printInvite() {
        log.info("You can invite the bot by using the following url: " + api.createBotInvite());
    }

    public void addListener() {
        api.addMessageCreateListener(event -> {
            listener.onMessage(event);
            //⬛🟧⬜🟦🟥🟫🟪🟩🟨
        });
    }
}
