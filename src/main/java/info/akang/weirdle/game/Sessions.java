package info.akang.weirdle.game;

import info.akang.weirdle.loader.Puzzle;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class Sessions {
    private final Map<User, Session> userIdSessions = new ConcurrentHashMap<>();

    public Session start(User user) {
        return userIdSessions.put(user, new Session(user));
    }

    public Session startGame(User user, Puzzle puzzle) {
        Session session = userIdSessions.get(user);
        session = (session == null) ? new Session(user, State.PLAYING, puzzle) : session.startPlaying(puzzle);
        userIdSessions.put(user, session);
        log.debug("Started session for {}, totalSessions: {}", user, userIdSessions.size());
        return session;
    }

    public void end(User user) {
        userIdSessions.remove(user);
        log.debug("Ended session for {}, totalSessions: {}", user, userIdSessions.size());
    }

    public Session getSession(User user) {
        return userIdSessions.get(user);
    }

    public int getSessionsSize() {
        return userIdSessions.size();
    }

    public void runEvictions(long sessionTimeToLiveSeconds) {
        long now = System.currentTimeMillis();

        log.debug("Pre-eviction, totalSessions: {}", userIdSessions.size());
        Set<User> users = userIdSessions.keySet();
        for (User user : users) {
            Session session = userIdSessions.get(user);
            if (session == null) {
                continue;
            }

            log.debug("inactiveTimeMs:{} ttlMs:{}", now - session.getLastActiveMs(), sessionTimeToLiveSeconds * 1000);

            if ((now - session.getLastActiveMs()) > (sessionTimeToLiveSeconds * 1000)) {
                userIdSessions.remove(user);
            }
        }
        log.debug("Post-eviction, totalSessions: {}", userIdSessions.size());
    }
}
