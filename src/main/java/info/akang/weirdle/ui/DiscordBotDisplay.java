package info.akang.weirdle.ui;

import info.akang.weirdle.game.PlayConfig;
import info.akang.weirdle.game.Session;
import info.akang.weirdle.game.Sessions;
import info.akang.weirdle.game.User;
import info.akang.weirdle.guess.GuessCalculation;
import info.akang.weirdle.guess.GuessValidationResponse;
import info.akang.weirdle.loader.Puzzle;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

public class DiscordBotDisplay implements Display {
    // ❎⏹️🔳🔲▪️▫️◾◽◼️◻️⬛⬜🟧🟦🟥🟫🟪🟩🟨❌
    // ✅ "🔲" "⬜" "⚠️"
    //"🇦 🇧 🇨 🇩 🇪 🇫 🇬 🇭 🇮 🇯 🇰 🇱 🇲 🇳 🇴 🇵 🇶 🇷 🇸 🇹 🇺 🇻 🇼 🇽 🇾 🇿"

    String SPACE = "⬜";
    String NO_GUESS = "🔲";
    String RIGHT = "✅";
    String WRONG = "❌";
    String WRONG_POSITION = "⚠️";
    AlphabetEmoji alphabetEmoji = new AlphabetEmoji();

    private final PlayConfig config;

    public DiscordBotDisplay(PlayConfig config) {
        this.config = config;
    }

    @Override
    public String start(User user) {
        return String.format("Hello %s, so weirddddd...", user.getName());
    }

    @Override
    public String end(User user) {
        return String.format("%s, bye friend!", user.getName());
    }

    @Override
    public String win(Session session) {
        return puzzle(session) + "\n" + "you won!";
    }

    @Override
    public String invalid(GuessCalculation calculation) {
        return calculation.getGuessValidationResponses().stream()
                .map(GuessValidationResponse::getMessage)
                .collect(Collectors.joining("\n"));
    }

    @Override
    public String lose(Session session) {
        return String.format("%s%n**Answer:** %s%nYou lost!", puzzle(session), session.getPuzzle().getAnswer().toUpperCase());
    }

    @Override
    public String startGame(Session session) {
        String msg = "Hi *" + session.getUser().getName() + "*! This is your puzzle:\n";
        msg += puzzle(session);
        return msg;
    }

    @Override
    public String puzzle(Session session) {
        Puzzle puzzle = session.getPuzzle();
        GuessCalculation lastGuess = session.getLastGuess();

        boolean noGuesses = lastGuess == null;

        char[] guess = noGuesses ? null: lastGuess.getGuess().toCharArray();
        char[] answer = puzzle.getAnswer().toCharArray();

        Set<Character> answerSet = new HashSet<>();
        for (char ans: answer) {
            answerSet.add(ans);
        }

        String msg = String.format("...%n**Riddle:** %s%n", puzzle.getMessage());
        msg += String.format("**Guesses:** %s/%s     **Num chars:** %s%n",
                session.getNumGuesses(), config.getMaxGuesses(), answer.length);

        for (int i = 0; i < answer.length; i++) {
            char ans = answer[i];
            Character gue = noGuesses ? null : guess[i];

            if (puzzle.getIgnore().contains(ans)) {
                if (ans == ' ') {
                    msg += SPACE;
                } else {
                    msg += ans;
                }
            } else if (noGuesses) {
                msg += NO_GUESS;
            } else if (ans == gue) {
                msg += Character.toUpperCase(gue);
            } else if (answerSet.contains(gue)) {
                msg += WRONG_POSITION;
            } else {
                msg += WRONG;
            }
        }

        msg += "     command: `@weirdle {guess}`";

        return msg;
    }

    @Override
    public String usage(User user) {
        String msg = "**Commands:**\n";
        msg += "  @weirdle help\n";
        msg += "  @weirdle start = start game\n";
        msg += "  @weirdle {guess} = during a game, make a guess\n";
        msg += "  @weirdle status = current game status\n";
        msg += "  @weirdle sessions = show number of sessions\n";
        msg += "  @weirdle end = end game\n";

        msg += "**Key:**\n";
        msg += "  " + NO_GUESS + " = initial state, no guess\n";
        msg += "  " + SPACE + " = space\n";
        msg += "  ABC... = character displayed if correct and in the right position\n";
        msg += "  " + WRONG_POSITION + " = right correct in the wrong position\n";
        msg += "  " + WRONG + " = wrong character\n";

        msg += "\n**TL;DR;** `weirdle start`\n";

        return msg;
    }

    @Override
    public String sessions(User user, Sessions sessions) {
        String msg = String.format("Number of sessions: %s%n", sessions.getSessionsSize());
        Session userSession = sessions.getSession(user);
        if (userSession != null) {
            msg += "You have an active session.";
        }

        return msg;
    }
}
