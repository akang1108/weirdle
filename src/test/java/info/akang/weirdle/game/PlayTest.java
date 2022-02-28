package info.akang.weirdle.game;

import info.akang.weirdle.loader.Puzzle;
import info.akang.weirdle.loader.Puzzles;
import info.akang.weirdle.ui.DiscordBotDisplay;
import info.akang.weirdle.ui.Messages;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static info.akang.weirdle.loader.RiddleLoader.RIDDLE_DEFAULT_IGNORE;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PlayTest {
    @Mock
    Puzzles puzzles;

    @Test
    void play() {
        Puzzle puzzle = new Puzzle(
                "Why did the elephant cross the road?",
                "it was the chicken's day off",
                RIDDLE_DEFAULT_IGNORE);
        when(puzzles.getPuzzle()).thenReturn(puzzle);

        Messages messages;

        Play play = new Play(PlayConfig.DEFAULT, new Sessions(), puzzles, new DiscordBotDisplay(PlayConfig.DEFAULT));
        User user = new User(1, "bob");
        messages = play.usage(user);
        System.out.println(messages);

        messages = play.startGame(user);
        System.out.println(messages);
    }
}
