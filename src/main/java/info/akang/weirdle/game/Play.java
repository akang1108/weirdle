package info.akang.weirdle.game;

import info.akang.weirdle.guess.*;
import info.akang.weirdle.loader.Puzzle;
import info.akang.weirdle.loader.Puzzles;
import info.akang.weirdle.ui.DiscordBotDisplay;
import info.akang.weirdle.ui.Display;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Play {
    private final PlayConfig config;
    private final Sessions sessions;
    private final Puzzles puzzles;
    private final Display display;
    private final GuessCalculator guessCalculator;
    private final ScheduledExecutorService scheduledExecutorService;

    public Play() {
        this(PlayConfig.DEFAULT, new Sessions(), new Puzzles(), new DiscordBotDisplay(PlayConfig.DEFAULT));
    }

    public Play(PlayConfig config, Sessions sessions, Puzzles puzzles, Display display) {
        this.config = config;
        this.sessions = sessions;
        this.puzzles = puzzles;
        this.display = display;

        this.guessCalculator = new GuessCalculator(List.of(new GuessLengthValidator()));

        this.scheduledExecutorService = Executors.newScheduledThreadPool(1);
        this.scheduledExecutorService.scheduleAtFixedRate(
                () -> sessions.runEvictions(config.getSessionEvictionPeriodSeconds()),
                config.getSessionEvictionPeriodSeconds(),
                config.getSessionEvictionPeriodSeconds(),
                TimeUnit.SECONDS);
    }

    public String start(User user) {
        sessions.start(user);
        return startGame(user);
    }

    public boolean gameInProgress(User user) {
        return sessions.getSession(user) != null;
    }

    public String startGame(User user) {
        Puzzle puzzle = puzzles.getPuzzle();
        Session session = sessions.startGame(user, puzzle);
        return display.startGame(session);
    }

    public String status(User user) {
        Session session = sessions.getSession(user);
        if (session == null) {
            return "No session, `@weirdle start` to start a game.";
        } else {
            return display.puzzle(session);
        }
    }

    public String guess(User user, String guess) {
        Session session = sessions.getSession(user);
        Puzzle puzzle = session.getPuzzle();

        GuessCalculation guessCalculation = guessCalculator.calculate(guess, puzzle);

        if (!guessCalculation.isValid()) {
            return display.invalid(guessCalculation);
        }

        session.addGuess(guessCalculation);

        if (guessCalculation.isCorrect()) {
            sessions.end(user);
            return display.win(session);
        } else if (session.getNumGuesses() >= config.getMaxGuesses()) {
            sessions.end(user);
            return display.lose(session);
        } else {
            return display.puzzle(session);
        }
    }

    public String end(User user) {
        sessions.end(user);
        return display.end(user);
    }

    public String usage(User user) {
        return display.usage(user);
    }

    public String sessions(User user) {
        return display.sessions(user, sessions);
    }
}
