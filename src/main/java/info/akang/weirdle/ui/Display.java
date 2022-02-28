package info.akang.weirdle.ui;

import info.akang.weirdle.game.Session;
import info.akang.weirdle.game.Sessions;
import info.akang.weirdle.game.User;
import info.akang.weirdle.guess.GuessCalculation;

public interface Display {
    Messages start(User user);
    Messages end(User user);
    Messages win(Session session);
    Messages invalid(GuessCalculation calculation);
    Messages lose(Session session);
    Messages startGame(Session session);
    Messages puzzle(Session session);
    Messages usage(User user);
    Messages sessions(User user, Sessions sessions);
    Messages noSession();
}
