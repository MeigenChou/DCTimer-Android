package com.dctimer.database;

import android.content.Context;
import android.text.TextUtils;

import com.dctimer.R;
import com.dctimer.model.Session;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SessionManager {
    private Context context;
    private DBHelper db;
    private List<Session> sessionList;
    private int currentSession;

    public SessionManager(Context context, DBHelper db) {
        this.context = context;
        this.db = db;
        sessionList = new ArrayList<>();
        db.getSession(sessionList);
        Collections.sort(sessionList, new Comparator<Session>() {
            @Override
            public int compare(Session session, Session t1) {
                return session.getSorting() - t1.getSorting();
            }
        });
    }

    public int getSessionLength() {
        return sessionList.size();
    }

    public int getCurrentSession() {
        return currentSession;
    }

    public void setCurrentSession(int currentSession) {
        this.currentSession = currentSession;
    }

    public String getSessionName(int i) {
        String name = sessionList.get(i).getName();
        if (TextUtils.isEmpty(name)) {
            if (i == 0)
                name = context.getString(R.string.default_session);
            else name = context.getString(R.string.session) + (i + 1);
        }
        return name;
    }

    public void setSessionName(int i, String name) {
        Session session = sessionList.get(i);
        session.setName(name);
        db.updateName(session.getId(), name);
    }

    public String[] getSessionNames() {
        String[] list = new String[sessionList.size()];
        for (int i = 0; i < sessionList.size(); i++) {
            list[i] = getSessionName(i);
        }
        return list;
    }

    public void updateSessionCount() {
        for (Session session : sessionList) {
            int id = session.getId();
            int count = db.getSessionCount(id);
            session.setCount(count);
        }
    }

    public int getPuzzle(int i) {
        Session session = sessionList.get(i);
        return session.getPuzzle();
    }

    public void setPuzzle(int i, int puzzle) {
        Session session = sessionList.get(i);
        if (session.getPuzzle() != puzzle) {
            session.setPuzzle(puzzle);
            db.updatePuzzle(session.getId(), puzzle);
        }
    }

    public int getMultiPhase(int i) {
        Session session = sessionList.get(i);
        return session.getMultiPhase();
    }

    public void setMultiPhase(int i, int mp) {
        Session session = sessionList.get(i);
        if (session.getMultiPhase() != mp) {
            session.setMultiPhase(mp);
            db.updateMultiPhase(session.getId(), mp);
        }
    }

    public int getAverage(int i) {
        Session session = sessionList.get(i);
        return session.getAvg();
    }

    public void setAverage(int i, int avg) {
        Session session = sessionList.get(i);
        if (session.getAvg() != avg) {
            session.setAvg(avg);
            db.updateAverage(session.getId(), avg);
        }
    }

    public void addSession(String name) {
        int id = db.addSession(name);
        Session session = new Session(id, name, 33, 0, 8011, sessionList.size() + 1);
        sessionList.add(session);
    }

    public Session getSession(int i) {
        return sessionList.get(i);
    }

    public void removeSession(int i) {
        Session session = sessionList.get(i);
        int id = session.getId();
        db.deleteSession(id);
        sessionList.remove(i);
    }

    public void move(int from, int to) {
        Session session = sessionList.remove(from);
        sessionList.add(to, session);
    }

    public void save() {
        for (int i = 0; i < sessionList.size(); i++)
            sessionList.get(i).setSorting(i + 1);
        db.saveSession(sessionList);
    }
}
