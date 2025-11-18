package network;

import java.util.concurrent.CopyOnWriteArrayList;
import interfaces.ISession;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class SessionManager {

    private static SessionManager instance;
    private final List<ISession> sessions;
    private final ConcurrentHashMap<Long, ISession> sessionById;
    private final AtomicInteger sessionCount;

    public static SessionManager gI() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public SessionManager() {
        this.sessions = new CopyOnWriteArrayList<>();
        this.sessionById = new ConcurrentHashMap<>();
        this.sessionCount = new AtomicInteger(0);
    }

    public void putSession(ISession session) {
        this.sessions.add(session);
        this.sessionById.put(session.getID(), session);
        this.sessionCount.incrementAndGet();
    }

    public void removeSession(ISession session) {
        this.sessions.remove(session);
        this.sessionById.remove(session.getID());
        this.sessionCount.decrementAndGet();
    }

    public List<ISession> getSessions() {
        return this.sessions;
    }

    public ISession findByID(long id) throws Exception {
        ISession session = this.sessionById.get(id);
        if (session == null) {
            throw new Exception("Session " + id + " does not exist");
        }
        return session;
    }

    public int getNumSession() {
        return this.sessionCount.get();
    }
}
