package info.akang.weirdle.ui;

import info.akang.weirdle.game.Session;
import info.akang.weirdle.game.Sessions;
import info.akang.weirdle.game.User;
import info.akang.weirdle.guess.GuessCalculation;

public interface Display {
    String start(User user);
    String end(User user);
    String win(Session session);
    String invalid(GuessCalculation calculation);
    String lose(Session session);
    String startGame(Session session);
    String puzzle(Session session);
    String usage(User user);
    String sessions(User user, Sessions sessions);
}
