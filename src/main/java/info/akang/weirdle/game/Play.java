package info.akang.weirdle.game;

import info.akang.weirdle.guess.*;
import info.akang.weirdle.loader.Puzzle;
import info.akang.weirdle.loader.Puzzles;
import info.akang.weirdle.ui.DiscordBotDisplay;
import info.akang.weirdle.ui.Display;
import info.akang.weirdle.ui.Messages;

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
                () -> sessions.runEvictions(config.getSessionTimeToLiveSeconds()),
                config.getSessionEvictionPeriodSeconds(),
                config.getSessionEvictionPeriodSeconds(),
                TimeUnit.SECONDS);
    }

    public Messages start(User user) {
        sessions.start(user);
        return startGame(user);
    }

    public Messages startGame(User user) {
        Puzzle puzzle = puzzles.getPuzzle();

        Session session = sessions.startGame(user, puzzle);
        session.updateLastActiveMs();

        return display.startGame(session);
    }

    public Messages status(User user) {
        Session session = sessions.getSession(user);

        if (session == null) {
            return display.noSession();
        } else {
            session.updateLastActiveMs();
            return display.puzzle(session);
        }
    }

    public boolean hasSession(User user) {
        return sessions.getSession(user) != null;
    }

    public Messages noSessionMessage(User user) {
        return display.noSession();
    }

    public Messages guess(User user, String guess) {
        Session session = sessions.getSession(user);
        session.updateLastActiveMs();

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

    public Messages giveUp(User user) {
        Session session = sessions.getSession(user);
        sessions.end(user);
        return display.lose(session);
    }

    public Messages end(User user) {
        sessions.end(user);
        return display.end(user);
    }

    public Messages usage(User user) {
        return display.usage(user);
    }

    public Messages sessions(User user) {
        Session session = sessions.getSession(user);
        if (session != null) {
            session.updateLastActiveMs();
        }
        return display.sessions(user, sessions);
    }
}
