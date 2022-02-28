package info.akang.weirdle.ui;

import info.akang.weirdle.game.PlayConfig;
import info.akang.weirdle.game.Session;
import info.akang.weirdle.game.Sessions;
import info.akang.weirdle.game.User;
import info.akang.weirdle.guess.GuessCalculation;
import info.akang.weirdle.guess.GuessValidationResponse;
import info.akang.weirdle.loader.Puzzle;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class DiscordBotDisplay implements Display {
    // ❎⏹️🔳🔲▪️▫️◾◽◼️◻️⬛⬜🟧🟦🟥🟫🟪🟩🟨❌
    // ✅ "🔲" "⬜" "⚠️"
    //"🇦 🇧 🇨 🇩 🇪 🇫 🇬 🇭 🇮 🇯 🇰 🇱 🇲 🇳 🇴 🇵 🇶 🇷 🇸 🇹 🇺 🇻 🇼 🇽 🇾 🇿"
    //⬛🟧⬜🟦🟥🟫🟪🟩🟨

    public static String SPACE = "⬜";
    public static String NO_GUESS = "🔲";
    public static String RIGHT = "✅";
//    public static String WRONG = "❌";
    public static String WRONG = "🟥";
//    public static String WRONG_POSITION = "⚠️";
    public static String WRONG_POSITION = "🟨";
    AlphabetEmoji alphabetEmoji = new AlphabetEmoji();

    private final PlayConfig config;

    public DiscordBotDisplay(PlayConfig config) {
        this.config = config;
    }

    @Override
    public Messages start(User user) {
        String msg = String.format("Hello %s, so weirddddd...", user.getName());
        return new Messages().msg1(msg);
    }

    @Override
    public Messages end(User user) {
        String msg = String.format("%s, bye friend!", user.getName());
        return new Messages().msg(msg);
    }

    @Override
    public Messages win(Session session) {
        Messages messages = puzzle(session);
        return messages.msg("You won!");
    }

    @Override
    public Messages invalid(GuessCalculation calculation) {
        String msg = calculation.getGuessValidationResponses().stream()
                .map(GuessValidationResponse::getMessage)
                .collect(Collectors.joining("\n"));
        return new Messages().msg(msg);
    }

    @Override
    public Messages lose(Session session) {
        Messages messages = puzzle(session);
        return messages.msg("You lost! The answer is: **" + session.getPuzzle().getAnswer().toUpperCase() + "**");
    }

    @Override
    public Messages startGame(Session session) {
        return puzzle(session);
    }

    @Override
    public Messages puzzle(Session session) {
        Puzzle puzzle = session.getPuzzle();
        GuessCalculation lastGuess = session.getLastGuess();

        boolean noGuesses = lastGuess == null;

        char[] guess = noGuesses ? null: lastGuess.getGuess().toCharArray();
        char[] answer = puzzle.getAnswer().toCharArray();

        Set<Character> answerSet = new HashSet<>();
        for (char ans: answer) {
            answerSet.add(ans);
        }

        // Puzzle question
        String msg = String.format("%s%n%n", puzzle.getMessage());

        String line1 = "";

        // Puzzle answer
        for (int i = 0; i < answer.length; i++) {
            char ans = answer[i];
            Character gue = noGuesses ? null : guess[i];

            if (puzzle.getIgnore().contains(ans)) {
                if (ans == ' ') {
                    line1 += SPACE;
                } else {
                    line1 += ans;
                }
            } else if (noGuesses) {
                line1 += NO_GUESS;
            } else if (ans == gue) {
                line1 += Character.toUpperCase(gue);
            } else if (answerSet.contains(gue)) {
                line1 += WRONG_POSITION;
            } else {
                line1 += WRONG;
            }
        }
        msg += line1;

        // Guesses
        msg += String.format(" %s chars %s/%s%n", answer.length, session.getNumGuesses(), config.getMaxGuesses());

        return new Messages().msg1(msg);
    }

    @Override
    public Messages usage(User user) {
        String msg = "|\n";
        msg += "**Commands:**\n";
        msg += "  @weirdle help | start | end | sessions\n";
        msg += "  w {guess} | status | give up\n";

        msg += "**Key:**";
        msg += "  " + NO_GUESS + " initial";
        msg += " " + SPACE + " space";
        msg += " " + WRONG_POSITION + " = wrong pos";
        msg += " " + WRONG + " = wrong char\n";

        msg += "**Play:**\n";
        msg += "  @weirdle start\n";
        msg += "  w {guess}";

        return new Messages().msg(msg);
    }

    @Override
    public Messages sessions(User user, Sessions sessions) {
        String msg = String.format("Total Sessions: %s. ", sessions.getSessionsSize());
        Session userSession = sessions.getSession(user);
        if (userSession != null) {
            msg += "You have an active session.";
        } else {
            msg += "You do not have an active session. Type `@weirdle start` to start a game.";
        }

        return new Messages().msg(msg);
    }

    @Override
    public Messages noSession() {
        String msg = "You do not have an active session. Type `@weirdle start` to start a game.";
        return new Messages().msg(msg);
    }
}
