package info.akang.weirdle.game;

import info.akang.weirdle.guess.GuessCalculation;
import info.akang.weirdle.loader.Puzzle;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@EqualsAndHashCode
@ToString
public class Session {
    private final long startMs;
    private long lastActiveMs;
    private User user;
    private State state;
    private Puzzle puzzle;
    private List<GuessCalculation> guesses;

    public Session(User user) {
        this(user, State.INITIAL, null);
    }

    public Session(User user, State state, Puzzle puzzle) {
        this.user = user;
        this.startMs = System.currentTimeMillis();
        this.state = state;
        this.puzzle = puzzle;
        this.guesses = new ArrayList<>();
        this.lastActiveMs = this.startMs;
    }

    public Session startPlaying(Puzzle puzzle) {
        this.state = State.PLAYING;
        this.puzzle = puzzle;
        updateLastActiveMs();
        return this;
    }

    public Session addGuess(GuessCalculation guessCalculation) {
        this.guesses.add(guessCalculation);
        updateLastActiveMs();
        return this;
    }

    public Session updateLastActiveMs() {
        this.lastActiveMs = System.currentTimeMillis();
        return this;
    }

    public GuessCalculation getLastGuess() {
        return this.guesses.size() == 0 ? null : this.guesses.get(this.guesses.size() - 1);
    }

    public int getNumGuesses() {
        return this.guesses.size();
    }
}
