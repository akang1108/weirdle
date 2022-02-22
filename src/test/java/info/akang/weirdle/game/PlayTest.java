package info.akang.weirdle.game;

import info.akang.weirdle.game.Play;
import info.akang.weirdle.game.PlayConfig;
import info.akang.weirdle.game.Sessions;
import info.akang.weirdle.game.User;
import info.akang.weirdle.loader.Puzzle;
import info.akang.weirdle.loader.Puzzles;
import info.akang.weirdle.loader.RiddleLoader;
import info.akang.weirdle.ui.DiscordBotDisplay;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

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

        String message;

        Play play = new Play(PlayConfig.DEFAULT, new Sessions(), puzzles, new DiscordBotDisplay(PlayConfig.DEFAULT));
        User user = new User(1, "bob");

        message = play.startGame(user);
        System.out.println(message);
    }
}
